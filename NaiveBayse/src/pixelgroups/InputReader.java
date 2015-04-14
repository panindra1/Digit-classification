package pixelgroups;

import java.io.*;
import java.util.*;
import java.util.List;

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
    protected ImageFormat imageFormat;
    protected Integer numberOfClasses;

    protected Integer imageRowSize = 28;
    protected Integer imageColumnSize = 28;

    public InputReader(String imageFile, String labelFile, FrameType frameType, Boolean isOverlapping,
                       ImageFormat imageFormat, Integer numberOfClasses, Integer imageRowSize, Integer imageColumnSize)
        throws IOException
    {
        init(imageFile, labelFile, frameType, isOverlapping, imageFormat, numberOfClasses, imageRowSize, imageColumnSize);
    }

    private void init(String imageFile, String labelFile, FrameType frameType, Boolean isOverlapping,
                      ImageFormat imageFormat, Integer numberOfClasses, Integer imageRowSize, Integer imageColumnSize)
        throws IOException
    {
        labels = new ArrayList<Integer>();
        classCounts = new HashMap<Integer, Integer>();
        images = new ArrayList<Byte[][]>();
        this.frameType = frameType;
        this.isOverlapping = isOverlapping;
        this.imageFormat = imageFormat;
        this.numberOfClasses = numberOfClasses;
        this.imageRowSize = imageRowSize;
        this.imageColumnSize = imageColumnSize;
        readLabels(labelFile);
        readImages(imageFile);

    }

    private void readImages(String fileName)
        throws IOException
    {
        File file = new File(fileName);
        br = new BufferedReader((new FileReader(file)));
        for (int i = 0; i < labels.size(); i++) {
            String[] image = new String[imageColumnSize];
            for (int j = 0; j < imageColumnSize; j++) {
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
        Byte[][] representation = new Byte[imageColumnSize][imageRowSize];
        for(int i = 0 ; i < imageColumnSize; i ++)
        {
            String line = image[i];
            //System.out.println("Line : " + line);
            char[] lineCharacters = line.toCharArray();
            Byte[] lineByte = new Byte[imageRowSize];
            for(int j = 0 ; j < imageRowSize; j ++)
            {
                if(lineCharacters[j] == ' ')
                {
                    lineByte[j] = 0;
                }
                else if(lineCharacters[j] == '#')
                {
                    lineByte[j] = 1;
                }
                else
                {
                    if(ImageFormat.BINARY.equals(imageFormat))
                    {
                        lineByte[j] = 1;
                    }
                    else
                    {
                        lineByte[j] = 2;
                    }

                }
            }
            representation[i] = lineByte;
        }
        return representation;
    }

    public List<Integer> getLabels()
    {
        return labels;
    }

    public List<Byte[][]> getImages()
    {
        return images;
    }
}
