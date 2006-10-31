/*
 * Created on 05.10.2004
 */
package de.hupc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;

import org.apache.log4j.Logger;

/**
 * @author oliverzein
 */
public class FileUtil {
    /**
     * Write data to a file.
     * @param data
     * @param filePath
     * @param append
     * @throws Exception
     */
    public static void writeFile(String data, String filePath, boolean append) throws Exception {
        FileOutputStream fout = null;
        
        try {
            Logger.getLogger("com.db.janus").info("Writing file: " + filePath);
            fout = new FileOutputStream(filePath, append);
            fout.write(data.getBytes());
        } catch (Exception e) {
            throw e;
        } finally {
        	if(fout != null) {
        		fout.flush();
        		fout.close();
        	}
        }
    }
    
    /**
     * Write data binary to a file.
     * @param data
     * @param filePath
     * @param append
     * @throws Exception
     */
    public static void writeFile(byte[] data, String filePath, boolean append) throws Exception {
        FileOutputStream fout = null;
        
        try {
            Logger.getLogger("com.db.janus").info("Writing file binary: " + filePath);
            fout = new FileOutputStream(filePath, append);
            fout.write(data);
        } catch (Exception e) {
            throw e;
        } finally {
        	if(fout != null) {
        		fout.flush();
        		fout.close();
        	}
        }
    }

    public static void mkdir(String filePath) throws Exception {
        File file = new File(filePath);
        file.mkdirs();
        Logger.getLogger("com.db.janus").info("Directory " + file.getPath() + " created.");
    }
    
    public static void mkdirFromFile(String filePath) throws Exception {
        File file = new File(filePath);
        
        if(file.isDirectory()) {
            file.mkdirs();
            Logger.getLogger("com.db.janus").info("Directory " + file.getPath() + " created.");
        } else {
	        if (!file.getParentFile().exists()) {
	            file.getParentFile().mkdirs();
	            Logger.getLogger("com.db.janus").info("Directory " + file.getParentFile() + " created.");
	        }
        }
    }

    public static void appendFile(String source, String dest) throws Exception {
        Logger.getLogger("com.db.janus").info("appendFile: " + source + " --> " + dest);        
        int bufferSize = 1024 * 1024;  // buffer size in byte
        byte[] b = new byte[bufferSize];
        int len;
        
        FileInputStream fin = new FileInputStream(source);
        FileOutputStream fout = new FileOutputStream(dest, true);
        
        while((len = fin.read(b)) != -1) {
            fout.write(b, 0, len);
        }
        
        fout.flush();
        fout.close();
        fin.close();
    }    
    
    /**
     * Reads a file into a string.
     * @param file
     * @return
     * @throws Exception
     */
    public String readFile(String file) throws Exception {
        FileReader fr = new FileReader(file);
        StringBuffer buf = new StringBuffer();
        char[] c = new char[10];
        int len;
        
        while((len = fr.read(c)) > -1) {
            buf.append(c, 0, len);
        }
        
        return buf.toString();
    }
    
	public static byte[] readFileByte(String fileName) throws Exception {
        File f = new File(fileName);
        byte[] b;
        
        if(f.exists()) {
	        FileInputStream fis = new FileInputStream(fileName);
	        b = new byte[(int)f.length()];
	        fis.read(b);
	        return b;
        } else {
            throw(new Exception("File does not exist!"));
        }
    }
}