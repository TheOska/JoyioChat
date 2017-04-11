package oska.joyiochat.rajawali;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

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

public class RoseObjectRenderer extends MovableObjectRenderer {
    private Object3D mObjectGroup;
    private Camera camera;
    private Context context;
    private RenderListener renderListener;
    private final String TAG = "ObjRender";
    private boolean renderCompleted;

    public RoseObjectRenderer(Context context) {
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
                mTextureManager, R.raw.rose_tri_resized2_obj);

        try {
            objParser.parse();
            mObjectGroup = objParser.getParsedObject();
            mObjectGroup.setScale(0.5f);

        } catch (ParsingException e) {
            e.printStackTrace();
        }



        Material rose = new Material();
        rose.setDiffuseMethod(new DiffuseMethod.Lambert());
        rose.setSpecularMethod(new SpecularMethod.Phong(Color.WHITE, 150));
        rose.enableLighting(true);

        Material leave = new Material();
        leave.setDiffuseMethod(new DiffuseMethod.Lambert());
        leave.setSpecularMethod(new SpecularMethod.Phong(Color.WHITE, 150));
        leave.enableLighting(true);

        Material root = new Material();
        root.setDiffuseMethod(new DiffuseMethod.Lambert());
        root.setSpecularMethod(new SpecularMethod.Phong(Color.WHITE, 150));
        root.enableLighting(true);

        try {
            rose.addTexture(new Texture("rose", R.drawable.rose));
            leave.addTexture(new Texture("leave", R.drawable.leave));
            root.addTexture(new Texture("root", R.drawable.root));
        } catch (ATexture.TextureException e) {
            e.printStackTrace();
        }
        rose.setColorInfluence(0);
        leave.setColorInfluence(0);
        root.setColorInfluence(0);

        mObjectGroup.getChildByName("Mesh").setMaterial(rose);
        mObjectGroup.getChildByName("pCube1").setMaterial(leave);
//        mObjectGroup.getChildByName("pCube2").setMaterial(leave);
        mObjectGroup.getChildByName("pCube3").setMaterial(leave);
        mObjectGroup.getChildByName("pCylinder1").setMaterial(root);
        for(int i = 0;i<mObjectGroup.getNumChildren() ; i++){
            Log.d("oska","Name : " +mObjectGroup.getChildAt(i).getName());
        }



        initObj(8,10,0,1.2f);
//        setObjRotationY(90);
        setChildOffsetPosX(RajawaliUtils.TEAR_OBJ_OFFSET_X);
        setChildOffsetPosY(RajawaliUtils.TEAR_OBJ_OFFSET_Y);

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
