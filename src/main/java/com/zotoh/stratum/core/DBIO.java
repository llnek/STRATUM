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

import static com.zotoh.core.db.DBUte.safeClose;
import static com.zotoh.core.db.JDBCUte.setStatement;
import static com.zotoh.core.db.JDBCUte.toSqlType;
import static com.zotoh.core.util.LangUte.AA;
import static com.zotoh.core.util.LangUte.LT;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static com.zotoh.core.util.StrUte.addAndDelim;
import static com.zotoh.stratum.core.MetaCache.COL_LHS;
import static com.zotoh.stratum.core.MetaCache.COL_LHSOID;
import static com.zotoh.stratum.core.MetaCache.COL_RHS;
import static com.zotoh.stratum.core.MetaCache.COL_RHSOID;
import static com.zotoh.stratum.core.MetaCache.COL_ROWID;
import static com.zotoh.stratum.core.MetaCache.COL_VERID;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import com.zotoh.core.db.DBRow;
import com.zotoh.core.db.DBVendor;
import com.zotoh.core.db.DELETEStmt;
import com.zotoh.core.db.JConnection;
import com.zotoh.core.db.JDBC;
import com.zotoh.core.db.JDBCInfo;
import com.zotoh.core.db.JDBCPool;
import com.zotoh.core.db.JDBCUte;
import com.zotoh.core.db.SELECTStmt;
import com.zotoh.core.util.Logger;
import com.zotoh.core.util.Tuple;
import com.zotoh.stratum.anote.Table;

/**
 * This class provides a set of methods for accessing the database. 
 *
 * @author kenl
 *
 */
public class DBIO {
	
    private final MetaCache _dbMeta= new MetaCache();
    private JDBCPool _pool;
    private JDBC _jdbc;
    private transient JConnection _txObj;
    
    private transient Logger _log=getLogger(DBIO.class);
    public Logger tlog() {  return _log;    }    

    /**
     * Execute a raw SQL query and return a list of results.
     * 
     * @param sql
     * @param pms
     * @return
     * @throws SQLException
     */
    public List< DBRow > fetchSQL(String sql, Tuple pms)
		throws SQLException {
		return _jdbc.fetchRows( new SELECTStmt(sql, pms) );
    }
    
    /**
     * Execute a raw SQL update/insert/delete.
     * 
     * @param sql the raw SQL.
     * @param params parameters (if any).
     * @return the result of the update.
     * @throws SQLException
     */
    public int execUpdateSQL(String sql, Object... params) throws SQLException, IOException {        
        JConnection jc= isTransacting() ? txObj() : _pool.getNextFree();
        int rc=0;        
        PreparedStatement ps= null;
        try {
            ps= jc.getConnection().prepareStatement( jiggleSQL(sql, -1) ) ;
            for (int i=0; i < params.length; ++i) {
                setStatement(ps, i+1, params[i ] ) ;
            }
            rc=ps.executeUpdate();
        }
        finally {
            safeClose(ps);
            if ( ! isTransacting()) { _pool.returnUsed(jc); }
        }
        
        return rc;
    }
    
    /**
     * 
     */
    public void close() {
    		_pool=null;
    }
    
    /**
     * Perform a "select count(*) " on a table.
     * 
     * @param target the class for that table.
     * @return the count.
     * @throws SQLException
     */
    public int countRows(Class<?> target) throws SQLException {        
    	TableMetaHolder tm= getTableMeta(target) ;
    	return tm==null ? 0 : _jdbc.countRows( tm.getName()  ) ;
    }
    
    /**
     * Call a database function.
     *     
     * @param name the name of the function in the database.
     * @param params the 
     * @param outs
     * @return
     * @throws SQLException
     * @throws IOException 
     */
    public List<Object> callProcAsFunction(String name, List<Object> params, List< Class<?> > outs)
		throws SQLException, IOException {
        return callSProc(name, params, outs, true);
    }
    
    /**
     * Call a database procedure.
     *  
     * @param name the name of the stored procedure.
     * @param params
     * @param outs
     * @return
     * @throws SQLException
     * @throws IOException 
     */
    public List<Object> callProc(String name, List<Object> params, List< Class<?> > outs)
		throws SQLException, IOException {        
        return callSProc(name, params, outs, false);
    }

    private List<Object> callSProc(String name, List<Object> params, List< Class<?> > outs, 
            boolean isFunc)	throws SQLException, IOException {

        JConnection jc = isTransacting() ? txObj() : _pool.getNextFree();
        Connection con;
        CallableStatement cs= null;
        ResultSet rs= null;
        String sql;
        Object v;
        Class<?> z;
        int inc;
        
        StringBuilder w= new StringBuilder(256) ;
        List<Object> rc= LT();
        
        if (params==null) { params= LT(); }
        if (outs==null) { outs= LT(); }
        
        try {
            con= jc.getConnection();
            for (int i=0; i < params.size(); ++i) {
                if ( params.get(i) != null) { addAndDelim(w, ",", "?" ) ; }            	
            }
            sql =  " { ?= CALL " +  name + "(" + w +  ") }" ;
            cs= con.prepareCall(sql);            
            inc=1;
            for (int i=0; i < params.size(); ++i) {
                v= params.get(i) ;
                if (v != null) {
                    setStatement(cs, inc , v);
                    ++inc;
                }
            }       
            inc=1;
            for (int i=0; i < outs.size(); ++i) {
                z= outs.get(i);
                if (z != null) {
                    cs.registerOutParameter( inc, toSqlType( z)) ;
                    ++inc;
                }                                    
            }
            
            cs.execute();  
            rs = cs.getResultSet();
            
            // add any function returned value
            if (rs != null && rs.next()) {
                rc.add( rs.getObject(1) );
            }
            
            // add out params
            inc=1;
            for (int i=0; i < outs.size(); ++i) {
                z= outs.get(i);
                if (z != null) {
                    rc.add( cs.getObject( inc ) );
                    ++inc;
                }                                    
            }
            
            return rc;
        }
        finally {
            safeClose(rs) ;
            safeClose(cs) ;
            if (!isTransacting()) { _pool.returnUsed(jc) ; }
        }
        
    }
    
    /**
     * Begin a database transaction.  This must end with a commit or rollback.
     * <br/>
     * Only one transaction can be active at any given time.
     * 
     * @throws SQLException
     */
    public void startTransaction() throws SQLException {
        if (_txObj != null) {
            throw new SQLException("Already in transaction") ;
        }
        _txObj= _jdbc.beginTransaction() ;
        if (_txObj == null) {
            throw new SQLException("Too many connections") ;            
        }
        _txObj.getConnection().setAutoCommit(false);
    }
    
    /**
     * Commit the current transaction.
     * 
     * @throws SQLException
     */
    public void commit() throws SQLException {
		JConnection t= _txObj;    
        if (_txObj == null) {
            throw new SQLException("Not in transaction") ;
        }
        try {
        	_jdbc.commitTransaction(t) ;
        }
        finally {
            _txObj=null;
            _pool.returnUsed(t) ;
        }
    }
    
    /**
     * Rollback the current transaction.</br>
     * NOTE:
     * Should not use any of those objects again as they could be in
     * an inconsistent state.
     * @throws SQLException
     */
    public void rollback() throws SQLException {
		JConnection t= _txObj;
        if (_txObj == null) {
            throw new SQLException("Not in transaction") ;
        }
        try {
        	_jdbc.cancelTransaction(t) ;
        }
        finally {
            _txObj=null;
            _pool.returnUsed(t) ;
        }
    }
    
    /**
     * Get table information.
     * 
     * @param target the class representing the table.
     * @return
     * @throws SQLException
     */
    public TableMetaHolder getTableMeta(Class<?> target) throws SQLException {        
        JConnection jc= _pool.getNextFree() ;
        try {
            return getTableMeta( jc.getConnection(), target );
        }
        finally {
            _pool.returnUsed(jc) ;
        }
    }
    
    /**
     * Get annotated class information.
     * 
     * @param target the class representing the table.
     * @return
     */
    public ClassMetaHolder getClassMeta(Class<?> target) {
        return _dbMeta.getClassMeta(target) ;
    }
    
    /**
     * @param target
     * @param rowID
     * @return
     * @throws SQLException
     */
    public <T> T fetchViaRowID( Class<T> target, long rowID) 
        throws SQLException, IOException {
		return fetchObj( target, new NameValues(COL_ROWID, rowID)) ;
    }
    
    /**
     * Get the object with this set of named cols.
     * 
     * @param target the object's class.
     * @param values cols & values.
     * @return the object.
     * @throws SQLException 
     */
    public <T> T fetchObj( Class<T> target, NameValues values) 
        throws SQLException , IOException   {
        JConnection jc= isTransacting() ? txObj() : _pool.getNextFree() ;
        try {            
            List<T> ret= innerFetch( jc.getConnection(), target, values);
            return ret.size()==0 ? null : ret.get(0) ;
        }
        finally {
            if(! isTransacting()) { _pool.returnUsed(jc) ; }
        }
    }
    
    /**
     * @param target
     * @param values
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public <T> List<T> fetchObjs( Class<T> target, NameValues values) 
            throws SQLException , IOException   {
        JConnection jc= isTransacting() ? txObj() : _pool.getNextFree() ;
        try {
            return innerFetch( jc.getConnection(), target, values);
        }
        finally {
            if(! isTransacting()) { _pool.returnUsed(jc) ; }
        }
    }
    
    private <T> List<T> innerFetch( Connection con, Class<T> z, NameValues values) 
			throws SQLException, IOException    {
    	
        TableMetaHolder tm= getTableMeta( con, z ) ;        
        List<Object> vs= LT();        
        String sql, w= values.toWhereClause(vs) ;
        
        sql= "SELECT * FROM " + tm.getName();
        if (w.length() > 0) { 
        	sql += " WHERE " + w; }
        
        return innerSelect(con, tm, z, sql, AA(vs) );
    }
    
    private <T> List<T> execFetch(  Connection con, Class<T> z, String sql, Object... vs)
		throws SQLException, IOException    {
        PreparedStatement ps=con.prepareStatement( jiggleSQL(sql, 1 ) ) ;
        ResultSet rs=null; 
        
        TableMetaHolder tm= getTableMeta( con, z ) ;
        List<T> ret= LT();
        int pos= 0;
        try {            
            for (int i=0; i < vs.length; ++i) {
            	if (vs[i] != null) {
            		setStatement(ps, ++pos, vs[i] ) ;
            	}
            }                        
            ret= readResults(tm, z,  rs= ps.executeQuery() ) ;
        }
        finally {
            safeClose(rs);
            safeClose(ps);
        }
        
        return ret;
    }
    
    /**
     * Fetch target objects via raw SQL.
     * 
     * @param target the class representing the table.
     * @param sql the raw SQL.
     * @param params parameters (if any).
     * @return list of results.
     * @throws SQLException
     */
    public <T> List<T> fetchViaSQL( Class<T> target, String sql, Object... params)
				throws SQLException, IOException    {
        JConnection jc= isTransacting() ? txObj() : _pool.getNextFree();      
        List<T> rc= LT();        
        try {
            rc= execFetch( jc.getConnection(), target, sql, params );
        }
        finally {
            if (! isTransacting()) { _pool.returnUsed(jc) ; }
        }        
        return rc;
    }
    
    /**
     * Returns all rows mapped to this object class.
     * 
     * @param target the class representing the table.
     * @return the list of results.
     * @throws SQLException error
     */
    public <T> List<T> fetch( Class<T> target ) 
            throws SQLException, IOException {
        
        JConnection jc= isTransacting() ? txObj() : _pool.getNextFree();      
        try  {
            return innerFetch( jc.getConnection(), target ) ;
        }
        finally {
            if( ! isTransacting() ) { _pool.returnUsed(jc) ; }
        }
        
    }
    
    private <T> List<T> innerFetch( Connection con , Class<T> z ) 
            throws SQLException, IOException {
        
        TableMetaHolder tm = getTableMeta( con, z ) ;     
        // tm should not be null
        String sql= "SELECT * FROM " + tm.getName();
        
        return innerSelect(con, tm, z, sql ) ;
    }

    private <T> List<T> innerSelect(Connection con, TableMetaHolder tm, 
    				Class<T> target, String sql, Object... pms )
        throws SQLException, IOException {
    
        PreparedStatement ps=con.prepareStatement( jiggleSQL( sql, 1) ) ;
        ResultSet rs= null;        
        List<T> ret= LT();
        int pos=0;
        try {
            for (int i=0; i < pms.length; ++i) {
            	if (pms[i] != null) {
            		setStatement(ps, ++pos, pms[i] );
            	}
            }
            ret= readResults(tm, target,  rs= ps.executeQuery() ) ;
        }
        finally {
            safeClose(rs);
            safeClose(ps);
        }
        
        return ret;
    }
    
    private <T> T newObj(Class<T> z) throws SQLException {
        try {
            return z.getConstructor().newInstance() ;
        }
        catch (Exception e) {
            throw new SQLException(e);
        }        
    }
    
    private <T> List<T> readResults(TableMetaHolder meta, Class<T> z, ResultSet rs) 
    				throws SQLException,IOException    {
        List<T> ret= LT() ;
        if (rs==null) { return ret; }
        
        ClassMetaHolder zm= _dbMeta.getClassMeta(z) ;        
    	ResultSetMetaData mm= rs.getMetaData();
    	int clen= mm.getColumnCount();
    	
        tlog().debug("readResults: n# of columns = {}" , clen) ;
        
        while ( rs.next() ) {
        	ret.add( fillObjFields( z, zm, (StratumObj) newObj(z), rs, mm)  );
        }
        return ret;
    }
    
    @SuppressWarnings("unchecked")
	private <T> T fillObjFields( Class<T> z, ClassMetaHolder zm, StratumObj obj, 
            ResultSet rs, ResultSetMetaData mm) 
    				throws SQLException, IOException {
    	
        Map<String,FldMetaHolder> flds=zm.getFldMetas();
        FldMetaHolder fm;
        Method st=null;
        Object r;
        int t, clen = mm.getColumnCount();
        String cn;
        Class<?> pt;
        Class<?>[] pms;
        
        for (int i=1; i <= clen; ++i) {
            cn=mm.getColumnName(i).toUpperCase();
            t= mm.getColumnType(i);
            if (java.sql.Types.NULL == t) { continue; }
                        
            fm= flds.get(cn);
            pt=null;
            if (COL_ROWID.equals(cn) || COL_VERID.equals(cn)) {
                pt=Long.class;
            }  else {                
                st= fm==null ? null : fm.getSetter();
                if (st==null) { continue; }
                pms=st.getParameterTypes();
                if (pms != null && pms.length > 0) { pt=pms[0] ; }                                   
            }
            if (pt==null) { continue; }
            
            r= JDBCUte.getObject(rs, i, t, pt) ;
            if (COL_ROWID.equals(cn) ) {
                obj.setRowID( (Long) r) ;
            }
            else if (COL_VERID.equals(cn)) {
                obj.setVerID((Long) r) ;
            }
            else if (st != null)
            try {
                st.invoke(obj, r) ;
            }
            catch (Exception e) {
                throw new IOException("Failed to setXXX() on col: " + cn + ", class = " + obj.getClass()) ;
            }
        }
        
        return (T) obj ;
    }
    
    /**
     * Update row(s) in the database.
     * 
     * @param objs the object(s) to be updated.
     * @throws SQLException the error.
     */
    public void update(StratumObj ... objs) throws SQLException , IOException {
        
        JConnection jc= isTransacting() ? txObj() : _pool.getNextFree();
        Connection con;
        try {
            con= jc.getConnection();
            for (int i=0; i < objs.length; ++i) {
                innerUpdate( con, objs[i] ) ;                
            }
        }
        finally {
            if (!isTransacting()) { _pool.returnUsed(jc) ; }
        }
    }
        
    private void innerUpdate( Connection con, StratumObj obj ) throws SQLException, IOException {
        Tuple t= getMeta(con, obj.getClass() ) ;
        doUpdate( con, (ClassMetaHolder) t.get(0), (TableMetaHolder) t.get(1), obj ) ;        
    }

    private void doUpdate( Connection con, ClassMetaHolder zm, TableMetaHolder tm, 
			StratumObj obj ) throws SQLException , IOException   {
    	
        StringBuilder bf= new StringBuilder(1024)
        .append( "UPDATE  " )
        .append( tm.getName() )
        .append( " SET " );

        Map<String,ColMetaHolder> cms= tm.getColMetas();
        Map<String,FldMetaHolder> flds=zm.getFldMetas();
        long curVer= obj.getVerID() ,
        				newVer= curVer+1;
        int ct;
        List<Tuple> vs= LT();
        
        StringBuilder w= new StringBuilder(512) ,
        b1= new StringBuilder(512);
        
        FldMetaHolder fld;
        Object arg;
        Method gm;
        String cn;
        ColMetaHolder cm;
        
        for (Map.Entry<String, ColMetaHolder> en : cms.entrySet()) {            
            cm= en.getValue();
            cn = cm.getName().toUpperCase();
            
            if (COL_ROWID.equals(cn)) { continue; }
            
            fld=flds.get(cn);
            gm = fld==null ? null : fld.getGetter();
            if (gm==null) { continue; }    
            
            if (fld.isAutoGen()) { continue; }            
            if (COL_VERID.equals(cn)) {
                ct=java.sql.Types.BIGINT;
                arg = newVer;
            } else {
                ct= cm.getSQLType();
                try {
                    arg= gm.invoke(obj);
                } 
                catch (Exception e) { 
                    throw new SQLException(e);
                }
            }
            
            vs.add( new Tuple( arg, ct) );
            
            addAndDelim(b1, ",", cn+"=?") ;            
        }

        // add in the row id, primary key
        vs.add(new Tuple( obj.getRowID(), java.sql.Types.BIGINT) );
        vs.add(new Tuple( curVer, java.sql.Types.BIGINT) );
        w.append(COL_ROWID).append("=? and ").append(COL_VERID).append("=?");
        
        bf.append(b1)
        .append(" WHERE ").append(w);
        
        int cnt= update( con, bf.toString(), AA(Tuple.class,vs) ) ;

        lockError(cnt, tm.getName(), obj.getRowID() );
        
        // update the version num
        obj.setVerID( newVer);
    }

    private void lockError(int cnt, String table, long rowID) throws SQLException {
        if (cnt==0) {
           throw new SQLException("Possible Optimistic lock failure for table: " + 
        				   table + ", rowid= " + rowID);
        }
    }

    private int update( Connection con, String sql, Tuple...  values ) 
				throws SQLException, IOException {
                
        PreparedStatement ps=con.prepareStatement( jiggleSQL(sql,2) ) ;        
        Tuple t;
        int n;        
        try {
            for (int i=0; i < values.length; ++i) {
                t= values[i ] ;
                setStatement(ps, i+1, (Integer) t.get(1), t.get(0) ) ;
            }            
            n= ps.executeUpdate() ;
        }
        finally {
            safeClose(ps);
        }
        
        tlog().debug("update: {} row(s)" , n) ;        

        return n;
    }
    
    /**
     * @param z
     * @throws SQLException
     */
    public void removeAll(Class<?> z) throws SQLException {
        Table t= z.getAnnotation(Table.class) ;
        if (t==null) { return; }
        String tn= t.table();
        
        this._jdbc.deleteRows( DELETEStmt.simpleDelete(tn) ) ;
    }
    
    /**
     * Remove row(s) from the database.
     * 
     * @param objs the list of objects to be removed.
     * @throws SQLException
     */
    public void remove(StratumObj ... objs) throws SQLException , IOException {
        JConnection jc= isTransacting() ? txObj() : _pool.getNextFree();
        Connection con;
        try {
            con= jc.getConnection();
            for (int i=0; i < objs.length; ++i) {
                innerRemove( con, objs[i] ) ;                
            }
        }
        finally {
            if ( ! isTransacting()) { _pool.returnUsed(jc) ; }
        }
    }
    
    private void innerRemove( Connection con, StratumObj obj) throws SQLException , IOException {        
        Tuple t= getMeta(con, obj.getClass() ) ;
        doRemove( con, (ClassMetaHolder) t.get(0), (TableMetaHolder) t.get(1), obj );
    }
    
    private void doRemove( Connection con, ClassMetaHolder zm, TableMetaHolder tm, 
            StratumObj obj ) 
    				throws SQLException  , IOException  {
        
        StringBuilder bd= new StringBuilder(512)
        .append("DELETE FROM ")
        .append( tm.getName())
        .append( " WHERE ")
        .append( COL_ROWID )
        .append(" =? and ")
        .append(COL_VERID)
        .append("=?");

        long curVer= obj.getVerID() ,
        				newVer = -1L; //curVer+1;

        int cnt= remove(con, bd.toString(), obj.getRowID(), curVer );

        lockError(cnt, tm.getName(), obj.getRowID() );
        
        // do we need to do this?
        obj.setVerID( newVer) ;
    }
    
    private int remove( Connection con, String sql, long rowID, long verID ) 
		throws SQLException , IOException {
        PreparedStatement ps=con.prepareStatement( jiggleSQL(sql, 0) ) ;
        int n;        
        try {            
            setStatement(ps, 1, java.sql.Types.BIGINT, rowID) ;
            setStatement(ps, 2, java.sql.Types.BIGINT, verID) ;
            n= ps.executeUpdate() ;
        }
        finally {
            safeClose(ps);
        }
                
        tlog().debug("removed {} row(s)" , n ) ;        

        return n;
    }

    /**
     * Returns true if a transaction is active.
     * 
     * @return
     */
    public boolean isTransacting() {        return _txObj != null;    }
    
    private JConnection txObj() {        return _txObj;    }
    
    /**
     * Insert new row(s) into database.
     * 
     * @param objs the object(s) to be created in the database.
     * @throws SQLException the error.
     */
    public void create(StratumObj ... objs) throws SQLException , IOException {

        JConnection jc= isTransacting() ? txObj() : _pool.getNextFree();
        Connection con;
        try {
            con= jc.getConnection();
            for (int i=0; i < objs.length; ++i) {
                innerCreate( con, objs[i] ) ;
            }
        }
        finally {
            if ( !isTransacting()) { _pool.returnUsed(jc) ; }
        }
    }
        
    private StratumObj innerCreate( Connection con, StratumObj obj ) 
            throws SQLException , IOException {
        Tuple t= getMeta( con, obj.getClass()) ;
        Tuple out= doInsert( con, (ClassMetaHolder) t.get(0), (TableMetaHolder) t.get(1), obj ) ;

        if (out != null) {
            obj.setRowID( (Long) out.get(0) );
        }
                
        return obj;
    }
    
    private Tuple doInsert( Connection con, ClassMetaHolder zm, TableMetaHolder tm,
            StratumObj obj ) 
    				throws SQLException, IOException {
                
        StringBuilder bf= new StringBuilder(1024)
        .append("INSERT INTO  ")
        .append(tm.getName())
        .append(" (");
        
        List<Tuple> values= LT();

        // column-name, db-type, value
        StringBuilder b2= new StringBuilder(512), 
        b1= new StringBuilder(512);
        String cn;

        Map<String, FldMetaHolder> flds= zm.getFldMetas();
        Map<String, ColMetaHolder> cms= tm.getColMetas();
        FldMetaHolder fld;
        Object arg;
        Method gm;
        ColMetaHolder cm;
        
        // always set to initial value
        obj.setVerID(1L);
        
        for (Map.Entry<String, ColMetaHolder> en : cms.entrySet()) {            
            
        	cm= en.getValue();
            cn= cm.getName().toUpperCase();
            fld=flds.get(cn) ;

            if (COL_VERID.equals(cn)) {
                arg= new Long(1L);
            } else {
                gm = fld==null ? null  : fld.getGetter();
                if (gm==null) { continue; }
                if (fld.isAutoGen()) { continue; }            
                try {
                    arg= gm.invoke(obj);                
                }
                catch (Exception e) {
                    throw new SQLException(e);
                }
            }
                        
            if (arg == null) { continue; }
                
            addAndDelim(b1, ",", cn) ;            
            addAndDelim(b2, ",", "?") ;
            
            values.add(new Tuple(arg, cm.getSQLType() ));                
        }

        bf.append(b1).append(") VALUES (").append(b2).append(")");
        
        return insert(con, zm, tm, bf.toString(), AA(Tuple.class,values) ) ;
    }
    
    private Tuple insert( Connection con, ClassMetaHolder zm, TableMetaHolder tm, 
			String sql, Tuple... values) 
		throws SQLException , IOException {
        
        tlog().debug("insert: SQL = {}" , sql) ;
        
        PreparedStatement ps= null;
        Tuple t, out=null;
        int n;
        
        if (tm.canGetGeneratedKeys()) {
            ps=con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        }  else {
            ps=con.prepareStatement(sql) ;
        }
        
        try {            
            for (int i=0; i < values.length; ++i) {
                t= values[ i ] ;
                setStatement(ps, i+1, (Integer)t.get(1), t.get(0)) ;
            }
            n= ps.executeUpdate() ;
            
            tlog().debug("insert: inserted {} row(s)", n) ;
            
            if (tm.canGetGeneratedKeys()) {
                ResultSet rs=ps.getGeneratedKeys();
                int cnt=0;
                ResultSetMetaData sm;
                if (rs != null) {
                    sm= rs.getMetaData();
                    cnt= sm.getColumnCount();
                }
                if (cnt==1 && rs.next()) {
                    out = new Tuple(rs.getObject(1)) ;
                }
            }
            
            return out;
        }
        finally {
            safeClose(ps);
        }
        
    }
    
    private TableMetaHolder getTableMeta( Connection con, Class<?> z ) 
    				throws SQLException    {
        Table t= z.getAnnotation(Table.class) ;
        String zn= z.getName();
        
        if (t==null) {
            throw new SQLException("No DBTable-Annotation for class : " + zn);
        }        
        
        String table= t.table();
        TableMetaHolder tm= _dbMeta.getTableMeta(table, con) ;
        
        if (tm==null) {
            throw new SQLException("No Table : " + table + " in DB");
        }
        
        return tm;
    }

    private String jiggleSQL(String sql, int type) {
        
        DBVendor v= _pool.getVendor();
                
        switch (type) {
            case 0: { sql = v.tweakDELETE(sql); } break;
            case 1: { sql = v.tweakSELECT(sql) ; } break;
            case 2: { sql = v.tweakUPDATE(sql) ; } break;
        	case -1: { sql = v.tweakSQL(sql); } break;
        }
        
        tlog().debug("jggleSQL= {}", sql);
        return sql;
    }
        
    /**
     * Constructor.
     * 
     * @param pool the database connection pool.
     */
    public DBIO(JDBCPool  pool)    {
        _jdbc = new JDBC(pool);
        _pool= pool; 
    }
    
    /**
     * @return
     */
    public JDBCInfo getInfo() {    	return _pool.getInfo();    }

    
    /**
     * @param lhs
     * @param rhs
     * @param fkey
     * @return
     * @throws SQLException
     * @throws IOException
     */
	public <T> T getO2O(StratumObj lhs, Class<T> rhs, String fkey)  throws SQLException, IOException  {
    	Table t= rhs.getAnnotation(Table.class) ;
    	if (t==null) { throw new SQLException( "RHS class " + rhs + " has no Table annotation" ); }
    	NameValues nvs= new NameValues(fkey, lhs.getRowID() );
    	return fetchObj(rhs, nvs) ;
    }
    
    /**
     * @param lhs
     * @param rhs
     * @param fkey
     * @throws SQLException
     * @throws IOException 
     */
    public void setO2O(StratumObj lhs, StratumObj rhs, String fkey)  throws SQLException, IOException  {
    	Table t= rhs.getClass().getAnnotation(Table.class) ;
    	if (t==null) { throw new SQLException( "RHS class " + 
    						rhs.getClass() + " has no Table annotation" ); }
    	String tn=t.table().toUpperCase();
    	long curVer = rhs.getVerID() , newVer = curVer+1 ,
    					rid = rhs.getRowID() ;
    	
    	String sql="UPDATE " + tn + " SET " + fkey + " =? , " + COL_VERID + "=? " + " WHERE " + 
    					COL_ROWID + "=? and " + COL_VERID + "=?" ;
    	
    	int cnt= execUpdateSQL(sql, lhs.getRowID(), newVer, rid , curVer ) ;
    	
    	lockError( cnt, tn, rid) ;
    	
    	// up the ver num
    	rhs.setVerID( newVer) ;
    }
    
    /**
     * @param lhs
     * @param rhs
     * @param fkey
     * @throws SQLException
     * @throws IOException 
     */
    public void purgeO2O(StratumObj lhs, Class<?> rhs, String fkey)  throws SQLException, IOException  {
        Table t= rhs.getAnnotation(Table.class) ;
        if (t==null) { throw new SQLException( "RHS class " + 
                            rhs + " has no Table annotation" ); }
        String rn=t.table().toUpperCase();
        String sql="DELETE from " + rn + " WHERE " + fkey + "=?";
        execUpdateSQL(sql, lhs.getRowID() ) ;
    }
    
    /**
     * @param lhs
     * @param rhs
     * @param fkey
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public <T> List<T> getO2M(StratumObj lhs, Class<T> rhs, String fkey)  
            throws SQLException,IOException  {
    	Table t= rhs.getAnnotation(Table.class) ;
    	if (t==null) { throw new SQLException( "RHS class " + rhs + " has no Table annotation" ); }
    	// do a general select
        NameValues nvs= new NameValues(fkey, lhs.getRowID() );        
        return fetchObjs(rhs, nvs);
    }
    
    /**
     * @param lhs
     * @param rhs
     * @param fkey
     * @throws SQLException
     * @throws IOException 
     */
    public void removeO2M(StratumObj lhs, StratumObj rhs, String fkey)  throws SQLException, IOException  {
    	Table t= rhs.getClass().getAnnotation(Table.class) ;
    	if (t==null) { throw new SQLException( "RHS class " + 
    						rhs.getClass() + " has no Table annotation" ); }
        String rn=t.table().toUpperCase();
        
        long curVer= rhs.getVerID() , newVer= curVer+1 ,
        				rid = rhs.getRowID() ;
        
        String sql= "UPDATE " + rn + " SET " + fkey + " =NULL , " + COL_VERID + "=? " +         
        				" WHERE " + COL_ROWID + "=? and " +
        				COL_VERID + "=?" ;
        
        int cnt= execUpdateSQL(sql, newVer, rid, curVer ) ;
        
        lockError(cnt, rn, rid) ;
        
        rhs.setVerID(newVer) ;
    }
    
    /**
     * @param lhs
     * @param rhs
     * @param fkey
     * @throws SQLException
     * @throws IOException 
     */
    public void purgeO2M(StratumObj lhs, Class<?> rhs, String fkey)  throws SQLException, IOException  {
        Table t= rhs.getAnnotation(Table.class) ;
        if (t==null) { throw new SQLException( "RHS class " + 
                            rhs.getClass() + " has no Table annotation" ); }
        String rn=t.table().toUpperCase();
        String sql="DELETE from " + rn + " WHERE " + fkey + "=?";
        execUpdateSQL(sql, lhs.getRowID() ) ;
    }
    
    /**
     * @param lhs
     * @param rhs
     * @param fkey
     * @throws SQLException
     * @throws IOException 
     */
    public void addO2M(StratumObj lhs, StratumObj rhs, String fkey)  throws SQLException, IOException  {
    	Table t= rhs.getClass().getAnnotation(Table.class) ;
    	if (t==null) { throw new SQLException( "RHS class " + 
    						rhs.getClass() + " has no Table annotation" ); }
        String tn=t.table().toUpperCase();
        long curVer = rhs.getVerID() , 
        				newVer = curVer+1;
        long rid= rhs.getRowID() ;
        
        String sql="UPDATE " + tn + " SET " + fkey + " =? , " + COL_VERID + "=? " + " WHERE " + 
        				COL_ROWID + "=? and " +	COL_VERID + "=?" ;
        
        int cnt= execUpdateSQL(sql, lhs.getRowID(), newVer, rid , curVer ) ;
        
        lockError(cnt, tn, rid) ;
        
        // up the ver num
        rhs.setVerID( newVer) ;
    }

        
    /**
     * @param lhs
     * @param rhs
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public <T> List<T> getM2M(StratumObj lhs, Class<T> rhs )
            throws SQLException, IOException {
        Table t=M2MTable.class.getAnnotation(Table.class) ;
        Class<?> z= lhs.getClass();        
        String tn=t.table().toUpperCase();
        t = rhs.getAnnotation(Table.class) ;        
        String rn= t.table().toUpperCase();
        t=z.getAnnotation(Table.class);        
        String ln= t.table().toUpperCase();
        
        String sql = "SELECT distinct res.* from " + rn + " res JOIN " + tn + " mm  ON " +
                "mm." + COL_LHS + "=? and " + "mm." + COL_RHS + "=? and " +
                "mm." + COL_LHSOID + "=? and " + "mm." + COL_RHSOID + " = res." + COL_ROWID ;
        
        return fetchViaSQL(rhs, sql, ln, rn, lhs.getRowID()) ;
	}
    
	/**
	 * @param lhs
	 * @param rhs
	 * @throws SQLException
	 * @throws IOException 
	 */
	public void removeM2M(StratumObj lhs, StratumObj rhs)  throws SQLException, IOException  {
        Table t=M2MTable.class.getAnnotation(Table.class) ;
        Class<?> z= lhs.getClass();        
        String tn=t.table().toUpperCase();
        t = rhs.getClass().getAnnotation(Table.class) ;        
        String rn= t.table().toUpperCase();
        t=z.getAnnotation(Table.class);        
        String ln= t.table().toUpperCase();
	    
		String sql ="DELETE from " + tn +
		        " where " + COL_RHS + "=? and " + COL_LHS + "=? and " +
		        COL_RHSOID + "=? and " + COL_LHSOID + "=?" ;
		
		execUpdateSQL(sql, rn, ln, rhs.getRowID(), lhs.getRowID()) ;
	}
		
	/**
	 * @param lhs
	 * @param rhs
	 * @throws SQLException
	 * @throws IOException 
	 */
	public void removeM2M(StratumObj lhs, Class<?> rhs)  throws SQLException, IOException  {
        Table t=M2MTable.class.getAnnotation(Table.class) ;
        Class<?> z= lhs.getClass();        
        String tn=t.table().toUpperCase();
        t = rhs.getAnnotation(Table.class) ;        
        String rn= t.table().toUpperCase();
        t=z.getAnnotation(Table.class);        
        String ln= t.table().toUpperCase();
        
        String sql ="DELETE from " + tn +
                " where " + COL_RHS + "=? and " + COL_LHS + "=? and " +
                COL_LHSOID + "=?";
        
        execUpdateSQL(sql, rn, ln, lhs.getRowID()) ;
	}
	
	public void addM2M(StratumObj lhs, StratumObj rhs)  throws SQLException, IOException  {
        Table t=M2MTable.class.getAnnotation(Table.class) ;
        Class<?> z= lhs.getClass();        
        String tn=t.table().toUpperCase();
        t = rhs.getClass().getAnnotation(Table.class) ;        
        String rn= t.table().toUpperCase();
        t=z.getAnnotation(Table.class);        
        String ln= t.table().toUpperCase();
        
        String sql ="INSERT into " + tn +
                " ( " + COL_VERID + ", " + COL_RHS + ", " + COL_LHS + ", " +
                COL_RHSOID + ", " + COL_LHSOID + ") values (?,?,?,?,?)" ;
        
        execUpdateSQL(sql, new Long(1L), rn, ln, rhs.getRowID(), lhs.getRowID()) ;
	}

	
    private Tuple getMeta( Connection con, Class<?> z) throws SQLException {
        ClassMetaHolder zm= _dbMeta.getClassMeta(z) ;
        TableMetaHolder tm= con==null ? null : getTableMeta(con, z);
        if (zm== null) { throw new SQLException("Failed to locate class info: " + z) ; }
        if (con != null && tm== null) { throw new SQLException("Failed to locate table info: " + z) ; }
        return new Tuple(zm,tm);
    }
    
}
