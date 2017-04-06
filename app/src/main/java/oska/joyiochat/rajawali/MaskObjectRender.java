package oska.joyiochat.rajawali;

import android.content.Context;
import android.graphics.Color;
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
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.methods.SpecularMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.NormalMapTexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.util.GLU;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;

import javax.microedition.khronos.opengles.GL10;

import oska.joyiochat.R;
import oska.joyiochat.listener.RenderListener;
import oska.joyiochat.utils.RajawaliUtils;

/**
 * Created by theoska on 4/5/17.
 */

public class MaskObjectRender extends Renderer implements OnObjectPickedListener {
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

    public MaskObjectRender(Context context) {
        super(context);
        this.context = context;
        renderListener = (RenderListener)context;
        setFrameRate(60);
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
        // load obj from resouce
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


//        try {
//
//
//            Material material2 = new Material();
//            material2.setDiffuseMethod(new DiffuseMethod.Lambert());
//            material2.setSpecularMethod(new SpecularMethod.Phong(Color.WHITE, 150));
//            material2.enableLighting(true);
//            try {
//                material2.addTexture(new Texture("earthDiffuseTex", R.drawable.earth_diffuse));
//                material2.addTexture(new NormalMapTexture("eartNormalTex", R.drawable.earth_diffuse));
//            } catch (ATexture.TextureException e) {
//                e.printStackTrace();
//            }
//            material2.setColorInfluence(0);
//
//            Material roadMaterial = new Material();
//            roadMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
//            roadMaterial.addTexture(new Texture("roadTex", R.drawable.earth_diffuse));
//            roadMaterial.setColorInfluence(0);
//            mObjectGroup.getChildAt(0).setMaterial(material2);
//
//        } catch (ATexture.TextureException e) {
//            e.printStackTrace();
//        }

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
    private void setupMovableObj() {
        mPicker = new ObjectColorPicker(this);
        mPicker.setOnObjectPickedListener(this);
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
    public void stopRendObj(){
        getCurrentScene().removeChild(mObjectGroup);
    }

    public void setRenderCompleted(){
        renderCompleted = true;
    }
    public void  startRendObj(){
        if(renderCompleted == true) {
            Log.d("oska", "startRendObj");
            getCurrentScene().addChild(mObjectGroup);
        }
    }
    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);
//        mVideoTexture.update();
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
    public void onRenderSurfaceSizeChanged(GL10 gl, int width, int height) {
        super.onRenderSurfaceSizeChanged(gl, width, height);
//        Log.d(TAG,"onRenderSurfaceSizeChanged called"  );
        mViewport[2] = getViewportWidth();
        mViewport[3] = getViewportHeight();
        mViewMatrix = camera.getViewMatrix();
        mProjectionMatrix = camera.getProjectionMatrix();
    }
    public void getObjectAt(float x, float y) {
        mPicker.getObjectAt(x, y);
    }

    /**
     * After gluUnProject the 3D Object:
     * Camera will not after, include camera rotation, distance between scene and camera
     */
    public void moveSelectedObject(float x, float y, float rotationY) {
        if (mObjectGroup == null)
            return;
        GLU.gluUnProject(x, getViewportHeight() - y, 0, mViewMatrix.getDoubleValues(), 0,
                mProjectionMatrix.getDoubleValues(), 0, mViewport, 0, mNearPos4, 0);
        GLU.gluUnProject(x, getViewportHeight() - y, 1.f, mViewMatrix.getDoubleValues(), 0,
                mProjectionMatrix.getDoubleValues(), 0, mViewport, 0, mFarPos4, 0);

        mNearPos.setAll(mNearPos4[0] / mNearPos4[3], mNearPos4[1]
                / mNearPos4[3], mNearPos4[2] / mNearPos4[3]);
        mFarPos.setAll(mFarPos4[0] / mFarPos4[3],
                mFarPos4[1] / mFarPos4[3], mFarPos4[2] / mFarPos4[3]);
        double factor = (Math.abs(mObjectGroup.getZ()) + mNearPos.z)
                / (camera.getFarPlane() - camera
                .getNearPlane());

        mNewObjPos.setAll(mFarPos);
        mNewObjPos.subtract(mNearPos);
        mNewObjPos.multiply(factor);
        mNewObjPos.add(mNearPos);
        mObjectGroup.setX(mNewObjPos.x);
        mObjectGroup.setY(mNewObjPos.y);
        mObjectGroup.setRotY(180+rotationY*1.5);
//        Log.d(TAG, "mObjectGroup RotX: " +mObjectGroup.getRotX());
//        Log.d(TAG, "mObjectGroup RotY: " +mObjectGroup.getRotY());
//        Log.d(TAG, "mObjectGroup RotZ: " +mObjectGroup.getRotZ());

    }
    @Override
    public void onNoObjectPicked() {

    }
    public void stopMovingSelectedObject() {
//        mObjectGroup = null;
    }

    public void zoomInOutObj(float z){
//        Log.d("zoomInOutObj", "z face index is " + z);
        z = z/ 1.5f;
        camera.setZ(RajawaliUtils.DEFAULT_CAMERA_Z_POS - z);
    }
}
