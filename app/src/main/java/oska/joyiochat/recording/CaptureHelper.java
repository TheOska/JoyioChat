package oska.joyiochat.recording;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.util.Log;

import oska.joyiochat.listener.VideoCaptureListener;

import static android.content.Context.MEDIA_PROJECTION_SERVICE;

public final class CaptureHelper {
  public static final int CREATE_SCREEN_CAPTURE = 4242;
  public static TelecineService mTelecineService;
  private CaptureHelper() {
    Log.d("oska", "CaptureHelper");

    throw new AssertionError("No instances.");
  }

  public static void fireScreenCaptureIntent(Activity activity) {
    Log.d("oska", "fireScreenCaptureIntent");

    MediaProjectionManager manager =
        (MediaProjectionManager) activity.getSystemService(MEDIA_PROJECTION_SERVICE);
    Intent intent = manager.createScreenCaptureIntent();
    // must passed to startActivityForResult() in order to start screen capture
    activity.startActivityForResult(intent, CREATE_SCREEN_CAPTURE);


  }

  public static boolean handleActivityResult(Activity activity, int requestCode, int resultCode,
                                      Intent data) {
    if (requestCode != CREATE_SCREEN_CAPTURE) {
      return false;
    }

    if (resultCode == Activity.RESULT_OK) {
      activity.startService(TelecineService.newIntent(activity.getApplicationContext(), resultCode, data, activity));
    } else {
    }


    return true;
  }

  public static boolean handleActivityResult(Activity activity, int requestCode, int resultCode,
                                             Intent data, VideoCaptureListener videoCaptureListener) {
    if (requestCode != CREATE_SCREEN_CAPTURE) {
      return false;
    }
    if (resultCode == Activity.RESULT_OK) {
      activity.startService(TelecineService.newIntent(activity.getApplicationContext(), resultCode, data, activity));
    } else {
    }


    return true;
  }
}
