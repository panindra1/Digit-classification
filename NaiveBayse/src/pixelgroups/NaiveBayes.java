package pixelgroups;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

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
    ImageFormat imageFormat;
    Integer numberOfClasses;

    public static final Double LAPLACE_SMOOTHER = 1.0;
    public static Double NUMBER_OF_STATUSES_POSSIBLE = 0.0;
    public static Double LAPLACE_SMOOTHER_DENOM = 0.0;

    public NaiveBayes(String trainingImageFile, String trainingLabelFile, String testImageFile, String testLabelFile,
                      FrameType frameType, Boolean isOverlapping, TestAlgorithm testAlgorithm, ImageFormat imageFormat,
                      Integer numberOfClasses, Integer imageRowSize, Integer imageColumnSize)
        throws IOException
    {
        trainer = new TrainingInputReader(trainingImageFile, trainingLabelFile, frameType, isOverlapping, imageFormat,
                                          numberOfClasses, imageRowSize, imageColumnSize);
        tester = new TestInputReader(testImageFile, testLabelFile, frameType, isOverlapping, imageFormat,
                                     numberOfClasses, imageRowSize, imageColumnSize);
        this.testAlgorithm = testAlgorithm;
        this.frameType = frameType;
        this.isOverlapping = isOverlapping;
        this.imageFormat = imageFormat;
        this.numberOfClasses = numberOfClasses;
        NUMBER_OF_STATUSES_POSSIBLE = Math.pow(imageFormat.getBase(), (frameType.noOfRows * frameType.noOfCols));
        LAPLACE_SMOOTHER_DENOM = LAPLACE_SMOOTHER * NUMBER_OF_STATUSES_POSSIBLE;
    }

    public void classifyTestImages()
    {
        int correct = 0;
        List<Byte[][]> images = tester.getImages();
        for(int i = 0 ; i < images.size() ; i ++)
        {
            //System.out.println("Image number : " + i);
            Integer guessedClass = guessClassForImage(images.get(i));
            //System.out.println("Guessed class : " + guessedClass);
            Integer actualClass = tester.getLabels().get(i);
            //System.out.println("Actual class : " + actualClass);
            if(guessedClass.equals(actualClass)) correct ++;
        }
        System.out.println(100 * correct / (images.size() + 0.0));
    }

    public Integer guessClassForImage(Byte[][] image)
    {
        Float bestProbability = Float.NEGATIVE_INFINITY;
        Integer bestClass = -1;

        for(int clazz = 0 ; clazz < numberOfClasses ; clazz ++)
        {
            //System.out.println("For class " + clazz);
            Float probability = getProbabilityForClassForImage(image, frameType, isOverlapping, clazz);
            if(probability >= bestProbability)
            {
                bestProbability = probability;
                bestClass = clazz;
            }
        }
        return bestClass;
    }

    private Float
    getProbabilityForClassForImage(Byte[][] image, FrameType frameType, Boolean isOverlapping, int clazz)
    {
        Float probability = 0.0f;
        List<Integer> frames = Util.getEncodedFramesForImage(image, frameType, isOverlapping, imageFormat);
        Integer trainingSetSize = trainer.getTrainingSetSize();
        for(int location = 0 ; location < frames.size() ; location ++)
        {
            Integer likelihoodForValueAtLocationForClass =
                trainer.getLikelihoodForValueAtLocationForClass(frames.get(location), location, clazz);
            //System.out.println("Likelihood at location " + location + " for value " + frames.get(location) + " for class " +  clazz + " : " + likelihoodForValueAtLocationForClass);
            Float probabilityForValueAtLocationForClass =
                    (float)Math.log10((likelihoodForValueAtLocationForClass + LAPLACE_SMOOTHER) / (trainer.getClassFrequency(clazz) + LAPLACE_SMOOTHER_DENOM));
            //System.out.println("Prpb : " + probabilityForValueAtLocationForClass);
            probability += probabilityForValueAtLocationForClass;
        }
        if(TestAlgorithm.MAXIMUM_A_POSTERIORI.equals(testAlgorithm))
        {
            probability += (float)Math.log10((trainer.getClassFrequency(clazz) + LAPLACE_SMOOTHER) / (trainingSetSize + LAPLACE_SMOOTHER_DENOM));
        }
        //System.out.println("Probab : " + probability);
        return probability;
    }

    public  static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        NaiveBayes nb = new NaiveBayes("trainingimages", "traininglabels", "testimages", "testlabels",
                                        FrameType.ONE_ONE, true, TestAlgorithm.MAXIMUM_A_POSTERIORI,
                                        ImageFormat.BINARY, 10, 28, 28);
        nb.classifyTestImages();
        long endtime = System.currentTimeMillis();
        System.out.println("Time taken : " + ((endtime - startTime + 0.0) / 1000.0) + " seconds");
    }
}
