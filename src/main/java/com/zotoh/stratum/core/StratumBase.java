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

import com.zotoh.stratum.core.StratumObj;


/**
 * @author kenl
 *
 */
public abstract class StratumBase implements StratumObj {

	private long _ver, _primaryKey=-1L;
		
	/* (non-Javadoc)
	 * @see com.zotoh.stratum.core.StratumObj#getRowID()
	 */
	public long getRowID() { return _primaryKey; }
	
	/* (non-Javadoc)
	 * @see com.zotoh.stratum.core.StratumObj#setRowID(long)
	 */
	public void setRowID(long n) { 
		_primaryKey=n; 
	}

    /* (non-Javadoc)
     * @see com.zotoh.stratum.core.StratumObj#getVerID()
     */
    public long getVerID() { return _ver; }
    
	/* (non-Javadoc)
	 * @see com.zotoh.stratum.core.StratumObj#setVerID(long)
	 */
	public void setVerID(long v) {
	    _ver=v;
	}
	
	
	

}
