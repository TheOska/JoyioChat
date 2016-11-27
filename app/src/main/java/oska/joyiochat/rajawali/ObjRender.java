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
import org.rajawali3d.animation.RotateOnAxisAnimation;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.lights.PointLight;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.util.GLU;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;

import javax.microedition.khronos.opengles.GL10;

import oska.joyiochat.R;

/**
 * Created by TheOska on 11/22/2016.
 */

public class ObjRender extends Renderer implements OnObjectPickedListener {
    private PointLight mLight;
    private DirectionalLight mDirectionalLight;
    private Context context;
    private Object3D mObjectGroup;
    private Animation3D mCameraAnim, mLightAnim;

    private int[] mViewport;
    private double[] mNearPos4;
    private double[] mFarPos4;
    private Vector3 mNearPos;
    private Vector3 mFarPos;
    private Vector3 mNewObjPos;
    private Matrix4 mViewMatrix;
    private Matrix4 mProjectionMatrix;
    private ObjectColorPicker mPicker;
    private boolean objRendered;
    private final String TAG = "ObjRender";
    public ObjRender(Context context) {
        super(context);
        this.context = context;
        setFrameRate(60);
    }

    @Override
    public void initScene() {

        objRendered =false;
        mViewport = new int[] { 0, 0, getViewportWidth(), getViewportHeight() };
        mNearPos4 = new double[4];
        mFarPos4 = new double[4];
        mNearPos = new Vector3();
        mFarPos = new Vector3();
        mNewObjPos = new Vector3();
        mViewMatrix = getCurrentCamera().getViewMatrix();
        mProjectionMatrix = getCurrentCamera().getProjectionMatrix();

        mPicker = new ObjectColorPicker(this);
        mPicker.setOnObjectPickedListener(this);

        mLight = new PointLight();
        mLight.setPosition(0, 0, 4);
        mLight.setPower(3);

        getCurrentScene().addLight(mLight);
        getCurrentCamera().setZ(16);


        LoaderOBJ objParser = new LoaderOBJ(mContext.getResources(),
                mTextureManager, R.raw.sun_glasses_obj);

        try {
            objParser.parse();
            mObjectGroup = objParser.getParsedObject();
            mObjectGroup.setScale(0.3f);

//            mCameraAnim = new RotateOnAxisAnimation(Vector3.Axis.Y, 180);
//            mCameraAnim.setDurationMilliseconds(8000);
//            mCameraAnim.setRepeatMode(Animation.RepeatMode.INFINITE);
//            mCameraAnim.setTransformable3D(mObjectGroup);


        } catch (ParsingException e) {
            e.printStackTrace();
        }

        mLightAnim = new EllipticalOrbitAnimation3D(new Vector3(),
                new Vector3(0, 10, 0), Vector3.getAxisVector(Vector3.Axis.Z), 0,
                360, EllipticalOrbitAnimation3D.OrbitDirection.CLOCKWISE);

        mLightAnim.setDurationMilliseconds(3000);
        mLightAnim.setRepeatMode(Animation.RepeatMode.INFINITE);
        mLightAnim.setTransformable3D(mLight);

        mObjectGroup.setDrawingMode(GLES20.GL_TRIANGLES);
        mPicker.registerObject(mObjectGroup);
//        getCurrentScene().registerAnimation(mCameraAnim);
        getCurrentScene().registerAnimation(mLightAnim);

        // after initialized then remove child
//        getCurrentScene().removeChild(mObjectGroup);
//        getCurrentScene().addChild(mObjectGroup);

//        mCameraAnim.play();
        mLightAnim.play();
    }

    public void stopRendObj(){
        getCurrentScene().removeChild(mObjectGroup);
    }

    public void  startRendObj(float posX, float posY){
        Log.d(TAG, "camera pos :" +getCurrentCamera().getPosition());
        Log.d(TAG, "object pos :" +mObjectGroup.getPosition());
//        getCurrentCamera().setLookAt(0,0,0);
        getCurrentScene().addChild(mObjectGroup);
//        getObjectAt(posX,posY);

        Log.d(TAG, "Post  rend camera pos :" +getCurrentCamera().getPosition());
        Log.d(TAG, "Post object pos :" +mObjectGroup.getPosition());
    }

    @Override
    public void onTouchEvent(MotionEvent event){
    }

    @Override
    public void onOffsetsChanged(float x, float y, float z, float w, int i, int j){

    }


    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);
    }

    @Override
    public void onObjectPicked(@NonNull Object3D object) {
        mObjectGroup = object;

    }

    @Override
    public void onNoObjectPicked() {

    }

    public void onRenderSurfaceSizeChanged(GL10 gl, int width, int height) {
        super.onRenderSurfaceSizeChanged(gl, width, height);
        Log.d(TAG,"onRenderSurfaceSizeChanged called"  );
        mViewport[2] = getViewportWidth();
        mViewport[3] = getViewportHeight();
        mViewMatrix = getCurrentCamera().getViewMatrix();
        mProjectionMatrix = getCurrentCamera().getProjectionMatrix();
    }
    public void getObjectAt(float x, float y) {
        mPicker.getObjectAt(x, y);
//        Log.d(TAG , "getObjectAt called");
//        GLU.gluUnProject(x, getViewportHeight() - y, 0, mViewMatrix.getDoubleValues(), 0,
//                mProjectionMatrix.getDoubleValues(), 0, mViewport, 0, mNearPos4, 0);
//
//        GLU.gluUnProject(x, getViewportHeight() - y, 1.f, mViewMatrix.getDoubleValues(), 0,
//                mProjectionMatrix.getDoubleValues(), 0, mViewport, 0, mFarPos4, 0);
//
//
//        mNearPos.setAll(mNearPos4[0] / mNearPos4[3], mNearPos4[1]
//                / mNearPos4[3], mNearPos4[2] / mNearPos4[3]);
//        mFarPos.setAll(mFarPos4[0] / mFarPos4[3],
//                mFarPos4[1] / mFarPos4[3], mFarPos4[2] / mFarPos4[3]);
//        double factor = (Math.abs(mObjectGroup.getZ()) + mNearPos.z)
//                / (getCurrentCamera().getFarPlane() - getCurrentCamera()
//                .getNearPlane());
//
//        mNewObjPos.setAll(mFarPos);
//        mNewObjPos.subtract(mNearPos);
//        mNewObjPos.multiply(factor);
//        mNewObjPos.add(mNearPos);
//
//        mObjectGroup.setX(mNewObjPos.x);
//        mObjectGroup.setY(mNewObjPos.y);
    }


    public void moveSelectedObject(float x, float y) {
        if (mObjectGroup == null)
            return;
        Log.d("ObjRender","moveSelectedObject called");
        //
        // -- unproject the screen coordinate (2D) to the camera's near plane
        //

        GLU.gluUnProject(x, getViewportHeight() - y, 0, mViewMatrix.getDoubleValues(), 0,
                mProjectionMatrix.getDoubleValues(), 0, mViewport, 0, mNearPos4, 0);

        //
        // -- unproject the screen coordinate (2D) to the camera's far plane
        //

        GLU.gluUnProject(x, getViewportHeight() - y, 1.f, mViewMatrix.getDoubleValues(), 0,
                mProjectionMatrix.getDoubleValues(), 0, mViewport, 0, mFarPos4, 0);

        //
        // -- transform 4D coordinates (x, y, z, w) to 3D (x, y, z) by dividing
        // each coordinate (x, y, z) by w.
        //

        mNearPos.setAll(mNearPos4[0] / mNearPos4[3], mNearPos4[1]
                / mNearPos4[3], mNearPos4[2] / mNearPos4[3]);
        mFarPos.setAll(mFarPos4[0] / mFarPos4[3],
                mFarPos4[1] / mFarPos4[3], mFarPos4[2] / mFarPos4[3]);

        //
        // -- now get the coordinates for the selected object
        //

        double factor = (Math.abs(mObjectGroup.getZ()) + mNearPos.z)
                / (getCurrentCamera().getFarPlane() - getCurrentCamera()
                .getNearPlane());

        mNewObjPos.setAll(mFarPos);
        mNewObjPos.subtract(mNearPos);
        mNewObjPos.multiply(factor);
        mNewObjPos.add(mNearPos);
        mObjectGroup.setX(mNewObjPos.x);
        mObjectGroup.setY(mNewObjPos.y);
        mObjectGroup.setRotY(180);
    }

    public void stopMovingSelectedObject() {
//        mObjectGroup = null;
    }

    public boolean isObjRendered() {
        return objRendered;
    }

    public void setObjRendered(boolean objRendered) {
        this.objRendered = objRendered;
    }
}
