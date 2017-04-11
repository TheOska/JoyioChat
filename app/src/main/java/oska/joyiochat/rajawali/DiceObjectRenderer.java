package oska.joyiochat.rajawali;

import android.content.Context;
import android.graphics.Color;
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
import org.rajawali3d.lights.PointLight;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.methods.SpecularMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.Renderer;

import oska.joyiochat.R;

/**
 * Created by theoska on 4/10/17.
 */

public class DiceObjectRenderer extends Renderer {
    private Object3D mObjectGroup;
    private Animation3D mCameraAnim, mLightAnim;
    private TranslateAnimation3D camHorizontalAnim;
    private PointLight mLight;

    private RotateOnAxisAnimation camRotationAnim;

    public DiceObjectRenderer(Context context) {
        super(context);
    }

    @Override
    protected void initScene() {

        mLight = new PointLight();
        mLight.setPosition(0, 0, 4);
        mLight.setPower(3);

        getCurrentScene().addLight(mLight);
        getCurrentCamera().setX(3);
        getCurrentCamera().setZ(16);
        getCurrentCamera().setY(5.5);

        LoaderOBJ objParser = new LoaderOBJ(mContext.getResources(),
                mTextureManager, R.raw.dice_obj);
        try {
            objParser.parse();
            mObjectGroup = objParser.getParsedObject();
            getCurrentScene().addChild(mObjectGroup);


            camHorizontalAnim = new TranslateAnimation3D(
                    new Vector3(3, 3.5, 16),
                    new Vector3(-3, 3.5 ,16));
            camHorizontalAnim.setDurationMilliseconds(3000);
            camHorizontalAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            camHorizontalAnim.setRepeatMode(Animation.RepeatMode.REVERSE_INFINITE);
            camHorizontalAnim.setTransformable3D(getCurrentCamera());

            Vector3 rotationAxis = new Vector3(3, 16, 3);
            rotationAxis.normalize();

            camRotationAnim = new RotateOnAxisAnimation(rotationAxis, 0, 360);
            camRotationAnim.setDurationMilliseconds(3000);
            camRotationAnim.setInterpolator(new LinearInterpolator());
            camRotationAnim.setRepeatMode(Animation.RepeatMode.REVERSE_INFINITE);
            camRotationAnim.setTransformable3D(mObjectGroup);

        } catch (ParsingException e) {
            e.printStackTrace();
        }

        try {


            Material diceMaterial = new Material();
            diceMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
            diceMaterial.setSpecularMethod(new SpecularMethod.Phong(Color.WHITE, 150));
            diceMaterial.enableLighting(true);

            Material darkMaterial = new Material();
            darkMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
            darkMaterial.setSpecularMethod(new SpecularMethod.Phong(Color.WHITE, 150));
            darkMaterial.enableLighting(true);

            try {
                diceMaterial.addTexture(new Texture("rose", R.drawable.cocacola));
                darkMaterial.addTexture(new Texture("dark", R.drawable.dark_material));
            } catch (ATexture.TextureException e) {
                e.printStackTrace();
            }
            darkMaterial.setColorInfluence(0);
            diceMaterial.setColorInfluence(0);
//				rose.setColorInfluence(0);

            mObjectGroup.getChildAt(0).setMaterial(darkMaterial);
            mObjectGroup.getChildAt(1).setMaterial(diceMaterial);

            for (int i = 0; i<mObjectGroup.getNumChildren();i++){
                Log.d("oska123", mObjectGroup.getName());
            }
            mObjectGroup.setScale(1);

        } catch (Exception e) {
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
        mLightAnim.play();

    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {

    }

    @Override
    public void onTouchEvent(MotionEvent event) {

    }
}
