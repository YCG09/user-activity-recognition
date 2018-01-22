package com.lenovo.ca.UAR.ResultValidate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class RealScenarioPredictResultProcess {
	public static void main(String[] args) throws IOException, ParseException {
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		File file_0 = new File("C:/Users/haoyu5/Desktop/part-r-00000");
		BufferedReader reader = null;
		ArrayList<String> data = new ArrayList<String>();
		String line = null;
		reader = new BufferedReader(new FileReader(file_0));

		// 一次读入一行，直到读入null为文件结束
		while ((line = reader.readLine()) != null) {
			data.add(line);
		}
		reader.close();
		
		File file_1 = new File("C:/Users/haoyu5/Desktop/result_predict.txt");
		ArrayList<String> activity = new ArrayList<String>();
		reader = new BufferedReader(new FileReader(file_1));

		// 一次读入一行，直到读入null为文件结束
		while ((line = reader.readLine()) != null) {
			activity.add(line);
		}
		reader.close();
		
		ArrayList<String> result = new ArrayList<String>();
		
		System.out.println(data.size()/200 + "---------" + activity.size());
		if(data.size() / 200 == activity.size()){
			int length = activity.size();
			for(int i = 0; i < length-1; i++){
				StringBuffer sb = new StringBuffer();
				String timestamp = data.get(i*200).split("\t")[2];
				long x_time = dfs.parse(timestamp).getTime();
				System.out.println(x_time);
				String predict = activity.get(i).split("\t")[1];
//				String predict = activity.get(i);
//				sb.append(predict).append("\t").append(x_time).append("\t").append(timestamp);
				sb.append(predict).append("\t").append(timestamp);
				result.add(sb.toString());
			}
		}
		try (FileOutputStream fos = new FileOutputStream(new File("C:/Users/haoyu5/Desktop/part-r-00006"), false);
				OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
				BufferedWriter bw = new BufferedWriter(osw);) {
			
			for (String d : result) {
				bw.write(d);
				bw.newLine();
			}
		} catch (Exception e) {
			System.out.print("write data error!");
			e.printStackTrace();
		}
	}
}
