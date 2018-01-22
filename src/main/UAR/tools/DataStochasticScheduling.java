package com.lenovo.ca.UAR.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

public class DataStochasticScheduling {

	public static void main(String[] args) throws Exception {
		File file = new File("C:/Users/haoyu5/Desktop/all.feature");
		BufferedReader reader = null;
		ArrayList<String> data = new ArrayList<String>();
		String line = null;
		reader = new BufferedReader(new FileReader(file));

		// 一次读入一行，直到读入null为文件结束
		while ((line = reader.readLine()) != null) {
			data.add(line);
		}
		reader.close();
		Collections.shuffle(data);
		try (FileOutputStream fos = new FileOutputStream(new File("C:/Users/haoyu5/Desktop/abc.arff"), false);
				OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
				BufferedWriter bw = new BufferedWriter(osw);) {
			bw.write("@relation MultiLabelData"); bw.newLine();
			bw.newLine();
			for(int i = 1; i <= 127; i++){
				String attrInfo = "@attribute Att" + i + "numeric";
				bw.write(attrInfo); bw.newLine();
			}
			for(int i = 1; i <= 6; i++){
				String classInfo = "@attribute Class" + i + "{0,1}";
				bw.write(classInfo); bw.newLine();
			}
			bw.newLine();
			bw.write("@data"); bw.newLine();
			for (String d : data) {
				bw.write(d);
				bw.newLine();
			}
		} catch (Exception e) {
			System.out.print("write data error!");
			e.printStackTrace();
		}
	}

}
