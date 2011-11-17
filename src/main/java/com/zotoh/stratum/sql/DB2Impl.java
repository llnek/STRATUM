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
 
package com.zotoh.stratum.sql;

/**
 * @author kenl
 *
 */
public class DB2Impl extends DBDriver {
    
    /* (non-Javadoc)
     * @see com.zotoh.stratum.sql.DBDriver#getBlobKeyword()
     */
    @Override
    protected String getBlobKeyword()     {
        return "BLOB(2G)";
    }

    /* (non-Javadoc)
     * @see com.zotoh.stratum.sql.DBDriver#genDrop(java.lang.String)
     */
    @Override
    protected String genDrop(String tbl)     {
        return new StringBuilder(256).append("DROP TABLE ")
        .append(tbl)
        .append(genExec()).append("\n\n")
        .toString();
    }
    
}
