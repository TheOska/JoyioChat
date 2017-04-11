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
public class ObjRender extends MovableObjectRenderer {
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

    public ObjRender(Context context) {
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
                mTextureManager, R.raw.sun_glasses_obj);

        try {
            objParser.parse();
            mObjectGroup = objParser.getParsedObject();
//            mObjectGroup.setMaterial(material);

        } catch (ParsingException e) {
            e.printStackTrace();
        }


        Material sunGlassesMat1 = new Material();
        sunGlassesMat1.setDiffuseMethod(new DiffuseMethod.Lambert());
        sunGlassesMat1.setSpecularMethod(new SpecularMethod.Phong(Color.WHITE, 150));
        sunGlassesMat1.enableLighting(true);


        Material sunGlassesMat2 = new Material();
        sunGlassesMat2.setDiffuseMethod(new DiffuseMethod.Lambert());
        sunGlassesMat2.setSpecularMethod(new SpecularMethod.Phong(Color.WHITE, 150));
        sunGlassesMat2.enableLighting(true);
        try {
            sunGlassesMat1.addTexture(new Texture("sunGlassesMat1", R.drawable.sun_glasses_material3));
            sunGlassesMat2.addTexture(new Texture("sunGlassesMat2", R.drawable.dark_material));

        } catch (ATexture.TextureException e) {
            e.printStackTrace();
        }
        sunGlassesMat1.setColorInfluence(0);
        sunGlassesMat2.setColorInfluence(0);

        for (int i = 0; i < mObjectGroup.getNumChildren(); i++){
            Log.d("oska1234" , "mObjectGroup name " + mObjectGroup.getChildAt(i).getName()  );
        }
        mObjectGroup.getChildAt(0).setMaterial(sunGlassesMat1);
        mObjectGroup.getChildAt(1).setMaterial(sunGlassesMat2);

        initObj(0,0,0,0.35f);

        setChildOffsetPosX(RajawaliUtils.GLASSES_OBJ_OFFSET_X);
        setChildOffsetPosY(RajawaliUtils.GLASSES_OBJ_OFFSET_Y);

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
