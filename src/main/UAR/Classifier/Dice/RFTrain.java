package com.lenovo.ca.UAR.Classifier.Dice;

import java.io.FileNotFoundException;
import java.util.Arrays;

import com.lenovo.ca.UAR.Utils.Utils;

import dice.tree.structure.Node;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import dice.data.Instances;
import dice.data.io.ArffReader;
import dice.tree.builder.TreeBuilder;

public class RFTrain {
	public int attrSize = 133;
	private int labelNum = 6;
	private int maxS = 5;
	private int treeNum = 100;
	private int maxTreeDepth = 15;
	private Node[] trees = new Node[treeNum];
	
	
	public String doTraining(int fold){
		String trainFilePath = "./data/fold_" + fold + "/activity-train.arff";
		ArffReader reader = new ArffReader();
	    reader.setFilePath(trainFilePath);
	    reader.setAttrSize(attrSize);
	    Instances trainInstances = reader.getInstances();
	    TreeBuilder treeBuilder = new TreeBuilder(0, TreeBuilder.CBR_RDT);
        treeBuilder.setInstances(trainInstances);
        treeBuilder.setMaxDeep(maxTreeDepth);
        treeBuilder.setMaxS(maxS);
        treeBuilder.setClsSize(labelNum);
        treeBuilder.init();
        trees = treeBuilder.buildTrees(treeNum);
        treeBuilder.clear();
        String newModel = getModelAsString(trainInstances.getAttributes());
        if(newModel != null && !newModel.isEmpty()){
            String savedPath = Utils.save(newModel, "classificator.json", fold);
            return savedPath;
        }else{
            return null;
        }
	}

    private String getModelAsString(int[] attributes) {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("attrSize", String.valueOf(this.attrSize));
            jsonObj.put("labelNum", String.valueOf(this.labelNum));
            jsonObj.put("maxS", String.valueOf(this.maxS));
            jsonObj.put("treeNum", String.valueOf(this.treeNum));
            jsonObj.put("maxTreeDepth", String.valueOf(this.maxTreeDepth));
            jsonObj.put("attributes", Arrays.toString(attributes).replace("[", "").replace("]", ""));
            jsonObj.put("trees", getTreeJsonArray());
            return jsonObj.toString();

        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private JSONArray getTreeJsonArray() {
        try {
            JSONArray jsonArray = new JSONArray();
            JSONObject jo;
            for (int i = 0; i < trees.length; i++) {
                jo = new JSONObject();
                jo.put("tree", Utils.toString(trees[i]));
                jsonArray.add(jo);
            }
            return jsonArray;

        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
    
	public static void main(String[] args) throws FileNotFoundException {
		
	}


}
