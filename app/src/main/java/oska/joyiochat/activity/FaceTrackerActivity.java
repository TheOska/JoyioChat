/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package oska.joyiochat.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;

import org.rajawali3d.view.ISurface;
import org.rajawali3d.view.SurfaceView;

import java.io.IOException;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import oska.joyiochat.R;
import oska.joyiochat.eventbus.BusStation;
import oska.joyiochat.eventbus.CaptureMessage;
import oska.joyiochat.face.tracker.GraphicFaceTracker;
import oska.joyiochat.listener.VideoCaptureListener;
import oska.joyiochat.permission.FaceTrackingMultiplePermissionListener;
import oska.joyiochat.permission.PermissionErrorListener;
import oska.joyiochat.permission.PermissionHelper;
import oska.joyiochat.rajawali.ObjRender;
import oska.joyiochat.recording.CaptureHelper;
import oska.joyiochat.recording.LetterRecordActivity;
import oska.joyiochat.recording.TelecineService;
import oska.joyiochat.utils.Utils;
import oska.joyiochat.views.CameraSourcePreview;
import oska.joyiochat.views.GraphicOverlay;

import static android.graphics.Bitmap.Config.ARGB_8888;

/**
 * Activity for the face tracker app.  This app detects faces with the rear facing camera, and draws
 * overlay graphics to indicate the position, size, and ID of each face.
 */
public final class FaceTrackerActivity extends AppCompatActivity implements VideoCaptureListener {
    private static final String TAG = "FaceTracker";
    int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    @BindView(R.id.topLayout) LinearLayout llRoot;
    @BindString(R.string.app_name) String appName;
    @BindColor(R.color.primary_normal) int primaryNormal;
    @BindView(R.id.iv_capture_video) ImageView ivCaptureVideo;
    private CameraSource mCameraSource = null;

    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private static final float HIGHEST_FPS = 60.0f;
    private static final float MID_FPS = 40.0f;
    private static final float LOW_FPS = 30.0f;
    private static final float LOWEST_FPS = 24.0f;
    private boolean startedCapturing;
    private Utils mUtils;
    private Activity mRefActivity;
    private TelecineService telecineService;
    private SurfaceView surface;
    ObjRender objRender;

    private MultiplePermissionsListener allPermissionsListener;

    /**
     * Initializes the UI and initiates the creation of a face detector.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_face_detection);
//        Dexter.initialize(this);
        ButterKnife.bind(this);
        checkPermission();
        init3DModelSetting();
        initScreenRecording();


    }
    private void checkPermission() {
        PermissionHelper ph = new PermissionHelper();
        MultiplePermissionsListener mpl = ph.factoryMultiPermissionListener(llRoot);
        PermissionErrorListener pel = new PermissionErrorListener();
        ph.checkAll(this,mpl, pel);

    }

    private void initScreenRecording() {
        startedCapturing = false;
        createPermissionListeners();
        setTaskDescription(new ActivityManager.TaskDescription(appName, rasterizeTaskIcon(), primaryNormal));

        if (checkDrawOverlay()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CaptureHelper.CREATE_SCREEN_CAPTURE);
        }
        if (ContextCompat.checkSelfPermission(FaceTrackerActivity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(FaceTrackerActivity.this,
                    Manifest.permission.READ_CONTACTS)) {
            } else {
                ActivityCompat.requestPermissions(FaceTrackerActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkDrawOverlay(){
        return !Settings.canDrawOverlays(this);
    }
    private void init3DModelSetting() {
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
        mRefActivity = this;

        surface = (SurfaceView) findViewById(R.id.rajawali_surface_view);
        surface.setFrameRate(LOWEST_FPS);
        surface.setRenderMode(ISurface.RENDERMODE_CONTINUOUSLY);
        surface.setTransparent(true);
        objRender = new ObjRender(this);
        surface.setSurfaceRenderer(objRender);

        mUtils = new Utils(this);
    }
    @OnClick(R.id.iv_capture_video)
    public void onClickCapture(){
        if (!startedCapturing) {
            CaptureHelper.fireScreenCaptureIntent(this);
            startedCapturing = true;
        }
        else
            BusStation.getBus().post(new CaptureMessage("stop"));

    }
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    /**
     * Creates and starts the camera.
     */
    private void createCameraSource() {

        Context context = getApplicationContext();
        // --------------------------------------------------------------------------------------------------------------//
        // -----------------------------------------standard face detect in run time (successfully gen 3d obj)------------------------------------//
//        FaceDetector detector = new FaceDetector.Builder(context)
//                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
//                .build();
        // --------------------------------------------------------------------------------------------------------------//
        FaceDetector detector = new FaceDetector.Builder(this)
                .setTrackingEnabled(true)
                .setProminentFaceOnly(true)
//                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();
        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }
        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(LOWEST_FPS)
                .build();

    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();

    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mCameraSource != null) {
//            mCameraSource.release();
//        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    //==============================================================================================
    // Camera Source Preview
    //==============================================================================================

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                // run CameraSourcePreview
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    //==============================================================================================
    // Graphic Face Tracker
    //==============================================================================================

    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay, mRefActivity, getApplicationContext(), objRender, mUtils);
        }
    }


    private void createPermissionListeners() {

        MultiplePermissionsListener feedbackViewMultiplePermissionListener =
                new FaceTrackingMultiplePermissionListener(this);

        allPermissionsListener =
                new CompositeMultiplePermissionsListener(feedbackViewMultiplePermissionListener,
                        SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.with(llRoot,
                                R.string.all_permissions_denied_feedback)
                                .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                                .build());

//        if (Dexter.isRequestOngoing()) {
//            return;
//        }
//        Dexter.checkPermissions(allPermissionsListener, Manifest.permission.SYSTEM_ALERT_WINDOW,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_SETTINGS);
    }

    @NonNull
    private Bitmap rasterizeTaskIcon() {
        Drawable drawable = getResources().getDrawable(R.drawable.ic_videocam_white_24dp, getTheme());

        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int size = am.getLauncherLargeIconSize();
        Bitmap icon = Bitmap.createBitmap(size, size, ARGB_8888);

        Canvas canvas = new Canvas(icon);
        drawable.setBounds(0, 0, size, size);
        drawable.draw(canvas);

        return icon;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!CaptureHelper.handleActivityResult(this, requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == CaptureHelper.CREATE_SCREEN_CAPTURE) {
            if (checkDrawOverlay()) {
                Toast.makeText(this, "not granded", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onStopVideoCapture(String fileName) {

    }
}
