# Digigt-classification
AI-assignment3

Filename: NaiveBayse.java
Input: trainingLabels, trainingimages for training model
       testlabels, testimages for applying training model on test data.

Output format:
  Accuracy (for MAP) is 77.10000000000001
  Accuracy (for Max Likelihood) = 77.0

Confusion Matrix
 There are 10 digit classes (0, 9)
  
  84.44   0.00   1.11   0.00   1.11   5.56   3.33   0.00   4.44   0.00  
  0.00  96.30   0.93   0.00   0.00   1.85   0.93   0.00   0.00   0.00  
  0.97   2.91  77.67   3.88   0.97   0.00   5.83   0.97   4.85   1.94  
  0.00   2.00   0.00  79.00   0.00   3.00   2.00   6.00   2.00   6.00  
  0.00   0.93   0.00   0.00  76.64   0.00   2.80   0.93   1.87  16.82  
  2.17   2.17   1.09  13.04   3.26  67.39   1.09   1.09   2.17   6.52  
  1.10   6.59   4.40   0.00   4.40   5.49  75.82   0.00   2.20   0.00  
  0.00   5.66   2.83   0.00   2.83   0.00   0.00  72.64   2.83  13.21  
  1.94   0.97   2.91  13.59   1.94   5.83   0.00   0.97  60.19  11.65  
  1.00   1.00   1.00   3.00   9.00   2.00   0.00   2.00   1.00  80.00  
  
  This is a 10x10 matrix whose entry in row r and column c is the percentage of test images from class r that are classified as class c.
