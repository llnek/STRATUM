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

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.zotoh.stratum.anote.Column;
import com.zotoh.stratum.anote.Many2Many;
import com.zotoh.stratum.anote.Table;
import com.zotoh.stratum.core.DBIO;


/**
 * @author kenl
 *
 */
@Table(table="TBL_DEPT")
public class Department extends DemoObj {
	
    String _dname;
    public Department() {}   
    
    
    // col

    @Column(id="DNAME",size=128,unique=true)
    public String getDeptID() {        return _dname;    }    
    public void setDeptID(String s) { _dname=s;    }
    
    // m2m
    
    public String getEmployeesFKey() { return "FK_DEPTS"; }
	@Many2Many(rhs=Employee.class)
    public List<Employee> getEmployees( DBIO db) throws SQLException, IOException {
    		return db.getM2M(this, Employee.class);
    }
    public void removeEmployee(DBIO db, Employee e) throws SQLException, IOException  {
		db.removeM2M(this, e);
    }
    public void removeEmployees(DBIO db) throws SQLException, IOException  {
		db.removeM2M(this, Employee.class);
    }
    public void addEmployee(DBIO db, Employee e) throws SQLException, IOException  {
    		db.addM2M(this, e);
    }
    
    

    
}
