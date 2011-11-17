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


import static com.zotoh.core.util.CoreUte.genTmpDir;
import static com.zotoh.core.util.CoreUte.niceFPath;
import static com.zotoh.core.util.LoggerFactory.getLogger;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zotoh.core.db.DBVendor;
import com.zotoh.core.db.DDLUte;
import com.zotoh.core.db.JDBCInfo;
import com.zotoh.core.util.Logger;
import com.zotoh.stratum.core.ClassMetaHolder;
import com.zotoh.stratum.core.DBIO;
import com.zotoh.stratum.core.NameValues;
import com.zotoh.stratum.core.Stratum;
import com.zotoh.stratum.sql.DBDriver;

import demo.shared.Address;
import demo.shared.Company;
import demo.shared.Department;
import demo.shared.Employee;
import demo.shared.Person;

/**
 * @author kenl
 *
 */
public final class JUTSample {
	
    protected final Logger s_log= getLogger(JUTSample.class);

    private static Stratum _app;
    private DBIO _db;
    private static File _dbPath;
    
    
    /**/
    public static junit.framework.Test suite()    {
        return new JUnit4TestAdapter(JUTSample.class);
    }

    /**/
    @BeforeClass
    public static void iniz() throws Exception     {
        System.out.println("@BeforeClass iniz()");        
        try         {
            __iniz();
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static void __iniz() throws Exception     {
        __finz();

        File path = genTmpDir();
        _dbPath=path;
        String rpath = niceFPath(path) ;
        String u= "sa" , p= "" ;
        String url;
        String drvr;
        DBVendor v;
        
        drvr= "org.h2.Driver";
        url = "jdbc:h2:" + rpath;
        v= DBVendor.H2;
        
        drvr= "org.hsqldb.jdbc.JDBCDriver";
        url = "jdbc:hsqldb:file:" + rpath;
        v= DBVendor.HSQLDB;
        
        JDBCInfo props= new JDBCInfo(drvr, url, u, p) ;        
        try         {
            DBDriver d= DBDriver.newDriver( v) ;
            String ddl= d.getDDL(Person.class, Employee.class, 
                    Address.class, Company.class, Department.class) ;
            DDLUte.loadDDL(props, ddl) ;
            
            _app= new Stratum( props);
        }
        finally {
        }        
        
    }
    
    @AfterClass
    public static void finz()     {
        System.out.println("@AfterClass finz()");
        __finz();
    }

    private static void __finz()    {
    	if (_app != null) { _app.finz(); }
    	if ( _dbPath != null) { _dbPath.delete(); }
    }
    
    @Before
    public void open() throws Exception     {
        _db= _app.openDB();
    }

    @After
    public void close() throws Exception     {
    	_db.close();
    }
    
    //======= test cases begin

    @Test
    public void crudOneEmployee() throws Exception     {
        Employee emp= new Employee();
        
        _db.startTransaction();
        
        inizOneEmp(emp, "Joe", "Blogg", "joeb");
        _db.create(emp) ;
        _db.commit();
        
        emp= _db.fetchObj(Employee.class, new NameValues( "LOGIN", "joeb"));
        assertTrue("secret".equals( emp.getPwd())) ;
        
        _db.startTransaction();
        assertFalse( emp.getIQ() == 43) ;
        emp.setIQ(43);
        _db.update(emp) ;
        _db.commit();

        emp= _db.fetchObj(Employee.class, new NameValues( "LOGIN", "joeb"));
        assertTrue( emp.getIQ() == 43) ;
        
        _db.startTransaction();
        _db.remove(emp) ;
        _db.commit();
        
        emp= _db.fetchObj(Employee.class, new NameValues( "LOGIN", "joeb"));
        assertTrue(emp == null);
    }

    @Test
    public void tstM2M() throws Exception     {
    }

    @Test
    public void tstO2OEx() throws Exception     {
        Company c = new Company( );
        
        c.setCompanyName("msft");
        
        Address a= new Address();
        a.setAddr1( "1 Windows Drive");
        a.setCity( "Redmond");
        a.setState( "WA");
        a.setZip( "88888");
        a.setCountry( "USA");
        
        _db.startTransaction();
        _db.create(c) ;
        _db.create(a);        
        c.setAddress(_db, a) ;
        _db.commit();
        
        c = _db.fetchObj(Company.class, 
                new NameValues("COMPANY_ID", "msft")) ;
        assertTrue(c != null);
        a= c.getAddress(_db) ;
        assertTrue(a != null);
        assertTrue( "88888".equals(a.getZip())) ;
        
        _db.startTransaction();        
        _db.purgeO2O(c, Address.class, c.getAddressFKey() ); 
        
        _db.remove(c);
        _db.commit();
    }

    @Test
    public void tstO2O() throws Exception     {
        Employee emp= new Employee();
        inizOneEmp(emp, "Santa", "Claus", "sc");
        
        Person p= new Person();

        p.setFirst("Mary");
        p.setLast( "Fairy");
        p.setBDay( new Date());
        p.setIQ( 91);
        p.setSex( "female");

        _db.startTransaction();        
        _db.create(emp);
        _db.create(p);
        
        emp.setSpouse(_db, p) ;
        p.setSpouse(_db, emp) ;
        
        _db.commit();
        
        emp= _db.fetchObj(Employee.class, new NameValues("LOGIN", "sc")) ;
        p = emp.getSpouse(_db, Person.class) ;
        assertTrue(p != null) ;
        Object o1= p.getSpouse(_db, Employee.class);
        assertTrue( o1 instanceof Employee);

        _db.startTransaction();
        _db.remove(emp);
        _db.remove(p);        
        _db.commit();
        
        emp= _db.fetchObj(Employee.class, new NameValues("LOGIN", "sc")) ;
        assertTrue(emp == null);
        
        assertTrue( 0 == _db.countRows(Person.class) );
    }

    @Test
    public void tstO2M_joined() throws Exception     {
        Company c = new Company(); c.setCompanyName( "msft");
        Department d1,d2,d3;
                        
        _db.startTransaction();        
        _db.create(c);
        
        d1= new Department(); d1.setDeptID("Finance");
        d2= new Department(); d2.setDeptID("Sales");
        d3= new Department(); d3.setDeptID("Marketing");        
        _db.create(d1,d2,d3);
        
        c.addDept( _db, d1) ;
        c.addDept( _db, d2) ;
        c.addDept( _db, d3) ;
        
        _db.commit();
        
        c= _db.fetchObj(Company.class, new NameValues("COMPANY_ID", "msft")) ;
        assertTrue(c.getDepts(_db).size() ==3);
        
        _db.startTransaction();
        
        _db.purgeO2M(c, Department.class, c.getDeptsFKey() ) ;
        _db.remove(c);
        _db.commit();
        
        c= _db.fetchObj(Company.class, new NameValues("COMPANY_ID", "msft")) ;
        assertTrue(c==null);
        
        assertTrue( 0 == _db.countRows(Department.class) );        
    }
    
    @Test
    public void tstO2M() throws Exception    {
        Company c= new Company();
        c.setCompanyName("msft") ;
        
        Employee e1,e2,e3;
        
        e1= new Employee();
        inizOneEmp(e1, "No1", "Coder", "no1");
        e2= new Employee();
        inizOneEmp(e2, "No2", "Coder", "no2");
        e3= new Employee();
        inizOneEmp(e3, "No3", "Coder", "no3");

        _db.startTransaction();
        
        _db.create(c);
        _db.create(e1);
        _db.create(e2);
        _db.create(e3);
        
        c.addEmployee(_db, e1) ;
        c.addEmployee( _db, e2) ;
        c.addEmployee( _db, e3) ;
        
        _db.commit();
        
        c= _db.fetchObj(Company.class, new NameValues("COMPANY_ID", "msft")) ;
        assertTrue( c.getEmployees(_db ).size() == 3);
        
        _db.startTransaction();
        _db.purgeO2M(c, Employee.class, c.getEmployeesFKey() );
        _db.remove(c);
        _db.commit();
        
        c= _db.fetchObj(Company.class, new NameValues("COMPANY_ID", "msft")) ;
        assertTrue(c==null);
        
        assertTrue( 0 == _db.countRows(Employee.class) );        
    }
    
    @Test
    public void tstListIDs() throws Exception     {
        Company c = new Company(); c.setCompanyName("msft") ;
        Employee e1, e2, e3;
        
        _db.startTransaction();
        _db.create(c);
        
        e1= new Employee();
        inizOneEmp(e1, "No1", "Coder", "no1");        
        e2= new Employee();
        inizOneEmp(e2, "No2", "Coder", "no2");        
        e3= new Employee();
        inizOneEmp(e3, "No3", "Coder", "no3");
        _db.create(e1,e2,e3);
       
        
        c.addEmployee(_db, e1) ;
        c.addEmployee(_db, e2) ;
        c.addEmployee(_db, e3) ;
        
        _db.commit();
                
        ClassMetaHolder zm= _db.getClassMeta(Employee.class) ;        
        List<Employee> lst= _db.fetchViaSQL(Employee.class, 
                "select * from " + zm.getTable() + " where " + c.getEmployeesFKey() + "= ?",
                c.getRowID() );
        assertTrue(lst.size()==3) ;
        
        _db.startTransaction();
        c=  _db.fetchObj(Company.class, new NameValues("COMPANY_ID", "msft")) ;
        _db.purgeO2M(c, Employee.class, c.getEmployeesFKey() );
        _db.remove(c);
        _db.commit();
        
        c=  _db.fetchObj(Company.class, new NameValues("COMPANY_ID", "msft")) ;
        assertTrue(c==null);
        
        assertTrue( 0 == _db.countRows(Employee.class) );        
    }

    @Test
    public void tstUpdate() throws Exception     {
        Employee emp= new Employee();
        inizOneEmp(emp, "No1", "Coder", "no1");
        
        _db.startTransaction();
        _db.create(emp);
        _db.commit();
        
        _db.startTransaction();
        emp.setBDay( new GregorianCalendar(2000,1,1,1,1).getTime());
        emp.setIQ( 100);
        emp.setSex( "female");        
        emp.setPwd("hello");
        emp.setSalary( (float)900.0);
        
        emp.setPic( "a simple picture".getBytes());     
        emp.setDesc( "a short description");  
        
        _db.update(emp);
        _db.commit();
        
        emp = _db.fetchObj(Employee.class, new NameValues("LOGIN", "no1")) ;
        Object s= emp.getPic();
        byte[] b= (byte[]) s;
        String str= new String(b);
        
        assertTrue("a simple picture".equals(str));
    }
    

    //======= test cases end
    
    //---- impl stuff
    
    private void inizOneEmp(Employee emp, String fn, String ln, String login) throws Exception     {
        emp.setFirst(fn);
        emp.setLast(ln);
        emp.setBDay( new Date());
        emp.setIQ(21);
        emp.setSex("male");        
        emp.setLogin(login);
        emp.setPwd("secret");
        emp.setSalary( (float)100.0);     
    }
        
    
    
}

