package oska.joyiochat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.view.ISurface;
import org.rajawali3d.view.SurfaceView;

import oska.joyiochat.R;
import oska.joyiochat.rajawali.CustomRenderer;
import oska.joyiochat.rajawali.ObjRender;


/**
 * Created by TheOska on 11/20/2016.
 */

public class TestRajawaliActivity extends AppCompatActivity  implements View.OnTouchListener{

    CustomRenderer renderer;

    private SurfaceView surface;
    ObjRender objRender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_raja);

        surface = (SurfaceView) findViewById(R.id.rajawali_surface_view);
        surface.setFrameRate(60.0);
        surface.setRenderMode(ISurface.RENDERMODE_WHEN_DIRTY);
        surface.setTransparent(true);
//
        objRender = new ObjRender(this);
//        renderer = new CustomRenderer(this);
        surface.setSurfaceRenderer(objRender);
        surface.setOnTouchListener(this);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        Log.d("FaceTrackerActivity", "onTouch");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("FaceTrackerActivity", "ACTION_DOWN");
                objRender.getObjectAt(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.d("FaceTrackerActivity", "ACTION_MOVE");
//                objRender.moveSelectedObject(event.getX(),
//                        event.getY());
                break;
            case MotionEvent.ACTION_UP:
                Log.d("FaceTrackerActivity", "ACTION_UP");
                objRender.stopMovingSelectedObject();
                break;
        }
        return true;
    }
}
