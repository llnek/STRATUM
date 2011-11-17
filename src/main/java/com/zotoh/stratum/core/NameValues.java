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


package com.zotoh.stratum.core;

import static com.zotoh.core.db.JDBCUte.isNil;
import static com.zotoh.core.util.LangUte.MP;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.zotoh.core.util.Null;

/**
 * @author kenl
 *
 */
public class NameValues {
	
    private Properties _ps= new Properties();
    private Map<String,String> _keys= MP();
    
    /**
     * @param name
     * @param value
     */
    public NameValues(String name, Object value)     {
        put(name, value);
    }
    
    /**
     * 
     */
    public NameValues()
    {}
    
    /**
     * @return
     */
    public Map<String,Object> entrySet() {
        Map<String, Object> rc= MP();
        String nm;
        Object val;
        for (Map.Entry<String, String> en : _keys.entrySet()) {
        	nm= en.getValue();
        	val=_ps.get( nm );
        	rc.put( nm, val==Null.NULL ? null : val );
        }        
        return rc;
    }
    
    /**
     * @param name
     * @param value
     */
    public void put(String name, Object value) {
    	if (name != null) {
    		_ps.put(name, value==null ? Null.NULL : value) ;
    		_keys.put(name.toUpperCase(), name) ;
    	}
    }
    
    /**
     * @param name
     * @return
     */
    public Object get(String name) {
    	String k= name==null ? null : _keys.get(name.toUpperCase()) ;
        Object obj= k==null ? null : _ps.get(k);
        return obj == Null.NULL ? null : obj;
    }
    
    /**
     * @return
     */
    public int size() { return _keys.size();    }
    
    /**
     * @param name
     * @return
     */
    public boolean contains(String name) {
        return name==null ? false : _keys.containsKey( name.toUpperCase());
    }

    /**
     * @param outPms
     * @return
     */
    public String toWhereClause(List<Object> outPms) {
    	StringBuilder w= new StringBuilder(256);
    	Object val;
    	String cn;
        for (Map.Entry<Object, Object> en : _ps.entrySet() ) {
            
        	if (w.length() > 0) { w.append(" AND "); }
            
    		cn= en.getKey().toString().toUpperCase();
    		val = en.getValue();
    		w.append(cn);
    		
    		if (isNil(val)) { w.append("=NULL"); }        			
    		else {
                w.append("=?") ;        			
    			outPms.add(val);
    		}
        }    	
        return w.toString();
    }
    
}
