/*??
 * COPYRIGHT (C) 2008-2009 CHERIMOIA LLC. ALL RIGHTS RESERVED.
 *
 * THIS IS FREE SOFTWARE; YOU CAN REDISTRIBUTE IT AND/OR
 * MODIFY IT UNDER THE TERMS OF THE APACHE LICENSE, 
 * VERSION 2.0 (THE "LICENSE").
 *
 * THIS LIBRARY IS DISTRIBUTED IN THE HOPE THAT IT WILL BE USEFUL,
 * BUT WITHOUT ANY WARRANTY; WITHOUT EVEN THE IMPLIED WARRANTY OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 *   
 * SEE THE LICENSE FOR THE SPECIFIC LANGUAGE GOVERNING PERMISSIONS 
 * AND LIMITATIONS UNDER THE LICENSE.
 *
 * You should have received a copy of the Apache License
 * along with this distribution; if not, you may obtain a copy of the 
 * License at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 ??*/

package com.zotoh.stratum.sql;

import static com.zotoh.core.util.StrUte.isEmpty;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.zotoh.core.db.DBVendor;
import com.zotoh.core.util.Tuple;
import com.zotoh.stratum.core.AssocMetaHolder;
import com.zotoh.stratum.core.ClassMetaHolder;
import com.zotoh.stratum.core.FldMetaHolder;
import com.zotoh.stratum.core.M2MTable;
import com.zotoh.stratum.core.MetaCache;

/**
 * @author kenl
 *
 */
public abstract class DBDriver {
	
    protected MetaCache m_meta= new MetaCache();
    protected static final String S_DDLSEP= "-- :";
    protected boolean m_useSep = true;
    
    /**
     * @param v
     * @return
     * @throws SQLException
     */
    public static DBDriver newDriver(DBVendor v) 
    throws SQLException {
        
        if (DBVendor.HSQLDB.equals(v))
        return new HSQLDBImpl();
        
        if (DBVendor.H2.equals(v))
        return new H2Impl();
        
        if (DBVendor.MYSQL.equals(v))
        return new MySQLImpl();
        
        if (DBVendor.SQLSERVER.equals(v))
        return new SQLSvrImpl();
        
        if (DBVendor.POSTGRESQL.equals(v))
        return new PostgreSQLImpl();
        
        if (DBVendor.ORACLE.equals(v))
        return new OracleImpl();
        
        if (DBVendor.DERBY.equals(v))
        return new DerbyImpl();
        
        if (DBVendor.DB2.equals(v))
        return new DB2Impl();
        
        throw new SQLException("Unsupported database : " + v) ;
    }
    
    /**
     * @param classes
     * @return
     * @throws Exception
     */
    public String getDDL(Class<?> ... classes ) throws Exception    {
    	
        StringBuilder body= new StringBuilder(1024);
        StringBuilder drops= new StringBuilder(512);
        
        ClassMetaHolder zm;
        String tn;
        
        for (int i=0; i < classes.length; ++i) {
            zm= m_meta.getClassMeta( classes[i]) ;
        }
        for (int i=-1; i < classes.length; ++i) {
        	if(i== -1) {
                zm= m_meta.getClassMeta(M2MTable.class) ;        		
        	} else {
        		zm= m_meta.getClassMeta( classes[i]) ;
        	}
            tn= zm.getTable().toUpperCase();
            drops.append( genDrop(tn)) ;
            body.append( f(zm));
        }
        
        return  "" + drops + body + genEndSQL(); 
    }
        
    /**
     * @param zm
     * @return
     * @throws Exception
     */
    protected String f(ClassMetaHolder zm)  throws Exception     {
    	
        String n= zm.getTable().toUpperCase();

        if (isEmpty(n)) { return ""; }
        
        Map<String,FldMetaHolder> getters = zm.getFldMetas() ;
        Map<String,AssocMetaHolder> assocs = ClassMetaHolder.getAssocMetas() ;
        
        return xx(n, getters, assocs);
    }
    
    /**
     * @param table
     * @param cols
     * @param autoKey
     * @return
     */
    protected String xx(String table, Map<String,FldMetaHolder> cols, Map<String,AssocMetaHolder> assocs)     {
        StringBuilder ddl= new StringBuilder(10000),
        				inx= new StringBuilder(256) ;
        
        //ddl.append( genDrop(table));
        
        ddl.append( genBegin(table));
        ddl.append(genBody(table, cols, assocs, inx));
        ddl.append(genEnd());
        if ( inx.length() > 0) {
        	ddl.append(inx.toString()) ;
        }        
        ddl.append( genGrant(table));
        
        return ddl.toString();
    }
        
    /**
     * @param tbl
     * @return
     */
    protected String genDrop( String tbl)    {
        return new StringBuilder(256).append("DROP TABLE ").append(tbl.toUpperCase()).
        append(genExec()).append("\n\n").toString();
    }

    /**
     * @param tbl
     * @return
     */
    protected String genBegin( String tbl)     {
        return new StringBuilder(256).append("CREATE TABLE ").append(tbl.toUpperCase()).
        append("\n(\n").toString();
    }
    
    /**
     * @param table
     * @param cols
     * @return
     */
    protected String genBody(String table, 
    				Map<String,FldMetaHolder> cols, 
    				Map<String,AssocMetaHolder> assocs, StringBuilder inx)     {
        StringBuilder bf= new StringBuilder(512);
        Class<?> dt;
        String zn, cn, col;
        Set<String> pkeys= new TreeSet<String>();
        Set<String> keys= new TreeSet<String>();
        FldMetaHolder def;
        
        for (Map.Entry<String, FldMetaHolder> en : cols.entrySet() )        {            
            def= en.getValue();
            cn= en.getKey();
            col=null;
            dt= def.getColType();
            zn= dt.getName();
            
            if (def.isPK()) { pkeys.add(cn); }
            else
            if (def.isUniqueKey()) { keys.add(cn); }
            
            if ( Boolean.class == dt || "boolean".equals(zn) ) { col= genBool(def); }
            else if ( java.sql.Timestamp.class== dt ) { col= genTimestamp(def); }
            else if ( java.util.Date.class== dt ) { col= genDate(def); }
            else if ( Integer.class== dt || "int".equals(zn)) {
                col= def.isAutoGen() ? genAutoInteger(table, def) :
                    genInteger(def);
            }
            else if ( Long.class== dt || "long".equals(zn)) { 
                col= def.isAutoGen() ? genAutoLong(table, def) :
                    genLong(def);
            }
            else if ( Double.class==dt || "double".equals(zn)) { col= genDouble(def); }
            else if ( Float.class==dt || "float".equals(zn)) { col= genFloat(def); }
            else if ( String.class==dt) { col= genString(def); }
//            else if ( StreamData.class==dt ) col= GenBlob(p);
            else if ( byte[].class== dt ) { col= genBytes(def); }

            if (isEmpty(col)) {            continue;            }

            if (bf.length() > 0) { bf.append(",\n"); }
            bf.append(col);
        }
        AssocMetaHolder asoc= assocs.get(table);
        inx.setLength(0) ;
        int iix=1;
        if (asoc != null) for (Tuple t : asoc.getFKeys()) {
        	Boolean m2m = (Boolean) t.get(0);
        	if (m2m) { continue; }
        	t.get(1);
        	t.get(2);        	
        	cn = (String) t.get(3) ;
            col = genColDef( cn, 
            				getLongKeyword() ,
//                            getStringKeyword() + "(255)" ,
                             true) ;
            if (bf.length() > 0) { bf.append(",\n"); }
            bf.append(col);
            
            inx.append(
				"CREATE INDEX " + table + "_IDX_" + iix + " ON " + table + " ( " + MetaCache.COL_ROWID + ", " + cn + " )" + 
									genExec() + "\n\n" );
            ++iix;
        }
        
        
        if (bf.length() > 0)         {
            String s= pkeys.size() ==0 ? "" : genPrimaryKey(pkeys);
            if ( !isEmpty(s)) {
                bf.append(",\n").append(s);
            }
            s= keys.size() ==0 ? "" : genUniques(keys);
            if ( !isEmpty(s)) {
                bf.append(",\n").append(s);
            }
        }

        return bf.toString();
    }
    
    /**
     * @return
     */
    protected String genEnd()     {
        return new StringBuilder(256).append("\n)").append(genExec()).append("\n\n").toString();
    }
    
    /**
     * @param tbl
     * @return
     */
    protected String genGrant(String tbl)    {
        return "";
    }
    
    protected String genEndSQL()     {
        return "";
    }

    /**
     * @param keys
     * @return
     */
    protected String genPrimaryKey(Set<String> keys)    {
        String[] a= keys.toArray(new String[0]) ;
        Arrays.sort(a);
        String b="";
        for (int i=0; i < a.length; ++i) {           
            if (b.length() > 0) { b += ","; }
            b += a[i];
        }        
        return getPad() + "PRIMARY KEY(" + b + ")";
    }
    
    protected String genUniques(Set<String> keys)    {
        String[] a= keys.toArray(new String[0]) ;
        Arrays.sort(a);
        String b="";
        for (int i=0; i < a.length; ++i) {           
            if (b.length() > 0) { b += ","; }
            b += a[i];
        }        
        return getPad() + "UNIQUE(" + b + ")";
    }
    
    /**
     * @param col
     * @param type
     * @param optional
     * @return
     */
    protected String genColDef(String col, String type, boolean optional)     {
        return new StringBuilder(256)
        .append(getPad()).append(col ).append(" ")
        .append( type )
        .append(" ")
        .append(nullClause( optional ))
        .toString()
        ;
    }
    
    /**
     * @param def
     * @return
     */
    protected String genBytes(FldMetaHolder def)    {
        return genColDef(def.getId(), getBlobKeyword(), def.isNullable()) ;
    }
    
    /**
     * @param def
     * @return
     */
    protected String genString(FldMetaHolder def)     {
        return genColDef(def.getId(), 
                getStringKeyword() + "(" + Integer.toString(def.getSize()) + ")"
                , def.isNullable()) ;
    }
    
    /**
     * @param def
     * @return
     */
    protected String genInteger(FldMetaHolder def)     {
        return genColDef(def.getId(), getIntKeyword(), def.isNullable()) ;
    }
    
    /**
     * @param table
     * @param def
     * @return
     */
    protected String genAutoInteger(String table, FldMetaHolder def)     {
        return "";
    }
    
    /**
     * @param def
     * @return
     */
    protected String genDouble(FldMetaHolder def)    {        
        return genColDef(def.getId(), getDoubleKeyword(), def.isNullable()) ;
    }
    
    /**
     * @param def
     * @return
     */
    protected String genFloat(FldMetaHolder def)     {        
        return genColDef(def.getId(), getFloatKeyword(), def.isNullable()) ;
    }
    
    /**
     * @param def
     * @return
     */
    protected String genLong(FldMetaHolder def)    {
        return genColDef(def.getId(), getLongKeyword(), def.isNullable()) ;
    }
    
    /**
     * @param table
     * @param def
     * @return
     */
    protected String genAutoLong(String table, FldMetaHolder def)    {
        return "";
    }
    
    /**
     * @param def
     * @return
     */
    protected String genTimestamp(FldMetaHolder def)    {
        return genColDef(def.getId(), getTSKeyword(), def.isNullable()) ;
    }
    
    /**
     * @param def
     * @return
     */
    protected String genDate(FldMetaHolder def)    {
        return genColDef(def.getId(), getDateKeyword(), def.isNullable()) ;
    }
    
    /**
     * @param def
     * @return
     */
    protected String genBool(FldMetaHolder def)    {
        return genColDef(def.getId(), getBoolKeyword(), def.isNullable()) ;
    }

    /**
     * @return
     */
    protected String getTSDefault()    {
        return "DEFAULT CURRENT_TIMESTAMP";
    }
    
    /**
     * @return
     */
    protected String getPad()    { 
        return "    "; 
    }
    
    /**
     * @return
     */
    protected String getFloatKeyword()    {
        return "FLOAT";
    }
    
    /**
     * @return
     */
    protected String getIntKeyword()    {
        return "INTEGER";
    }

    /**
     * @return
     */
    protected String getTSKeyword()    {
        return "TIMESTAMP";
    }
    
    /**
     * @return
     */
    protected String getDateKeyword()    {
        return "DATE";
    }
    
    /**
     * @return
     */
    protected String getBoolKeyword()    {
        return "INTEGER";
    }

    /**
     * @return
     */
    protected String getLongKeyword()    {
        return "BIGINT";
    }

    /**
     * @return
     */
    protected String getDoubleKeyword()    {
        return "DOUBLE PRECISION";
    }

    /**
     * @return
     */
    protected String getStringKeyword()    {
        return "VARCHAR";
    }
    
    /**
     * @return
     */
    protected String getBlobKeyword()    {
        return "BLOB";
    }

    /**
     * @param optional
     * @return
     */
    protected String nullClause(boolean optional)    {
        return optional ? getNull() : getNotNull();
    }
    
    /**
     * @return
     */
    protected String getNotNull()     { 
        return "NOT NULL"; 
    }

    /**
     * @return
     */
    protected String getNull()     { 
        return "NULL"; 
    }
    
    /**
     * @return
     */
    protected String genExec()     {
        return ";\n" + genSep() ;
    }
    
    /**
     * @return
     */
    protected String genSep()     { 
        return m_useSep ? S_DDLSEP : ""; 
    }
    
    /**
     * 
     */
    protected DBDriver()
    {}
    
}
