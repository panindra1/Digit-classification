/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author panindra
 */
public class NaiveBayse {

    /**
     * @param args the command line arguments
     */
    
    static Map<Integer, ArrayList<ArrayList<Double>>> foregroundProbabilityMap = new HashMap<>();
    static Map<Integer, ArrayList<ArrayList<Double>>> backgroundProbabilityMap = new HashMap<>();
    static Map<Integer, Double> prioriMap = new HashMap<>();
    static Map<Integer, ArrayList<Integer>> confusionMap = new HashMap<>();
    
    static double[] totalProbability = new double[10];   
    static double[] maxProbability = new double[10];
    static double laplaceConstantNum = 1;
    static double laplaceConstantDen = laplaceConstantNum * 2;
    static double laplaceConstant = laplaceConstantNum/laplaceConstantDen;
    
    static ArrayList<Double> result_prob = new ArrayList<Double>();
    static ArrayList<Integer> result = new ArrayList<>();
    static ArrayList<Integer> result_ML = new ArrayList<Integer>();
            
        
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        String filename = "traininglabels";
        File file = new File(filename);
        BufferedReader br
                = new BufferedReader(new FileReader(file));
        
        ArrayList<Integer> trainingClass = new ArrayList<>();
        String line;       
        Map<Integer, ArrayList<Integer>> indexOfClassMap = new HashMap<>();
        int i = 0;
        while ((line = br.readLine()) != null) {
            int num = Integer.parseInt(line);
            trainingClass.add(num);
            if(indexOfClassMap.containsKey(num)) {
                ArrayList<Integer> values = indexOfClassMap.get(num);
                values.add(i);
                indexOfClassMap.put(num, values);
            }
            else {
                ArrayList<Integer> values = new ArrayList<>();
                values.add(i);
                indexOfClassMap.put(num, values);
            }
            i = i +1;
        }
        
        //System.out.println("Array size" + indexOfClassMap.get(8).size());
                 
        //iterating over keys 
        for (Integer key : indexOfClassMap.keySet()) {
            //System.out.println("Key = " + key);
            ArrayList<Integer> values = indexOfClassMap.get(key);
            Collections.sort(values);
            ArrayList<Integer> newValues = new ArrayList<>();
            ArrayList<char[]> linesToProcess = new ArrayList<>();
                    
            //add 0 to 27 to the values
            for(int adder = 0; adder <=27; adder++){
                newValues.clear();
                for(int a = 0; a < values.size(); a++){
                    newValues.add((values.get(a) * 28) + adder);
                }
                int lineNum = 0;
                filename = "trainingimages";
                file = new File(filename);
                br = new BufferedReader(new FileReader(file));
                while ((line = br.readLine()) != null) {
                    //System.out.println("line = " + line);
                    if(newValues.contains(lineNum)){
                        linesToProcess.add(line.toCharArray());
                        //System.out.println(Arrays.toString(line.split("")));
                    }
                    lineNum++;
                }
                // process the lines read
                processLineSet(linesToProcess, key);

                linesToProcess.clear();
            }    
        }    
        
       // System.out.println(foregroundProbabilityMap);
        //System.out.println(backgroundProbabilityMap);
        
        for (Integer key : backgroundProbabilityMap.keySet()) {
            ArrayList<ArrayList<Double>> entry = backgroundProbabilityMap.get(key);
            for(ArrayList<Double> valueList : entry) {
                for(int val = 0; val < valueList.size(); val++) {
                    if(val == 0) {
                        valueList.set(val, laplaceConstant);
                    }
                }
            }
        }
        
        
        
        
        filename = "testimages";
        file = new File(filename);
        br = new BufferedReader(new FileReader(file));
        int lineNum = 0; 
        int[] classDigitValues = new int[2];
        
        String[] testDigit  = new String[28];
        while ((line = br.readLine()) != null) {
            testDigit[lineNum] = line;
            
            if(lineNum == 27) {
                lineNum = -1;
                classDigitValues = giveClass(testDigit);
                result.add(classDigitValues[0]);
                result_ML.add(classDigitValues[1]);
                testDigit = new String[28];
            }
            lineNum++;
        }
        
        filename = "testlabels";
        file = new File(filename);
        br = new BufferedReader(new FileReader(file));
        line = "";
        int classVal = 0;
        int positiveCountForMAP = 0;  
        int positiveCountForML = 0;
        
        
        while ((line = br.readLine()) != null) {                        
            int value = Integer.parseInt(line);            
            if( value == result.get(classVal)) {
                positiveCountForMAP++; 
            }
            if( value == result_ML.get(classVal)){
            	positiveCountForML++;
            }    
             
            if(confusionMap.containsKey(value)) {                    
                 ArrayList<Integer> confusionVals =confusionMap.get(value);
                 confusionVals.set(result.get(classVal), confusionVals.get(result.get(classVal)) + 1);
                 confusionMap.put(value, confusionVals);
             }
             else {
                 ArrayList<Integer> confusionVals = new ArrayList<>(9);
                 for(int m = 0 ; m < 10; m++) 
                    confusionVals.add(0);
                 
                 confusionVals.set(result.get(classVal), 1);
                 confusionMap.put(value, confusionVals);
             }                                
            classVal++;
        }
        
        double accuracy = (double)positiveCountForMAP/classVal;
        System.out.println("Accuracy (for MAP) is " + accuracy * 100);
        
        System.out.println("Accuracy (for Max Likelihood) = " + ((double)positiveCountForML/classVal) * 100);
        
        ArrayList<Integer> totalValMat  = new ArrayList<>();
        for(int index = 0 ; index < confusionMap.size(); index++) {
            int val = 0;
            for(int col = 0 ; col < confusionMap.size(); col++) {
                val+= confusionMap.get(index).get(col);                
            }            
            totalValMat.add(val);
            //System.out.println("Confusion Matrix :" + confusionMap.get(index));            
        }
        
        double[][] confusionMatrix = new double[10][10]; 
        for(int index = 0 ; index < confusionMap.size(); index++) {
            for(int col = 0 ; col < confusionMap.size(); col++) {
            	confusionMatrix[index][col] = ((double)(confusionMap.get(index).get(col)) / totalValMat.get(index) * 100);
                System.out.print(String.format("%6.2f ", confusionMatrix[index][col]));            
            }
            System.out.println(" ");
        }
        
        //print the classification rate for each digit (percentage of all test images of a given digit correctly classified). 
        System.out.println("\nClassification rate of each class");
        for(int index = 0 ; index < confusionMap.size(); index++) {
            for(int col = 0 ; col < confusionMap.size(); col++) {
                if(index == col){
                	System.out.println("Class " + index + " : " + confusionMatrix[index][col]);

                }
            }
            System.out.println(" ");
        }
        
        //Calculating LOG Odds 
        calcLogOdds(4, 9);
        //calcLogOdds(7, 9);
        //calcLogOdds(8, 3);
        //calcLogOdds(3,  5);
        
        //calcLogOdds(0, 6);
        //calcLogOdds(3,  5);
        //calcLogOdds(1, 9);
        //calcLogOdds(8, 6);
        
        
        //calcLogOdds(0, 9);
        //calcLogOdds(8, 3);
        
        showPrototypicalInstance();

    }
    
    static int[] giveClass(String[] testDigit) {
        int clssDigit = 0, lineNum = 0;;
        
        double probCLass;
        ArrayList<ArrayList<Double>> foregroundVals;
        ArrayList<ArrayList<Double>> backgroundVals;
        
        for(int cls = 0; cls <=9 ; cls++) {                            
            foregroundVals = foregroundProbabilityMap.get(cls);
            backgroundVals = backgroundProbabilityMap.get(cls);
            probCLass = prioriMap.get(cls);
            lineNum = 0;
            
            double probabilityClsVals = 1;            
            
            for(String arr : testDigit) {
                
                char[] chrArr = arr.toCharArray();
                for(int x = 0; x < arr.length(); x++) {
                    if(chrArr[x] == ' ' || (chrArr[x] == 0)) {
                        probabilityClsVals *= backgroundVals.get(lineNum).get(x);                        
                    }
                    else {
                        probabilityClsVals *= foregroundVals.get(lineNum).get(x);                        
                    }
                }
                
                lineNum++;
                if(lineNum == 28) {
                    lineNum =0;
                }
                
            }  
            totalProbability[cls] = probCLass * probabilityClsVals;    
            maxProbability[cls] = probabilityClsVals;
        }
        
        double max = 0;
        
        for(int high = 0; high < totalProbability.length; high++) {            
            if(max < totalProbability[high]) {
                max = totalProbability[high];
                clssDigit = high;
            }
        }
        
        int[] classDigit = new int[2];  
        classDigit[0] = clssDigit;
        result_prob.add(totalProbability[clssDigit]);
        
        max = 0;
        clssDigit=0;
        for(int high = 0; high < maxProbability.length; high++) {            
            if(max < maxProbability[high]) {
                max = maxProbability[high];
                clssDigit = high;
            }
        }
        
        classDigit[1] = clssDigit;
        
        return classDigit;        
    }
    
    public static void processLineSet(ArrayList<char[]> linesToProcess, int classNum){
        double background =0, foreground = 0;
        double foregroundProb = 0;
        double backgroundProb = 0;
        ArrayList<Double> fgProbabilityList = new ArrayList<>();
        ArrayList<Double> bgProbabilityList = new ArrayList<>();        
        prioriMap.put(classNum, (double)linesToProcess.size() / 5000);
        
        for(int ctr = 0; ctr < linesToProcess.get(0).length; ctr++){
            background = 0;
            foreground = 0;
            for(int x = 0; x < linesToProcess.size(); x++){
                char[] currLine = linesToProcess.get(x);
                
                if((currLine[ctr] == ' ') || currLine[ctr] == 0){
                    background++;
                }
                else {
                    //System.out.println("currline = " +currLine[ctr]);
                    foreground++;
                }
            }
            foregroundProb = (foreground+laplaceConstantNum)/(linesToProcess.size() + laplaceConstantDen);
            backgroundProb = (background+laplaceConstantNum)/(linesToProcess.size() + laplaceConstantDen);
            fgProbabilityList.add(foregroundProb);
            bgProbabilityList.add(backgroundProb);
            
        }
        
        if(foregroundProbabilityMap.containsKey(classNum)){
            ArrayList<ArrayList<Double>> currList = foregroundProbabilityMap.get(classNum);
            currList.add(fgProbabilityList);
            foregroundProbabilityMap.put(classNum, currList);
        }
        else {
            ArrayList<ArrayList<Double>> currList = new ArrayList<>();
            currList.add(fgProbabilityList);
            foregroundProbabilityMap.put(classNum, currList);
        }
        
        if(backgroundProbabilityMap.containsKey(classNum)){
            ArrayList<ArrayList<Double>> currList = backgroundProbabilityMap.get(classNum);
            currList.add(bgProbabilityList);
            backgroundProbabilityMap.put(classNum, currList);
        }
        else {
            ArrayList<ArrayList<Double>> currList = new ArrayList<>();
            currList.add(bgProbabilityList);
            backgroundProbabilityMap.put(classNum, currList);
        }
        
    }
    
    public static void calcLogOdds(int c1, int c2){
    	ArrayList<ArrayList<Double>> c1Values = foregroundProbabilityMap.get(c1);
    	ArrayList<ArrayList<Double>> c2Values = foregroundProbabilityMap.get(c2);
    	
    	ArrayList<ArrayList<Double>> oddsRatioMatrix = new ArrayList<ArrayList<Double>>();
    	ArrayList<Double> currLineValues;
    	double oddRatioVal = 1; 
    	for(int lineNum =0; lineNum < c1Values.size(); lineNum++){
    		currLineValues = new ArrayList<Double>();
    		for(int pos = 0; pos < c1Values.get(0).size(); pos++){
    			oddRatioVal = c1Values.get(lineNum).get(pos)/c2Values.get(lineNum).get(pos);
    			currLineValues.add(oddRatioVal);
    		}
    		oddsRatioMatrix.add(currLineValues);
    	}
    	
    	//print the foreground prob of C1 and C2 in Ascii format
    	printAsciiMap(c1Values, c1);
    	printAsciiMap(c2Values, c2);
    	    			
    	System.out.println("---------------------------------------------------");
    	System.out.println("Odds Ratio matrix of " + c1 + " " + c2);
    	for(int lineNum = 0 ; lineNum < oddsRatioMatrix.size(); lineNum++){
    		System.out.print("[");
    		for(int pos = 0; pos < oddsRatioMatrix.get(0).size(); pos++){
    			double logValue = Math.log(oddsRatioMatrix.get(lineNum).get(pos));
    			System.out.print(String.format("%6.2f ", logValue) + ",");
    		}
    		System.out.print("],");
    		System.out.println();
    	}
    	
    	System.out.println("--------------------------------------------------");
    	System.out.println("Odds Ratio Map for " + c1 + " " + c2);
    	for(int lineNum = 0 ; lineNum < oddsRatioMatrix.size(); lineNum++){
    		for(int pos = 0; pos < oddsRatioMatrix.get(0).size(); pos++){
    			double logVal = Math.log(oddsRatioMatrix.get(lineNum).get(pos));
    			if(logVal > 0.5){
    				System.out.print("+");
    			
    			}
    			else if (logVal < 0.5 && logVal > -0.5){
    				System.out.print(".");
    				
    			}
    			
    			else {
    				System.out.print("-");
    			}
    			
    		}
    		System.out.println();
    	}
    	
    } //end func
   
    private static void printAsciiMap(ArrayList<ArrayList<Double>> probabilityMap, int classVal){
    	
    	
    	System.out.println("\nAscii map of " + classVal);
    	
    	for(int lineNum = 0 ; lineNum < probabilityMap.size(); lineNum++){
    		System.out.print("[");
    		for (int pos=0; pos < probabilityMap.get(lineNum).size(); pos++ ){
    			System.out.print(String.format("%6.2f", Math.log(probabilityMap.get(lineNum).get(pos))) + ", ");
    		}
    		System.out.print("],");
    		System.out.println();
    	}
    		
    	for(int lineNum = 0 ; lineNum < probabilityMap.size(); lineNum++){
    		for(int pos = 0; pos < probabilityMap.get(lineNum).size(); pos++){
    			double value = (probabilityMap.get(lineNum).get(pos));
    			if(value > 0.5){
    				System.out.print("+");
    			
    			}
    			else if (value < 0.5 && value > -0.5){
    				System.out.print(".");
    				
    			}
    			
    			else {
    				System.out.print("-");
    			}
    			
    		}
    		System.out.println();
    	}
    	//System.out.println("-----------------------------------------------------\n");
    }// end func
    
    private static void showPrototypicalInstance() throws NumberFormatException, IOException{
    	String filename = "testlabels";
        File file = new File(filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
		
        String line = "";
        int classVal = 0;
        int lineIndex = 0;
        
        ArrayList<ArrayList<Double>> probValues = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<Integer>> positions = new ArrayList<ArrayList<Integer>>();
        
        for(int i=0;i<=9;i++){
        	probValues.add(new ArrayList<Double>());
        	positions.add(new ArrayList<Integer>());
        }
        
        
        while ((line = br.readLine()) != null) {   
        	int value = Integer.parseInt(line);            
            if( value == result.get(lineIndex)) {
            		ArrayList<Integer> currPos = positions.get(value);
            		currPos.add(lineIndex);
            		positions.set(value, currPos);
            	
//            	else {
//            		ArrayList<Integer> currPos = new ArrayList<Integer>();
//            		currPos.add(lineIndex);
//            		positions.set(value, currPos);
//            	}
            	
            		ArrayList<Double> currVals = probValues.get(value);
            		currVals.add(result_prob.get(lineIndex));
            		probValues.set(value, currVals);
            	
//            		else {
//            		ArrayList<Double> currVals = new ArrayList<Double>();
//            		currVals.add(result_prob.get(lineIndex));
//            		probValues.set(value, currVals);
//            	}
            }
            lineIndex++;
            
            
        }
        
        //now find the highest for each class in probValues Arraylist
        ArrayList<Double> probabilityValues = new ArrayList<Double>();
        for(int cls = 0; cls <=9 ; cls++){
        	probabilityValues = probValues.get(cls);
        	double maxVal = Double.MIN_VALUE;
        	int maxIndex = 0;
        	for(int indx = 0; indx < probabilityValues.size(); indx++){
        		if(probabilityValues.get(indx) > maxVal){
        			maxVal = probabilityValues.get(indx);
        			maxIndex = indx;
        		}
        		
        	}
        	System.out.println("Best image of class " + cls + " is at position " + (positions.get(cls).get(maxIndex)) * 28);
        	System.out.println("The Maximum A POsteriori = " + maxVal);
        }
    }
    
}
