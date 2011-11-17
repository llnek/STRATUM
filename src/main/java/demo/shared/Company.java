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
import com.zotoh.stratum.anote.One2Many;
import com.zotoh.stratum.anote.One2One;
import com.zotoh.stratum.anote.Table;
import com.zotoh.stratum.core.DBIO;


@Table(table="TBL_COMPANY")
public class Company extends DemoObj {
	
    String _companyID;
    byte[] _logo;
    double _revenue;

    
    public Company()   {} 

    // o2o
    
    public String getAddressFKey() { return "FK_COMPANY"; }
    @One2One(rhs=Address.class)
    public Address getAddress(DBIO db) throws SQLException, IOException {
        return db.getO2O(this, Address.class, getAddressFKey()); 
    }
    public void setAddress(DBIO db, Address a) throws SQLException, IOException {
    		db.setO2O(this, a, getAddressFKey());
    }

    
    // col
    
    @Column(id="COMPANY_ID",size=255, unique=true)
    public String getCompanyName() {        return _companyID;    }
    public void setCompanyName(String n) {
        _companyID=n ;        
    }

    // col
    
    @Column(id="REVENUE")
    public double getRevenue() {        return _revenue;    }
    public void setRevenue(double d) {
        _revenue=d ;        
    }

    // col
    
    @Column(id="LOGO")
    public byte[] getLogo() {        return _logo;    }
    public void setLogo(byte[] b) {
        _logo=b ;
    }
    
    // o2m
    
    public String getEmployeesFKey() { return "FK_COMPANY"; }
	@One2Many(rhs=Employee.class)
    public List<Employee> getEmployees(DBIO db) throws SQLException, IOException {
    		return db.getO2M(this, Employee.class, getEmployeesFKey());
    }
    public void removeEmployee(DBIO db, Employee e) throws SQLException, IOException  {
    		db.removeO2M(this, e, getEmployeesFKey());
    }
    public void addEmployee(DBIO db, Employee e) throws SQLException, IOException  {
		db.addO2M(this, e, getEmployeesFKey());
    }
    
    // o2m
    
    public String getDeptsFKey() { return "FK_COMPANY"; }
	@One2Many(rhs=Department.class)
    public List<Department> getDepts(DBIO db) throws SQLException, IOException {
		return db.getO2M(this, Department.class, getDeptsFKey());
    }
    public void removeDept(DBIO db, Department d) throws SQLException, IOException  {
		db.removeO2M(this, d, getDeptsFKey() );
	}
	public void addDept(DBIO db, Department d) throws SQLException, IOException  {
		db.addO2M(this, d, getDeptsFKey() );
	}
    
    
    
	
	
	
	
	
}
