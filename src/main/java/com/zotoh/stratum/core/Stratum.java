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

import java.sql.SQLException;

import com.zotoh.core.db.JDBCInfo;
import com.zotoh.core.db.JDBCPoolManager;
import com.zotoh.core.util.GUID;


/**
 * @author kenl
 *
 */
public class Stratum {
	
    private JDBCPoolManager _mgr;
    
    /**
     * @param props
     * @throws SQLException
     */
    public Stratum(JDBCInfo props) throws SQLException {        
        _mgr= new JDBCPoolManager();
        _mgr.createPool("p1", props) ;
    }
  
    /**
     * @param info
     * @return
     * @throws SQLException
     */
    public DBIO openDB(JDBCInfo info) throws SQLException {
        return new DBIO( _mgr.createPool( GUID.generate(), info) );
    }
    
    /**
     * @return
     */
    public DBIO openDB() {
        return new DBIO( _mgr.getPool("p1"));
    }
    
    /**
     * 
     */
    public void finz() {
        try { _mgr.finz(); } finally {
        	_mgr=null;
        }
    }
    
    
    
}
