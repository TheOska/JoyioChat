package oska.joyiochat.face.tracker;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import oska.joyiochat.activity.FaceTrackerActivity;
import oska.joyiochat.listener.FaceInfoDetectListener;
import oska.joyiochat.rajawali.ObjRender;
import oska.joyiochat.utils.MobileVisionUtils;
import oska.joyiochat.utils.RajawaliUtils;
import oska.joyiochat.utils.Utils;
import oska.joyiochat.views.GraphicOverlay;


/**
 * Created by TheOska on 9/17/2016.
 */

/**
 * Face tracker for each detected individual. This maintains a face graphic within the app's
 * associated face overlay.
 *
 * Create a callback to the GraphicOverlay to compu
 */
public class GraphicFaceTracker extends Tracker<Face> {
    private static final String TAG = "GraphicFaceTracker";
    private GraphicOverlay mOverlay;
    private FaceGraphic mFaceGraphic;
    private FaceTrackerActivity activity;
    private Context context;
    private ObjRender objRender;
    private Utils mUtils;
    private int lastEmotionIndex;
    private final float scaleX = 2.88005601079881f;
    private final float scaleY = 3.196312015938482f;
    private double emotionChangeStart, deltaTime;
    private boolean timeLocked;
    private float faceSmilingRate, faceX, faceY, rotationY;
    private FaceInfoDetectListener faceInfoDetectListener = new FaceInfoDetectListener() {
        @Override
        public void onSmilingProbabilityChanged(float smilingRate) {
            faceSmilingRate = smilingRate;
        }

        @Override
        public void onFaceXYChanged(float x, float y) {
            faceX = x;
            faceY = y;
        }

        @Override
        public void onFaceRotationChanged(float y) {
            Log.d("onFaceRotationChanged" ,"y + " +y);
            rotationY = y;
        }

        @Override
        public void onFaceInOut(float z) {
            Log.d("onFaceRotationChanged" ,"z + " +z);

        }
    };
    public GraphicFaceTracker(GraphicOverlay overlay, Activity activity,  Context context, ObjRender objRender, Utils utils) {
        mOverlay = overlay;
        mUtils = utils;
        mFaceGraphic = new FaceGraphic(overlay, activity, mUtils, faceInfoDetectListener);
        this.activity = (FaceTrackerActivity)activity;
        this.context = context;
        this.objRender = objRender;
        lastEmotionIndex = -1;
        timeLocked =false;


        Log.d("mUtils", "x :" +mUtils.getScreenWidth());
        Log.d("mUtils", "y :" +mUtils.getScreenWidth());
    }


    /**
     * Start tracking the detected face instance within the face overlay.
     *
     */
    @Override
    public void onNewItem(int faceId, Face item) {
        mFaceGraphic.setId(faceId);
    }

    /**
     * Update the position/characteristics of the face within the overlay.
     */
    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
        mOverlay.add(mFaceGraphic);
        mFaceGraphic.updateFace(face);

        if(faceSmilingRate > MobileVisionUtils.THRESHOLD_SMILE ){
            initFalseDetection();
            if(lastEmotionIndex != MobileVisionUtils.EMOTION_INDEX_SMILE) {
                renderGlass();
                lastEmotionIndex = MobileVisionUtils.EMOTION_INDEX_SMILE;
            }
            objRender.moveSelectedObject(faceX+ RajawaliUtils.glassObjOffsetX ,
                                         faceY+ RajawaliUtils.glassObjOffsetY,
                                         rotationY);
        }

        if(faceSmilingRate < MobileVisionUtils.THRESHOLD_SAD && lastEmotionIndex == MobileVisionUtils.EMOTION_INDEX_SMILE){
            if(timeLocked == false) {
                emotionChangeStart = System.currentTimeMillis();
                timeLocked = true;
            }
            deltaTime = System.currentTimeMillis() - emotionChangeStart;

        }
        if(deltaTime > MobileVisionUtils.FALSE_POSITIVE_FILTER ){
            remove3D();
            lastEmotionIndex = MobileVisionUtils.EMOTION_INDEX_SAD;
        }

    }

    private void initFalseDetection() {
        emotionChangeStart = 0;
        timeLocked = false;
        deltaTime= 0 ;
    }


    public void renderGlass(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                objRender.startRendObj();
            }
        });
    }
    public void remove3D(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                objRender.stopRendObj();
            }
        });
    }

    /**
     * Hide the graphic when the corresponding face was not detected.  This can happen for
     * intermediate frames temporarily (e.g., if the face was momentarily blocked from
     * view).
     */
    @Override
    public void onMissing(FaceDetector.Detections<Face> detectionResults) {
        mOverlay.remove(mFaceGraphic);
    }

    /**
     * Called when the face is assumed to be gone for good. Remove the graphic annotation from
     * the overlay.
     */
    @Override
    public void onDone() {
        mOverlay.remove(mFaceGraphic);
    }

}
