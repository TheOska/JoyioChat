package oska.joyiochat.recording;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.otto.Subscribe;

import javax.inject.Inject;
import javax.inject.Provider;

import oska.joyiochat.R;
import oska.joyiochat.eventbus.BusStation;
import oska.joyiochat.eventbus.CaptureMessage;

import static android.app.Notification.PRIORITY_MIN;

public final class TelecineService extends Service {
  private static final String EXTRA_RESULT_CODE = "result-code";
  private static final String EXTRA_DATA = "data";
  private static final int NOTIFICATION_ID = 99118822;
  private static final String SHOW_TOUCHES = "show_touches";
  private static Activity refActivity;
  IBinder mBinder = new LocalBinder();

  static Intent newIntent(Context context, int resultCode, Intent data, Activity activity) {
    Log.d("oska", "TelecineService");
    refActivity = activity;
    Intent intent = new Intent(context, TelecineService.class);
    intent.putExtra(EXTRA_RESULT_CODE, resultCode);
    intent.putExtra(EXTRA_DATA, data);
    return intent;
  }

  @Inject @ShowCountdown Provider<Boolean> showCountdownProvider;
  @Inject @VideoSizePercentage Provider<Integer> videoSizePercentageProvider;
  @Inject @RecordingNotification Provider<Boolean> recordingNotificationProvider;
  @Inject @ShowTouches Provider<Boolean> showTouchesProvider;

  @Inject
  ContentResolver contentResolver;

  private boolean running;
  private RecordingSession recordingSession;

  private final RecordingSession.Listener listener = new RecordingSession.Listener() {
    @Override
    public void onStart() {
      if (showTouchesProvider.get()) {
        Settings.System.putInt(contentResolver, SHOW_TOUCHES, 1);
      }

      if (!recordingNotificationProvider.get()) {
        return; // No running notification was requested.
      }

      Context context = getApplicationContext();
      String title = context.getString(R.string.notification_recording_title);
      String subtitle = context.getString(R.string.notification_recording_subtitle);
      Notification notification = new Notification.Builder(context) //
          .setContentTitle(title)
          .setContentText(subtitle)
          .setSmallIcon(R.drawable.ic_videocam_white_24dp)
          .setColor(context.getResources().getColor(R.color.primary_normal))
          .setAutoCancel(true)
          .setPriority(PRIORITY_MIN)
          .build();

      startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public void onStop() {
      if (showTouchesProvider.get()) {
        Settings.System.putInt(contentResolver, SHOW_TOUCHES, 0);
      }
    }

    @Override
    public void onEnd() {
      stopSelf();
    }
  };

  @Override
  public int onStartCommand(@NonNull Intent intent, int flags, int startId) {
    if (running) {
      return START_NOT_STICKY;
    }
    running = true;

    int resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, 0);
    Intent data = intent.getParcelableExtra(EXTRA_DATA);
    if (resultCode == 0 || data == null) {
      throw new IllegalStateException("Result code or data missing.");
    }

    ((TelecineApplication) getApplication()).injector().inject(this);

    recordingSession =
        new RecordingSession(this, listener, resultCode, data, showCountdownProvider,
            videoSizePercentageProvider, refActivity);
    recordingSession.showOverlay();
    return START_NOT_STICKY;
  }
  @Subscribe
  public void stopRecordingBusStation(CaptureMessage message){
    Log.d("oska","receive bus message");
    if(message.getMsg().toString().equals("stop")){
      recordingSession.stopRecording();

    }
  }  @Override
  public void onDestroy() {
    recordingSession.destroy();
    BusStation.getBus().unregister(this);

    super.onDestroy();
  }

  @Override
  public void onCreate() {
    super.onCreate();
    BusStation.getBus().register(this);

  }

  @Override
  public IBinder onBind(@NonNull Intent intent) {
//    throw new AssertionError("Not supported.");
    return mBinder;
  }

  public class LocalBinder extends Binder {
    public TelecineService getServerInstance() {
      return TelecineService.this;
    }
  }
  public void triggerService(){
    Log.d("oska", "trigger service function");
  }
}
