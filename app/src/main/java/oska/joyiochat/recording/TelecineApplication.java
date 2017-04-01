package oska.joyiochat.recording;

import android.app.Application;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;


public final class TelecineApplication extends Application {

  private TelecineComponent telecineComponent;

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d("oska", "TelecineApplication");

    telecineComponent = DaggerTelecineComponent.builder()
        .telecineModule(new TelecineModule(this))
        .build();

    Realm.init(this);

    Stetho.initialize(
            Stetho.newInitializerBuilder(this)
                    .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                    .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                    .build());

  }

  final TelecineComponent injector() {
    return telecineComponent;
  }
}
