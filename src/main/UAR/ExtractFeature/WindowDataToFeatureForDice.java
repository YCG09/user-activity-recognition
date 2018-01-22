package com.lenovo.ca.UAR.ExtractFeature;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

import com.lenovo.ca.UAR.Utils.Complex;
import com.lenovo.ca.UAR.Utils.FFT;

public class WindowDataToFeatureForDice {
	public static String calculateFeatures(ArrayList<String> window){
		int length_1 = window.size();
		int length_2 = 64;
		int length_3 = 16;
		StringBuffer features = new StringBuffer();
		if(window == null || window.size() == 0){
			return null;
		}
		String activity = null;
		String label = null;
		double[] ACCELERATOR_X = new double[length_2];
		double[] ACCELERATOR_Y = new double[length_2];
		double[] ACCELERATOR_Z = new double[length_2];
		double[] ACCELERATOR_N = new double[length_2];
		double[] ACCELERATOR_E = new double[length_2];
		double[] ACCELERATOR_D = new double[length_2];
		double[] ACCELERATOR_AMP = new double[length_2];
		double[] GYROSCOPE_X = new double[length_2];
		double[] GYROSCOPE_Y = new double[length_2];
		double[] GYROSCOPE_Z = new double[length_2];
		double[] GYROSCOPE_A = new double[length_2];
		double[] GYROSCOPE_B = new double[length_2];
		double[] GYROSCOPE_C = new double[length_2];
		
		int index = 0;
		for(String s : window) {
			if(index >= length_1 - length_2){
				int position = index - (length_1 - length_2);
				String[] cols = s.split("\t");
				ACCELERATOR_X[position] = Double.parseDouble(cols[3]);
				ACCELERATOR_Y[position] = Double.parseDouble(cols[4]);
				ACCELERATOR_Z[position] = Double.parseDouble(cols[5]);
				GYROSCOPE_X[position] = Double.parseDouble(cols[6]);
				GYROSCOPE_Y[position] = Double.parseDouble(cols[7]);
				GYROSCOPE_Z[position] = Double.parseDouble(cols[8]);
				activity = cols[1];
			}
			index++;
		}
			
		ACCELERATOR_N = MedianFilter(ACCELERATOR_X, 3);
		ACCELERATOR_E = MedianFilter(ACCELERATOR_Y, 3);
		ACCELERATOR_D = MedianFilter(ACCELERATOR_Z, 3);
		
		GYROSCOPE_A = MedianFilter(GYROSCOPE_X, 3);
		GYROSCOPE_B = MedianFilter(GYROSCOPE_Y, 3);
		GYROSCOPE_C = MedianFilter(GYROSCOPE_Z, 3);
		
		for(int i = 0; i < length_2; i++){
			ACCELERATOR_AMP[i] = Math.sqrt(Math.pow(ACCELERATOR_N[i], 2) + Math.pow(ACCELERATOR_E[i], 2)) * Math.signum(ACCELERATOR_D[i]);

		}
		double[] ACCELERATOR_ANGLE = calculateAngle(ACCELERATOR_N, ACCELERATOR_E);
		String ACCELERATOR_D_FEATURE = extractFeature(ACCELERATOR_D);
		String ACCELERATOR_AMP_FEATURE = extractFeature(ACCELERATOR_AMP);
		String ACCELERATOR_ANGLE_FEATURE = extractFeature(ACCELERATOR_ANGLE);
		
		String GYROSCOPE_A_FEATURE = extractFeature(GYROSCOPE_A);
		String GYROSCOPE_B_FEATURE = extractFeature(GYROSCOPE_B);
		String GYROSCOPE_C_FEATURE = extractFeature(GYROSCOPE_C);
		
		features.append(ACCELERATOR_D_FEATURE);
		features.append(ACCELERATOR_AMP_FEATURE);
		features.append(ACCELERATOR_ANGLE_FEATURE);
		features.append(GYROSCOPE_A_FEATURE);
		features.append(GYROSCOPE_B_FEATURE);
		features.append(GYROSCOPE_C_FEATURE);
		
		double corrcoef_1 = Correlation(ACCELERATOR_D, ACCELERATOR_AMP);
		features.append(corrcoef_1).append(",");
		double corrcoef_2 = Correlation(ACCELERATOR_ANGLE, ACCELERATOR_AMP);
		features.append(corrcoef_2).append(",");
		double corrcoef_3 = Correlation(ACCELERATOR_D, ACCELERATOR_ANGLE);
		features.append(corrcoef_3).append(",");
		double corrcoef_4 = Correlation(ACCELERATOR_D, GYROSCOPE_A);
		features.append(corrcoef_4).append(",");
		double corrcoef_5 = Correlation(ACCELERATOR_D, GYROSCOPE_B);
		features.append(corrcoef_5).append(",");
		double corrcoef_6 = Correlation(ACCELERATOR_D, GYROSCOPE_C);
		features.append(corrcoef_6).append(",");
		double corrcoef_7 = Correlation(ACCELERATOR_AMP, GYROSCOPE_A);
		features.append(corrcoef_7).append(",");
		double corrcoef_8 = Correlation(ACCELERATOR_AMP, GYROSCOPE_B);
		features.append(corrcoef_8).append(",");
		double corrcoef_9 = Correlation(ACCELERATOR_AMP, GYROSCOPE_C);
		features.append(corrcoef_9).append(",");
		
		ArrayList<Double> magnetic_window = new ArrayList<>();
		ArrayList<String> list = new ArrayList<>();
	
		for(String d : window){
			if(list.size() == length_3){
				double squaredSum = SquaredSum(list);
				magnetic_window.add(squaredSum);
				list.clear();
				list.add(d);
			}else{
				list.add(d);
			}
			
		}
		if (list.size() == length_3) {
			double squaredSum = SquaredSum(list);
			magnetic_window.add(squaredSum);
		}
		int length_4 = magnetic_window.size();
		double[] MAGNETIC_AMP = new double[length_4];
		for(int i = 0; i < length_4; i++){
			MAGNETIC_AMP[i] = magnetic_window.get(i);
		}
		double magnetic_max = MathMax(MAGNETIC_AMP);
		features.append(magnetic_max).append(",");
		double magnetic_min = MathMin(MAGNETIC_AMP);
		features.append(magnetic_min).append(",");
		double magnetic_avg = ArithmeticMean(MAGNETIC_AMP);
		features.append(magnetic_avg).append(",");
		double magnetic_std = StandardDeviation(MAGNETIC_AMP);
		features.append(magnetic_std).append(",");
		double magnetic_q1 = MathQ1(MAGNETIC_AMP);
		features.append(magnetic_q1).append(",");
		double magnetic_q3 = MathQ3(MAGNETIC_AMP);
		features.append(magnetic_q3).append(",");
		double magnetic_q_diff = magnetic_q3 - magnetic_q1;
		features.append(magnetic_q_diff).append(",");
		double magnetic_range = MathRange(MAGNETIC_AMP);
		features.append(magnetic_range).append(",");
		double magnetic_energy = Energy(MAGNETIC_AMP);
		features.append(magnetic_energy).append(",");
		double magnetic_mae = MAE(MAGNETIC_AMP);
		features.append(magnetic_mae).append(",");
		
		if("SEDENTARY".equals(activity)){
			label = "1,0,0,0,0,0";
		}else if("INCAR".equals(activity)){
			label = "0,1,0,0,0,0";
		}else if("RUNNING".equals(activity)){
			label = "0,0,1,0,0,0";
		}else if("WALKING".equals(activity)){
			label = "0,0,0,1,0,0";
		}else if("UPSTAIRS".equals(activity)){
			label = "0,0,0,1,0,0";
		}else if("DOWNSTAIRS".equals(activity)){
			label = "0,0,0,1,0,0";
		}else if("INTRAIN".equals(activity)){
			label = "0,0,0,0,1,0";
		}else if("BIKING".equals(activity)){
			label = "0,0,0,0,0,1";
		}else{
			label = "0,0,0,0,0,0";
		}
		
		features.append(label);
//		if("UPSTAIRS".equals(activity)){
//			activity = "STAIRS";
//		}else if("DOWNSTAIRS".equals(activity)){
//			activity = "STAIRS";
//		}
//		features.append(activity);
		return features.toString();
	}
	
	public static String extractFeature(double[] window){
		StringBuffer sb = new StringBuffer();
		double average = ArithmeticMean(window);
		sb.append(average).append(",");
		double variance = StandardDeviation(window);
		sb.append(variance).append(",");
		double energy = Energy(window);
		sb.append(energy).append(",");
		double Q1 = MathQ1(window);
		sb.append(Q1).append(",");
		double Q3 = MathQ3(window);
		sb.append(Q3).append(",");
		double Q_diff = Q3 - Q1;
		sb.append(Q_diff).append(",");
		double mae = MAE(window);
		sb.append(mae).append(",");
		double max = MathMax(window);
		sb.append(max).append(",");
		double min = MathMin(window);
		sb.append(min).append(",");
		double range = MathRange(window);
		sb.append(range).append(",");
		ArrayList<Double> JF = JumpFall(window);
		for(double jf : JF){
			sb.append(jf).append(",");
		}
		String AF = AmpFreq(window);
		sb.append(AF).append(",");
		return sb.toString();
		
	}
	public static double[] calculateAngle(double[] north, double[] east) {
		int n = Math.min(north.length, east.length);
		double[] angles = new double[n];

		// Calculate the tan() of North and East
		for (int i = 0; i < n; i++) {
			angles[i] = Math.acos(north[i] * east[i] / (Math.sqrt(Math.pow(north[i], 2) + Math.pow(east[i], 2)) * Math.abs(east[i])));
		}
		return angles;
	}
	
	public static double MathMax(double[] w) {
		double maxw = -100000;
		for (int i = 0; i < w.length; i++) {
			maxw = Math.max(maxw, w[i]);
		}
		return maxw;
	}

	public static double MathMin(double[] w) {
		double minw = 100000;
		for (int i = 0; i < w.length; i++) {
			minw = Math.min(minw, w[i]);
		}
		return minw;
	}
	
	public static double MathRange(double[] w) {
		double maxw = -100000;
		double minw = 100000;
		for (int i = 0; i < w.length; i++) {
			maxw = Math.max(maxw, w[i]);
			minw = Math.min(minw, w[i]);
		}
		double range = maxw - minw;
		return range;
	}
	public static ArrayList<Double> JumpFall(double[] w) {
		double jump = 0, fall = 0;
		double jumpt = 0, fallt = 0;
		ArrayList<Double> list = new ArrayList<>();
		for (int i = 0; i < w.length - 1; i++) {
			if (w[i] < w[i + 1]) {
				jumpt += w[i + 1] - w[i];
				if (fallt > fall) {
					fall = fallt;
				}
				fallt = 0;
			}
			if (w[i] > w[i + 1]) {
				fallt += w[i] - w[i + 1];
				if (jumpt > jump) {
					jump = jumpt;
				}
				jumpt = 0;
			}
		}
		if (jumpt > jump) {
			jump = jumpt;
		}
		if (fallt > fall) {
			fall = fallt;
		}
		list.add(jump);
		list.add(fall);
		return list;
	}
	
	public static double SquaredSum (ArrayList<String> list) {
		int length = 16;
		double[] x_Axis = new double[length];
		double[] y_Axis = new double[length];
		double[] z_Axis = new double[length];
		int index = 0;
		for(String s : list) {
			String[] cols = s.split("\t");
			x_Axis[index] = Double.parseDouble(cols[9]);
			y_Axis[index] = Double.parseDouble(cols[10]);
			z_Axis[index] = Double.parseDouble(cols[11]);
			index++;
		}
		
		double result = Math.sqrt(Math.pow(ArithmeticMean(x_Axis), 2) + Math.pow(ArithmeticMean(y_Axis), 2) + Math.pow(ArithmeticMean(z_Axis), 2));
		return result;
	}
	
	
	public static String AmpFreq(double[] w) {
		StringBuffer result = new StringBuffer();
		int n = w.length;
		Complex[] x = new Complex[n];
		for (int j = 0; j < n; ++j) {
			x[j] = new Complex(w[j], 0);
		}

		Complex[] y = FFT.fft(x);

		double[] max3 = new double[3];
		int[] max3_int = new int[3];
		for (int j = 0; j < n / 2; ++j) {
			double amp = y[j].abs();
			int freq = j;
			for (int k = 0; k < 3; ++k) {
				if (amp > max3[k]) {
					double tmpd = amp;
					int tmpi = freq;
					amp = max3[k];
					freq = max3_int[k];
					max3[k] = (float) tmpd;
					max3_int[k] = tmpi;
				}
			}
		}
		if (max3_int[0] < max3_int[1]) {
			int tmp = max3_int[0];
			max3_int[0] = max3_int[1];
			max3_int[1] = tmp;
		}
		if (max3_int[1] < max3_int[2]) {
			int tmp = max3_int[1];
			max3_int[1] = max3_int[2];
			max3_int[2] = tmp;
		}
		if (max3_int[0] < max3_int[1]) {
			int tmp = max3_int[0];
			max3_int[0] = max3_int[1];
			max3_int[1] = tmp;
		}

		double amp1w = max3[0];
		double amp2w = max3[1];
		double amp3w = max3[2];
		int freq1w = max3_int[0];
		int freq2w = max3_int[1];
		int freq3w = max3_int[2];
		result.append(amp1w).append(",").append(amp2w).append(",").append(amp3w).append(",").append(freq1w).append(",").append(freq2w).append(",").append(freq3w);
		return result.toString();
	}
	
	public static double ArithmeticMean(double[] windows) {
		double sum = 0;
		int n = windows.length;
		for (int i = 0; i < n; i++) {
			sum += windows[i];
		}
		return sum / n;

	}
	
	public static double StandardDeviation(double[] windows) {
		double sum = 0;
		double mean = 0;
		double num = 0;
		for (int i = 0; i < windows.length; i++) {
			num += windows[i];
		}
		mean = num / windows.length;
		for (int i = 0; i < windows.length; i++) {
			sum += Math.pow(windows[i] - mean, 2);
		}
		double stdev = Math.sqrt(sum / windows.length);
		return stdev;
	}
	
	public static double Energy(double[] windows) {
		double sum = 0;
		for (int i = 0; i < windows.length; i++) {
			sum += Math.pow(windows[i], 2);
		}
		double energy = sum / windows.length;
		return energy;
	}

	public static double MathQ1(double[] windows){
		ArrayList<Double> list = new ArrayList<>();
		for(double d : windows){
			list.add(d);
		}
		Collections.sort(list);
		int N = windows.length;
		if(N % 4 == 0){
			return (list.get(N/4-1) + list.get(N/4)) * 1.0 / 2;
		}else{
			int index = N / 4;
			return list.get(index);
		}
	}
	
	public static double MathQ3(double[] windows){
		ArrayList<Double> list = new ArrayList<>();
		for(double d : windows){
			list.add(d);
		}
		Collections.sort(list);
//		for(double d:list){
//			System.out.print(d + " ");
//		}
		int N = windows.length;
		if(N % 4 == 0){
			return (list.get(3*N/4-1) + list.get(3*N/4)) * 1.0 / 2;
		}else{
			int index = 3*N / 4;
			return list.get(index);
		}
	}
	public static double MAE(double[] windows) {
		double sum = 0;
		double mean = 0;
		double num = 0;
		for (int i = 0; i < windows.length; i++) {
			num += windows[i];
		}
		mean = num / windows.length;
		for (int i = 0; i < windows.length; i++) {
			sum += Math.abs(windows[i] - mean);
		}
		double mae = sum / windows.length;
		return mae;
	}
	public static double Covariation(double[] win1, double[] win2) {
		double sum = 0;
		int n = Math.min(win1.length, win2.length);
		for (int i = 0; i < n; i++) {
			sum += win1[i] * win2[i];
		}
		double cov = (sum / n) - (ArithmeticMean(win1) * ArithmeticMean(win2));
		return cov;
	}

	public static double Correlation(double[] win1, double[] win2) {
		double coeffcient = Covariation(win1, win2) / Math.sqrt(Covariation(win1, win1) * Covariation(win2, win2));
		return coeffcient;
	}
	
	public static double[] MedianFilter(double[] windows, int length) {
		int N = windows.length;
		double[] samples = new double[N];
		if (windows == null || windows.length < length) {
			return windows;
		}else{
			for(int j = 0; j < (length-1)/2; j++){
				samples[j] = windows[j];
			}
			for(int i = (length-1)/2; i < N-(length-1)/2; i++){
				ArrayList<Double> list = new ArrayList<>();
				int left = i;
				int right = i;
				list.add(windows[i]);
				for(int l = 0; l < (length-1)/2; l++){
					list.add(windows[--left]);
					list.add(windows[++right]);
				}
				Collections.sort(list);
				samples[i] = list.get((length-1)/2);
			}
			for(int k = N-(length-1)/2; k < N; k++){
				samples[k] = windows[k];
			}
		}
		return samples;
	}
	
	public static void generateDataSet(int num, String filepath){
		int NUM = num;
		int window_length = 200;
		File file = new File(filepath);
		BufferedReader reader = null;
		ArrayList<String> dataSet = new ArrayList<>();
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			// 一次读入一行，直到读入null为文件结束
			while ((line = reader.readLine()) != null) {
				dataSet.add(line);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		ArrayList<String> features = new ArrayList<>();
		ArrayList<String> window = new ArrayList<>();
		System.out.println(dataSet.size());
		for(String d : dataSet){
			if(window.size() == window_length){
				String fString = calculateFeatures(window);
				features.add(fString);
//				for(String s : window){
//					System.out.println(s);
//				}
//				System.out.println("------------------------------");
				window.clear();
				window.add(d);
			}else{
				window.add(d);
			}
			
		}
		if(window.size() == window_length){
			String fString = calculateFeatures(window);
			features.add(fString);
		}
//		Collections.shuffle(features);
		System.out.println(features.size());
		try (FileOutputStream fos = new FileOutputStream(new File("C:/Users/ycg3/Desktop/test/train_feature"), true);
				OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
				BufferedWriter bw = new BufferedWriter(osw);) {
			int line = 0;
			for(int k = 0; k < NUM; k++){
				bw.write(features.get(k));
				bw.newLine();
			}
//			for (String f : features) {
//				bw.write(f);
//				bw.newLine();
//				line++;
//			}
			
		} catch (Exception e) {
			System.out.print("write data error!");
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args) throws IOException {
		for(int fold = 0; fold < 1; fold++){
			System.out.println("Fold " + fold + "---------------------------");
		
			generateDataSet(2320, "C:/Users/ycg3/Desktop/dataset/incar_1");
			generateDataSet(500, "C:/Users/ycg3/Desktop/dataset/incar_2");
			generateDataSet(750, "C:/Users/ycg3/Desktop/dataset/sedentary_1");
			generateDataSet(790, "C:/Users/ycg3/Desktop/dataset/sedentary_2");
			generateDataSet(410, "C:/Users/ycg3/Desktop/dataset/sedentary_3");
			generateDataSet(1930, "C:/Users/ycg3/Desktop/dataset/running_1");
			generateDataSet(2160, "C:/Users/ycg3/Desktop/dataset/walking_2017-03-29");
			generateDataSet(1990, "C:/Users/ycg3/Desktop/dataset/intrain");
			generateDataSet(2530, "C:/Users/ycg3/Desktop/dataset/biking_2017-03-29");
	    
			
			
//			generateDataSet(1132, "C:/Users/ycg3/Desktop/part-r-00000");
			
			File file = new File("C:/Users/ycg3/Desktop/test/train_feature");
			BufferedReader reader = null;
			ArrayList<String> data = new ArrayList<String>();
			String line = null;
			reader = new BufferedReader(new FileReader(file));

			// 一次读入一行，直到读入null为文件结束
			while ((line = reader.readLine()) != null) {
				data.add(line);
			}
			reader.close();
//			Collections.shuffle(data);
			try (FileOutputStream fos = new FileOutputStream(new File("D:/Context_Awareness/Algorithm/RandomForest/data/fold_" + fold + "/activity-train.arff"), false);
					OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
					BufferedWriter bw = new BufferedWriter(osw);) {
				bw.write("@relation MultiLabelData"); bw.newLine();
				bw.newLine();
				for(int i = 1; i <= 127; i++){
					String attrInfo = "@attribute Att" + i + " " + "numeric";
					bw.write(attrInfo); bw.newLine();
				}
				for(int i = 1; i <= 6; i++){
					String classInfo = "@attribute Class" + i + " " + "{0,1}";
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


}
