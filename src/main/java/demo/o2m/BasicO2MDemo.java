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
 
package demo.o2m;

import java.util.List;

import com.zotoh.stratum.core.DBIO;
import com.zotoh.stratum.core.NameValues;

import demo.shared.Company;
import demo.shared.Demo;
import demo.shared.Employee;


public class BasicO2MDemo extends Demo {
	
    
    public BasicO2MDemo(DBIO io)     {
        super(io);
    }

    @Override
    protected void run() throws Exception     {
    	
        create_one_company();
        
        add_employees();
        
        verify_employees();
        
        demo_cascade_delete();
        
        demo_no_objects();
    }

    private void create_one_company() throws Exception     {
        _db.startTransaction();
        
        Company company= new Company() ;        
        byte[] bits= new byte[10000];
        company.setCompanyName("ACME Web 2.0 Inc.");
        company.setRevenue(99084534.00);
        company.setLogo(bits);
        _db.create(company) ;
        
        _db.commit();        
        log("Created Company: ACME Web 2.0 Inc. OK.");        
    }

    private void add_employees() throws Exception     {
        _db.startTransaction();
        
        
        Company company= fetch_company();
        Employee e1, e2, e3;
                
        // add 3 employees
        e1= iniz_employee("No1", "Coder", "no1");        
        e2= iniz_employee("No2", "Coder", "no2");
        e3= iniz_employee("No3", "Coder", "no3");

        _db.create(e1, e2, e3) ;

        company.addEmployee(_db, e1) ;
        company.addEmployee(_db, e2) ;        
        company.addEmployee(_db, e3) ;
        
        _db.commit();        
        log("Added 3 Employees to Company. OK.");        
    }

    private void verify_employees() throws Exception     {
        // get the company
        Company company= fetch_company();
        List<Employee> employees= company.getEmployees(_db) ;

        // walk through those 3 employees
        
        Employee e1= employees.get(0);
        Employee e2= employees.get(1);
        Employee e3= employees.get(2);
        
        log("Company has employee: " + e3.getFirst() + ". OK.");
        log("Company has employee: " + e2.getFirst() + ". OK.");
        log("Company has employee: " + e1.getFirst() + ". OK.");
    }

    private void demo_cascade_delete() throws Exception     {
        _db.startTransaction();
                
        Company company = fetch_company();        
        Object o= company.getLogo();
        byte[] bits= (byte[]) o;
        
        log("Company Logo size = " + bits.length + ". OK.");
        
        _db.purgeO2M(company, Employee.class, company.getEmployeesFKey() ) ;
        _db.remove(company) ;
        
        
        _db.commit();
        log("Deleted company and its employees. OK.");
    }
    
    private Company fetch_company() throws Exception     {
        return (Company) 
        _db.fetchObj(Company.class, 
                new NameValues("COMPANY_ID", "ACME Web 2.0 Inc."));
    }
    
    
    
    
}
