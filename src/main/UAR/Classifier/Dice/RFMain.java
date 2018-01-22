package com.lenovo.ca.UAR.Classifier.Dice;

import java.io.FileNotFoundException;

import com.lenovo.ca.UAR.Utils.Utils;

import dice.data.Instances;
import dice.data.io.ArffReader;

public class RFMain {

	public static void main(String[] args) throws FileNotFoundException {
		//train and save the model
		int labelNum = 6;
		int[][] confusionMatrix = new int[labelNum][labelNum];
		for(int fold = 0; fold < 10; fold++){
			//train and save the model
			RFTrain trainSample = new RFTrain();
			trainSample.doTraining(fold);
			//load model and predict
			String trainedTrees = Utils.loadModel("classificator.json", fold);
			RFPredict testSample = new RFPredict(trainedTrees);
//			//count accuracy
			String testFiles = "./data/fold_" + fold + "/activity-test.arff";
			ArffReader rd = new ArffReader();
			rd.setFilePath(testFiles);
			rd.setAttrSize(133);
			Instances testInstances =  rd.getInstances();
			int[][] matrix = testSample.statistics((Instances) testInstances, fold);
			for(int i = 0; i < labelNum; i++){
				for(int j = 0; j < labelNum; j++){
					confusionMatrix[i][j] += matrix[i][j];
				}
			}
			
		}
		
		System.out.println("Confusion Matrix is: -----------------");
		
		System.out.format("%-10s", "SEDENTARY");
		System.out.format("%-10s", "INCAR");
		System.out.format("%-10s", "RUNNING");
		System.out.format("%-10s", "WALKING");
		System.out.format("%-10s", "INTRAIN");
		System.out.format("%-10s", "BIKING");
		System.out.println();
		
		for(int i = 0; i < labelNum; i++){
			for(int j = 0; j < labelNum; j++){
				System.out.format("%-10d",confusionMatrix[i][j]);
			}
			System.out.println();
		}
		int totalInstance = 0;
		int correctInstance = 0;
		
		int correctSEDENTARY = 0;
		int totalSEDENTARY = 0;
		int predictSEDENTARY = 0;
		
		int correctINCAR = 0;
		int totalINCAR = 0;
		int predictINCAR = 0;
		
		int correctRUNNING = 0;
		int totalRUNNING = 0;
		int predictRUNNING = 0;
		
		int correctWALKING = 0;
		int totalWALKING = 0;
		int predictWALKING = 0;
		
		int correctINTRAIN = 0;
		int totalINTRAIN = 0;
		int predictINTRAIN = 0;
		
		int correctBIKING = 0;
		int totalBIKING = 0;
		int predictBIKING = 0;
	
		for(int i = 0; i < labelNum; i++){
			for(int j = 0; j < labelNum; j++){
				totalInstance += confusionMatrix[i][j];
				if(i == j){
					correctInstance += confusionMatrix[i][j];
				}
				if(i == 0){
					totalSEDENTARY += confusionMatrix[i][j];
				}
				if(i == 1){
					totalINCAR += confusionMatrix[i][j];
				}
				if(i == 2){
					totalRUNNING += confusionMatrix[i][j];
				}
				if(i == 3){
					totalWALKING += confusionMatrix[i][j];
				}
				if(i == 4){
					totalINTRAIN += confusionMatrix[i][j];
				}
				if(i == 5){
					totalBIKING += confusionMatrix[i][j];
				}
				
				if(j == 0){
					predictSEDENTARY += confusionMatrix[i][j];
				}
				if(j == 1){
					predictINCAR += confusionMatrix[i][j];
				}
				if(j == 2){
					predictRUNNING += confusionMatrix[i][j];
				}
				if(j == 3){
					predictWALKING += confusionMatrix[i][j];
				}
				if(j == 4){
					predictINTRAIN += confusionMatrix[i][j];
				}
				if(j == 5){
					predictBIKING += confusionMatrix[i][j];
				}

				
				if(i == 0 && i == j){
					correctSEDENTARY = confusionMatrix[i][j];
				}
				if(i == 1 && i == j){
					correctINCAR = confusionMatrix[i][j];
				}
				if(i == 2 && i == j){
					correctRUNNING = confusionMatrix[i][j];
				}
				if(i == 3 && i == j){
					correctWALKING = confusionMatrix[i][j];
				}
				if(i == 4 && i == j){
					correctINTRAIN = confusionMatrix[i][j];
				}
				if(i == 5 && i == j){
					correctBIKING = confusionMatrix[i][j];
				}
			}
		}
		
		System.out.println("Total Accuracy:" + correctInstance * 1.0 / totalInstance * 100 + "%");
		System.out.println("Accuracy of Each Class:");
		System.out.println("SEDENTARY is: " + correctSEDENTARY + "---" + totalSEDENTARY + "---" + correctSEDENTARY * 1.0 / totalSEDENTARY * 100 + "%");
		System.out.println("INCAR is: " + correctINCAR + "---" + totalINCAR + "---" + correctINCAR * 1.0 / totalINCAR * 100 + "%");
		System.out.println("RUNNING is: " + correctRUNNING + "---" + totalRUNNING + "---" + correctRUNNING * 1.0 / totalRUNNING * 100 + "%");
		System.out.println("WALKING is: " + correctWALKING + "---" + totalWALKING + "---" + correctWALKING * 1.0 / totalWALKING * 100 + "%");
		System.out.println("INTRAIN is: " + correctINTRAIN + "---" + totalINTRAIN + "---" + correctINTRAIN * 1.0 / totalINTRAIN * 100 + "%");
		System.out.println("BIKING is: " + correctBIKING + "---" + totalBIKING + "---" + correctBIKING * 1.0 / totalBIKING * 100 + "%");

		
		System.out.println("Precision of Each Class:");
		System.out.println("SEDENTARY is: " + correctSEDENTARY + "---" + predictSEDENTARY + "---" + correctSEDENTARY * 1.0 / predictSEDENTARY * 100 + "%");
		System.out.println("INCAR is: " + correctINCAR + "---" + predictINCAR + "---" + correctINCAR * 1.0 / predictINCAR * 100 + "%");
		System.out.println("RUNNING is: " + correctRUNNING + "---" + predictRUNNING + "---" + correctRUNNING * 1.0 / predictRUNNING * 100 + "%");
		System.out.println("WALKING is: " + correctWALKING + "---" + predictWALKING + "---" + correctWALKING * 1.0 / predictWALKING * 100 + "%");
		System.out.println("INTRAIN is: " + correctINTRAIN + "---" + predictINTRAIN + "---" + correctINTRAIN * 1.0 / predictINTRAIN * 100 + "%");
		System.out.println("BIKING is: " + correctBIKING + "---" + predictBIKING + "---" + correctBIKING * 1.0 / predictBIKING * 100 + "%");


	}

}
