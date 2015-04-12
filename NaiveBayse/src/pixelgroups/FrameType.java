package pixelgroups;

/**
 * Created by darshan on 4/12/15.
 */
public enum FrameType
{
    TWO_TWO(2, 2),
    TWO_FOUR(2, 4),
    FOUR_TWO(4, 2),
    FOUR_FOUR(4, 4),
    TWO_THREE(2, 3),
    THREE_TWO(3, 2),
    THREE_THREE(3, 3);

    int noOfRows, noOfCols;

    FrameType(int noOfRows, int noOfCols)
    {
        this.noOfRows = noOfRows;
        this.noOfCols = noOfCols;
    }
}
