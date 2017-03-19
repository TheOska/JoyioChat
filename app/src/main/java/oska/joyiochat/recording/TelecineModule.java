package oska.joyiochat.recording;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.util.Log;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.MODE_PRIVATE;

//contains method with @Provides

/** injected Class
 *  1.BooleanPreference
 *  2.IntPreference
* */
@Module
final class TelecineModule {
  private static final String PREFERENCES_NAME = "telecine";
  private static final boolean DEFAULT_SHOW_COUNTDOWN = false;
  private static final boolean DEFAULT_HIDE_FROM_RECENTS = false;
  private static final boolean DEFAULT_SHOW_TOUCHES = false;
  private static final boolean DEFAULT_RECORDING_NOTIFICATION = false;
  private static final int DEFAULT_VIDEO_SIZE_PERCENTAGE = 100;

  private final TelecineApplication app;

  TelecineModule(TelecineApplication app) {
    this.app = app;
    Log.d("oska", "TelecineModule");

  }



  @Provides @Singleton
  ContentResolver provideContentResolver() {
    return app.getContentResolver();
  }

  @Provides @Singleton
  SharedPreferences provideSharedPreferences() {
    return app.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
  }

  @Provides @Singleton @ShowCountdown BooleanPreference provideShowCountdownPreference(
      SharedPreferences prefs) {
    return new BooleanPreference(prefs, "show-countdown", DEFAULT_SHOW_COUNTDOWN);
  }

  @Provides @ShowCountdown
  Boolean provideShowCountdown(@ShowCountdown BooleanPreference pref) {
    return DEFAULT_SHOW_COUNTDOWN;
  }

  @Provides @Singleton @RecordingNotification
  BooleanPreference provideRecordingNotificationPreference(SharedPreferences prefs) {
    return new BooleanPreference(prefs, "recording-notification", DEFAULT_RECORDING_NOTIFICATION);
  }

  @Provides @RecordingNotification
  Boolean provideRecordingNotification(
      @RecordingNotification BooleanPreference pref) {
    return DEFAULT_RECORDING_NOTIFICATION;
  }

  @Provides @Singleton @HideFromRecents BooleanPreference provideHideFromRecentsPreference(
      SharedPreferences prefs) {
    return new BooleanPreference(prefs, "hide-from-recents", DEFAULT_HIDE_FROM_RECENTS);
  }

  @Provides @Singleton @ShowTouches BooleanPreference provideShowTouchesPreference(
      SharedPreferences prefs) {
    return new BooleanPreference(prefs, "show-touches", DEFAULT_SHOW_TOUCHES);
  }

  @Provides @ShowTouches
  Boolean provideShowTouches(@ShowTouches BooleanPreference pref) {
    return DEFAULT_SHOW_TOUCHES;
  }


  // Video Size percentage is Provides to the Activity
  @Provides @Singleton @VideoSizePercentage IntPreference provideVideoSizePercentagePreference(
      SharedPreferences prefs) {
    return new IntPreference(prefs, "video-size", DEFAULT_VIDEO_SIZE_PERCENTAGE);
  }

  @Provides @VideoSizePercentage
  Integer provideVideoSizePercentage(
      @VideoSizePercentage IntPreference pref) {
    return DEFAULT_VIDEO_SIZE_PERCENTAGE;
  }
}
