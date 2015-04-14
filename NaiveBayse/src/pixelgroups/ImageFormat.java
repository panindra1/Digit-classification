package pixelgroups;

/**
 * Created by darshan on 4/13/15.
 */
public enum ImageFormat {
    BINARY(2),
    TERNARY(3);

    int base;

    ImageFormat(int base)
    {
        this.base = base;
    }

    public int getBase()
    {
        return base;
    }
}
