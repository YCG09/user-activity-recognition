package com.lenovo.ca.UAR.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;


public class Utils {
    /** Write the object to a Base64 string. */
    public static String toString( Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }
    
    /** Read the object from Base64 string. */
    public static Object fromString( String s ) throws IOException,
            ClassNotFoundException {
        byte [] data = Base64.decode(s, Base64.DEFAULT);
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(  data ) );
        Object o  = ois.readObject();
        ois.close();
        return o;
    }
    
    
    public static String save(final String content, final String fileName, int fold) {
        if(content.isEmpty() || fileName == null || fileName.isEmpty()){
            return null;
        }
        String savedPath = "./data/fold_" + fold + "/" + fileName;
        try (FileOutputStream fos = new FileOutputStream(new File(savedPath), false);
				OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
				BufferedWriter bw = new BufferedWriter(osw);) {
			bw.write(content);
			return savedPath;
		} catch (Exception e) {
			System.out.print("write data error!");
			e.printStackTrace();
			return null;
		}
        
    }
    
    public static String loadModel(String fileName, int fold) throws FileNotFoundException{
        String content = null;
        if(fileName == null || fileName.isEmpty()){
            return content;
        }
        
    	File label = new File("./data/fold_" + fold + "/" + fileName);
		BufferedReader reader = null;
		reader = new BufferedReader(new FileReader(label));

		// 一次读入一行，直到读入null为文件结束
		try {
			content = reader.readLine();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return content;
    }
}
