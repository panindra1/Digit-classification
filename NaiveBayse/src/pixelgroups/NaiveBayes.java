package pixelgroups;

import java.io.IOException;
import java.util.*;

/**
 * Created by darshan on 4/12/15.
 */
public class NaiveBayes
{
    TrainingInputReader trainer;
    TestInputReader tester;
    TestAlgorithm testAlgorithm;

    FrameType frameType;
    Boolean isOverlapping;

    public NaiveBayes(String imageFile, String labelFile, FrameType frameType, Boolean isOverlapping,
                      TestAlgorithm testAlgorithm)
        throws IOException
    {
        trainer = new TrainingInputReader(imageFile, labelFile, frameType, isOverlapping);
        tester = new TestInputReader(imageFile, labelFile, frameType, isOverlapping);
        this.testAlgorithm = testAlgorithm;
        this.frameType = frameType;
        this.isOverlapping = isOverlapping;
    }

    public void classifyTestImages()
    {
        List<Byte[][]> images = tester.getImages();
        for(int i = 0 ; i < images.size() ; i ++)
        {
            Integer guessedClass = guessClassForImage(images.get(0));
            System.out.println("Guessed class : " + guessedClass);
            Integer actualClass = tester.getLabels().get(i);
            System.out.println("Actual class : " + actualClass);
        }
    }

    public Integer guessClassForImage(Byte[][] image)
    {
        Double bestProbability = 0.0;
        Integer bestClass = -1;

        for(int clazz = 0 ; clazz < 10 ; clazz ++)
        {
            Double probability = getProbabilityForClassForImage(image, frameType, isOverlapping, clazz);
            if(probability > bestProbability)
            {
                bestProbability = probability;
                bestClass = clazz;
            }
        }
        return bestClass;
    }

    private Double
    getProbabilityForClassForImage(Byte[][] image, FrameType frameType, Boolean isOverlapping, int clazz)
    {
        Double probability = 0.0;
        List<Integer> frames = Util.getEncodedFramesForImage(image, frameType, isOverlapping);
        Integer trainingSetSize = trainer.getTrainingSetSize();
        for(int location = 0 ; location < frames.size() ; location ++)
        {
            Integer likelihoodForValueAtLocationForClass =
                trainer.getLikelihoodForValueAtLocationForClass(frames.get(location), location, clazz);
            Double probabilityForValueAtLocationForClass =
                Math.log(likelihoodForValueAtLocationForClass / (trainingSetSize + 0.0));
            probability += probabilityForValueAtLocationForClass;
        }
        probability += Math.log((trainer.getClassFrequency(clazz) + 0.0)/ trainingSetSize);
        return probability;
    }
}
