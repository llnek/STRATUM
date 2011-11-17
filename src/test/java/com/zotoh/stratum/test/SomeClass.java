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
 

package com.zotoh.stratum.test;

import java.sql.Time;
import java.util.Date;

import com.zotoh.stratum.anote.Column;
import com.zotoh.stratum.anote.Table;
import com.zotoh.stratum.core.StratumBase;


/**
 * @author kenl
 *
 */
@Table(table="TABLE_SOMECLASS")
public class SomeClass extends StratumBase {
	
    java.sql.Timestamp a_timestamp;
    String a_string, a_pwd;
    boolean a_bool;
    int a_int;
    long rowid, a_long;
    float a_float;
    double a_double;
    Date a_date;
    Time a_time;
    byte[] a_blob;
    
    
    public SomeClass(String str)    {
        a_string=str;
    }
    
    public SomeClass()
    {}
    
    public long getRowID() { return rowid;}

    public void setRowID(long n) {
    	rowid=n;
    }

    @Column(id="A_STRING")
    public String getAString() {
        return a_string;
    }
    
    public void setAString(String s) {
        a_string=s;
    }
    
    @Column(id="A_PWD")
    public String getAPwd() {
        return a_pwd;
    }
    
    public void setAPwd(String s) {
        a_pwd=s;
    }
    
    @Column(id="A_BOOL")
    public boolean getABool() {
        return a_bool;
    }
    
    public void setABool(boolean b) {
        a_bool=b;
    }

    @Column(id="A_INT")
    public int getAInt() {
        return a_int;
    }
    
    public void setAInt(int n) {
        a_int=n;
    }
    
    @Column(id="A_LONG")
    public long getALong() {
        return a_long;
    }
    
    public void setALong(long n) {
        a_long=n;
    }
    
    @Column(id="A_FLOAT")
    public float getAFloat() {
        return a_float;
    }
    
    public void setAFloat(float f) {
        a_float=f;
    }
    
    @Column(id="A_DOUBLE")
    public double getADouble() {
        return a_double;
    }
    
    public void setADouble(double d) {
        a_double=d;
    }
    
    @Column(id="A_DATE")
    public Date getADate() {
        return a_date;
    }
    
    public void setADate(Date d) {
        a_date=d;
    }

    
    @Column(id="A_TIME")
    public Time getATime() {
        return a_time;
    }
    
    public void setATime(Time t) {
        a_time=t;
    }
    
    @Column(id="A_TIMESTAMP")
    public java.sql.Timestamp getATimestamp() {
        return a_timestamp;
    }
    
    public void setATimestamp(java.sql.Timestamp t) {
        a_timestamp=t;
    }
    
    @Column(id="A_BLOB")
    public byte[] getABlob() {
        return a_blob;
    }
    
    public void setABlob(byte[] b) {
        a_blob=b;
    }
    
    
}
