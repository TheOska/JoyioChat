package oska.joyiochat.rajawali;

import android.content.Context;
import android.graphics.Color;

import org.rajawali3d.Object3D;
import org.rajawali3d.cameras.Camera;
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
 * Created by theoska on 4/10/17.
 */

public class TearObjectRender extends MovableObjectRenderer {
    private Object3D mObjectGroup;
    private Camera camera;
    private Context context;
    private RenderListener renderListener;
    private final String TAG = "ObjRender";
    private boolean renderCompleted;

    public TearObjectRender(Context context) {
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
                mTextureManager, R.raw.cry_obj);

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
            tearMaterial.addTexture(new Texture("tear", R.drawable.tear_material));
        } catch (ATexture.TextureException e) {
            e.printStackTrace();
        }
        tearMaterial.setColorInfluence(0);
//
        mObjectGroup.getChildAt(0).setMaterial(tearMaterial);
        mObjectGroup.setScale(3);
        renderListener.onRendered();

        initObj();
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
