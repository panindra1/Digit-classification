package pixelgroups;

import java.io.*;
import java.util.*;

/**
 * Created by darshan on 4/12/15.
 */
public abstract class InputReader {
    protected BufferedReader br;
    protected List<Integer> labels;
    protected List<Byte[][]> images;
    protected Map<Integer, Integer> classCounts; //class : count

    protected FrameType frameType;
    protected Boolean isOverlapping;

    public static final Integer IMAGE_ROW_SIZE = 28;
    public static final Integer IMAGE_COL_SIZE = 28;

    public InputReader(String imageFile, String labelFile, FrameType frameType, Boolean isOverlapping)
        throws IOException
    {
        init(imageFile, labelFile, frameType, isOverlapping);
    }

    private void init(String imageFile, String labelFile, FrameType frameType, Boolean isOverlapping)
        throws IOException
    {
        labels = new ArrayList<Integer>();
        classCounts = new HashMap<Integer, Integer>();
        images = new ArrayList<Byte[][]>();
        this.frameType = frameType;
        this.isOverlapping = isOverlapping;
        readImages(imageFile);
        readLabels(labelFile);

    }

    private void readImages(String fileName)
        throws IOException {
        File file = new File(fileName);
        br = new BufferedReader((new FileReader(file)));
        for (int i = 0; i < labels.size(); i++) {
            String[] image = new String[IMAGE_COL_SIZE];
            for (int j = 0; j < IMAGE_COL_SIZE; j++) {
                image[j] = br.readLine();
            }
            Byte[][] imageRepresentation = getRepresentation(image);
            images.add(imageRepresentation);
        }

    }

    private void readLabels(String fileName)
            throws IOException
    {
        File file = new File(fileName);
        br = new BufferedReader(new FileReader(file));
        String line;
        while((line = br.readLine()) != null)
        {
            Integer clazz = Integer.parseInt(line);
            labels.add(clazz);
            appendClassCount(clazz);
        }
    }

    private void appendClassCount(Integer clazz)
    {
        Integer count = classCounts.get(clazz);
        if(count == null)
        {
            count = 1;
        }
        else
        {
            count ++;
        }
        classCounts.put(clazz, count);
    }

    //life would've been easier with Boolean, but we'll need Byte if we wanna do ternary also
    private Byte[][] getRepresentation(String[] image)
    {
        Byte[][] representation = new Byte[IMAGE_COL_SIZE][IMAGE_ROW_SIZE];
        for(int i = 0 ; i < image.length ; i ++)
        {
            String line = image[i];
            char[] lineCharacters = line.toCharArray();
            Byte[] lineByte = new Byte[IMAGE_ROW_SIZE];
            for(int j = 0 ; j < lineCharacters.length ; j ++)
            {
                if(lineCharacters[j] == ' ')
                {
                    lineByte[j] = 0;
                }
                else
                {
                    lineByte[j] = 1;
                }
            }
            representation[i] = lineByte;
        }
        return representation;
    }

    /*protected List<Integer> getEncodedFrames(Byte[][] image)
    {
        List<Integer> frames;
        if(isOverlapping)
        {
            frames = getOverlappingFrames(image);
        }
        else
        {
            frames = getDiscreteFrames(image);
        }
        return frames;
    }

    private List<Integer> getOverlappingFrames(Byte[][] image)
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

    private List<Integer> getDiscreteFrames(Byte[][] image)
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

    private Integer calculateFrameValue(Byte[][] frame)
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
    }*/

    public List<Integer> getLabels()
    {
        return labels;
    }

    public List<Byte[][]> getImages()
    {
        return images;
    }
}
