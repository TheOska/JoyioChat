package oska.joyiochat.utils;

/**
 * Created by TheOska on 11/27/2016.
 */

public class MobileVisionUtils {
    public static final int EMOTION_INDEX_SAD = 0;
    public static final int EMOTION_INDEX_SMILE = 1;

    public static final double THRESHOLD_SMILE = 0.3;
    public static final double THRESHOLD_SAD = 0.2;

    // filter out the false change
    public static final double FALSE_POSITIVE_FILTER = 500;
}
