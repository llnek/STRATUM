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
 
package demo.crud;

import java.text.SimpleDateFormat;
import java.util.List;

import com.zotoh.stratum.core.DBIO;
import com.zotoh.stratum.core.NameValues;

import demo.shared.Demo;
import demo.shared.Employee;

/**
 * @author kenl
 *
 */
public class BasicCRUDDemo extends Demo {
    
    /**
     * @param io
     */
    public BasicCRUDDemo(DBIO io)    {
        super(io);
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.stratum.samples.Demo#run()
     */
    @Override
    public void run() throws Exception     {
    	
        // create an employee- Joe Bloggs
        demo_basic_object_create();
        
        // read back the object- Joe Bloggs
        demo_fetch_object();

        // update the object- Joe Bloggs
        demo_update_object();
        
        // delete the object- Joe Bloggs
        demo_delete_object();

        // failed to read back the object- Jow Bloggs is gone
        demo_no_objects();
    }
    
    private void demo_basic_object_create() throws Exception     {
    	
        _db.startTransaction();
        
        Employee employee=  iniz_employee("Joe", "Blogg", "jblogg") ;        
        employee.setBDay(new SimpleDateFormat("yyyyMMdd").parse("19990601"));
        employee.setIQ(5);        
        employee.setSex( "male");
        
        _db.create(employee) ;
        
        
        _db.commit();        
        log("Created Employee: Joe Blogg. OK.");
    }

    private void demo_fetch_object() throws Exception    {
    	
        Employee emp= (Employee) _db.fetchObj(Employee.class, 
                new NameValues("LOGIN", "jblogg") );
        if (emp == null) {
            throw new Exception("Joe Blogg not in database") ;
        }
        log("Fetched Employee: Joe Blogg. OK.");        
        log("Joe Blogg's object-id is: " + emp.getRowID());        
    }
    
    private void demo_update_object() throws Exception    {
    	
        _db.startTransaction();
        
        Employee employee= (Employee) _db.fetchObj(Employee.class, 
                new NameValues("LOGIN", "jblogg") );
        
        employee.setIQ( 25);        
        _db.update(employee) ;
        
        _db.commit();
        log("Updated Employee: Joe Blogg. OK.");        
    }
    
    private void demo_delete_object() throws Exception    {
    	
        _db.startTransaction();
        
        Employee employee= (Employee) _db.fetchObj(Employee.class, 
                new NameValues("LOGIN", "jblogg") );        
        _db.remove(employee) ;
        
        _db.commit();
        log("Deleted Employee: Joe Blogg. OK.");        
    }
    
    protected void demo_no_objects() throws Exception    {
        List<Employee> rc= _db.fetch(Employee.class) ;
        if (rc.size()==0)
        log("Employee: Joe Blogg is no longer in the database. OK.");
        else
        log("Employee: database still has data. Not OK.");
    }

}
