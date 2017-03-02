package oska.joyiochat.utils;

/**
 * Created by TheOska on 11/27/2016.
 */

public class MobileVisionUtils {
    public static final int EMOTION_INDEX_SAD = 0;
    public static final int EMOTION_INDEX_SMILE = 1;

    public static final int EMOTION_INDEX_LEFT_EYE_OPEN = 2;
    public static final int EMOTION_INDEX_LEFT_EYE_CLOSE = 3;

    public static final int EMOTION_INDEX_RIGHT_EYE_OPEN = 4;
    public static final int EMOTION_INDEX_RIGHT_EYE_CLOSE = 5;

    public static final double THRESHOLD_SMILE = 0.3;
    public static final double THRESHOLD_SAD = 0.2;

    // filter out the false change
    public static final double FALSE_POSITIVE_FILTER = 500;

    public static final double THRESHOLD_EYE_LEFT_OPEN = 0.6;
    public static final double THRESHOLD_EYE_LEFT_CLOSE = 0.58;

    public static final double THRESHOLD_EYE_RIGHT_OPEN = 0.6;
    public static final double THRESHOLD_EYE_RIGHT_CLOSE = 0.58;


}
