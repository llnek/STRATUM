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

import com.zotoh.stratum.core.FldMetaHolder;



/**
 * @author kenl
 *
 */
public class H2Impl extends DBDriver {
    
    /* (non-Javadoc)
     * @see com.zotoh.stratum.sql.DBDriver#getBlobKeyword()
     */
    @Override
    protected String getBlobKeyword()     {
        return "BLOB";
    }

    /* (non-Javadoc)
     * @see com.zotoh.stratum.sql.DBDriver#getDoubleKeyword()
     */
    @Override
    protected String getDoubleKeyword()     {
        return "DOUBLE";
    }

    /* (non-Javadoc)
     * @see com.zotoh.stratum.sql.DBDriver#getFloatKeyword()
     */
    @Override
    protected String getFloatKeyword()     {
        return "FLOAT";
    }

    /* (non-Javadoc)
     * @see com.zotoh.stratum.sql.DBDriver#genDrop(java.lang.String)
     */
    @Override
    protected String genDrop(String tbl)     {
        return new StringBuilder(256).append("DROP TABLE ")
        .append(tbl)
        .append(" IF EXISTS CASCADE").
        append(genExec()).append("\n\n")
        .toString();
    }

    /* (non-Javadoc)
     * @see com.zotoh.stratum.sql.DBDriver#genBegin(java.lang.String)
     */
    @Override
    protected String genBegin(String tbl)     {
        return new StringBuilder(256).append("CREATE CACHED TABLE ")
        .append(tbl)
        .append("\n(\n").toString();
    }

    /* (non-Javadoc)
     * @see com.zotoh.stratum.sql.DBDriver#genGrant(java.lang.String)
     */
//    @Override
//    protected String genGrant(String tbl)     {
//        return new StringBuilder(256).append("GRANT ALL ON ")
//        .append(tbl)
//        .append(" TO PUBLIC").append(genExec()).append("\n")
//        .toString();
//    }

    /* (non-Javadoc)
     * @see com.zotoh.stratum.sql.DBDriver#genEndSQL()
     */
//    @Override
//    protected String genEndSQL()     {
//        return "COMMIT;";
//    }

    /* (non-Javadoc)
     * @see com.zotoh.stratum.sql.DBDriver#genAutoInteger(java.lang.String, com.zotoh.stratum.core.DBColDef)
     */
    @Override
    protected String genAutoInteger( String table, FldMetaHolder def)     {
        String type= getIntKeyword();
        String col= def.getId();
        
        return new StringBuilder(256)
        .append(getPad()).append(col ).append(" ")
        .append( type )
        .append( def.isPK() ? " IDENTITY( 1 ) " : " AUTO_INCREMENT( 1 ) " )
        .toString()
        ;
    }

    /* (non-Javadoc)
     * @see com.zotoh.stratum.sql.DBDriver#genAutoLong(java.lang.String, com.zotoh.stratum.core.DBColDef)
     */
    @Override
    protected String genAutoLong( String table, FldMetaHolder def)     {
        String type= getLongKeyword();
        String col= def.getId();
        
        return new StringBuilder(256)
        .append(getPad()).append(col ).append(" ")
        .append( type )
        .append( def.isPK() ? " IDENTITY( 1 ) " : " AUTO_INCREMENT( 1 ) " )
        .toString()
        ;
    }

    /**
     * 
     */
    public H2Impl()
    {}
    
}
