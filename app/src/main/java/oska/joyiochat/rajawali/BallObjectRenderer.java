package oska.joyiochat.rajawali;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation;
import org.rajawali3d.animation.Animation3D;
import org.rajawali3d.animation.EllipticalOrbitAnimation3D;
import org.rajawali3d.animation.RotateOnAxisAnimation;
import org.rajawali3d.animation.TranslateAnimation3D;
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
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;

import oska.joyiochat.R;
import oska.joyiochat.listener.RenderListener;
import oska.joyiochat.utils.RajawaliUtils;

/**
 * Created by theoska on 4/8/17.
 */

public class BallObjectRenderer extends Renderer{

    private PointLight mLight;
    private Object3D mObjectGroup;
    private Animation3D mCameraAnim, mLightAnim;
    private Camera camera;
    private Context context;
    private TranslateAnimation3D camHorizontalAnim;
    private RotateOnAxisAnimation camRotationAnim;
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


        getCurrentScene().addLight(mLight);
        getCurrentCamera().setX(3);
        getCurrentCamera().setZ(16);
        getCurrentCamera().setY(-3);


        renderCompleted = false;
        setupTransformMatrix();

        setupLighting();
        camera.setZ(RajawaliUtils.DEFAULT_CAMERA_Z_POS);

        setupObj();
        setupLightingAnim();
        renderListener.onRendered();
    }

    private void setupObj() {
        LoaderOBJ objParser = new LoaderOBJ(mContext.getResources(),
                mTextureManager, R.raw.pokemon_ball_resize_2_obj);
        try {
            objParser.parse();
            mObjectGroup = objParser.getParsedObject();
            getCurrentScene().addChild(mObjectGroup);


            camHorizontalAnim = new TranslateAnimation3D(
                    new Vector3(3, -3, 16),
                    new Vector3(-3, -3 ,16));
            camHorizontalAnim.setDurationMilliseconds(3000);
            camHorizontalAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            camHorizontalAnim.setRepeatMode(Animation.RepeatMode.REVERSE_INFINITE);
            camHorizontalAnim.setTransformable3D(getCurrentCamera());


            Vector3 axis = new Vector3(3, 16, -3);
            axis.normalize();

            camRotationAnim = new RotateOnAxisAnimation(axis, 0, 360);
            camRotationAnim.setDurationMilliseconds(3000);
            camRotationAnim.setInterpolator(new LinearInterpolator());
            camRotationAnim.setRepeatMode(Animation.RepeatMode.REVERSE_INFINITE);
            camRotationAnim.setTransformable3D(mObjectGroup);

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
            roadMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
            roadMaterial.addTexture(new Texture("roadTex", R.drawable.earth_diffuse));
            roadMaterial.setColorInfluence(0);

            mObjectGroup.getChildAt(0).setMaterial(material2);
            for (int i = 0 ; i< mObjectGroup.getNumChildren(); i++ ){
                mObjectGroup.getChildAt(i).setMaterial(material2);
                Log.d("LoadModel" , "num " + i );
            }
//				ATexture aTexture = new Texture("texture" , R.raw.pokeball_mtl);
//				mObjectGroup.getMaterial().addTexture(aTexture);
            mObjectGroup.setScale(0.5);

        } catch (ATexture.TextureException e) {
            e.printStackTrace();
        }

        mLightAnim = new EllipticalOrbitAnimation3D(new Vector3(),
                new Vector3(0, 10, 0), Vector3.getAxisVector(Vector3.Axis.Z), 0,
                360, EllipticalOrbitAnimation3D.OrbitDirection.CLOCKWISE);

        mLightAnim.setDurationMilliseconds(3000);
        mLightAnim.setRepeatMode(Animation.RepeatMode.INFINITE);
        mLightAnim.setTransformable3D(mLight);

        getCurrentScene().registerAnimation(camHorizontalAnim);
        getCurrentScene().registerAnimation(mLightAnim);
        getCurrentScene().registerAnimation(camRotationAnim);
        camHorizontalAnim.play();
        camRotationAnim.play();

    }
    public void stopRendObj(){
        getCurrentScene().removeChild(mObjectGroup);
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



}

