# user-activity-recognition
User Activity Recognition basing on Mobile Devices

We do some analysis experiment using Python
We develop the pipeline using Java to support Android system

feature_selection_UAR.ipynb contains the process of feature extraction
curve_visualize.ipynb contains visualization form of dataset

dataset contains the whole training dataset
data_for_analysis contains partial dataset for analysis

folder Preprocess corresponds to transforming JSON format data to a tab-separated String
folder ExtractFeature corresponds to extracting features and saving results to an arff file
folder Classifier corresponds to implementing classifier, which include traning, testing and serializing model into file
folder ResultValidate corresponds to smoothing the prediction result to generating trace of activity for each user
the other folders corresponds to  some auxiliary tools

The whole algorithm part was implemented by myself, but the Android part was implemented by my partner
