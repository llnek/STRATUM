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
import java.util.Date;

import com.zotoh.stratum.anote.Column;
import com.zotoh.stratum.anote.One2One;
import com.zotoh.stratum.anote.Table;
import com.zotoh.stratum.core.DBIO;


@Table(table="TBL_PERSON")
public class Person extends DemoObj  {
	
    String _first, _last, _sex;
    int _IQ;
    Date _bday;

    
    public Person()     {}

    
    
    // col
    
    @Column(id="FIRST_NAME",optional=false)
    public String getFirst() {          return _first;    }
    public void setFirst(String n) {
        _first=n;
    }
    
    // col
    
    @Column(id="LAST_NAME",optional=false)
    public String getLast() {         return _last;    }
    public void setLast(String n) {
        _last=n;
    }

    // col
    
    @Column(id="IQ")
    public int getIQ() {        return _IQ;    }
    public void setIQ(int n) {
        _IQ=n;
    }
    
    // col
    
    @Column(id="BDAY",optional=false)
    public Date getBDay() {        return _bday;    }    
    public void setBDay(Date d) {
        _bday=d;
    }

    // col
    
    @Column(id="SEX",optional=false,size=8)
    public String getSex() {        return _sex;    }
    public void setSex(String x) {
        _sex=x;
    }
    
    
    // o2o
    
    public String getSpouseFKey() { return "FK_SPOUSE"; }    
    @One2One(rhs=Person.class)    
    public <T> T getSpouse(DBIO db, Class<T> rhs) throws SQLException, IOException {        
    		return db.getO2O(this, rhs, getSpouseFKey());
    }    
    public void setSpouse(DBIO db, Person p) throws SQLException, IOException  {
    		db.setO2O(this, p, getSpouseFKey());
    }
    
    
    
    
    
}
