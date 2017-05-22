package oska.joyiochat.rajawali;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation;
import org.rajawali3d.animation.Animation3D;
import org.rajawali3d.animation.EllipticalOrbitAnimation3D;
import org.rajawali3d.animation.TranslateAnimation3D;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.lights.PointLight;
import org.rajawali3d.materials.textures.StreamingTexture;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.util.GLU;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import oska.joyiochat.listener.RenderListener;
import oska.joyiochat.utils.RajawaliUtils;

/**
 * Created by theoska on 4/9/17.
 */

public abstract class MovableObjectRenderer extends Renderer implements OnObjectPickedListener {

    private PointLight mLight;
    private DirectionalLight mDirectionalLight;
    private Context context;

    // Type Object3D can contain more than one 3d elements in one obj file
    private Object3D mObjectGroup;
    private Animation3D mCameraAnim, mLightAnim;
    private Camera camera;
    private int[] mViewport = new int[] { 0, 0, getViewportWidth(), getViewportHeight() };
    private double[] mNearPos4 = new double[4];
    private double[] mFarPos4 = new double[4];
    private Vector3 mNearPos = new Vector3();
    private Vector3 mFarPos = new Vector3();
    private Vector3 mNewObjPos  = new Vector3();
    private Matrix4 mViewMatrix;
    private Matrix4 mProjectionMatrix;
    private ObjectColorPicker mPicker;
    private MediaPlayer mMediaPlayer;
    private StreamingTexture mVideoTexture;
    private RenderListener renderListener;
    private final String TAG = "MovableObjectRenderer";
    private boolean renderCompleted;
    private boolean isInteractiveObj;
    private boolean isFirstRunObj;
    private float childOffsetX = 0;
    private float childOffsetY = 0;
    private float childOffsetZ = 0;
    private TranslateAnimation3D cameraAnimation;
    private boolean isSetAnimation = false;

    // offset that after 3d projection
    private float pickedObjOffsetY;
    private float pickedObjOffsetX;
    private float pickedObjOffsetZ;

    private float previousX = -1;
    private float previousY = -1;
    private List<Float> smoothingListX;
    private List<Float> smoothingListY;

    /**
     * Fixed position around
     * X : center of two eyes
     * Y : eyes position
     * all movable positon are starting from that index
     * */
    private final int CENTER_POSITION_X = 3;
    private final int HEAD_POSITION_Y = 3;
    private final int CENTER_PROJECTION_Z = 16;

    public MovableObjectRenderer(Context context) {
        super(context);
        this.context = context;
        setFrameRate(45);
        this.renderListener = (RenderListener)context;
        isInteractiveObj = false;
        // default value
        pickedObjOffsetY = 180;
        pickedObjOffsetX = 0;
        pickedObjOffsetZ = 0;
        smoothingListX = new ArrayList<>();
        smoothingListY = new ArrayList<>();

    }

    public MovableObjectRenderer(Context context, Object3D mObjectGroup ) {
        super(context);
        this.context = context;
        setFrameRate(45);
        this.renderListener = (RenderListener)context;
        this.mObjectGroup = mObjectGroup;
        smoothingListX = new ArrayList<>();
        smoothingListY = new ArrayList<>();


    }

    protected void initLighting(){
        mLight = new PointLight();
        mLight.setPosition(0, 0, 4);
        mLight.setPower(3);


        getCurrentScene().addLight(mLight);
    }
    protected void setupLighting() {

        mLightAnim = new EllipticalOrbitAnimation3D(new Vector3(),
                new Vector3(0, 10, 0), Vector3.getAxisVector(Vector3.Axis.Z), 0,
                360, EllipticalOrbitAnimation3D.OrbitDirection.CLOCKWISE);

        mLightAnim.setDurationMilliseconds(3000);
        mLightAnim.setRepeatMode(Animation.RepeatMode.INFINITE);
        mLightAnim.setTransformable3D(mLight);
        getCurrentScene().registerAnimation(mLightAnim);

        mLightAnim.play();
    }

    protected void setScale(float scaleAmount){
        if(mObjectGroup != null)
            mObjectGroup.setScale(scaleAmount);
    }
    protected void setChildOffsetPosX(float offsetX){
        if(mObjectGroup != null){
            childOffsetX = offsetX;

        }
    }

    protected void setChildOffsetPosY(float offsetY){
        if(mObjectGroup != null){
            childOffsetY = offsetY;
        }
    }
    protected void setChildOffsetPosZ(float offsetZ){
        if(mObjectGroup != null)
            childOffsetZ = offsetZ;
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
//        mVideoTexture.update();
    }

    @Override
    public void onObjectPicked(@NonNull Object3D object) {
        mObjectGroup = object;

    }


    @Override
    public void onNoObjectPicked() {

    }

    @Override
    public void onRenderSurfaceDestroyed(SurfaceTexture surfaceTexture) {
        super.onRenderSurfaceDestroyed(surfaceTexture);
//        mMediaPlayer.stop();
//        mMediaPlayer.release();
    }
    public void onRenderSurfaceSizeChanged(GL10 gl, int width, int height) {
        super.onRenderSurfaceSizeChanged(gl, width, height);
//        Log.d(TAG,"onRenderSurfaceSizeChanged called"  );
        mViewport[2] = getViewportWidth();
        mViewport[3] = getViewportHeight();
        mViewMatrix = getCamera().getViewMatrix();
        mProjectionMatrix = getCamera().getProjectionMatrix();
    }
    public void getObjectAt(float x, float y) {
        mPicker.getObjectAt(x, y);
    }


    protected void initProjection(){
        mViewMatrix = getCamera().getViewMatrix();
        mProjectionMatrix = getCamera().getProjectionMatrix();
    }
    protected void initPicker(){
        mPicker = new ObjectColorPicker(this);
        mPicker.setOnObjectPickedListener(this);

    }

    protected void initObj(int posX, int posY, int posZ, float scale){
        mObjectGroup = getObject3D();
        mPicker.registerObject(mObjectGroup);
        mObjectGroup.setDrawingMode(GLES20.GL_TRIANGLES);
        mObjectGroup.setPosition(posX, posY, posZ);
        mObjectGroup.setScale(scale);
    }
    protected void setObjRotationX(int degree){
        mObjectGroup.setRotX(degree);
    }
    protected void setObjRotationY(int degree){
        mObjectGroup.setRotY(degree);
    }
    protected void setObjRotationZ(int degree){
        mObjectGroup.setRotZ(degree);
    }
    /**
     * After gluUnProject the 3D Object:
     * Camera will not after, include camera rotation, distance between scene and camera
     */
    public void moveSelectedObject(float x, float y, float rotationY) {
        if (mObjectGroup == null){

            Log.d(TAG, "mObjectGroup is null");
            mObjectGroup = getObject3D();
        }
        if( mObjectGroup == null) {
            Log.d(TAG, "mObjectGroup is null AGAIN!!!!! " + renderCompleted );
            return;
        }

        if(previousX != -1) {
            x += childOffsetX;
            x = (x+previousX) / 2;
            y += childOffsetY;
            y = (y+previousY)/2;
        }

//        if(smoothingListX.size() != 5){
//            smoothingListX.add(x);
//        }else{
//            smoothingListX.remove(0);
//            smoothingListX.add(x);
//            x = averageSmoothingData(smoothingListX);
//        }
//
//        if(smoothingListY.size() != 5){
//            smoothingListY.add(y);
//        }else{
//            smoothingListY.remove(0);
//            smoothingListY.add(y);
//            y = averageSmoothingData(smoothingListY);
//        }

        GLU.gluUnProject(x, getViewportHeight() - y, 0, mViewMatrix.getDoubleValues(), 0,
                mProjectionMatrix.getDoubleValues(), 0, mViewport, 0, mNearPos4, 0);

        GLU.gluUnProject(x, getViewportHeight() - y, 1.f, mViewMatrix.getDoubleValues(), 0,
                mProjectionMatrix.getDoubleValues(), 0, mViewport, 0, mFarPos4, 0);

        mNearPos.setAll(mNearPos4[0] / mNearPos4[3], mNearPos4[1]
                / mNearPos4[3], mNearPos4[2] / mNearPos4[3]);
        mFarPos.setAll(mFarPos4[0] / mFarPos4[3],
                mFarPos4[1] / mFarPos4[3], mFarPos4[2] / mFarPos4[3]);

        double factor = (Math.abs(mObjectGroup.getZ()) + mNearPos.z)
                / (getCamera().getFarPlane() - getCamera()
                .getNearPlane());

        mNewObjPos.setAll(mFarPos);
        mNewObjPos.subtract(mNearPos);
        mNewObjPos.multiply(factor);
        mNewObjPos.add(mNearPos);
        mObjectGroup.setX(mNewObjPos.x);
        mObjectGroup.setY(mNewObjPos.y);
        if(isInteractiveObj == false) {
            mObjectGroup.setRotY(pickedObjOffsetY + rotationY );
            mObjectGroup.setRotX(pickedObjOffsetX);
            mObjectGroup.setRotZ(pickedObjOffsetZ);
        }
        else
            mObjectGroup.setRotation(new Vector3(mNewObjPos.x, mNewObjPos.y, mObjectGroup.getZ()), 180);
        if(isSetAnimation){
            cameraAnimation.getTransformable3D().setPosition(mNewObjPos.x, mNewObjPos.y,mObjectGroup.getZ());
        }
        previousX = x;
        previousY = y;

    }

    private float averageSmoothingData(List<Float> smoothingList) {
        float averageData = 0;
        for(float s : smoothingList){
            averageData += s;
        }
        return averageData / smoothingList.size();
    }

    protected void setCameraAnimation(TranslateAnimation3D animation){
        cameraAnimation = animation;
        isSetAnimation = true;
    }
    protected void setPickedObjOffsetY(float pickedObjOffsetY){
        this.pickedObjOffsetY = pickedObjOffsetY;
    }
    protected void setPickedObjOffsetX(float pickedObjOffsetX){
        this.pickedObjOffsetX = pickedObjOffsetX;
    }
    protected void setPickedObjOffsetZ(float pickedObjOffsetZ){
        this.pickedObjOffsetY = pickedObjOffsetY;
    }
    public void stopRendObj(){
        getCurrentScene().removeChild(mObjectGroup);
    }

    public void setRenderCompleted(){
        renderCompleted = true;
    }
    public boolean getRenderCompleted(){
        return renderCompleted;
    }
    public void startRendObj(){
        if(renderCompleted == true) {
            Log.d("oska", "startRendObj");
            getCurrentScene().addChild(mObjectGroup);
        }
    }
    public void setisInteracableObj(){
        isInteractiveObj = true;
    }

    public void setObjectPostionX(float postionX){
        mObjectGroup.setX(postionX);
    }

    public void stopMovingSelectedObject() {
//        mObjectGroup = null;
    }

    public void zoomInOutObj(float z){
        z = z/ 1.5f;
//        getCamera().setZ(RajawaliUtils.DEFAULT_CAMERA_Z_POS - z);
    }
    public abstract Object3D getObject3D();

    public abstract Camera getCamera();
}
