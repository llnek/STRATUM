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

import com.zotoh.stratum.anote.Column;
import com.zotoh.stratum.anote.Table;

/**
 * @author kenl
 *
 */
@Table(table="STRATUM_MMTABLE")
public class M2MTable extends StratumBase {
	
	private long _rhsObj, _lhsObj;
	private String _lhs, _rhs;
	
	/**
	 * 
	 */
	public M2MTable() {}
	

	@Column(id="II_LHS", unique=true)
	public String getLHS() { return _lhs; }
	public void setLHS(String s) {
		_lhs=s;
	}
	
	@Column(id="II_RHS", unique=true)
	public String getRHS() { return _rhs; }
	public void setRHS(String s) {
		_rhs=s;
	}
	
	@Column(id="II_LHSOID", unique=true)
	public long getLhsObjID() { return _lhsObj; }
	public void setLhsObjID(long id) {
		_lhsObj=id;
	}
	
	@Column(id="II_RHSOID", unique=true)
	public long getRhsObjID() { return _rhsObj; }
	public void setRhsObjID(long id) {
		_rhsObj=id;
	}
	
	
}
