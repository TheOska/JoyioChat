package oska.joyiochat.views;

/**
 * Created by TheOska on 11/22/2016.
 */

import android.graphics.Canvas;
import android.util.Log;

import com.google.android.gms.vision.CameraSource;

import static com.google.android.gms.wearable.DataMap.TAG;

/**
 * Base class for a custom graphics object to be rendered within the graphic overlay.  Subclass
 * this and implement the {@link Graphic#draw(Canvas)} method to define the
 * graphics element.  Add instances to the overlay using {@link GraphicOverlay#add(Graphic)}.
 */
public abstract class Graphic {
    private GraphicOverlay mOverlay;

    public Graphic(GraphicOverlay overlay) {
        mOverlay = overlay;
    }

    /**
     * Draw the graphic on the supplied canvas.  Drawing should use the following methods to
     * convert to view coordinates for the graphics that are drawn:
     * <ol>
     * <li>{@link Graphic#scaleX(float)} and {@link Graphic#scaleY(float)} adjust the size of
     * the supplied value from the preview scale to the view scale.</li>
     * <li>{@link Graphic#translateX(float)} and {@link Graphic#translateY(float)} adjust the
     * coordinate from the preview's coordinate system to the view coordinate system.</li>
     * </ol>
     *
     * @param canvas drawing canvas
     */
    public abstract void draw(Canvas canvas);

    /**
     * Adjusts a horizontal value of the supplied value from the preview scale to the view
     * scale.
     */
    public float scaleX(float horizontal) {
        return horizontal * mOverlay.getmWidthScaleFactor();
    }

    /**
     * Adjusts a vertical value of the supplied value from the preview scale to the view scale.
     */
    public float scaleY(float vertical) {
        return vertical * mOverlay.getmHeightScaleFactor();
    }

    /**
     * Adjusts the x coordinate from the preview's coordinate system to the view coordinate
     * system.
     */
    public float translateX(float x) {
        if (mOverlay.getmFacing() == CameraSource.CAMERA_FACING_FRONT) {
            Log.d(TAG, "inside if case");
            return mOverlay.getWidth() - scaleX(x);
        } else {
            Log.d(TAG, "inside else case");
            return scaleX(x);
        }
    }

    /**
     * Adjusts the y coordinate from the preview's coordinate system to the view coordinate
     * system.
     */
    public float translateY(float y) {
        return scaleY(y);
    }

    public void postInvalidate() {
        mOverlay.postInvalidate();
    }
}
