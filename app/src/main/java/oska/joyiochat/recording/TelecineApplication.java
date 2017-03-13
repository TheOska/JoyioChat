package oska.joyiochat.recording;

import android.app.Application;
import android.util.Log;


public final class TelecineApplication extends Application {

  private TelecineComponent telecineComponent;

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d("oska", "TelecineApplication");

    telecineComponent = DaggerTelecineComponent.builder()
        .telecineModule(new TelecineModule(this))
        .build();
  }

  final TelecineComponent injector() {
    return telecineComponent;
  }
}
