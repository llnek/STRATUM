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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.zotoh.core.db.DBVendor;
import com.zotoh.core.io.StreamUte;
import com.zotoh.core.util.MetaUte;
import com.zotoh.core.util.StrUte;
import com.zotoh.core.xml.XmlUte;

/**
 * @author kenl
 *
 */
public class GenSQLFile {
	
    /**
     * @param args
     */
    public static void main(String[] args) {
        
        try  {
            
            if (args.length != 3) {
                usage();
            }  else {
                start(args[0], args[1], new File( args[2]));
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        
    }
    
    private static void usage() {
        System.out.println("GenSQLFile <manifest-file> [ hsqldb | mysql | oracle | mssql | postgresql ] <output-file>") ;
        System.out.println("e.g.") ;
        System.out.println("GenSQLFile sample.txt hsqldb sample.sql") ;
        System.out.println("") ;
    }
    
    private static void start(String file, String db, File out) throws Exception {
        DBVendor v= DBVendor.fromString(db);
        InputStream inp= null;
        Class<?>[] css= null;
        
        if (v==null) {            usage(); return ; }
                
        try        {
            inp= new FileInputStream(file);
            css= readFile(inp);
        }
        catch (Exception e) {
            css=null;
            System.err.print("Failed to parse manifest file : " + file);
        }
        finally {
            StreamUte.close(inp);
        }
        
        if (css != null) {
            genDDL(v, out, css);
        }
        
    }

    /**
     * @param v
     * @param css
     * @return
     * @throws Exception
     */
    public static String genDDL( DBVendor v, Class<?> ... css) 
    				throws Exception {        
        return DBDriver.newDriver(v).getDDL(css) ;                
    }
    
    /**
     * @param v
     * @param out
     * @param css
     * @throws Exception
     */
    public static void genDDL( DBVendor v, File out, Class<?> ... css ) 
    				throws Exception {    
        StreamUte.writeFile( out, genDDL(v, css) );
    }

    
    private static Class<?>[] readFile(InputStream inp) throws Exception {
        
        List< Class<?> > lst;
        
        try         {
            Document doc= XmlUte.parseXML(inp) ;
            Element root= doc.getDocumentElement();            
            lst= readClasses( getFirst( root, "classes") );            
        }
        finally {
            StreamUte.close(inp) ;
        }

        return lst.toArray(new Class<?>[0]);
        
    }

    private static List< Class<?> > readClasses( Element top) throws Exception {
        
        NodeList nl= top.getElementsByTagName("class") ;
        List< Class<?> > lst= new ArrayList< Class<?> >();
        Element em;
        String s;
        Class<?> z;
        
        if (nl != null) for (int i=0; i < nl.getLength(); ++i) {            
            em= (Element) nl.item(i) ;
            s= StrUte.trim( em.getAttribute("id") );
            z= MetaUte.forName(s);
            if (z==null) {
//                System.err.println("Failed to load class : " + s) ;
                throw new ClassNotFoundException() ;
            }
            lst.add(z);
        }
        
        return lst;
    }
    
    private static Element getFirst(Element top, String tag) {
        
        NodeList nl= top==null ? null : top.getElementsByTagName( tag);
        Element e=null;
        
        if (nl != null && nl.getLength() > 0) {
            e= (Element) nl.item(0);
        }        
        return e;
    }

    
    
}
