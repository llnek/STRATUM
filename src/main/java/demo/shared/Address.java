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

import com.zotoh.stratum.anote.Column;
import com.zotoh.stratum.anote.Table;


@Table(table="TBL_ADDRESS")
public class Address extends DemoObj {
	
    String _addr1,     _addr2,    _city ,
    _state ,     _zip,     _country;
        
    
    public Address( ) {}
    
    // col
    
    @Column(id="ADDR1")
    public String getAddr1() {        return _addr1;    }
    public void setAddr1(String s) {
        _addr1=s;
    }
    
    // col
    
    @Column(id="ADDR2")
    public String getAddr2() {        return _addr2;    }
    public void setAddr2(String s) {
        _addr2=s;
    }

    // col
    
    @Column(id="CITY",size=128)
    public String getCity() {        return _city;    }
    public void setCity(String s) {
        _city=s;
    }

    // col
    
    @Column(id="STATE",size=128)
    public String getState() {        return _state;    }
    public void setState(String s) {
        _state=s;
    }

    // col
    
    @Column(id="ZIP",size=64)
    public String getZip() {        return _zip;    }
    public void setZip(String s) {
        _zip=s;
    }


    // col
    
    @Column(id="COUNTRY",size=128)
    public String getCountry() {        return _country;    }
    public void setCountry(String s) {
        _country=s;
    }
    
    
    
    
    
    
    
    
    
}
