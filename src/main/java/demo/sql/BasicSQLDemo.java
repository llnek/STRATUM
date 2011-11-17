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
 

package demo.sql;

import static com.zotoh.core.util.StrUte.strstr;
import static com.zotoh.stratum.core.MetaCache.COL_VERID;

import java.util.Date;
import java.util.List;

import com.zotoh.core.db.DBRow;
import com.zotoh.core.util.Tuple;
import com.zotoh.stratum.core.DBIO;

import demo.shared.Demo;


/**
 * @author kenl
 *
 */
public class BasicSQLDemo extends Demo {
	
    
    /**
     * @param io
     */
    public BasicSQLDemo(DBIO io)     {
        super(io);
    }

    /* (non-Javadoc)
     * @see com.zotoh.stratum.samples.Demo#run()
     */
    @Override
    protected void run() throws Exception    {
    	
        demo_create_via_sql();
        
        demo_fetch_via_sql();
        
        demo_delete_via_sql();
        
        demo_no_objects();
        
        demo_call_func();
        
        demo_call_proc();
        
    }

    private void demo_create_via_sql() throws Exception {
        
        _db.startTransaction();
        
        String sql= "insert into TBL_PERSON (FIRST_NAME,LAST_NAME,IQ,BDAY,SEX,$VER) VALUES (?,?,?,?,?,?)" ;
        sql=strstr(sql, "$VER", COL_VERID);
        
        _db.execUpdateSQL(sql, "John", "Smith", 195, new Date(), "male", 1L);        
        _db.execUpdateSQL(sql, "Mary", "Smith", 150, new Date(), "female", 1L );
        
        _db.commit();
    
        log("Create John & Mary Smith(s). OK.") ;
    }
    
    private void demo_fetch_via_sql() throws Exception {
        
        String sql= "select FIRST_NAME,IQ from TBL_PERSON";
        List<DBRow> lst = _db.fetchSQL(sql, new Tuple()) ;
        
        String f1= (String) lst.get(0).get("FIRST_NAME") ;
        String f2= (String) lst.get(1).get("FIRST_NAME") ;
        
        int n1= (Integer) lst.get(0).get("IQ") ;
        int n2= (Integer) lst.get(1).get("IQ") ;
      
        log("Person (1) = " + f1 + ", IQ = " + n1) ;
        log("Person (2) = " + f2 + ", IQ = " + n2) ;
        
    }
    
    private void demo_delete_via_sql() throws Exception {

        _db.startTransaction();
        
        String sql= "delete from TBL_PERSON where IQ=?";
        
        _db.execUpdateSQL(sql, 195) ;
        _db.execUpdateSQL(sql, 150) ;
        
        _db.commit();
    }

    private void demo_call_func() throws Exception {        
    }
    
    private void demo_call_proc() throws Exception {        
    }
    
    
    
    
    
    
}
