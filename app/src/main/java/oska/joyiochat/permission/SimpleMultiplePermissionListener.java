package oska.joyiochat.permission;

import android.util.Log;

import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

/**
 * Created by theoska on 3/25/17.
 */

public class SimpleMultiplePermissionListener  implements MultiplePermissionsListener {

    /**
     * Can config this class to show feedback on View or Others
     * */
//    private final SampleActivity activity;

    public SimpleMultiplePermissionListener() {
//        this.activity = activity;
    }

    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
        for (PermissionGrantedResponse response : report.getGrantedPermissionResponses()) {
//            activity.showPermissionGranted(response.getPermissionName());
            Log.d("Secrush" , response.getPermissionName());
        }

        for (PermissionDeniedResponse response : report.getDeniedPermissionResponses()) {
//            activity.showPermissionDenied(response.getPermissionName(), response.isPermanentlyDenied());
            Log.d("Secrush" , response.getPermissionName() + " " + response.isPermanentlyDenied());

        }
    }

    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions,
                                                             PermissionToken token) {
//        activity.showPermissionRationale(token);
        Log.d("Secrush" ,token + " " );

    }
}
