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
 

package demo.sql;

import java.util.ArrayList;
import java.util.List;

import com.zotoh.core.db.DBVendor;
import com.zotoh.core.db.DDLUte;
import com.zotoh.stratum.core.DBIO;
import com.zotoh.stratum.sql.GenSQLFile;

import demo.shared.Address;
import demo.shared.Company;
import demo.shared.Demo;
import demo.shared.Department;
import demo.shared.Employee;
import demo.shared.Person;


/**
 * @author kenl
 *
 */
public class BasicGenSQLDemo extends Demo {
    
    
    public BasicGenSQLDemo(DBIO io)     {
        super(io);
    }

    @SuppressWarnings("serial") 
	@Override
    protected void run() throws Exception     {
        List<Class<?>> css= new ArrayList<Class<?>> () {{  
            add(Person.class) ;
            add(Employee.class) ;
            add(Company.class) ;
            add(Department.class) ;
            add(Address.class) ;
        }};
                
        String ddl= GenSQLFile.genDDL(DBVendor.H2, 
                css.toArray(new Class<?>[0]));
//        log(ddl) ;
                
        DDLUte.loadDDL( _db.getInfo() , ddl) ;
    }

}
