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

@Table(table="TBL_EMPLOYEE")
public class Employee extends Person {
	
    String _login, _code, _desc;
    byte[] _picture;
    float _salary;
    
    
    public Employee() {}

    
    // m2m

    public String getDeptsFKey() { return "FK_EMPS"; }
	@Many2Many(rhs=Department.class)
    public List<Department> getDepts( DBIO db)     throws SQLException, IOException {
    		return db.getM2M(this, Department.class);
    }
    public void removeDepts(DBIO db) throws SQLException, IOException  {
		db.removeM2M(this, Department.class) ;
    }
    public void removeDept(DBIO db, Department d) throws SQLException, IOException  {
		db.removeM2M(this, d) ;
    }
    public void addDept(DBIO db, Department d) throws SQLException, IOException  {
    		db.addM2M(this, d) ;
    }

    // col
    
    @Column(id="LOGIN",size=128,unique=true)
    public String getLogin() {        return _login;    }
    public void setLogin(String n) {
        _login=n;
    }
    
    // col
    
    @Column(id="DESCR")
    public String getDesc() {         return _desc;    }
    public void setDesc(String n) {
        _desc=n;
    }

    // col
    
    @Column(id="PASSCODE")
    public String getPwd() {        return _code;    }
    public void setPwd(String n) {
        _code=n;
    }

    // col
    
    @Column(id="PICTURE")
    public byte[] getPic() {        return _picture;    }
    public void setPic(byte[] b) {
        _picture=b;
    }
    
    // col
    @Column(id="SALARY")
    public float getSalary() {        return _salary;    }
    public void setSalary(float f) {
        _salary=f;
    }
        
    
    
}
