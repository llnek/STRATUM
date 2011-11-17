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
 
package demo.m2m;

import java.util.List;

import com.zotoh.stratum.core.DBIO;
import com.zotoh.stratum.core.NameValues;

import demo.shared.Company;
import demo.shared.Demo;
import demo.shared.Department;
import demo.shared.Employee;
import demo.shared.Person;

/**
 * @author kenl
 *
 */
public class BasicM2MDemo extends Demo {
    
    /**
     * @param io
     */
    public BasicM2MDemo(DBIO io)     {
        super(io);
    }

    /* (non-Javadoc)
     * @see com.zotoh.stratum.samples.Demo#run()
     */
    @Override
    protected void run() throws Exception     {
    	
        create_company();
        
        add_depts();
        
        add_employees();
        
        bind_dept_employees();
        
        bind_employee_depts();
        
        verify_m2m();
        
        cleanup();
        
        demo_no_objects();
    }

    private void create_company() throws Exception     {
        _db.startTransaction();
        
        Company company= new Company();
        company.setCompanyName("ACME Web 2.0 Inc.") ;        
        _db.create(company) ;
        
        
        _db.commit();        
        log("Created company: " + company.getCompanyName() + " OK.");
    }
    
    
    private void add_depts() throws Exception     {
        _db.startTransaction();
        
        Company company= fetch_company();        
        Department d1,d2,d3;
        
        d1= new Department(); d1.setDeptID("Finance");
        d2= new Department(); d2.setDeptID("Sales");
        d3= new Department(); d3.setDeptID("Marketing");        
        _db.create(d1,d2,d3);
        
        company.addDept(_db, d1) ;
        company.addDept(_db, d2) ;
        company.addDept(_db, d3) ;
        _db.update(d1,d2,d3);
        
        _db.commit();        
        log("Added 3 departments to Company. OK.");
    }
    
    
    private void add_employees() throws Exception     {
        _db.startTransaction();
        
        Company company= fetch_company();
        Employee e1,e2,e3;
        
        e1= iniz_employee( "No1", "Coder", "no1");        
        e2= iniz_employee( "No2", "Coder", "no2");        
        e3= iniz_employee("No3", "Coder", "no3");
        _db.create(e1,e2,e3);
                
        company.addEmployee(_db, e1);
        company.addEmployee(_db, e2);
        company.addEmployee(_db, e3);
        
        _db.update(e1,e2,e3);
        
        _db.commit();        
        log("Added 3 employees to Company. OK.");
    }
    
    
    private void bind_dept_employees() throws Exception     {
        _db.startTransaction();
        
        Department dept= fetch_dept("Finance");        
        Employee e1,e2,e3;
        
        // get the many2many association to link to employees
        
        e1= fetch_emp("no1");
        e2= fetch_emp("no2");
        e3= fetch_emp("no3");
        
        dept.addEmployee(_db, e1) ;
        dept.addEmployee(_db, e2) ;
        dept.addEmployee(_db, e3) ;
        
        _db.commit();        
        log("Finance department now has 3 members. OK.");
    }

    private void bind_employee_depts() throws Exception     {
        _db.startTransaction();
        
        
        // get the many2many association and bind to some departments
        Employee emp= fetch_emp("no3") ;
        Department d1,d2,d3;        
        
        d1= fetch_dept("Marketing");
        d2= fetch_dept("Sales");
        d3= fetch_dept("Finance");

        emp.addDept(_db, d1);
        emp.addDept(_db, d2);
        emp.addDept(_db, d3);
        
        
        _db.commit();        
        log("Employee No3 now belongs to 3 departments. OK.");
    }

    
    private void verify_m2m() throws Exception     {
//        Company company= fetch_company();
        Employee e3;
        Department dept = fetch_dept("Finance") ;        
        List<Employee> emps= dept.getEmployees(_db);
        
        if (emps.size()==3)        { 
            log("Finance department indeed has 3 members. OK."); 
        }
        
        e3=null;
        for (int i=0; i < emps.size(); ++i) {
            if ( "no3".equals( emps.get(i).getLogin())) {
                e3= emps.get(i);
                break;
            }
        }
        
        List<Department> depts= e3.getDepts(_db) ;
        if (depts.size()==3)        { 
            log("No3 Coder indeed belongs to 3 departments. OK.");               
        }

    }

    private Employee fetch_emp(String login) throws Exception     {
        return (Employee) 
        _db.fetchObj(Employee.class, new NameValues("LOGIN", login)) ;
    }

    private Department fetch_dept(String name) throws Exception     {
        return (Department) 
        _db.fetchObj(Department.class, new NameValues("DNAME", name)) ;
    }

    private void cleanup() throws Exception     {
        _db.startTransaction();
        
        Company company= fetch_company();        

        List<Department> depts= company.getDepts(_db) ;
        Department d;
        for (int i=0; i < depts.size(); ++i) {
            d= depts.get(i);
            d.removeEmployees(_db);
        }
        
        List<Employee> emps= company.getEmployees(_db) ;
        Employee emp;
        for (int i=0; i < emps.size(); ++i) {
            emp= emps.get(i);
            emp.removeDepts(_db) ;
        }

        _db.purgeO2M(company, Department.class, company.getDeptsFKey() ); 
        _db.purgeO2M(company, Employee.class, company.getEmployeesFKey() ); 
        
        _db.removeAll(Person.class);
        _db.remove(company) ;
        
        _db.commit();
    }

    private Company fetch_company() throws Exception     {
        return (Company) 
        _db.fetchObj(Company.class, new NameValues("COMPANY_ID", "ACME Web 2.0 Inc."));
    }
    
    
    
    
    
}
