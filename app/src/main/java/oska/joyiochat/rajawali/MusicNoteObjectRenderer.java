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

public class MusicNoteObjectRenderer extends MovableObjectRenderer {
    private Object3D mObjectGroup;
    private Camera camera;
    private Context context;
    private RenderListener renderListener;
    private final String TAG = "ObjRender";
    private boolean renderCompleted;

    public MusicNoteObjectRenderer(Context context) {
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
                mTextureManager, R.raw.musicnode2_obj);

        try {
            objParser.parse();
            mObjectGroup = objParser.getParsedObject();

        } catch (ParsingException e) {
            e.printStackTrace();
        }


        for(int i = 0;i<mObjectGroup.getNumChildren() ; i++){
            Log.d("oska","Name : " +mObjectGroup.getChildAt(i).getName());
        }


        initObj(8,10,0,0.35f);

        setPickedObjOffsetY(290);
        setPickedObjOffsetZ(20);
        setChildOffsetPosX(RajawaliUtils.ROSE_OBJ_OFFSET_X);
        setChildOffsetPosY(RajawaliUtils.ROSE_OBJ_OFFSET_Y);
//        setisInteracableObj();
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
