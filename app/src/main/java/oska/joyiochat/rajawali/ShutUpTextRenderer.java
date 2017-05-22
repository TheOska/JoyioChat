package oska.joyiochat.rajawali;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.opengl.GLES20;
import android.util.Log;
import android.view.MotionEvent;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation;
import org.rajawali3d.animation.Animation3D;
import org.rajawali3d.animation.EllipticalOrbitAnimation3D;
import org.rajawali3d.animation.RotateOnAxisAnimation;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.lights.PointLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.AlphaMapTexture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.Renderer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import oska.joyiochat.listener.RenderListener;

/**
 * Created by theoska on 4/7/17.
 */

public class ShutUpTextRenderer extends Renderer {

    private PointLight mLight;
    private Object3D mObjectGroup;
    private Animation3D mCameraAnim, mLightAnim;
    private Camera camera;
    private Context context;
    private RenderListener renderListener;
    private final String TAG = "CanvasTextRenderer";
    private boolean renderCompleted;

    private AlphaMapTexture mTimeTexture;
    private Bitmap mTimeBitmap;
    private Canvas mTimeCanvas;
    private Paint mTextPaint;
    private SimpleDateFormat mDateFormat;
    private int mFrameCount;
    private boolean mShouldUpdateTexture;


    public ShutUpTextRenderer(Context context){
        super(context);
        this.context = context;
        renderListener = (RenderListener)context;
    }




    @Override
    protected void initScene() {
        camera = getCurrentCamera();
        renderCompleted = false;
        setupLighting();
        initObj();

        renderListener.onRendered();


    }

    private void initObj() {
        Material timeSphereMaterial = new Material();
        timeSphereMaterial.enableLighting(true);
        timeSphereMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
        mTimeBitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
        mTimeTexture = new AlphaMapTexture("timeTexture", mTimeBitmap);
        try {
            timeSphereMaterial.addTexture(mTimeTexture);
        } catch (ATexture.TextureException e) {
            e.printStackTrace();
        }
        timeSphereMaterial.setColorInfluence(1);

        Sphere parentSphere = null;


        for (int i = 0; i < 20; i++) {
            Material textMaterial = new Material();
            mTimeCanvas = new Canvas(mTimeBitmap);
            mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.setColor(Color.WHITE);
            mTextPaint.setTextSize(35);
            mTimeCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            mTimeCanvas.drawText("Shut up", 75,
                    128, mTextPaint);

            mTimeTexture.setBitmap(mTimeBitmap);
            mTextureManager.replaceTexture(mTimeTexture);

            // create new bitmap
            mTimeBitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);

            // create new timeTexture
            mTimeTexture = new AlphaMapTexture("timeTexture", mTimeBitmap);

            // add to timeTexture
            try {
                textMaterial.addTexture(mTimeTexture);
            } catch (ATexture.TextureException e) {
                e.printStackTrace();
            }
            textMaterial.setColorInfluence(1);


            Sphere timeSphere = new Sphere(.6f, 12, 12);
            timeSphere.setMaterial(textMaterial);
            timeSphere.setDoubleSided(true);
            timeSphere.setColor((int)(Math.random() * 0xffffff));


            if (parentSphere == null) {
                Log.d(TAG, "parentSphere  "  + i);
                timeSphere.setPosition(0, 0, -3);
                timeSphere.setRenderChildrenAsBatch(true);
                getCurrentScene().addChild(timeSphere);
                parentSphere = timeSphere;
            } else {
                // setup position
                timeSphere.setX(-3 + (float) (Math.random() * 6));
                timeSphere.setY(-3 + (float) (Math.random() * 6));
                timeSphere.setZ(-3 + (float) (Math.random() * 6));
                 parentSphere.addChild(timeSphere);
            }
            // random rotation direction
            int direction = Math.random() < .5 ? 1 : -1;

            RotateOnAxisAnimation anim = new RotateOnAxisAnimation(Vector3.Axis.Y, 0,
                    360 * direction);
            anim.setRepeatMode(Animation.RepeatMode.INFINITE);
            anim.setDurationMilliseconds(i == 0 ? 12000
                    : 4000 + (int) (Math.random() * 4000));
            anim.setTransformable3D(timeSphere);
            getCurrentScene().registerAnimation(anim);
            anim.play();
        }
    }

    public void updateTimeBitmap() {
        new Thread(new Runnable() {
            public void run() {
                if (mTimeCanvas == null) {

                    mTimeCanvas = new Canvas(mTimeBitmap);
                    mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    mTextPaint.setColor(Color.WHITE);
                    mTextPaint.setTextSize(35);
                    mDateFormat = new SimpleDateFormat("HH:mm:ss",
                            Locale.ENGLISH);
                }
                //
                // -- Clear the canvas, transparent
                //
                mTimeCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                //
                // -- Draw the time on the canvas
                //
//                mTimeCanvas.drawText(mDateFormat.format(new Date()), 75,
//                        128, mTextPaint);
                mTimeCanvas.drawText(mDateFormat.format(new Date()), 75,
                        128, mTextPaint);
                //
                // -- Indicates that the texture should be updated on the OpenGL thread.
                //
                mShouldUpdateTexture = true;
            }
        }).start();
    }

    @Override
    protected void onRender(long ellapsedRealtime, double deltaTime) {
//        //
//        // -- not a really accurate way of doing things but you get the point :)
//        //
//        if (mFrameCount++ >= mFrameRate) {
//            mFrameCount = 0;
//            updateTimeBitmap();
//        }
//        //
//        // -- update the texture because it is ready
//        //
//        if (mShouldUpdateTexture) {
//            mTimeTexture.setBitmap(mTimeBitmap);
//            mTextureManager.replaceTexture(mTimeTexture);
//            mShouldUpdateTexture = false;
//        }
        super.onRender(ellapsedRealtime, deltaTime);
    }


    private void setupLighting() {

        mLight = new PointLight();
        mLight.setPosition(0, 0, 4);
        mLight.setPower(3);
        getCurrentScene().addLight(mLight);
    }
    private void setupLightingAnim(){
        mLightAnim = new EllipticalOrbitAnimation3D(new Vector3(),
                new Vector3(0, 10, 0), Vector3.getAxisVector(Vector3.Axis.Z), 0,
                360, EllipticalOrbitAnimation3D.OrbitDirection.CLOCKWISE);

        mLightAnim.setDurationMilliseconds(3000);
        mLightAnim.setRepeatMode(Animation.RepeatMode.INFINITE);
        mLightAnim.setTransformable3D(mLight);

        mObjectGroup.setDrawingMode(GLES20.GL_TRIANGLES);
        getCurrentScene().registerAnimation(mLightAnim);
        mLightAnim.play();


    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {

    }

    @Override
    public void onTouchEvent(MotionEvent event) {

    }

    public void setRenderCompleted(){
        renderCompleted = true;
    }

}
