package com.lenovo.ca.UAR.Classifier.Weka;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class WekaClassifier {
	public String getClassInfo() {
		return classInfo;
	}
	public void setClassInfo(String classInfo) {
		this.classInfo = classInfo;
	}

	private Instances dataSet;
	private Instances formatFile;
//	private Classifier classifier;
	private RandomForest classifier;
	private int sizeOfDataset;
	private int sizeOfAttribute;
	private String classInfo;
	
	public void loadTrainSet(String inputPath) throws IOException {
		dataSet = new Instances(new BufferedReader(new FileReader(inputPath)));
		setSizeOfDataset(dataSet.numInstances());
		setSizeOfAttribute(dataSet.numAttributes());
		dataSet.setClassIndex(this.sizeOfAttribute - 1);
	}
	public void buildClassifierModel(String option) throws Exception {
		this.classifier = new RandomForest();
		classifier.setOptions(weka.core.Utils.splitOptions(option));
		this.classifier.buildClassifier(dataSet);
	}
	public void buildClassifierModel() throws Exception {
		this.classifier = new RandomForest();
		this.classifier.buildClassifier(dataSet);
	}
	public void saveModelFile(String savePath) throws Exception {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(savePath + "RandomForest.model"));
		oos.writeObject(classifier);
		oos.flush();
		oos.close();
		
		try (FileOutputStream fos = new FileOutputStream(new File(savePath + "formatFile.config"), false);
				OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
				BufferedWriter bw = new BufferedWriter(osw);) {
			bw.write("@relation MultiLabelData"); bw.newLine();
			bw.newLine();
			for(int i = 1; i <= sizeOfAttribute-1; i++){
				String attrInfo = "@attribute Att" + i + " " + "numeric";
				bw.write(attrInfo); bw.newLine();
			}
			bw.write(classInfo); bw.newLine();
			bw.newLine();
			bw.write("@data"); 
//			bw.newLine();
		} catch (Exception e) {
			System.out.print("write data error!");
			e.printStackTrace();
		}
	}
	
	public void loadModelFile(String loadPath) throws Exception {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(loadPath + "RandomForest.model"));
		classifier = (RandomForest) ois.readObject();
		ois.close();
		
		formatFile = new Instances(new BufferedReader(new FileReader(loadPath + "formatFile.config")));
		formatFile.setClassIndex(formatFile.numAttributes() - 1);
	}
	public String classifyInstance(double[] testSample) throws Exception {
		Instance ins = new DenseInstance(sizeOfAttribute);
		ins.setDataset(formatFile);
		for (int i = 0; i < ins.numAttributes() - 1; i++) {
			ins.setValue(i, testSample[i]);
		}
        double[] distributions = classifier.distributionForInstance(ins);
		
		double result = classifier.classifyInstance(ins);
//		System.out.println(result);
		
		Attribute attr = ins.attribute(ins.classIndex());
		int classIndex = (int) result;
		String resultClass = attr.value(classIndex);
		return resultClass;
	}
	
	/**
	 * @relation MultiLabelData
	 * @attribute Atti(1<=i<=N) numeric
	 * @attribute Class {SEDENTARY,INCAR,RUNNING,WALKING,INTRAIN,BIKING}
	 * 
	 * @data
	 * <f1,f2,...>,<?>
	 * */
	public ArrayList<String> predictFromFile(String filePath) throws Exception {
		Instances testSet = new Instances(new BufferedReader(new FileReader(filePath)));
		testSet.setClassIndex(testSet.numAttributes() - 1);
		ArrayList<String> list = new ArrayList<>();
		for (int i = 0; i < testSet.numInstances(); i++) {
			double result = classifier.classifyInstance(testSet.instance(i));

			Attribute attr = testSet.attribute(testSet.classIndex());
			int classIndex = (int) result;
			String label = attr.value(classIndex);
			list.add(label);
//			double[] d = classifier.distributionForInstance(testSet.instance(i));
		}
		return list;
	}
	
	/**
	 * @relation MultiLabelData
	 * @attribute Atti(1<=i<=N) numeric
	 * @attribute Class {SEDENTARY,INCAR,RUNNING,WALKING,INTRAIN,BIKING}
	 * 
	 * @data
	 * <f1,f2,...>,<label>
	 * */
	public double evaluateFromFile(String filePath) throws Exception {
		Instances testSet = new Instances(new BufferedReader(new FileReader(filePath)));
		testSet.setClassIndex(testSet.numAttributes() - 1);
		Evaluation evaluation = new Evaluation(dataSet);
		evaluation.evaluateModel(classifier, testSet);
		FastVector predictions = new FastVector();
		predictions.appendElements(evaluation.predictions());
        System.out.println(evaluation.toMatrixString());
        double accuracy = calculateAccuracy(predictions);
		return accuracy;
	}
    public static double calculateAccuracy(FastVector predictions) {
        double correct = 0;

        for (int i = 0; i < predictions.size(); i++) {
            NominalPrediction np = (NominalPrediction) predictions.elementAt(i);
            if (np.predicted() == np.actual()) {
                correct++;
            }
        }

        return 100 * correct / predictions.size();
    }
	public int getSizeOfAttribute() {
		return sizeOfAttribute;
	}

	public void setSizeOfAttribute(int sizeOfAttribute) {
		this.sizeOfAttribute = sizeOfAttribute;
	}
	public void setSizeOfDataset(int sizeOfDataset) {
		this.sizeOfDataset = sizeOfDataset;
	}

	public int getSizeOfDataset() {
		return sizeOfDataset;
	}

	public static void main(String[] args) throws Exception {
		WekaClassifier clf = new WekaClassifier();
		
		// declare class info
		clf.setClassInfo("@attribute Class {ambient,music,voice}");
		clf.loadTrainSet("C:/Users/haoyu5/Desktop/abc.arff");
//		clf.buildClassifierModel();
		clf.buildClassifierModel("-P 100 -I 100 -num-slots 1 -K 0 -M 1.0 -V 0.001 -S 1");
		clf.saveModelFile("C:/Users/haoyu5/Desktop/");
		clf.loadModelFile("C:/Users/haoyu5/Desktop/");
		//Test case one
//		double accuracy = clf.evaluateFromFile("C:/Users/haoyu5/Desktop/activity-test.arff");
//		System.out.println("Accuracy is: " + accuracy);
		//Test case two--label unknown
//		ArrayList<String> predict = clf.predictFromFile("C:/Users/haoyu5/Desktop/activity-test.arff");
//		for(String s: predict){
//			System.out.println(s);
//		}
		//Test case three
//		double[] sample = {-0.012227557056250006,0.23915558590635436,0.05734490742177547,-0.14511712,0.11220901,0.25732613,0.161397695766797,0.5105046,-0.8816998,1.3922044,0.99390881,1.2973052,4.47125244140625,3.6963553428649902,3.0423877239227295,14,7,6,0.05808751945807448,0.2859662955085583,0.08515088208368024,-0.1817595559504362,0.31874262249812174,0.5005021784485579,0.2556361678380498,0.5000024503500998,-0.43565086167354705,0.9356533120236469,0.9051703271330843,0.8713017233470941,4.257875919342041,4.170101642608643,3.8130598068237305,14,11,1,1.1891915955012577,0.9840402989275314,2.3825119607242122,0.42459305739839875,2.0464788578285362,1.6218858004301375,0.8458653585251529,3.086522350441811,0.014969833430397862,3.0715525170114133,2.9197504885296928,3.03853068178067,76.10826110839844,22.88189697265625,15.00365161895752,4,1,0,0.016547679521875004,0.05681169914575946,0.0035013948573869667,-0.011665344,0.044258117,0.055923461,0.04192434255146484,0.13613892,-0.1235199,0.25965882,0.2503357,0.25965882,1.059051513671875,0.9874289035797119,0.9533592462539673,16,14,0,0.00979709652296875,0.09992631230809412,0.010081250991775126,-0.046218872,0.06764984,0.11386871200000001,0.07623377458425293,0.17936707,-0.2675171,0.44688417,0.42211915,0.38963319,1.784457802772522,1.4965468645095825,1.4423859119415283,16,6,1,-0.003408908843750001,0.0436959419708951,0.001920956004228828,-0.021575928,0.019706726,0.041282654,0.031729415182617175,0.08468628,-0.13209534,0.21678162,0.193618778,0.174713138,0.7679580450057983,0.747924268245697,0.5812710523605347,16,15,14,0.6523225742497721,0.16263754969924576,0.2141789265037594,-0.275216675218946,-0.3721902627754916,-0.2615245572576151,-0.12802750310250513,-0.11442062321981525,-0.15158344232032395,64.10811825590069,61.312299118281416,62.910802877320286,0.9265156320448713,62.01348787919286,63.60450596879055,1.5910180895976893,2.795819137619276,3958.627549885472,0.7913166294820323};
//		String label = clf.classifyInstance(sample);
//		System.out.println(label);
	}

}
