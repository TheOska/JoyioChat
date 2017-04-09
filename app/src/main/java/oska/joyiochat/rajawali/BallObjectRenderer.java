package oska.joyiochat.rajawali;

import android.content.Context;
import android.opengl.GLES20;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation;
import org.rajawali3d.animation.Animation3D;
import org.rajawali3d.animation.EllipticalOrbitAnimation3D;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.lights.PointLight;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;

import oska.joyiochat.R;
import oska.joyiochat.listener.RenderListener;
import oska.joyiochat.utils.RajawaliUtils;

/**
 * Created by theoska on 4/8/17.
 */

public class BallObjectRenderer extends Renderer implements OnObjectPickedListener {

    private PointLight mLight;
    private Object3D mObjectGroup;
    private Animation3D mCameraAnim, mLightAnim;
    private Camera camera;
    private Context context;

    /**
     * Variables for For 3d Transform
     * */
    private int[] mViewport;
    private double[] mNearPos4;
    private double[] mFarPos4;
    private Vector3 mNearPos;
    private Vector3 mFarPos;
    private Vector3 mNewObjPos;
    private Matrix4 mViewMatrix;
    private Matrix4 mProjectionMatrix;
    private ObjectColorPicker mPicker;
    /**
     * Check Render is completed
     * */
    private RenderListener renderListener;
    private final String TAG = "ObjRender";
    private boolean renderCompleted;


    public BallObjectRenderer(Context context){
        super(context);
        this.context = context;
        renderListener = (RenderListener)context;
    }
    @Override
    protected void initScene() {
        camera = getCurrentCamera();
        renderCompleted = false;
        setupTransformMatrix();

        setupLighting();
        setupMovableObj();
        camera.setZ(RajawaliUtils.DEFAULT_CAMERA_Z_POS);

        setupObj();
        setupLightingAnim();
        renderListener.onRendered();
    }

    private void setupObj() {
        LoaderOBJ objParser = new LoaderOBJ(mContext.getResources(),
                mTextureManager, R.raw.pokemon_ball);

        try {
            objParser.parse();
            mObjectGroup = objParser.getParsedObject();
            mObjectGroup.setScale(0.35f);
//            mObjectGroup.setMaterial(material);

        } catch (ParsingException e) {
            e.printStackTrace();
        }
    }
    public void stopRendObj(){
        getCurrentScene().removeChild(mObjectGroup);
    }


    private void setupMovableObj() {
        mPicker = new ObjectColorPicker(this);
        mPicker.setOnObjectPickedListener(this);
    }


    public void startRendObj(){
        if(renderCompleted == true) {
            Log.d("oska", "startRendObj");
            getCurrentScene().addChild(mObjectGroup);
        }
    }
    private void setupTransformMatrix() {
        mViewport = new int[] { 0, 0, getViewportWidth(), getViewportHeight() };
        mNearPos4 = new double[4];
        mFarPos4 = new double[4];
        mNearPos = new Vector3();
        mFarPos = new Vector3();
        mNewObjPos = new Vector3();
        mViewMatrix = camera.getViewMatrix();
        mProjectionMatrix = camera.getProjectionMatrix();

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
        mPicker.registerObject(mObjectGroup);
        getCurrentScene().registerAnimation(mLightAnim);
        mLightAnim.play();


    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {

    }

    @Override
    public void onTouchEvent(MotionEvent event) {

    }


    @Override
    public void onObjectPicked(@NonNull Object3D object) {
        mObjectGroup = object;

    }

    @Override
    public void onNoObjectPicked() {

    }
}

