package oska.joyiochat.permission;

import android.util.Log;

import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;

/**
 * Created by theoska on 3/25/17.
 */

public class PermissionErrorListener implements PermissionRequestErrorListener {
    @Override public void onError(DexterError error) {
        Log.e("Secrush", "There was an error: " + error.toString());
    }
}
