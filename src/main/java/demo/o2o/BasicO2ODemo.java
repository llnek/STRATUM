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
 
package demo.o2o;

import java.text.SimpleDateFormat;
import java.util.List;

import com.zotoh.stratum.anote.Table;
import com.zotoh.stratum.core.DBIO;

import demo.shared.Demo;
import demo.shared.Employee;
import demo.shared.Person;

/**
 * @author kenl
 *
 */
public class BasicO2ODemo extends Demo {
	
    
    public BasicO2ODemo(DBIO io)     {
        super(io);
    }

    @Override
    protected void run() throws Exception     {
    	
        create_joe_bloggs_wife();
        
        create_joe_blogg();
        
        demo_attach_joe_to_wife();
        
        demo_verify_wedlock();
        
        demo_quick_delete();
        
        demo_no_objects();
    }

    
    private void create_joe_bloggs_wife() throws Exception     {
    	
        _db.startTransaction();
        
               
        Person person= new Person();                        
        person.setBDay( new SimpleDateFormat("yyyyMMdd").parse("19701220"));
        person.setFirst("Marian");
        person.setLast( "Jones");
        person.setIQ(250);        
        person.setSex( "female");
        _db.create(person);
        
        
        
        _db.commit();        
        log("Created Person: Marian Jones. OK.");
    }
    
    private void create_joe_blogg() throws Exception     {
    	
        _db.startTransaction();
        
        Employee employee= iniz_employee("Joe", "Blogg", "jblogg") ;               
        employee.setBDay(new SimpleDateFormat("yyyyMMdd").parse("19650202"));
        employee.setIQ(290);
        employee.setSex( "male");        
        _db.create(employee);
        
        
        
        
        _db.commit();        
        log("Created Employee: Joe Blogg. OK.");
    }

    private void demo_attach_joe_to_wife() throws Exception     {
        _db.startTransaction();
        
        Employee joe= (Employee) fetch_person_object( Employee.class, "Joe", "Blogg");        
        Person marian= fetch_person_object(Person.class, "Marian", "Jones");
        
        joe.setSpouse(_db, marian) ;
        marian.setSpouse(_db, joe) ;
        
        _db.update(marian) ;
        _db.update(joe) ;
        
        _db.commit();        
        log("Joe & Marian are now married. OK.");
    }

    private void demo_verify_wedlock() throws Exception     {
        Employee joe= (Employee) fetch_person_object( Employee.class, "Joe", "Blogg");
        Person marian= fetch_person_object( Person.class, "Marian", "Jones");
        
        // re-examine the spouse object for both Joe & Marian
        String s1, s2;
        Employee j2 = marian.getSpouse(_db, Employee.class) ;
        Person p2= joe.getSpouse(_db, Person.class) ;
        s1=j2.getFirst();
        s2=p2.getFirst();
        
        log("Marian's spouse is: " + s1);        
        log("Joe's spouse is: " + s2);
        
        if (s1.equals("Joe") && s2.equals("Marian")) {
            log("Joe & Marian are married ? TRUE.");
        }
        else {
            log("Joe & Marian are married ? FALSE.");            
        }
    }

    private void demo_quick_delete() throws Exception     {
        _db.startTransaction();
        
        Employee joe= (Employee) fetch_person_object( Employee.class, "Joe", "Blogg");
        Person marian= fetch_person_object( Person.class, "Marian", "Jones");

        _db.remove(marian) ;
        _db.remove(joe) ;
        
        _db.commit();        
        log("Both Joe & Marian are removed. OK.");
    }
    
    private <T> Person fetch_person_object
        (Class<T> typeId, String fname, String lname) throws Exception
    {
        Table t= typeId.getAnnotation(Table.class) ;        
        String sql= "select * from " + t.table() + " where FIRST_NAME=? AND LAST_NAME=?";
        List<T> rc= _db.fetchViaSQL(typeId, sql, fname,lname ) ;
        
        return rc.size() ==0 ? null : (Person) rc.get(0);
        
    }

}
