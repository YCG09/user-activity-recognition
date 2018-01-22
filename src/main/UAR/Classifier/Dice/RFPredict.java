package com.lenovo.ca.UAR.Classifier.Dice;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;

import com.lenovo.ca.UAR.Utils.Utils;

import dice.tree.structure.Node;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import dice.data.Instance;
import dice.data.Instances;
import dice.tree.model.CBRRDTModel;

public class RFPredict {
    private int attrSize = 0;
    private int labelNum = 0;
    private int maxS = 0;
    private Node[] trees = null;
    private CBRRDTModel model;
    private int[] attributes = null;

    public RFPredict(String trainedTrees) {
        parseJsonObj(trainedTrees);
        model = new CBRRDTModel();
        model.init(trees, attributes, maxS);
    }
    private void parseJsonObj(final String jsonStr) {
        if (jsonStr == null || jsonStr.trim().isEmpty())
            return;
        try {
            JSONObject jsonObj = JSONObject.fromObject(jsonStr);
            this.attrSize = jsonObj.getInt("attrSize");
            this.labelNum = jsonObj.getInt("labelNum");
            this.maxS = jsonObj.getInt("maxS");
            int treeNum = jsonObj.getInt("treeNum");
            this.trees = new Node[treeNum];
            JSONArray treesArray = jsonObj.getJSONArray("trees");
            parseTreesJsonArray(treesArray);
            String attrStr = jsonObj.getString("attributes");
            parseAttributes(attrStr);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    private void parseTreesJsonArray(final JSONArray ja) {
        if (ja == null)
            return;
        JSONObject jo;
        for (int i = 0; i < ja.size(); i++) {
            try {
                jo = ja.getJSONObject(i);
                trees[i] = (Node) Utils.fromString(jo.getString("tree"));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
    
    private void parseAttributes(final String attributes){
        if(attributes == null || attributes.isEmpty()){
            return;
        }
        String[] attrs = attributes.split(",");
        int attrLen = attrs.length;
        this.attributes = new int[attrLen];
        for(int index = 0; index < attrLen; ++index){
            this.attributes[index] = Integer.parseInt(attrs[index].trim());
        }
    }    
    public double Entropy(double[] w) {
		double sum = 0;
		double entropy = 0;
		for (int i = 0; i < w.length; i++) {
			sum += w[i];
		}
		for (int i = 0; i < w.length; i++) {
			double c = w[i] / sum;
			if(c != 0.0){
				entropy += c * (Math.log(c) / Math.log(2));
			}
			
		}
		return entropy;
	}
    public int[][] statistics(Instances testInstances, int fold) {
    	
        Iterator<Instance> it = testInstances.iterator();
        int count = 0;
		int count_SEDENTARY = 0;
		int count_INCAR = 0;
		int count_RUNNING = 0;
		int count_WALKING = 0;
		int count_INTRAIN = 0;
		int count_BIKING = 0;
		
		int total_SEDENTARY = 0;
		int total_INCAR = 0;
		int total_RUNNING = 0;
		int total_WALKING = 0;
		int total_INTRAIN = 0;
		int total_BIKING = 0;
		
		int[][] confusionMatrix = new int[labelNum][labelNum];
		ArrayList<String> result = new ArrayList<>();
		while(it.hasNext()){
			Instance inst = it.next();
			CBRRDTModel.Prediction pred = model.estimate(inst);
			int[] T = new int[labelNum];
			double[] P = new double[labelNum];
			for(int i = 0; i < labelNum; i++){
				T[i] = (int)inst.getValue(attrSize-labelNum+i);
			}
			for(int i = 0; i < labelNum; i++){
				if(T[i] == 1){
					if(i == 0){
						total_SEDENTARY++;
					}else if(i == 1){
						total_INCAR++;
					}else if(i == 2){
						total_RUNNING++;
					}else if(i == 3){
						total_WALKING++;
					}else if(i == 4){
						total_INTRAIN++;
					}else if(i == 5){
						total_BIKING++;
					}
				}else{
					continue;
				}
			}
			for(int i = 0; i < labelNum; i++){
				Double t = pred.dist.get(i);
				if(t == null){
					P[i] = 0.0;
				}else{
					P[i] = t;
				}
			}
		
			StringBuffer sb = new StringBuffer();
			sb.append("[");
			for(int i = 0; i < labelNum; i++){
				sb.append(P[i]).append(",");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("]").append("\t");
			
//			sb.append(Entropy(P)).append("\t");
			
//			StringBuffer sb = new StringBuffer();
//			for(int i = 0; i < labelNum; i++){
//				sb.append(P[i]).append("\t");
//			}
//			sb.deleteCharAt(sb.length() - 1);
		
//			sb.append("[");
//			for(int i = 0; i < labelNum; i++){
//				sb.append(T[i]).append(",");
//			}
//			sb.deleteCharAt(sb.length() - 1);
//			sb.append("]");
			
			double max;
			max = P[0];
			for (int j = 1; j < labelNum; j++) {
				if (max < P[j]) {
					max = P[j];
				}
			}
			for (int j = 0; j < labelNum; j++) {

				if (max == P[j] && T[j] == 1) {
					count++;
					if(j == 0){
						count_SEDENTARY++;
					}else if(j == 1){
						count_INCAR++;
					}else if(j == 2){
						count_RUNNING++;
					}else if(j == 3){
						count_WALKING++;
					}else if(j == 4){
						count_INTRAIN++;
					}else if(j == 5){
						count_BIKING++;
					}
				}

			}
			int row = 0;
			int colomn = 0;
			for (int k = 0; k < labelNum; k++) {
				if(max == P[k]){
					colomn = k;
				}
				if(T[k] == 1){
					row = k;
				}
			}
			confusionMatrix[row][colomn] += 1;
		
			if(colomn == 0 && P[colomn] >= 0.4){
//				sb.append("SEDENTARY");
				sb.append("0");
			}else if(colomn == 1 && P[colomn] >= 0.4){
//				sb.append("INCAR");
				sb.append("1");
			}else if(colomn == 2 && P[colomn] >= 0.5){
//				sb.append("RUNNING");
				sb.append("2");
			}else if(colomn == 3 && P[colomn] >= 0.6){
//				sb.append("WALKING");
				sb.append("3");
			}else if(colomn == 4 && P[colomn] >= 0.5){
//				sb.append("INTRAIN");
				sb.append("4");
			}else if(colomn == 5 && P[colomn] >= 0.4){
//				sb.append("BIKING");
				sb.append("5");
			}else{
//				sb.append("UNKNOWN");
				sb.append("-1");
			}
		
			result.add(sb.toString());
			
		}
		try (FileOutputStream fos = new FileOutputStream(new File("C:/Users/haoyu5/Desktop/result_predict.txt"), true);
				OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
				BufferedWriter bw = new BufferedWriter(osw);) {
			for (String f : result) {
				bw.write(f);
				bw.newLine();
			}
		} catch (Exception e) {
			System.out.print("write data error!");
			e.printStackTrace();
		}
		System.out.println("Fold " + fold + "-----------------");
		System.out.println("accurate:" + count * 1.0 / testInstances.size() * 100 + "%");
		System.out.println("SEDENTARY is: " + count_SEDENTARY + "---" + total_SEDENTARY + "---" + count_SEDENTARY * 1.0 / total_SEDENTARY * 100 + "%");
		System.out.println("INCAR is: " + count_INCAR + "---" + total_INCAR + "---" + count_INCAR * 1.0 / total_INCAR * 100 + "%");
		System.out.println("RUNNING is: " + count_RUNNING + "---" + total_RUNNING + "---" + count_RUNNING * 1.0 / total_RUNNING * 100 + "%");
		System.out.println("WALKING is: " + count_WALKING + "---" + total_WALKING + "---" + count_WALKING * 1.0 / total_WALKING * 100 + "%");
		System.out.println("INTRAIN is: " + count_INTRAIN + "---" + total_INTRAIN + "---" + count_INTRAIN * 1.0 / total_INTRAIN * 100 + "%");
		System.out.println("BIKING is: " + count_BIKING + "---" + total_BIKING + "---" + count_BIKING * 1.0 / total_BIKING * 100 + "%");

		System.out.println("Confusion Matrix is: ");
		System.out.format("%-8s", "SEDENTARY");
		System.out.format("%-8s", "INCAR");
		System.out.format("%-8s", "RUNNING");
		System.out.format("%-8s", "WALKING");
		System.out.format("%-8s", "INTRAIN");
		System.out.format("%-8s", "BIKING");
		System.out.println();
		
		for(int i = 0; i < labelNum; i++){
			for(int j = 0; j < labelNum; j++){
				System.out.format("%-8d",confusionMatrix[i][j]);
			}
			System.out.println();
		}
		model.clear();
		return confusionMatrix;
    }
	public static void main(String[] args) {

	}


}
