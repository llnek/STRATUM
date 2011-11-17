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

import static com.zotoh.core.util.LangUte.MP;

import java.util.Map;

import com.zotoh.stratum.core.FldMetaHolder;

/**
 * @author kenl
 *
 */
public class OracleImpl extends DBDriver {
	    
	private Map<String,FldMetaHolder> _ids= MP();
	
    /* (non-Javadoc)
     * @see com.zotoh.stratum.sql.DBDriver#getStringKeyword()
     */
    @Override
    protected String getStringKeyword()     {
        return "VARCHAR2";
    }

    /* (non-Javadoc)
     * @see com.zotoh.stratum.sql.DBDriver#genDrop(java.lang.String)
     */
    @Override
    protected String genDrop(String tbl)     {
        return new StringBuilder(256).append("DROP TABLE ")
        .append(tbl)
        .append(" CASCADE CONSTRAINTS PURGE")
        .append(genExec()).append("\n\n")
        .toString();
    }

    /* (non-Javadoc)
     * @see com.zotoh.stratum.sql.DBDriver#getTSDefault()
     */
    @Override
    protected String getTSDefault()     {
        return "DEFAULT SYSTIMESTAMP";
    }

    /* (non-Javadoc)
     * @see com.zotoh.stratum.sql.DBDriver#getLongKeyword()
     */
    @Override
    protected String getLongKeyword()     {
        return "NUMBER(38)";
    }

    /* (non-Javadoc)
     * @see com.zotoh.stratum.sql.DBDriver#getDoubleKeyword()
     */
    @Override
    protected String getDoubleKeyword()     {
        return "BINARY_DOUBLE";
    }

    /* (non-Javadoc)
     * @see com.zotoh.stratum.sql.DBDriver#getFloatKeyword()
     */
    @Override
    protected String getFloatKeyword()     {
        return "BINARY_FLOAT";
    }

    /* (non-Javadoc)
     * @see com.zotoh.stratum.sql.DBDriver#genAutoInteger(java.lang.String, com.zotoh.stratum.core.DBColDef)
     */
    @Override
    protected String genAutoInteger(String table, FldMetaHolder def)     {
        _ids.put(table, def) ;
        return genInteger(def);
    }

    /* (non-Javadoc)
     * @see com.zotoh.stratum.sql.DBDriver#genAutoLong(java.lang.String, com.zotoh.stratum.core.DBColDef)
     */
    @Override
    protected String genAutoLong(String table, FldMetaHolder def)     {
        _ids.put(table, def) ;
        return genLong(def);
    }

    /* (non-Javadoc)
     * @see com.zotoh.stratum.sql.DBDriver#genEndSQL()
     */
    protected String genEndSQL()     {
        StringBuilder sql= new StringBuilder(256);
        FldMetaHolder col;
        String tn;
        
        for (Map.Entry<String,FldMetaHolder> en : _ids.entrySet()) {
            col= en.getValue();
            tn= en.getKey();
            sql.append( create_sequence(tn));
            sql.append( create_sequence_trigger(tn, col.getId())) ;
        }
                
        return sql.toString();
    }
    
    private String create_sequence(String table) {
        
        return "CREATE SEQUENCE SEQ_" + table +  
            " START WITH 1 INCREMENT BY 1" + 
            genExec() + 
            "\n\n";
    }

    private String create_sequence_trigger(String table, String key) {
    
        return "CREATE OR REPLACE TRIGGER TRIG_" + table + "\n" +
        "BEFORE INSERT ON " + table + "\n" + 
        "REFERENCING NEW AS NEW\n" + 
        "FOR EACH ROW\n" + 
        "BEGIN\n" + 
        "SELECT SEQ_" + table + ".NEXTVAL INTO :NEW." + key + " FROM DUAL;\n" +
        "END" + genExec() + "\n\n" ;
        
    }

    
    
    
}
