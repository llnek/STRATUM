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

import static com.zotoh.core.util.LangUte.AC;
import static com.zotoh.core.util.LangUte.MP;
import static com.zotoh.core.util.LangUte.TM;

import java.util.Collections;
import java.util.Map;

/**
 * Holds database table information. 
 *
 * @author kenl
 *
 */
public class TableMetaHolder {
	
    private Map<String, ColMetaHolder> _keys= TM();
    private Map<String, ColMetaHolder> _cols= MP();
    private String _table;
    private boolean _supportsGetGeneratedKeys ;
    private boolean _supportsTransactions ;
    
    /**
     * @param t
     */
    public TableMetaHolder(String t)     {
        _table=t;
    }
    
    /**
     * @return
     */
    public String getName()     {         return _table;    }

    /**
     * @return
     */
    public boolean canGetGeneratedKeys()     {        return _supportsGetGeneratedKeys;    }
    
    /**
     * @return
     */
    public boolean canTransact()    {        return _supportsTransactions;    }
        
    /**
     * @param b
     */
    public void setGetGeneratedKeys(boolean b)     {
        _supportsGetGeneratedKeys=b;
    }
    
    /**
     * @param b
     */
    public void setTransact(boolean b)     {
        _supportsTransactions=b;
    }
    
    /**
     * @param c
     * @param isKey
     */
    public void addCol(ColMetaHolder c, boolean isKey)     {
        if (c != null) {
            String cn= c.getName().toUpperCase();
            _cols.put(cn, c) ;
            if (isKey) {
                _keys.put(cn, c);
            }
        }
    }
    
    /**
     * @param c
     */
    public void addCol(ColMetaHolder c)     {
        addCol(c, false);
    }
    
    /**
     * @param n
     * @return
     */
    public ColMetaHolder getColMeta(String n)     {
        // always do lookup with uppercase
        return n==null ? null : _cols.get(n.toUpperCase() ) ;
    }
    
    /**
     * @param cn
     * @return
     */
    public boolean hasCol(String cn)     {        
        // always do lookup with uppercase
        return cn==null ? false : _cols.containsKey(cn.toUpperCase() ) ;
    }
    
    /**
     * @return
     */
    public Map<String,ColMetaHolder> getColMetas()     {        
        return Collections.unmodifiableMap( _cols );
    }
    
    /**
     * @return
     */
    public Map<String,ColMetaHolder> getKeys()     {        
        return Collections.unmodifiableMap( _keys );
    }
    
    /**
     * @return
     */
    public ColMetaHolder[] getKeysAsArray()     {        
        return AC( ColMetaHolder.class, _keys.values() );
    }
    
    
    
}
