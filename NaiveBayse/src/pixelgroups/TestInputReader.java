package pixelgroups;

import java.io.IOException;

/**
 * Created by darshan on 4/12/15.
 */
public class TestInputReader extends InputReader
{
    public TestInputReader(String imageFile, String labelFile, FrameType frameType, Boolean isOverlapping)
        throws IOException
    {
        super(imageFile, labelFile, frameType, isOverlapping);
    }

}
