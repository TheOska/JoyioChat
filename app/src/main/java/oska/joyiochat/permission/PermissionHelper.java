package oska.joyiochat.permission;

import android.Manifest;
import android.app.Activity;
import android.view.ViewGroup;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;

import oska.joyiochat.R;


/**
 * Created by theoska on 3/25/17.
 */

public class PermissionHelper {

    public PermissionHelper() {
    }

    public void checkAll(Activity activity, MultiplePermissionsListener allPermissionsListener, PermissionRequestErrorListener errorListener) {
        Dexter.withActivity(activity)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.SYSTEM_ALERT_WINDOW,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_SETTINGS,
                        Manifest.permission.INTERNET
                )


                .withListener(allPermissionsListener)
                .withErrorListener(errorListener)
                .check();
    }


    public MultiplePermissionsListener factoryMultiPermissionListener(ViewGroup rootView) {

        MultiplePermissionsListener feedbackViewMultiplePermissionListener =
                new SimpleMultiplePermissionListener();

        return new CompositeMultiplePermissionsListener(feedbackViewMultiplePermissionListener,
                SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.with(rootView,
                        R.string.all_permissions_denied_feedback)
                        .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                        .build());
    }

}
