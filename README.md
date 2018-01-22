# User Activity Recognition
## About
This project is an User Activity Recognition basing on Mobile Devices, we do some analysis experiment using Python, and develop the pipeline using Java to support Android system.

## Description
* Folder `Preprocess` corresponds to transforming JSON format data to a tab-separated String
* Folder `ExtractFeature` corresponds to extracting features and saving results to an arff file
* Folder `Classifie`r corresponds to implementing classifier, which include traning, testing and serializing model into file
* Folder `ResultValidate` corresponds to smoothing the prediction result to generating trace of activity for each user
* The other folders corresponds to some auxiliary tools

## Tools
* `feature_selection_UAR.ipynb` contains the process of feature extraction
* `curve_visualize.ipynb` contains visualization form of dataset

## DataSet
* `dataset` contains the whole training dataset
* `data_for_analysis` contains partial dataset for analysis
