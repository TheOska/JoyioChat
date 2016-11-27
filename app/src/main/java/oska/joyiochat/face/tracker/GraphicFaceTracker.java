package oska.joyiochat.face.tracker;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.util.Log;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import org.rajawali3d.view.ISurface;
import org.rajawali3d.view.SurfaceView;

import oska.joyiochat.R;
import oska.joyiochat.rajawali.CustomRenderer;
import oska.joyiochat.activity.FaceTrackerActivity;
import oska.joyiochat.rajawali.ObjRender;
import oska.joyiochat.utils.FaceUtils;
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
    private SurfaceView surface;
    private Context context;
    private ObjRender objRender;

    private int lastEmotionIndex;
    private final float scaleX = 2.88005601079881f;
    private final float scaleY = 3.196312015938482f;

    public GraphicFaceTracker(GraphicOverlay overlay, Activity activity,  Context context, ObjRender objRender) {
        mOverlay = overlay;
        mFaceGraphic = new FaceGraphic(overlay, activity);
        this.activity = (FaceTrackerActivity)activity;
        this.context = context;
        this.objRender = objRender;
        lastEmotionIndex = -1;
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
    int callOnce = 0;
    float leftEyePosX;
    float leftEyePosY;

    float rightEyePos;
    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
        mOverlay.add(mFaceGraphic);
        mFaceGraphic.updateFace(face);
        if(callOnce != 0){
            for (Landmark landmark : face.getLandmarks()) {
                switch (landmark.getType()) {
                    case Landmark.LEFT_EYE:
                        leftEyePosX = face.getPosition().x;
                        leftEyePosY = face.getPosition().y;
                        break;
                }
            }
        }
        if(mFaceGraphic.getSmileRate() > FaceUtils.THRESHOLD_SMILE ){
            Log.d(TAG, "inside smile");
            if(lastEmotionIndex != FaceUtils.EMOTION_INDEX_SMILE) {
                renderGlass(leftEyePosX * scaleX , leftEyePosY * scaleY );
                lastEmotionIndex = FaceUtils.EMOTION_INDEX_SMILE;
            }
            Log.d(TAG, "box left position " + mFaceGraphic.getBoxLeft());
            Log.d(TAG, "box top position " + mFaceGraphic.getBoxTop());
            objRender.moveSelectedObject(leftEyePosX+ mFaceGraphic.getX() , leftEyePosY+mFaceGraphic.getY());
//            objRender.getObjectAt(leftEyePosX, leftEyePosY);
//            else
//                objRender.moveSelectedObject(callOnce,100f);
        }
        callOnce+= 50;

        if(mFaceGraphic.getSmileRate() < FaceUtils.THRESHOLD_SAD && lastEmotionIndex == FaceUtils.EMOTION_INDEX_SMILE){
            Log.d(TAG, "inside sad");
            remove3D();
            lastEmotionIndex = FaceUtils.EMOTION_INDEX_SAD;
        }

    }



    public void renderGlass(final float leftEyePosX, final float leftEyePosY){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                objRender.startRendObj(leftEyePosX, leftEyePosY);
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
