package oska.joyiochat.face.tracker;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import oska.joyiochat.activity.FaceTrackerActivity;
import oska.joyiochat.eventbus.EmotionSelectMessage;
import oska.joyiochat.listener.EmotionSelectListener;
import oska.joyiochat.listener.FaceInfoDetectListener;
import oska.joyiochat.module.EmotionModel;
import oska.joyiochat.rajawali.CanvasTextRenderer;
import oska.joyiochat.rajawali.MaskObjectRender;
import oska.joyiochat.rajawali.MovableObjectRenderer;
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
public class GraphicFaceTracker extends Tracker<Face> implements EmotionSelectListener{
    private static final String TAG = "GraphicFaceTracker";
    private GraphicOverlay mOverlay;
    private FaceGraphic mFaceGraphic;
    private FaceTrackerActivity activity;
    private Context context;
    private MovableObjectRenderer masterRenderer;
    private boolean isMovable, isFirstRunObj;
    private int selectedModelIndex;
    private Utils mUtils;
    private int lastEmotionIndex, lastEyeOpenIndex;
    private final float scaleX = 2.88005601079881f;
    private final float scaleY = 3.196312015938482f;
    private ArrayList<EmotionModel> emotionModelArrayList;
    private double emotionChangeStart, deltaTime;
    private boolean timeLocked;
    private float faceSmilingRate, eyesLeftOpenRate, faceX, faceY, faceZ, rotationY;
    private FaceInfoDetectListener faceInfoDetectListener = new FaceInfoDetectListener() {
        @Override
        public void onSmilingProbabilityChanged(float smilingRate) {
            faceSmilingRate = smilingRate;
        }

        @Override
        public void onEyesOpenProbabilityChanged(float eyeLeft, float eyeRight) {
            eyesLeftOpenRate = eyeLeft;
        }

        /**
         * faceX,Y is from the green box top RIGHT position
         *
         * however, the mirror should be reflect to top LEFT position
         * */
        @Override
        public void onFaceXYChanged(float x, float y) {
            faceX = x;
            faceY = y;


        }

        @Override
        public void onFaceRotationChanged(float y) {
            rotationY = y;
        }

        @Override
        public void onFaceInOut(float z) {
            faceZ = z;
        }
    };
    public GraphicFaceTracker(GraphicOverlay overlay, Activity activity, Context context, ArrayList<EmotionModel> emotionModelArrayList, Utils utils) {
        mOverlay = overlay;
        mUtils = utils;
        mFaceGraphic = new FaceGraphic(overlay, activity, mUtils, faceInfoDetectListener);
        this.activity = (FaceTrackerActivity)activity;
        this.context = context;
        lastEmotionIndex = -1;
        timeLocked =false;
        this.emotionModelArrayList = emotionModelArrayList;
        lastEyeOpenIndex = MobileVisionUtils.EMOTION_INDEX_LEFT_EYE_CLOSE;
        isFirstRunObj = false;
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

//        checkSmilingCondition();
        checkEyeOpenCondition();

    }



    @Override
    public void onSelected(int selectedIndex) {
        selectedModelIndex = selectedIndex;
        isFirstRunObj = false;
        if (emotionModelArrayList.get(selectedModelIndex).getObjRenderer() instanceof MovableObjectRenderer){
            masterRenderer = (MovableObjectRenderer) emotionModelArrayList.get(selectedModelIndex).getObjRenderer();
            isMovable = true;
            Log.d("oska123", "it is movable");
        }else{
            isMovable = false;
            Log.d("oska123", "it is not movable");

        }
    }

    private void checkEyeOpenCondition() {
        if(isMovable == false)
            return;
        if(eyesLeftOpenRate > MobileVisionUtils.THRESHOLD_EYE_LEFT_OPEN ){
            initFalseDetection();
            if(lastEyeOpenIndex != MobileVisionUtils.EMOTION_INDEX_LEFT_EYE_OPEN) {
                renderObject();
                lastEyeOpenIndex = MobileVisionUtils.EMOTION_INDEX_LEFT_EYE_OPEN;
            }
            masterRenderer.moveSelectedObject(faceX, faceY, rotationY);
            return;
//            masterRenderer.zoomInOutObj(-faceZ);
        }else if(isFirstRunObj == false && masterRenderer.getRenderCompleted() == true){
            Log.d("oska123", "checkEyeOpenCondition 's getRenderCompleter is true");
            isFirstRunObj = true;
            renderObject();
            lastEyeOpenIndex = MobileVisionUtils.EMOTION_INDEX_LEFT_EYE_OPEN;
        }


        if(eyesLeftOpenRate < MobileVisionUtils.THRESHOLD_EYE_LEFT_CLOSE && lastEyeOpenIndex == MobileVisionUtils.EMOTION_INDEX_LEFT_EYE_OPEN){

            if(timeLocked == false) {
                emotionChangeStart = System.currentTimeMillis();
                timeLocked = true;
            }
            deltaTime = System.currentTimeMillis() - emotionChangeStart;

        }
        if(deltaTime > MobileVisionUtils.FALSE_POSITIVE_FILTER ){
            remove3D();
            lastEyeOpenIndex = MobileVisionUtils.EMOTION_INDEX_LEFT_EYE_CLOSE;
        }

    }

    private void checkSmilingCondition() {
        if(isMovable == false)
            return;
        if(faceSmilingRate > MobileVisionUtils.THRESHOLD_SMILE ){
            initFalseDetection();
            if(lastEmotionIndex != MobileVisionUtils.EMOTION_INDEX_SMILE) {
                renderObject();
                lastEmotionIndex = MobileVisionUtils.EMOTION_INDEX_SMILE;
            }
            masterRenderer.moveSelectedObject(faceX ,
                    faceY,
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


    public void renderObject(){
//        remove3D();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("oska" , "renderGlass run");

                masterRenderer.startRendObj();
//                masterRenderer.startRendObj();

//                RajawaliUtils.changable(activity);
//                RajawaliUtils.changableRot(activity);

            }
        });
    }
    public void remove3D(){
        Log.d("oska", "remove3d");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                masterRenderer.stopRendObj();
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
