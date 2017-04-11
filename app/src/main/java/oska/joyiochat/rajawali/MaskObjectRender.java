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

public class MaskObjectRender extends MovableObjectRenderer {
    private Object3D mObjectGroup;
    private Camera camera;
    private Context context;
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
        renderCompleted = false;
        camera = getCurrentCamera();
        initProjection();
        initPicker();
        initLighting();
        camera.setZ(RajawaliUtils.DEFAULT_CAMERA_Z_POS);
        LoaderOBJ objParser = new LoaderOBJ(mContext.getResources(),
                mTextureManager, R.raw.themask_o2_obj);

        try {
            objParser.parse();
            mObjectGroup = objParser.getParsedObject();
            mObjectGroup.setScale(0.5f);

        } catch (ParsingException e) {
            e.printStackTrace();
        }

        Material maskMaterial = new Material();
        maskMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
        maskMaterial.setSpecularMethod(new SpecularMethod.Phong(Color.WHITE, 150));
        maskMaterial.enableLighting(true);
        try {
            maskMaterial.addTexture(new Texture("earthDiffuseTex", R.drawable.root_texture));
        } catch (ATexture.TextureException e) {
            e.printStackTrace();
        }
        maskMaterial.setColorInfluence(0);
//
        mObjectGroup.getChildAt(0).setMaterial(maskMaterial);

        renderListener.onRendered();

        initObj(0,0,0,1);
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
