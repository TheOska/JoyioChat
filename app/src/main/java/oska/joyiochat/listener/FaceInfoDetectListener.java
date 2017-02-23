package oska.joyiochat.listener;

/**
 * Created by TheOska on 1/18/2017.
 */

public interface FaceInfoDetectListener {

    void onSmilingProbabilityChanged(float smilingRate);

    void onFaceXYChanged(float x, float y);

    void onFaceRotationChanged(float y);

    void onFaceInOut(float z);
}
