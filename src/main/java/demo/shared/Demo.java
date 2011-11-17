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
 
package demo.shared;

import java.util.Date;

import com.zotoh.core.util.Logger;
import com.zotoh.core.util.LoggerFactory;
import com.zotoh.stratum.core.DBIO;
import com.zotoh.stratum.core.NameValues;

/**
 * @author kenl
 *
 */
public abstract class Demo {
	
    protected Logger _log= LoggerFactory.getLogger(Demo.class);    
    protected DBIO _db;
    
   
    protected Demo(DBIO io)    {
        _db= io;
    }
    
    protected void log(String msg)    {
//        if (false) _log.info(msg);
        System.out.println(msg);
    }

    
    public void start() throws Exception     {
        log("\n");
        log("==============================================================");
        log("Start Demo Run: " + getClass().getName());
        run();
        log("==============================================================");
        log("\n");
    }
    
    protected abstract void run() throws Exception;
    

    protected Object fetch_employee(String login) throws Exception     {        
        return _db.fetchObj(Employee.class, 
                new NameValues("LOGIN", login) ) ;
    }
    
    
    protected Employee iniz_employee(  String fname, String lname, String login) throws Exception     {
        Employee employee= new Employee();
        
        employee.setBDay(new Date()) ;
        employee.setLogin(login) ;
        
        employee.setFirst(fname) ;
        employee.setLast(lname) ;
        
        employee.setSalary( (float) 100.0) ;
        employee.setIQ(21) ;
        employee.setPwd("secret") ;
        employee.setSex("male") ;
        
        return employee;
    }

    protected void demo_no_objects() throws Exception     {
        int c;
        
        c= _db.countRows( Company.class );
        log("Company count = " + c);

        c= _db.countRows( Department.class );        
        log("Department count = " + c);
        
        c= _db.countRows( Employee.class );
        log("Employee count = " + c);
        
        c= _db.countRows( Person.class );
        log("Person count = " + c);
        
    }
    

}
