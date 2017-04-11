package oska.joyiochat.utils;

/**
 * Created by TheOska on 11/27/2016.
 */

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

/**
 * class for object rendering offset*/
public class RajawaliUtils {
    public static final int glassObjOffsetX = 30;
    public static final int glassObjOffsetY = 120;

    public static final float DEFAULT_CAMERA_Z_POS = 16f;


    /**
     * position around
     * X: center of two eyes
     * Y: eyes level position
     * */
    public static final int GLASSES_OBJ_OFFSET_X = 30;
    public static final int GLASSES_OBJ_OFFSET_Y = 120;

    public static  int TEAR_OBJ_OFFSET_X = -260;
    public static  int TEAR_OBJ_OFFSET_Y = 300;

    public static int ROSE_OBJ_OFFSET_X = 0;
    public static int ROSE_OBJ_OFFSET_Y = 450;

    public static void changable(final Activity activity){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 50; i++) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ROSE_OBJ_OFFSET_Y += 10;
                }
            }
        });

//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        TEAR_OBJ_OFFSET_X -= 10;
//                        Log.d("oska123", "current Tear X " + TEAR_OBJ_OFFSET_X);
//                    }
//                });
//            }
//        }).start();


    }

}
