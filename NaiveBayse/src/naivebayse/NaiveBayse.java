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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
        System.out.println(foregroundProbabilityMap);
        System.out.println(backgroundProbabilityMap);
    }
    
    public static void processLineSet(ArrayList<char[]> linesToProcess, int classNum){
        int background =0, foreground = 0;
        double foregroundProb = 0;
        double backgroundProb = 0;
        ArrayList<Double> fgProbabilityList = new ArrayList<>();
        ArrayList<Double> bgProbabilityList = new ArrayList<>();        
        
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
