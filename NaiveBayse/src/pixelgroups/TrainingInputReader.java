package pixelgroups;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by darshan on 4/12/15.
 */
public class TrainingInputReader
    extends InputReader
{
    Map<Integer, Map<Integer, Integer[]>> likelihoods; //Fij : {location : class count array}

    public TrainingInputReader(String imageFile, String labelFile, FrameType frameType, Boolean isOverlapping)
        throws IOException
    {
        super(imageFile, labelFile, frameType, isOverlapping);
        init();
    }

    private void init()
    {
        likelihoods = new HashMap<Integer, Map<Integer, Integer[]>>();
    }

    private void calculateLikelihoods()
    {
        for(int i = 0 ; i < images.size() ; i ++)
        {
            Integer label = labels.get(i);
            List<Integer> frames = Util.getEncodedFramesForImage(images.get(i), frameType, isOverlapping);
            for(int j = 0 ; j < frames.size() ; j ++)
            {
                insertLikelihood(frames.get(j), label, j);
            }

        }
    }

    private void insertLikelihood(Integer frameValue, Integer clazz, Integer location)
    {
        Map<Integer, Integer[]> valueLikelihood = likelihoods.get(frameValue);
        if(valueLikelihood == null)
        {
            valueLikelihood = new HashMap<Integer, Integer[]>();
        }
        else
        {
            Integer[] clazzCountArray = valueLikelihood.get(location);
            if(clazzCountArray == null)
            {
                clazzCountArray = new Integer[10];
                for(int i = 0 ; i < clazzCountArray.length ; i ++) clazzCountArray[i] = 0;
            }
            clazzCountArray[clazz] += 1;
            valueLikelihood.put(location, clazzCountArray);
        }
    }

    public Integer getLikelihoodForValueAtLocationForClass(Integer value, Integer location, Integer clazz)
    {
        Integer likelihood = 0;
        Map<Integer, Integer[]> valueLikelihood = likelihoods.get(value);
        if(valueLikelihood != null)
        {
            Integer[] clazzValues = valueLikelihood.get(location);
            if(clazzValues != null)
            {
                likelihood = clazzValues[clazz];
            }
        }
        return likelihood;
    }

    public Integer getClassFrequency(Integer clazz)
    {
        return classCounts.get(clazz);
    }

    public Integer getTrainingSetSize()
    {
        return labels.size();
    }
}
