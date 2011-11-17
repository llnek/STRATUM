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


package com.zotoh.stratum.core;

import static com.zotoh.core.util.LangUte.MP;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *  This cache holds database table information, and annotated class
 *  information.
 *
 * @author kenl
 *
 */
public final class MetaCache {
	
    public static final String COL_ROWID= "II_ROWID";
    public static final String COL_VERID= "II_VERID";
    
    public static final String COL_RHS= "II_RHS";
    public static final String COL_LHS= "II_LHS";
    
    public static final String COL_RHSOID= "II_RHSOID";
    public static final String COL_LHSOID= "II_LHSOID";
    
    
    
    private Map<Class<?>, ClassMetaHolder> _classes= MP();
    private Map<String, TableMetaHolder> _meta= MP();

    /**
     * 
     */
    public MetaCache() {
    	loadClassMeta(M2MTable.class);
    }
    
    /**
     * Get table information from cache, or from the database.
     * 
     * @param table table name.
     * @param con a valida db connection.
     * @return
     * @throws SQLException
     */
    public TableMetaHolder getTableMeta( String table, Connection con) 
			throws SQLException    {       
        TableMetaHolder m= getTableMeta(table);
        
        if (m==null && con != null) {
            m= loadTableMeta(table, con) ;
        }
        
        return m; 
    }
    
    /**
     * Get table information from cache.
     * 
     * @param table table name.
     * @return
     */
    public TableMetaHolder getTableMeta( String table)     {
        return table==null ? null : _meta.get(table.toUpperCase()) ;
    }

    /**
     * Get class information from cache.
     * 
     * @param target the class.
     * @return
     */
    public ClassMetaHolder getClassMeta( Class<?> target)    {
        ClassMetaHolder rc= _classes.get(target);
        if (rc ==null) {
            rc= loadClassMeta(target);
        }
        return rc;
    }
    
    private synchronized ClassMetaHolder loadClassMeta( Class<?> z)     {
        ClassMetaHolder rc=null;
        try        {
            rc= new ClassMetaHolder().scan(z);
            _classes.put(z, rc) ;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to parse Class annotations : " + z.getName() );
        }
        return rc;
    }
    
    private synchronized TableMetaHolder loadTableMeta(String table, Connection con) 
		throws SQLException     {       
        DatabaseMetaData m= con.getMetaData();
        String pn = m.getDatabaseProductName();
        String ret= null,
        catalog=null,
        n,
        schema=null;
        ResultSet rs= null;        
                
        if ("Oracle".equalsIgnoreCase(pn )) {
            schema = "%"; // use "%" as the pattern
        }
        
        // first try uppercase
        try {
            rs= m.getTables(catalog, schema, table.toUpperCase(), null);
            if (rs != null && rs.next()) {
                ret= rs.getString(3);            
            }            
        }
        finally {
            safeClose(rs);            
        }
                
        // then try lowercase
        if (ret == null)
        try   {
            rs = m.getTables( catalog, schema, table.toLowerCase(), null);
            if ( rs != null && rs.next()) {
                ret = rs.getString(3);
            }
        }
        finally {
            safeClose(rs);            
        }
        
        // not good, try mixed case... arrrrrrrrrrhhhhhhhhhhhhhh
        if (ret == null)
        try        {
            rs = m.getTables( catalog, schema, "%", null);
            while ( rs != null && rs.next()) {
                n = rs.getString(3);
                if (table.equalsIgnoreCase(n)) {
                    ret = n;
                    break;
                }
            }
        }
        finally {
            safeClose(rs);            
        }

        if (ret==null) {
            ret=table;
        }
        
        return loadColumns(m, catalog, schema, ret);
    }
    
    private TableMetaHolder loadColumns(DatabaseMetaData m, String catalog, String schema, String table) 
			throws SQLException     {    
        TableMetaHolder tm= new TableMetaHolder(table);
        Set<String> keys= new HashSet<String>();
        ColMetaHolder c;
        String cn;
        int type;
        boolean opt;
        
        ResultSet rs= m.getPrimaryKeys(catalog, schema, table) ;        
        try   {
            while ( rs != null && rs.next()) {
                cn = rs.getString(4);
                keys.add(cn.toUpperCase() );
            }
        }
        finally {
            safeClose(rs);                        
        }
        
        rs = m.getColumns( catalog, schema, table, "%");
        try  {
            while ( rs != null && rs.next()) {
                opt= rs.getInt(11) != DatabaseMetaData.columnNoNulls ;
                cn = rs.getString(4);
                cn=cn.toUpperCase();
                type = rs.getInt(5);
                c= new ColMetaHolder(cn, type, opt);
                tm.addCol(c, keys.contains(cn));
            }
        }
        finally {
            safeClose(rs);                        
        }
               
        tm.setGetGeneratedKeys( m.supportsGetGeneratedKeys());
        tm.setTransact( m.supportsTransactions());
        
        // we store the key with uppercase
        _meta.put( table.toUpperCase(), tm) ;
        return tm;
    }
    
    private ResultSet safeClose(ResultSet rs) {
        if ( rs != null)         { 
            try { rs.close(); } catch (Exception e) {} 
        }
        return null;
    }
    
}
