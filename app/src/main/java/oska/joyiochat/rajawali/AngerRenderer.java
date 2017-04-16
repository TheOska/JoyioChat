package oska.joyiochat.rajawali;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation3D;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.lights.PointLight;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.methods.SpecularMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;

import oska.joyiochat.R;
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
public class AngerRenderer extends MovableObjectRenderer {
    private PointLight mLight;
    private DirectionalLight mDirectionalLight;
    private Context context;

    // Type Object3D can contain more than one 3d elements in one obj file
    private Object3D mObjectGroup;
    private Animation3D  mLightAnim;
    private Camera camera;
    private RenderListener renderListener;
    private final String TAG = "ObjRender";
    private boolean renderCompleted;

    public AngerRenderer(Context context) {
        super(context);
        this.context = context;
        setFrameRate(30);
        this.renderListener = (RenderListener)context;
    }

    @Override
    public void initScene() {
        renderCompleted = false;
        camera = getCurrentCamera();
        initProjection();
        initPicker();
        initLighting();
        camera.setZ(RajawaliUtils.DEFAULT_CAMERA_Z_POS);


        LoaderOBJ objParser = new LoaderOBJ(mContext.getResources(),
                mTextureManager, R.raw.angry2_obj);

        try {
            objParser.parse();
            mObjectGroup = objParser.getParsedObject();
//            mObjectGroup.setMaterial(material);

        } catch (ParsingException e) {
            e.printStackTrace();
        }



        Material angryMaterial = new Material();
        angryMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
        angryMaterial.setSpecularMethod(new SpecularMethod.Phong(Color.WHITE, 150));
        angryMaterial.enableLighting(true);
        try {
            angryMaterial.addTexture(new Texture("angry", R.drawable.cocacola));

        } catch (ATexture.TextureException e) {
            e.printStackTrace();
        }
        angryMaterial.setColorInfluence(0);

        mObjectGroup.getChildAt(0).setMaterial(angryMaterial);
//        mObjectGroup.getChildAt(1).setMaterial(angryMaterial);
//

        for (int i = 0; i < mObjectGroup.getNumChildren(); i++){
            mObjectGroup.getChildAt(i).setMaterial(angryMaterial);
        }
        initObj(0,0,0,5f);
        setObjRotationX(45);
        setChildOffsetPosX(RajawaliUtils.ANGRY_OBJ_OFFSET_X);
        setChildOffsetPosY(RajawaliUtils.ANGRY_OBJ_OFFSET_Y);

        setupLighting();
        renderListener.onRendered();
        setRenderCompleted();
//        mMediaPlayer.start();
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
