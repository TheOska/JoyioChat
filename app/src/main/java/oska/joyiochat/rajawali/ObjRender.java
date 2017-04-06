package oska.joyiochat.rajawali;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
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
import org.rajawali3d.animation.RotateOnAxisAnimation;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.lights.PointLight;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.methods.SpecularMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.NormalMapTexture;
import org.rajawali3d.materials.textures.StreamingTexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Cube;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.util.GLU;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;

import javax.microedition.khronos.opengles.GL10;

import oska.joyiochat.R;
import oska.joyiochat.listener.FaceInfoDetectListener;
import oska.joyiochat.listener.RenderListener;
import oska.joyiochat.utils.RajawaliUtils;

/**
 * Created by TheOska on 11/22/2016.
 */

/**
 * This class is aiming to setup and config:
 * 1. Rajawali sense(include light source and camera)
 * 2. 3D object rendering (include render which object for .obj source)
 * 3. 3D object moving(changing the 3D object position)
 */
public class ObjRender extends Renderer implements OnObjectPickedListener {
    private PointLight mLight;
    private DirectionalLight mDirectionalLight;
    private Context context;

    // Type Object3D can contain more than one 3d elements in one obj file
    private Object3D mObjectGroup;
    private Animation3D mCameraAnim, mLightAnim;
    private Camera camera;
    private int[] mViewport;
    private double[] mNearPos4;
    private double[] mFarPos4;
    private Vector3 mNearPos;
    private Vector3 mFarPos;
    private Vector3 mNewObjPos;
    private Matrix4 mViewMatrix;
    private Matrix4 mProjectionMatrix;
    private ObjectColorPicker mPicker;
    private MediaPlayer mMediaPlayer;
    private StreamingTexture mVideoTexture;
    private RenderListener renderListener;
    private final String TAG = "ObjRender";
    private boolean renderCompleted;

    public ObjRender(Context context) {
        super(context);
        this.context = context;
        setFrameRate(30);
        this.renderListener = (RenderListener)context;
    }

    @Override
    public void initScene() {
        renderCompleted = false;
        mViewport = new int[] { 0, 0, getViewportWidth(), getViewportHeight() };
        mNearPos4 = new double[4];
        mFarPos4 = new double[4];
        mNearPos = new Vector3();
        mFarPos = new Vector3();
        mNewObjPos = new Vector3();
        camera = getCurrentCamera();
        mViewMatrix = camera.getViewMatrix();
        mProjectionMatrix = camera.getProjectionMatrix();
        // picker is the property that user can move the object
        mPicker = new ObjectColorPicker(this);
        mPicker.setOnObjectPickedListener(this);

        mLight = new PointLight();
        mLight.setPosition(0, 0, 4);
        mLight.setPower(3);

//        try {
//            Material material = new Material();
//            material.enableLighting(true);
//            material.setDiffuseMethod(new DiffuseMethod.Lambert());
//            material.setSpecularMethod(new SpecularMethod.Phong());
//            mMediaPlayer = MediaPlayer.create(getContext(),
//                    R.raw.sintel_trailer_480p);
//            mMediaPlayer.setLooping(true);
//            mVideoTexture = new StreamingTexture("sintelTrailer", mMediaPlayer);
//        } catch (Resources.NotFoundException e) {
//            e.printStackTrace();
//        }

//        Material material = new Material();
//        material.setColorInfluence(0);
//        try {
//            material.addTexture(mVideoTexture);
//        } catch (ATexture.TextureException e) {
//            e.printStackTrace();
//        }


        getCurrentScene().addLight(mLight);
        camera.setZ(RajawaliUtils.DEFAULT_CAMERA_Z_POS);

        // load obj from resouce
        LoaderOBJ objParser = new LoaderOBJ(mContext.getResources(),
                mTextureManager, R.raw.sun_glasses_obj);

        try {
            objParser.parse();
            mObjectGroup = objParser.getParsedObject();
            mObjectGroup.setScale(0.35f);
//            mObjectGroup.setMaterial(material);

        } catch (ParsingException e) {
            e.printStackTrace();
        }


        try {


            Material material2 = new Material();
            material2.setDiffuseMethod(new DiffuseMethod.Lambert());
            material2.setSpecularMethod(new SpecularMethod.Phong(Color.WHITE, 150));
            material2.enableLighting(true);
            try {
                material2.addTexture(new Texture("earthDiffuseTex", R.drawable.earth_diffuse));
                material2.addTexture(new NormalMapTexture("eartNormalTex", R.drawable.earth_diffuse));
            } catch (ATexture.TextureException e) {
                e.printStackTrace();
            }
            material2.setColorInfluence(0);

            Material roadMaterial = new Material();
            roadMaterial.enableLighting(true);
            roadMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
            roadMaterial.addTexture(new Texture("roadTex", R.drawable.earth_diffuse));
            roadMaterial.setColorInfluence(0);
            mObjectGroup.getChildAt(1).setMaterial(material2);

        } catch (ATexture.TextureException e) {
            e.printStackTrace();
        }

//        Object3D mEarth = new Sphere(1, 32, 32);
//        mEarth.setZ(-.5f);
//        getCurrentScene().addChild(mEarth);



        setupLighting();
        renderListener.onRendered();
//        mMediaPlayer.start();
    }

    private void setupLighting() {
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
    public void onPause() {
        super.onPause();
//        if (mMediaPlayer != null)
//            mMediaPlayer.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (mMediaPlayer != null)
//            mMediaPlayer.start();
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
//        Log.d("ObjRender","moveSelectedObject called");
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

    public void stopMovingSelectedObject() {
//        mObjectGroup = null;
    }

    public void zoomInOutObj(float z){
//        Log.d("zoomInOutObj", "z face index is " + z);
        z = z/ 1.5f;
        camera.setZ(RajawaliUtils.DEFAULT_CAMERA_Z_POS - z);
    }
}
