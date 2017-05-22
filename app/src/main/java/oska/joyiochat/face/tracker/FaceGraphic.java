package oska.joyiochat.face.tracker;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import org.rajawali3d.view.SurfaceView;

import oska.joyiochat.listener.FaceInfoDetectListener;
import oska.joyiochat.rajawali.CustomRenderer;
import oska.joyiochat.utils.Utils;
import oska.joyiochat.views.Graphic;
import oska.joyiochat.views.GraphicOverlay;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
class FaceGraphic extends Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;
    private final double scaleX = 2.88005601079881;
    private final double scaleY = 3.196312015938482;
    private float boxXOffset, boxYOffset, boxLeft, boxTop, boxRight, boxBottom, x, y;

    //    private RajawaliListener rajawaliListener;
    private static final int COLOR_CHOICES[] = {
        Color.BLUE,
        Color.CYAN,
        Color.GREEN,
        Color.MAGENTA,
        Color.RED,
        Color.WHITE,
        Color.YELLOW
    };

    // load the different color of COLOR_CHOICES[]
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    private volatile Face mFace;
    private int mFaceId;
    private Activity mActivity;
    private Utils mUtils;
    private FaceInfoDetectListener faceInfoDetectListener;

    FaceGraphic(GraphicOverlay overlay, final Activity activity, Utils utils, FaceInfoDetectListener faceInfoDetectListener) {
        super(overlay);
        mUtils = utils;
        this.faceInfoDetectListener = faceInfoDetectListener;
        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
        mActivity = activity;


        Log.d("mUtils", "x :" +mUtils.getScreenWidth());
        Log.d("mUtils", "y :" +mUtils.getScreenWidth());
    }
    void setId(int id) {
        mFaceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;

        if (face == null) {
            return;
        }
        // @face.getPosition(); Returns the top left position of the face within the image.
        // @face.getWidth(); Returns the width of the face region in pixels.
        // Draws a circle at the position of the detected face, with the face's track id below.
        x = translateX(face.getPosition().x + face.getWidth() / 2);
        y = translateY(face.getPosition().y + face.getHeight() / 2);

        faceInfoDetectListener.onSmilingProbabilityChanged(face.getIsSmilingProbability());
        faceInfoDetectListener.onEyesOpenProbabilityChanged(face.getIsLeftEyeOpenProbability(), face.getIsRightEyeOpenProbability());
        faceInfoDetectListener.onFaceXYChanged(x,y);
        faceInfoDetectListener.onFaceRotationChanged(face.getEulerY());
        faceInfoDetectListener.onFaceInOut(face.getEulerZ());
//        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
//        canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);
//        canvas.drawText("happiness: " + String.format("%.2f", face.getIsSmilingProbability()), x - ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint);
//        canvas.drawText("right eye: " + String.format("%.2f", face.getIsRightEyeOpenProbability()), x + ID_X_OFFSET * 2, y + ID_Y_OFFSET * 2, mIdPaint);
//        canvas.drawText("left eye: " + String.format("%.2f", face.getIsLeftEyeOpenProbability()), x - ID_X_OFFSET*2, y - ID_Y_OFFSET*2, mIdPaint);
//
//
//        // Draws a bounding box around the face.
//         boxXOffset = scaleX(face.getWidth() / 2.0f);
//         boxYOffset = scaleY(face.getHeight() / 2.0f);
//         boxLeft = x - boxXOffset;
//         boxTop = y - boxYOffset;
//         boxRight = x + boxXOffset;
//         boxBottom = y + boxYOffset;
//        canvas.drawRect(boxLeft, boxTop, boxRight, boxBottom, mBoxPaint);

//        canvas.drawPoint();
//        drawLandmark(face ,canvas);

    }

    private void drawLandmark(Face face, Canvas canvas) {

            for (Landmark landmark : face.getLandmarks()) {
                switch (landmark.getType()) {
                    case Landmark.LEFT_EYE:
                        Log.d("FaceView", "left eye :" + landmark.getPosition());
                        break;
                    case Landmark.RIGHT_EYE:
                        Log.d("FaceView", "Right eye :" + landmark.getPosition());
                        break;
                }
                int cx = (int) (landmark.getPosition().x * scaleX);
                int cy = (int) (landmark.getPosition().y * scaleY);
                canvas.drawCircle(Math.abs(mUtils.getScreenWidth() - cx), cy, 10, mFacePositionPaint);
            }
    }

}
