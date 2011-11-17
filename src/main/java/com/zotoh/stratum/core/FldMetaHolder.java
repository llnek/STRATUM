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

import java.lang.reflect.Method;

import com.zotoh.stratum.anote.Column;


/**
 * @author kenl
 *
 */
public class FldMetaHolder {
	
	protected static FldMetaHolder DUMBO= new FldMetaHolder();
	private Method[] _mtds= new Method[2];
	private Column _col;
	
	/**
	 * 
	 */
	public FldMetaHolder() {}
	
	/**
	 * @return
	 */
	public Method getGetter() { return _mtds[0]; }
	
	/**
	 * @return
	 */
	public Method getSetter() { return _mtds[1]; }
	
	/**
	 * @param m
	 */
	public void setGetter(Method m) { 
		_col=m.getAnnotation(Column.class) ;
		_mtds[0]=m;
	}
	
	/**
	 * @param m
	 */
	public void setSetter(Method m) { _mtds[1]=m; }
	
	/**
	 * @return
	 */
	public boolean isUniqueKey() { return _col == null ? false : _col.unique();	}
	
	/**
	 * @return
	 */
	public boolean isAutoGen() { return _col == null ? false : _col.autogen();	}
	
	/**
	 * @return
	 */
	public String getId() { return _col==null ? "" : _col.id().toUpperCase(); }
	
	/**
	 * @return
	 */
	public boolean isNullable() { return _col==null ? true : _col.optional() ; }
	
	/**
	 * @return
	 */
	public int getSize() { return _col==null ? 0 : _col.size(); }
	
	/**
	 * @return
	 */
	public Class<?> getColType() { return _mtds[0]==null ? null : _mtds[0].getReturnType(); }
	
	/**
	 * @return
	 */
	public boolean isPK() { return false; }
	
	
}
