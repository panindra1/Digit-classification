package pixelgroups;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by darshan on 4/12/15.
 */
public class Util
{
    public static List<Integer> getEncodedFramesForImage(Byte[][] image, FrameType frameType, Boolean isOverlapping)
    {
        List<Integer> frames;
        if(isOverlapping)
        {
            frames = getOverlappingFrames(image, frameType);
        }
        else
        {
            frames = getDiscreteFrames(image, frameType);
        }
        return frames;
    }

    private static List<Integer> getOverlappingFrames(Byte[][] image, FrameType frameType)
    {
        List<Integer> overLappingFrames = new ArrayList<Integer>();
        int windowColLength = image.length - frameType.noOfCols + 1;
        int windowRowLength = image[0].length - frameType.noOfRows + 1;
        for(int i = 0 ; i < windowColLength ; i ++)
        {
            for(int j = 0 ; j < windowRowLength ; j ++)
            {
                Byte[][] frame = new Byte[frameType.noOfCols][frameType.noOfRows];
                for(int m = 0 ; m < frameType.noOfCols ; m ++)
                {
                    frame[m] = new Byte[frameType.noOfRows];
                    for(int n = 0 ; n < frameType.noOfRows ; n ++)
                    {
                        frame[m][n] = image[i + m][j + n];
                    }
                }
                overLappingFrames.add(calculateFrameValue(frame));
            }
        }
        return overLappingFrames;
    }

    private static List<Integer> getDiscreteFrames(Byte[][] image, FrameType frameType)
    {
        List<Integer> overLappingFrames = new ArrayList<Integer>();
        int windowColLength = image.length - frameType.noOfCols + 1;
        int windowRowLength = image[0].length - frameType.noOfRows + 1;
        for(int i = 0 ; i < windowColLength ; i += frameType.noOfCols)
        {
            for(int j = 0 ; j < windowRowLength ; j += frameType.noOfRows)
            {
                Byte[][] frame = new Byte[frameType.noOfCols][frameType.noOfRows];
                for(int m = 0 ; m < frameType.noOfCols ; m ++)
                {
                    frame[m] = new Byte[frameType.noOfRows];
                    for(int n = 0 ; n < frameType.noOfRows ; n ++)
                    {
                        frame[m][n] = image[i + m][j + n];
                    }
                }
                overLappingFrames.add(calculateFrameValue(frame));
            }
        }
        return overLappingFrames;
    }

    private static Integer calculateFrameValue(Byte[][] frame)
    {
        Integer frameValue = 0;
        for(int i = 0 ; i < frame.length ; i ++)
        {
            for(int j = 0 ; j < frame[i].length ; j ++)
            {
                frameValue = (frameValue << 1) + frame[i][j];
            }
        }
        return frameValue;
    }
}
