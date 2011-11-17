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
 
package com.zotoh.stratum.test;

import static com.zotoh.core.util.LoggerFactory.getLogger;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zotoh.core.db.DBVendor;
import com.zotoh.core.db.DDLUte;
import com.zotoh.core.db.JDBCInfo;
import com.zotoh.core.util.CoreUte;
import com.zotoh.core.util.Logger;
import com.zotoh.stratum.core.ClassMetaHolder;
import com.zotoh.stratum.core.DBIO;
import com.zotoh.stratum.core.Stratum;
import com.zotoh.stratum.sql.DBDriver;

public class JUTDB  {
	
    private static final Logger s_log= getLogger(JUTDB.class);
    
    private static ClassMetaHolder s_meta;
    private static Stratum _app;
    private static DBIO s_db;
    private static File _dbPath;
    
    public static junit.framework.Test suite()     {
        return new JUnit4TestAdapter(JUTDB.class);
    }

    @BeforeClass
    public static void iniz()     throws Exception {
        
        s_log.info("@BeforeClass iniz()");        
        try         {
            __iniz();
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    private static JDBCInfo setHSQLDB(String user, String pwd, String rpath) 
    throws Exception  {
        return new JDBCInfo(  
                "org.hsqldb.jdbcDriver" ,  
                "jdbc:hsqldb:file:" + rpath ,  
                user, pwd  ) ;
    }

    private static JDBCInfo setH2(String user, String pwd, String rpath) 
    throws Exception  {
        return new JDBCInfo(  
                "org.h2.Driver" ,  
                "jdbc:h2:"+rpath ,  
                user, pwd  ) ;
    }
    
    @SuppressWarnings("unused")
    private static JDBCInfo setMySQL(String user, String pwd) 
    throws Exception    {
        
        return new JDBCInfo (
                "com.mysql.jdbc.Driver", 
                "jdbc:mysql://localhost:3306/dbf",
                user,  pwd );
    }

    @SuppressWarnings("unused")
    private static JDBCInfo setPostgreSQL(
            String host, int port,
            String user, String pwd) 
    throws Exception   {
        
        return new JDBCInfo (
                "org.postgresql.Driver" , 
                "jdbc:postgresql://localhost/dbf", 
                user, pwd );
        
    }

    @SuppressWarnings("unused")
    private static JDBCInfo setMSSQL(String user, String pwd) throws Exception     {
        StringBuilder b= new StringBuilder(256)
        .append("jdbc:sqlserver://localhost:1433;databaseName=dbf;user=")        
        .append(user)
        .append(";password=")
        .append(pwd) ;
        
        return new JDBCInfo        (
            "com.microsoft.sqlserver.jdbc.SQLServerDriver", b.toString(),
            user, pwd
        );
    }

    @SuppressWarnings("unused")
    private static JDBCInfo setOracle(String user, String pwd) throws Exception     {
        StringBuilder b= new StringBuilder(256)
        .append("jdbc:oracle:thin:").append(user)
        .append("/")
        .append(pwd)
        .append("@//localhost:1521/XE");
        
        return new JDBCInfo        (
            "oracle.jdbc.driver.OracleDriver", b.toString(),
            user, pwd
        );
    }

    private static void __iniz() throws Exception    {
        __finz();
        
        String user= "sa";
        String pwd="";
        JDBCInfo props;
        DBVendor v;
        
        _dbPath = CoreUte.genTmpDir();
        
        props= setH2(user, pwd, CoreUte.niceFPath(_dbPath));
        v= DBVendor.H2;
        
        s_meta= new ClassMetaHolder().scan(SomeClass.class);
        _app= new Stratum(props);
        try         {
        	
            DDLUte.loadDDL(props, 
                    DBDriver.newDriver(v).getDDL(SomeClass.class) );
            
            newObj();
        }
        finally {
        }        
        
    }
    
    @AfterClass
    public static void finz()    {
        System.out.println("@AfterClass finz()");
        __finz();
    }

    private static void __finz()    {
    	if (_app != null) { _app.finz(); }
    }
    
    @Before
    public void open() throws Exception    {
        s_db= _app.openDB();
    }

    @After
    public void close() throws Exception    {
        s_db.close();
    }
    
    //======= test cases begin

    private SomeClass getDBObj() throws Exception    {
        String tbl= s_meta.getTable();
        return s_db.fetchViaSQL(SomeClass.class, 
                "select * from " + tbl + " where A_STRING=?", "z") .get(0);
    }
        
    @Test
    public void tstBoolean() throws Exception    {
    	
        SomeClass a= getDBObj();
        
        s_db.startTransaction();
        
        a.setABool(Boolean.TRUE);
        s_db.update(a) ;        
        s_db.commit();
                        
        assertTrue( getDBObj().getABool() );        
    }

    @Test
    public void tstString() throws Exception    {
        SomeClass a= getDBObj();
        
        s_db.startTransaction();
        a.setAPwd( "x");
        s_db.update(a);
        s_db.commit();
        
        a= getDBObj();
        
        assertTrue("x".equals(  a.getAPwd() ));
    }

    @Test
    public void tstTimestamp() throws Exception    {
    	
        Timestamp ts= new Timestamp( new Date().getTime() );
        SomeClass a= getDBObj();

        s_db.startTransaction();
        a.setATimestamp(ts);
        s_db.update(a);
        s_db.commit();
        
        a= getDBObj();
        assertTrue(ts.getTime() == a.getATimestamp().getTime());        
    }

    @SuppressWarnings("deprecation")
	@Test
    public void tstDate() throws Exception    {
        SomeClass a= getDBObj();
        Date now= new Date();
        
        s_db.startTransaction();
        a.setADate( now);
        s_db.update(a);
        s_db.commit();
        
        a= getDBObj();

        assertTrue( now.getYear() == a.getADate().getYear() );
        assertTrue( now.getMonth() == a.getADate().getMonth() );
        assertTrue( now.getDay() == a.getADate().getDay() );
    }

    @Test
    public void tstBigString() throws Exception    {
    }

    @Test
    public void tstOID() throws Exception    {
    }

    @Test
    public void tstPWD() throws Exception    {
    }

    @Test
    public void tstDouble() throws Exception    {
        double dd= Double.MAX_VALUE;
        SomeClass a= getDBObj();
        boolean oracle=false;
        boolean derby=false;
        
        if ( oracle ) {
            // seems that this is the largest double oracle can store ? 
            dd= Double.valueOf("1e125");
        }
        if ( derby) {
            // seems that this is the largest double derby can store ? 
            dd= Double.valueOf("1.79769E+308");
        }

        s_db.startTransaction();
        a.setADouble( dd);
        s_db.update(a);
        s_db.commit();
        
        a= getDBObj();
        assertTrue( dd == a.getADouble());
    }

    @Test
    public void tstFloat() throws Exception    {
        float ff= Float.MAX_VALUE;
        SomeClass a= getDBObj();

        s_db.startTransaction();
        a.setAFloat(ff);
        s_db.update(a);
        s_db.commit();
        
        a= getDBObj();
        assertTrue( ff == a.getAFloat());
    }

    @Test
    public void tstLong() throws Exception    {
        long ll= Long.MAX_VALUE;
        SomeClass a= getDBObj();
        
        s_db.startTransaction();        
        a.setALong( ll);
        s_db.update(a);
        s_db.commit();
        
        a= getDBObj();
        assertTrue( ll == a.getALong());
    }

    @Test
    public void tstInt() throws Exception    {
        int ii= Integer.MAX_VALUE;
        SomeClass a= getDBObj();

        s_db.startTransaction();
        a.setAInt(ii);
        s_db.update(a);
        s_db.commit();
        
        a= getDBObj();
        assertTrue(ii == a.getAInt());
    }

    @Test
    public void tstStreamAsBytes() throws Exception    {
    }

    @Test
    public void tstStream() throws Exception    {
    }

    @Test
    public void tstBlobAsStream() throws Exception    {
    }

    @Test
    public void tstBlob() throws Exception    {
        byte[] bits= new byte[4096];
        SomeClass a= getDBObj();
        
        bits[3333]= (byte) 7;
        a.setABlob(bits);
        s_db.startTransaction();
        s_db.update(a);
        s_db.commit();
        
        a= getDBObj();
        assertTrue( a.getABlob()[3333] == bits[3333] );
    }

    private static void newObj() throws Exception {
    	s_db= _app.openDB();
    	s_db.startTransaction();
    	s_db.create( new SomeClass("z"));
    	s_db.commit();
    }
    
}

