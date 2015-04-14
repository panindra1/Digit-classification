package pixelgroups;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by darshan on 4/12/15.
 */
public class Util
{
    public static List<Integer>
    getEncodedFramesForImage(Byte[][] image, FrameType frameType, Boolean isOverlapping, ImageFormat imageFormat)
    {
        List<Integer> frames;
        if(isOverlapping)
        {
            frames = getOverlappingFrames(image, frameType, imageFormat);
        }
        else
        {
            frames = getDiscreteFrames(image, frameType, imageFormat);
        }
        return frames;
    }

    private static List<Integer> getOverlappingFrames(Byte[][] image, FrameType frameType, ImageFormat imageFormat)
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
                        //System.out.println("frame " + m + " " + n + " set to " + frame[m][n]);
                    }
                }
                overLappingFrames.add(calculateFrameValue(frame, imageFormat));
            }
        }
        return overLappingFrames;
    }

    private static List<Integer> getDiscreteFrames(Byte[][] image, FrameType frameType, ImageFormat imageFormat)
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
                       // System.out.println("frame " + m + " " + n + " set to " + frame[m][n]);
                    }
                }
                overLappingFrames.add(calculateFrameValue(frame, imageFormat));
            }
        }
        return overLappingFrames;
    }

    private static Integer calculateFrameValue(Byte[][] frame, ImageFormat imageFormat)
    {
        String frameValue = "";
        for(int i = 0 ; i < frame.length ; i ++)
        {
            for(int j = 0 ; j < frame[i].length ; j ++)
            {
                frameValue = frameValue + frame[i][j];
            }
        }
        Integer frameValueBase10 = Integer.valueOf(frameValue, imageFormat.getBase());
        return frameValueBase10;
    }
}
