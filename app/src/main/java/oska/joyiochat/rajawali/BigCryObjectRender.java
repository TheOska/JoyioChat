package oska.joyiochat.rajawali;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation;
import org.rajawali3d.animation.TranslateAnimation3D;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.methods.SpecularMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;

import oska.joyiochat.R;
import oska.joyiochat.listener.RenderListener;
import oska.joyiochat.utils.RajawaliUtils;

/**
 * Created by theoska on 4/10/17.
 */

public class BigCryObjectRender extends MovableObjectRenderer {
    private Object3D mObjectGroup;
    private Camera camera;
    private Context context;
    private RenderListener renderListener;
    private final String TAG = "ObjRender";
    private boolean renderCompleted;
    private TranslateAnimation3D camHorizontalAnim;

    public BigCryObjectRender(Context context) {
        super(context);
        this.context = context;
        renderListener = (RenderListener)context;
        setFrameRate(60);
    }

    @Override
    protected void initScene() {
        renderCompleted = false;
        camera = getCurrentCamera();
        initProjection();
        initPicker();
        initLighting();
        camera.setZ(RajawaliUtils.DEFAULT_CAMERA_Z_POS);
        LoaderOBJ objParser = new LoaderOBJ(mContext.getResources(),
                mTextureManager, R.raw.cry2_obj);

        try {
            objParser.parse();
            mObjectGroup = objParser.getParsedObject();
            mObjectGroup.setScale(0.5f);

        } catch (ParsingException e) {
            e.printStackTrace();
        }

        Material tearMaterial = new Material();
        tearMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
        tearMaterial.setSpecularMethod(new SpecularMethod.Phong(Color.WHITE, 150));
        tearMaterial.enableLighting(true);
        try {
            tearMaterial.addTexture(new Texture("tear", R.drawable.crytexture));
        } catch (ATexture.TextureException e) {
            e.printStackTrace();
        }
        tearMaterial.setColorInfluence(0);
//
        mObjectGroup.getChildAt(0).setMaterial(tearMaterial);
        mObjectGroup.getChildAt(1).setMaterial(tearMaterial);

        Log.d("oska123", "texture number " + mObjectGroup.getNumChildren() );
        for(int i = 0 ; i< mObjectGroup.getNumChildren(); i++){
            mObjectGroup.getChildAt(i);
        }
//        mObjectGroup.setScale(3);
        renderListener.onRendered();

        initObj(8,-5,0,5);
        setChildOffsetPosX(RajawaliUtils.TEAR_OBJ_OFFSET_X);
        setChildOffsetPosY(RajawaliUtils.TEAR_OBJ_OFFSET_Y);

//        camHorizontalAnim.getTransformable3D().setPosition();
        camHorizontalAnim = new TranslateAnimation3D(
                new Vector3(3, -4, 16),
                new Vector3(-3, -4 ,16));
        camHorizontalAnim.setDurationMilliseconds(2000);
        camHorizontalAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        camHorizontalAnim.setRepeatMode(Animation.RepeatMode.REVERSE_INFINITE);
        camHorizontalAnim.setTransformable3D(getCurrentCamera());

        getCurrentScene().registerAnimation(camHorizontalAnim);
        camHorizontalAnim.play();
        setCameraAnimation(camHorizontalAnim);
//        setObjectPostionX(6);
        setupLighting();

        renderListener.onRendered();
        setRenderCompleted();

    }


    @Override
    public Object3D getObject3D() {
        return mObjectGroup;
    }

    @Override
    public Camera getCamera() {
        return camera;
    }

}
