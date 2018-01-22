package com.lenovo.ca.UAR.Classifier.Weka;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.Attribute;

public class RandomForestWeka {

	public static void main(String[] args) throws Exception {
		int labelNum = 6;
		Classifier cls = new RandomForest();
		//train
		Instances train = new Instances(new BufferedReader(new FileReader("C:/Users/haoyu5/Desktop/activity-train.arff")));
		train.setClassIndex(train.numAttributes() - 1);
		cls.buildClassifier(train);
//		serialize and save model
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("C:/Users/haoyu5/Desktop/RandomForest.model"));
		oos.writeObject(cls);
		oos.flush();
		oos.close();
		//load model and predict
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream("C:/Users/haoyu5/Desktop/RandomForest.model"));
		RandomForest rf = (RandomForest) ois.readObject();
		ois.close();
		//test: set corresponding label ?
		Instances test = new Instances(new BufferedReader(new FileReader("C:/Users/haoyu5/Desktop/activity-test.arff")));
		test.setClassIndex(test.numAttributes() - 1);
		ArrayList<String> list = new ArrayList<>();
		for (int i = 0; i < test.numInstances(); i++) {
			double result = rf.classifyInstance(test.instance(i));

			Attribute attr = test.attribute(test.classIndex());
			int classIndex = (int) result;
			String label = attr.value(classIndex);

			double[] d = rf.distributionForInstance(test.instance(i));
			StringBuffer sb = new StringBuffer();
			sb.append("[");
			for(int j = 0; j < labelNum; j++){
				sb.append(d[j]).append(",");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("]").append("\t");
			sb.append(label);
			list.add(sb.toString());
		}
		try (FileOutputStream fos = new FileOutputStream(new File("C:/Users/haoyu5/Desktop/result_predict.txt"), false);
				OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
				BufferedWriter bw = new BufferedWriter(osw);) {
			for (String f : list) {
				bw.write(f);
				bw.newLine();
			}
		} catch (Exception e) {
			System.out.print("write data error!");
			e.printStackTrace();
		}
	}

}
