package oska.joyiochat.face.tracker;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import org.rajawali3d.view.ISurface;
import org.rajawali3d.view.SurfaceView;

import oska.joyiochat.R;
import oska.joyiochat.rajawali.CustomRenderer;
import oska.joyiochat.activity.FaceTrackerActivity;
import oska.joyiochat.rajawali.ObjRender;
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
    private CustomRenderer renderer;
    private Context context;
    private ObjRender objRender;
    private static final int EMOTION_INDEX_SAD = 0;
    private static final int EMOTION_INDEX_SMILE = 1;
    private int lastEmotionIndex;

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

    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
        mOverlay.add(mFaceGraphic);
        mFaceGraphic.updateFace(face);

        if(mFaceGraphic.getSmileRate() > 0.55 && lastEmotionIndex != EMOTION_INDEX_SMILE){
            Log.d(TAG, "inside smile");
            renderGlass();
            lastEmotionIndex = EMOTION_INDEX_SMILE;
        }
        if(mFaceGraphic.getSmileRate() < 0.2 && lastEmotionIndex == EMOTION_INDEX_SMILE){
            Log.d(TAG, "inside sad");
            remove3D();
            lastEmotionIndex = EMOTION_INDEX_SAD;
        }

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
