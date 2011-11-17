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

import static com.zotoh.core.util.LangUte.LT;

import java.util.List;

import com.zotoh.core.util.Tuple;

/**
 * @author kenl
 *
 */
public class AssocMetaHolder {
	
	private List<Tuple> _fkeys= LT();
	
	/**
	 * @return
	 */
	public List<Tuple> getFKeys() { return _fkeys; }
	
	/**
	 * 
	 */
	public AssocMetaHolder() {}
	
	/**
	 * @param m2m
	 * @param lhs
	 * @param rhs
	 * @param fkey
	 */
	public void add(boolean m2m, Class<?> lhs, Class<?> rhs, String fkey) {
		_fkeys.add( new Tuple(m2m, lhs, rhs,fkey));
	}
	
	
}
