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
 
package demo;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.zotoh.core.crypto.PwdFactory;
import com.zotoh.core.db.JDBCInfo;
import com.zotoh.core.util.CoreUte;
import com.zotoh.core.util.Logger;
import com.zotoh.core.util.LoggerFactory;
import com.zotoh.stratum.core.DBIO;
import com.zotoh.stratum.core.Stratum;

import demo.crud.BasicCRUDDemo;
import demo.m2m.BasicM2MDemo;
import demo.o2m.BasicO2MDemo;
import demo.o2o.BasicO2ODemo;
import demo.sql.BasicGenSQLDemo;
import demo.sql.BasicSQLDemo;

/**
 * @author kenl
 *
 */
public class SampleApp {
	
    private Logger _log= LoggerFactory.getLogger(SampleApp.class);    
    private static Stratum s_db;
        
    /**
     * @param args
     */
    public static void main(String[] args)     {
        try   {
        	if ( !parseArgs(args)) {
        		usage();
        	} else {
        		new SampleApp().start(args);
        	}
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        finally {
        }
    }
    
    @SuppressWarnings("serial")
	private static boolean parseArgs(String[] args) {
    	Set<String> m= new HashSet<String>() {{ 
    		add("all"); add("crud"); add("sql"); add("o2o"); add("o2m"); add("m2m"); add("ddl");
    	}};
    	String opt= args.length > 0 ? args[0] : "";
    	return m.contains(opt) ;
    }
    
    private static void usage() {
    	System.out.println("Usage: SampleApp < all | sql | crud | 020 | o2m | m2m >");
    	System.out.println("options:");
    	System.out.println("all - all the demos.");
    	System.out.println("sql - basic sql operations demo.");
    	System.out.println("crud - CRUD demo.");
    	System.out.println("o2o - one 2 one association demo.");
    	System.out.println("o2m - one 2 many association demo.");
    	System.out.println("m2m - many 2 many association demo.");
    }
    
    private void start(String[] args) throws Exception     {
        _log.info("Starting sample application") ;
        
        File dbdir= args.length > 1 ? new File(args[1]) : CoreUte.genTmpDir() ;
        initialize(dbdir);
    
        boolean all = "all".equals(args[0]) ;
        DBIO io= s_db.openDB();
        
        new BasicGenSQLDemo(io ).start();        	
        // load DDL only ?
        
        if ("ddl".equals(args[0])) {
            return;
        }
        
        
        if (all || "sql".equals(args[0])) {
            new BasicSQLDemo(io).start();        	
        }        
        
        // basic CRUD
        if ( all || "crud".equals(args[0])) {
        	new BasicCRUDDemo(io).start();
        }
        
        // one to one
        if (all || "o2o".equals(args[0])) {
        	new BasicO2ODemo(io).start();
        }
        
        // one to many
        if ( all || "o2m".equals(args[0])) {
        	new BasicO2MDemo(io).start();
        }
        
        // many to many
        if ( all || "m2m".equals(args[0])) {
        	new BasicM2MDemo(io).start();
        }
        
        _log.info("Done.") ;
    }
    
    private void initialize(File dbdir) throws Exception     {
        String dbid= PwdFactory.getInstance().createRandomText(8);
        String rpath = CoreUte.niceFPath( dbdir);
        JDBCInfo props= new JDBCInfo         (
            "org.h2.Driver",
            "jdbc:h2:"+ rpath +"/data/"+dbid,
            "sa", ""
        );
        s_db= new Stratum(props);
    }
    
    
    
    
    
}
