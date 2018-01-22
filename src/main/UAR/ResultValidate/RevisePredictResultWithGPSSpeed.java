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

public class RevisePredictResultWithGPSSpeed {
	public static double calculateSpeed(ArrayList<String> window){
		double sum = 0;
		int n = window.size();
		for (int i = 0; i < n; i++) {
			sum += Double.parseDouble(window.get(i).split("\t")[12]);
		}
		return sum / n;
	}
	
	public static int probabilityOfSpeed (double speed){
		double[] probability = new double[5];
		/**
		 * INCAR
		 * RUNNING
		 * WALKING
		 * INTRAIN
		 * BIKING
		 * */
		double[][] normalDistribution = {{6.10671695224, 0.444434793672}, {1.99984158113, 0.0887173034551}, {1.2, 0.3}, {12.0459960891, 0.762784293253}, {2.8, 0.5}};
		for(int i = 0; i < 5; i++){
			probability[i] =  Math.exp(Math.pow(speed - normalDistribution[i][0], 2) * (-1.0) / (2.0 * Math.pow(normalDistribution[i][1], 2))) / (Math.sqrt(2 * Math.PI) * normalDistribution[i][1]);
		}
		double max;
		int index = 0;
		max = probability[0];
		for (int j = 1; j < 5; j++) {
			if (max < probability[j]) {
				max = probability[j];
				index = j;
			}
		}
		
		return index;
	}
	
	public static void main(String[] args) throws IOException, ParseException {
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		int window_length = 200;
		File file_0 = new File("C:/Users/haoyu5/Desktop/part-r-00000");
		BufferedReader reader = null;
		ArrayList<String> dataSet = new ArrayList<String>();
		String line = null;
		reader = new BufferedReader(new FileReader(file_0));

		// 一次读入一行，直到读入null为文件结束
		while ((line = reader.readLine()) != null) {
			dataSet.add(line);
		}
		reader.close();
		
		ArrayList<Double> speedSet = new ArrayList<>();
		ArrayList<String> window = new ArrayList<>();
		for(String d : dataSet){
			if(window.size() == window_length){
				double fString = calculateSpeed(window);
				speedSet.add(fString);
				window.clear();
				window.add(d);
			}else{
				window.add(d);
			}
			
		}
		if(window.size() == window_length){
			double fString = calculateSpeed(window);
			speedSet.add(fString);
		}
		
		File file_1 = new File("C:/Users/haoyu5/Desktop/result_predict.txt");
		ArrayList<String> activitySet = new ArrayList<String>();
		reader = new BufferedReader(new FileReader(file_1));

		// 一次读入一行，直到读入null为文件结束
		while ((line = reader.readLine()) != null) {
			activitySet.add(line);
		}
		reader.close();
		
		
		ArrayList<String> result = new ArrayList<String>();
		System.out.println("dataSet size is: " + dataSet.size()/200);
		System.out.println("speedSet size is: " + speedSet.size());
		System.out.println("activitySet size is: " + activitySet.size());
		if(dataSet.size() / 200 == activitySet.size()){
			int length = activitySet.size();
			for(int i = 0; i < length-1; i++){
				StringBuffer sb = new StringBuffer();
				String timestamp = dataSet.get(i*200).split("\t")[2];
				long x_time = dfs.parse(timestamp).getTime();
				String predict = activitySet.get(i).split("\t")[1];
				double speed = speedSet.get(i);
				if(!"-1".equals(predict) && !"0".equals(predict) && !"1".equals(predict) && !"2".equals(predict) && !"4".equals(predict)){
					int index = probabilityOfSpeed(speed) + 1;
					if(index != Integer.parseInt(predict)){
						predict = "-1";
					}
				}
				sb.append(predict).append("\t").append(x_time).append("\t").append(timestamp);
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
