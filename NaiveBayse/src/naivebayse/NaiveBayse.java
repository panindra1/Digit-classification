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
public class NaiveBayse {

    /**
     * @param args the command line arguments
     */
    
    static Map<Integer, ArrayList<ArrayList<Double>>> foregroundProbabilityMap = new HashMap<>();
    static Map<Integer, ArrayList<ArrayList<Double>>> backgroundProbabilityMap = new HashMap<>();
    static Map<Integer, Double> prioriMap = new HashMap<>();
    static Map<Integer, ArrayList<Integer>> confusionMap = new HashMap<>();
    
    static double[] totalProbability = new double[10];    
    
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
        
        System.out.println("Array size" + indexOfClassMap.get(8).size());
                 
        //iterating over keys 
        for (Integer key : indexOfClassMap.keySet()) {
            System.out.println("Key = " + key);
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
       
         
        double laplaceConstantNum = 50;
        double laplaceConstantDen = laplaceConstantNum * 2;
        double laplaceConstant = laplaceConstantNum/laplaceConstantDen;
        
        for (Integer key : foregroundProbabilityMap.keySet()) {
            ArrayList<ArrayList<Double>> entry = foregroundProbabilityMap.get(key);
            
            
            for(ArrayList<Double> valueList : entry) {
                for(int val = 0; val < valueList.size(); val++) {
                    if(valueList.get(val) == 0) {
                        valueList.set(val, laplaceConstant);
                    }
                }
            }
        }
        //System.out.println(foregroundProbabilityMap);
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
        
        filename = "testimages";
        file = new File(filename);
        br = new BufferedReader(new FileReader(file));
        int lineNum = 0;        
        
        String[] testDigit  = new String[28];
        while ((line = br.readLine()) != null) {
            testDigit[lineNum] = line;
            
            if(lineNum == 27) {
                lineNum = -1;
                result.add(giveClass(testDigit));
                testDigit = new String[28];
            }
            lineNum++;
        }
        
        filename = "testlabels";
        file = new File(filename);
        br = new BufferedReader(new FileReader(file));
        line = "";
        int classVal = 0;
        int positiveCount = 0;        
        
        while ((line = br.readLine()) != null) {                        
            int value = Integer.parseInt(line);            
            if( value == result.get(classVal)) {
                positiveCount++;                
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
        
        double accuracy = (double)positiveCount/classVal;
        System.out.println("Accuracy is " + accuracy * 100);
        
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

    }
    
    static int giveClass(String[] testDigit) {
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
        }
        
        double max = 0;
        
        for(int high = 0; high < totalProbability.length; high++) {            
            if(max < totalProbability[high]) {
                max = totalProbability[high];
                clssDigit = high;
            }
        }        
        return clssDigit;        
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
            foregroundProb = foreground/linesToProcess.size();
            backgroundProb = background/linesToProcess.size();
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
   
    
}
