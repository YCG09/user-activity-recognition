package com.lenovo.ca.UAR.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class ThresholdAndConfidenceLevelCurve {
	public static void main(String[] args) throws IOException {
		
		int labelNum = 7;
		File file = new File("C:/Users/haoyu5/Desktop/result_predict.txt");
		BufferedReader reader = null;
		ArrayList<String> data = new ArrayList<String>();
		ArrayList<String> result = new ArrayList<>();
		String line = null;
		reader = new BufferedReader(new FileReader(file));

		// 一次读入一行，直到读入null为文件结束
		while ((line = reader.readLine()) != null) {
			data.add(line);
		}
		reader.close();
		double threshold = 0;
		while(threshold <= 1.0){
			int TP = 0;
			int SEDENTARY_TP = 0;
			int INCAR_TP = 0;
			int RUNNING_TP = 0;
			int WALKING_TP = 0;
			int INTRAIN_TP = 0;
			int BIKING_TP = 0;
			int STAIRS_TP = 0;

			int FP = 0;
			int SEDENTARY_FP = 0;
			int INCAR_FP = 0;
			int RUNNING_FP = 0;
			int WALKING_FP = 0;
			int INTRAIN_FP = 0;
			int BIKING_FP = 0;
			int STAIRS_FP = 0;
			
			int count = 0;
			int count_SEDENTARY = 0;
			int count_INCAR = 0;
			int count_RUNNING = 0;
			int count_WALKING = 0;
			int count_STAIRS = 0;
			int count_INTRAIN = 0;
			int count_BIKING = 0;
			
			for(String s : data){
				String[] cols = s.split("\t")[0].replace("[", "").replace("]", "").split(",");
				String[] vals = s.split("\t")[1].replace("[", "").replace("]", "").split(",");
				double[] P = new double[labelNum];
				double[] T = new double[labelNum];
				for(int i = 0; i < labelNum; i++){
					P[i] = Double.parseDouble(cols[i]);
					T[i] = Double.parseDouble(vals[i]);
				}
				double max;
				max = P[0];
				for (int j = 1; j < labelNum; j++) {
					if (max < P[j]) {
						max = P[j];
					}
				}
				for (int k = 0; k < labelNum; k++) {

					if (max == P[k] && T[k] == 1 && P[k] >= threshold) {
						TP++;
						if(k == 0){
							SEDENTARY_TP++;
						}else if(k == 1){
							INCAR_TP++;
						}else if(k == 2){
							RUNNING_TP++;
						}else if(k == 3){
							WALKING_TP++;
						}else if(k == 4){
							STAIRS_TP++;
//							WALKING_TP++;
						}else if(k == 5){
							INTRAIN_TP++;
						}else if(k == 6){
							BIKING_TP++;
						}
					}else if(max == P[k] && T[k] != 1 && P[k] >= threshold){
						FP++;
						if(k == 0){
							SEDENTARY_FP++;
						}else if(k == 1){
							INCAR_FP++;
						}else if(k == 2){
							RUNNING_FP++;
						}else if(k == 3){
							WALKING_FP++;
						}else if(k == 4){
							STAIRS_FP++;
//							WALKING_FP++;
						}else if(k == 5){
							INTRAIN_FP++;
						}else if(k == 6){
							BIKING_FP++;
						}
					}
					if(T[k] == 1){
						count++;
						if(k == 0){
							count_SEDENTARY++;
						}else if(k == 1){
							count_INCAR++;
						}else if(k == 2){
							count_RUNNING++;
						}else if(k == 3){
							count_WALKING++;
						}else if(k == 4){
							count_STAIRS++;
//							count_WALKING++;
						}else if(k == 5){
							count_INTRAIN++;
						}else if(k == 6){
							count_BIKING++;
						}
					}

				}
			}
			double precision = TP * 1.0 / (TP + FP);
			double sedentary_precision = SEDENTARY_TP * 1.0 / (SEDENTARY_TP + SEDENTARY_FP);
			double incar_precision = INCAR_TP * 1.0 / (INCAR_TP + INCAR_FP);
			double running_precision = RUNNING_TP * 1.0 / (RUNNING_TP + RUNNING_FP);
			double walking_precision = WALKING_TP * 1.0 / (WALKING_TP + WALKING_FP);
			double stairs_precision = STAIRS_TP * 1.0 / (STAIRS_TP + STAIRS_FP);
			double intrain_precision = INTRAIN_TP * 1.0 / (INTRAIN_TP + INTRAIN_FP);
			double biking_precision = BIKING_TP * 1.0 / (BIKING_TP + BIKING_FP);
			
			double recall = TP * 1.0 / count;
			double sedentary_recall = SEDENTARY_TP * 1.0 / count_SEDENTARY;
			double incar_recall = INCAR_TP * 1.0 / count_INCAR;
			double running_recall = RUNNING_TP * 1.0 / count_RUNNING;
			double walking_recall = WALKING_TP * 1.0 / count_WALKING;
			double stairs_recall = STAIRS_TP * 1.0 / count_STAIRS;
			double intrain_recall = INTRAIN_TP * 1.0 / count_INTRAIN;
			double biking_recall = BIKING_TP * 1.0 / count_BIKING;
			
			System.out.println("threshold is: " + threshold);
			System.out.println("precision is: " + TP + "---" + FP + "---" + precision);
			System.out.println("sedentary_precision is: " + SEDENTARY_TP + "---" + SEDENTARY_FP + "---" + sedentary_precision);
			System.out.println("incar_precision is: " + INCAR_TP + "---" + INCAR_FP + "---" + incar_precision);
			System.out.println("running_precision is: " + RUNNING_TP + "---" + RUNNING_FP + "---" + running_precision);
			System.out.println("walking_precision is: " + WALKING_TP + "---" + WALKING_FP + "---" + walking_precision);
			System.out.println("stairs_precision is: " + STAIRS_TP + "---" + STAIRS_FP + "---" + stairs_precision);
			System.out.println("intrain_precision is: " + INTRAIN_TP + "---" + INTRAIN_FP + "---" + intrain_precision);
			System.out.println("biking_precision is: " + BIKING_TP + "---" + BIKING_FP + "---" + biking_precision);
			
			StringBuffer sb = new StringBuffer();
			sb.append(threshold).append("\t")
				.append(precision).append("\t")
				.append(sedentary_precision).append("\t")
				.append(incar_precision).append("\t")
				.append(running_precision).append("\t")
				.append(walking_precision).append("\t")
				.append(stairs_precision).append("\t")
				.append(intrain_precision).append("\t")
				.append(biking_precision).append("\t")
				.append(recall).append("\t")
				.append(sedentary_recall).append("\t")
				.append(incar_recall).append("\t")
				.append(running_recall).append("\t")
				.append(walking_recall).append("\t")
				.append(stairs_recall).append("\t")
				.append(intrain_recall).append("\t")
				.append(biking_recall);
			result.add(sb.toString());
			threshold += 0.02;
		}
		try (FileOutputStream fos = new FileOutputStream(new File("C:/Users/haoyu5/Desktop/confidence_curve.txt"), false);
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
