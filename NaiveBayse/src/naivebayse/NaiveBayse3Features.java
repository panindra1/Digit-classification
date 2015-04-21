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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author panindra
 */
public class NaiveBayse3Features {

    /**
     * @param args the command line arguments
     */
    
    //static Map<Integer, ArrayList<ArrayList<Double>>> foregroundProbabilityMap = new HashMap<>();
    static Map<Integer, ArrayList<ArrayList<Double>>> plusProbabilityMap = new HashMap<Integer, ArrayList<ArrayList<Double>>>();
    static Map<Integer, ArrayList<ArrayList<Double>>> hashProbabilityMap = new HashMap<Integer, ArrayList<ArrayList<Double>>>();
    static Map<Integer, ArrayList<ArrayList<Double>>> backgroundProbabilityMap = new HashMap<>();
    static Map<Integer, Double> prioriMap = new HashMap<>();
    static Map<Integer, ArrayList<Integer>> confusionMap = new HashMap<>();
    
    static double[] totalProbability = new double[10];   
    static double[] maxProbability = new double[10];
    static double laplaceConstantNum = 1;
    static double laplaceConstantDen = laplaceConstantNum * 3;
    static double laplaceConstant = laplaceConstantNum/laplaceConstantDen;
        
    
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
        
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<Integer> result_ML = new ArrayList<Integer>();
        
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
        
        for(int index = 0 ; index < confusionMap.size(); index++) {
            for(int col = 0 ; col < confusionMap.size(); col++) {
                System.out.print(String.format("%.2f ",((double)(confusionMap.get(index).get(col)) / totalValMat.get(index) * 100)));            
            }
            System.out.println(" ");
        }
        
        //Calculating LOG Odds 
        //calcLogOdds(0, 6);
        //calcLogOdds(3,  5);
        //calcLogOdds(1, 9);
        //calcLogOdds(8, 6);
        
        
        //calcLogOdds(0, 9);
        //calcLogOdds(8, 3);

    }
    
    static int[] giveClass(String[] testDigit) {
        int clssDigit = 0, lineNum = 0;;
        
        double probCLass;
        //ArrayList<ArrayList<Double>> foregroundVals;
        ArrayList<ArrayList<Double>> backgroundVals;
        
        ArrayList<ArrayList<Double>> plusVals;
        ArrayList<ArrayList<Double>> hashVals;
        
        
        for(int cls = 0; cls <=9 ; cls++) {                            
            //foregroundVals = foregroundProbabilityMap.get(cls);
            backgroundVals = backgroundProbabilityMap.get(cls);
            
            plusVals = plusProbabilityMap.get(cls);
            hashVals = hashProbabilityMap.get(cls);
            
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
                        //probabilityClsVals *= foregroundVals.get(lineNum).get(x);
                    	if(chrArr[x] == '+'){
                    		probabilityClsVals *= plusVals.get(lineNum).get(x);
                    	}
                    	else {
                    		probabilityClsVals *= hashVals.get(lineNum).get(x);
                    	}
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
        double plusSymbol=0, hashSymbol=0;
        double plusProbability=0, hashProbability=0;
        double foregroundProb = 0;
        double backgroundProb = 0;
        ArrayList<Double> fgProbabilityList = new ArrayList<>();
        ArrayList<Double> plusProbabilityList = new ArrayList<>();
        ArrayList<Double> hashProbabilityList = new ArrayList<>();
        
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
                    if(currLine[ctr] == '+'){
                    	plusSymbol++;
                    }
                    else {
                    	hashSymbol++;
                    }
                }
            }
            foregroundProb = (foreground+laplaceConstantNum)/(linesToProcess.size() + laplaceConstantDen);
            
            plusProbability = (plusSymbol+laplaceConstantNum)/(linesToProcess.size() + laplaceConstantDen);
            hashProbability = (hashSymbol+laplaceConstantNum)/(linesToProcess.size() + laplaceConstantDen);
            
            backgroundProb = (background+laplaceConstantNum)/(linesToProcess.size() + laplaceConstantDen);
            
            fgProbabilityList.add(foregroundProb);
            bgProbabilityList.add(backgroundProb);
            
            plusProbabilityList.add(plusProbability);
            hashProbabilityList.add(hashProbability);
        }
        
/*        if(foregroundProbabilityMap.containsKey(classNum)){
            ArrayList<ArrayList<Double>> currList = foregroundProbabilityMap.get(classNum);
            currList.add(fgProbabilityList);
            foregroundProbabilityMap.put(classNum, currList);
        }
        else {
            ArrayList<ArrayList<Double>> currList = new ArrayList<>();
            currList.add(fgProbabilityList);
            foregroundProbabilityMap.put(classNum, currList);
        }
  */      
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
        
        if(plusProbabilityMap.containsKey(classNum)){
            ArrayList<ArrayList<Double>> currList = plusProbabilityMap.get(classNum);
            currList.add(plusProbabilityList);
            plusProbabilityMap.put(classNum, currList);
        }
        else {
            ArrayList<ArrayList<Double>> currList = new ArrayList<>();
            currList.add(plusProbabilityList);
            plusProbabilityMap.put(classNum, currList);
        }
        
        if(hashProbabilityMap.containsKey(classNum)){
            ArrayList<ArrayList<Double>> currList = hashProbabilityMap.get(classNum);
            currList.add(hashProbabilityList);
            hashProbabilityMap.put(classNum, currList);
        }
        else {
            ArrayList<ArrayList<Double>> currList = new ArrayList<>();
            currList.add(hashProbabilityList);
            hashProbabilityMap.put(classNum, currList);
        }
        
    }
    
    /*
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
    			
    	System.out.println("---------------------------------------------------");
    	System.out.println("Odds Ratio matrix of " + c1 + " " + c2);
    	for(int lineNum = 0 ; lineNum < oddsRatioMatrix.size(); lineNum++){
    		for(int pos = 0; pos < oddsRatioMatrix.get(0).size(); pos++){
    			System.out.print(String.format("%.2f ", oddsRatioMatrix.get(lineNum).get(pos)));
    		}
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
    
    */
   
    
}
