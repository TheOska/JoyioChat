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
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.squareup.otto.Subscribe;
import com.wang.avi.AVLoadingIndicatorView;

import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.view.ISurface;
import org.rajawali3d.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import oska.joyiochat.R;
import oska.joyiochat.adapter.EmotionListAdapter;
import oska.joyiochat.eventbus.BusStation;
import oska.joyiochat.eventbus.CaptureMessage;
import oska.joyiochat.eventbus.JoyioVideoMessage;
import oska.joyiochat.face.tracker.GraphicFaceTracker;
import oska.joyiochat.listener.EmotionSelectListener;
import oska.joyiochat.listener.GeneralRecyclerViewTouchListener;
import oska.joyiochat.listener.RenderListener;
import oska.joyiochat.listener.VideoCaptureListener;
import oska.joyiochat.module.EmotionModel;
import oska.joyiochat.permission.FaceTrackingMultiplePermissionListener;
import oska.joyiochat.permission.PermissionErrorListener;
import oska.joyiochat.permission.PermissionHelper;
import oska.joyiochat.rajawali.AngerRenderer;
import oska.joyiochat.rajawali.BirdObjRenderer;
import oska.joyiochat.rajawali.CanvasTextRenderer;
import oska.joyiochat.rajawali.CloudObjRenderer;
import oska.joyiochat.rajawali.BigCryObjectRender;
import oska.joyiochat.rajawali.CupObjRenderer;
import oska.joyiochat.rajawali.DeerObjRenderer;
import oska.joyiochat.rajawali.DiceObjectRenderer;
import oska.joyiochat.rajawali.FootballObjectRenderer;
import oska.joyiochat.rajawali.LikeObjRenderer;
import oska.joyiochat.rajawali.LoveEye2ObjRender;
import oska.joyiochat.rajawali.LoveEyeObjectRender;
import oska.joyiochat.rajawali.MaskObjectRender;
import oska.joyiochat.rajawali.MusicNoteObjectRenderer;
import oska.joyiochat.rajawali.ObjRender;
import oska.joyiochat.rajawali.RoseObjectRenderer;
import oska.joyiochat.rajawali.ShutUpTextRenderer;
import oska.joyiochat.rajawali.TearObjectRender;
import oska.joyiochat.recording.CaptureHelper;
import oska.joyiochat.recording.TelecineService;
import oska.joyiochat.utils.EmotionModelIndex;
import oska.joyiochat.rajawali.PokemonBallObjRenderer;
import oska.joyiochat.utils.Utils;
import oska.joyiochat.views.CameraSourcePreview;
import oska.joyiochat.views.GraphicOverlay;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static android.os.Environment.DIRECTORY_MOVIES;

/**
 * Activity for the face tracker app.  This app detects faces with the rear facing camera, and draws
 * overlay graphics to indicate the position, size, and ID of each face.
 */
public final class FaceTrackerActivity extends AppCompatActivity implements VideoCaptureListener, RenderListener {

    private static final String TAG = "FaceTracker";
    int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    @BindView(R.id.topLayout)
    LinearLayout llRoot;
    @BindString(R.string.app_name)
    String appName;
    @BindColor(R.color.primary_normal)
    int primaryNormal;
    @BindView(R.id.iv_capture_video)
    ImageView ivCaptureVideo;
    @BindView(R.id.rv_emotion_list)
    RecyclerView rvEmotion;
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
    private EmotionSelectListener emotionSelectListener;
    private ArrayList<EmotionModel> emotionModelArrayList;
    public int specOffsetX;
    public int specOffsetY;
    public int specOffsetZ;

    private boolean startedCapturing;
    private Utils mUtils;
    private Activity mRefActivity;
    private TelecineService telecineService;
    private SurfaceView surface;

    ObjRender objRender;
    MaskObjectRender maskObjectRender;
    private CanvasTextRenderer canvasTextRenderer;
    private MultiplePermissionsListener allPermissionsListener;
    private Activity activity;
    private EmotionListAdapter emotionListAdapter;
    private int selectedModelIndex = -1;

    /**
     * Initializes the UI and initiates the creation of a face detector.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
//        BusStation.getBus().register(this);
        activity = this;
        setContentView(R.layout.activity_face_detection);
        ButterKnife.bind(this);
        checkPermission();
        init3DModelSetting();
        initEmotionModel();
        initRecyclerView();

        initScreenRecording();


    }

    private void initEmotionModel() {
        emotionModelArrayList = new ArrayList<>();

        EmotionModel modelSpace = new EmotionModel(0, "null", 0, false);
//        emotionModelArrayList.add(modelSpace);
//        emotionModelArrayList.add(modelSpace);

        EmotionModel model1 = new EmotionModel(1, "camera", R.drawable.ic_camera_white_36dp, true);
        emotionModelArrayList.add(model1);

        EmotionModel model2 = new EmotionModel(EmotionModelIndex.GLASSES_MODEL,
                "glasses", R.drawable.glasses, false);
        emotionModelArrayList.add(model2);

        EmotionModel model3 = new EmotionModel(EmotionModelIndex.QUESTION_MARK_MODEL,
                "question", R.drawable.question_mark, false);
        emotionModelArrayList.add(model3);

        EmotionModel model4 = new EmotionModel(EmotionModelIndex.MASK_MODEL,
                "mask", R.drawable.mask_icon, false);
        emotionModelArrayList.add(model4);

        EmotionModel model5 = new EmotionModel(EmotionModelIndex.TEAR_MODEL,
                "tear", R.drawable.tear_icon, false);
        emotionModelArrayList.add(model5);

        EmotionModel model6 = new EmotionModel(EmotionModelIndex.POKEMON_MODEL,
                "pokemon ball", R.drawable.pokeball_icon, false);
        emotionModelArrayList.add(model6);

        EmotionModel model7 = new EmotionModel(EmotionModelIndex.ROSE_MODEL,
                "pokemon ball", R.drawable.rose_icon, false);
        emotionModelArrayList.add(model7);

        EmotionModel model8 = new EmotionModel(EmotionModelIndex.DICE_MODEL,
                "pokemon ball", R.drawable.dice_icon, false);
        emotionModelArrayList.add(model8);

        EmotionModel model9 = new EmotionModel(EmotionModelIndex.FOOTBALL_MODEL,
                "Football", R.drawable.footbal, false);
        emotionModelArrayList.add(model9);

        EmotionModel model10 = new EmotionModel(EmotionModelIndex.CLOUD_MODEL,
                "cloud", R.drawable.cloud_icon, false);
        emotionModelArrayList.add(model10);

        EmotionModel model11 = new EmotionModel(EmotionModelIndex.ANGRY_MODEL,
                "cloud", R.drawable.angry, false);
        emotionModelArrayList.add(model11);

        EmotionModel model12 = new EmotionModel(EmotionModelIndex.LIKE_MODEL,
                "cloud", R.drawable.like_icon, false);
        emotionModelArrayList.add(model12);

        EmotionModel model13 = new EmotionModel(EmotionModelIndex.BIRD_MODEL,
                "bird", R.drawable.bird_icon, false);
        emotionModelArrayList.add(model13);

        EmotionModel model14 = new EmotionModel(EmotionModelIndex.DRINK_MODEL,
                "drink", R.drawable.coca_drink_icon, false);
        emotionModelArrayList.add(model14);

        EmotionModel model15 = new EmotionModel(EmotionModelIndex.BIG_CRY_MODEL,
                "drink", R.drawable.tear_icon, false);
        emotionModelArrayList.add(model15);


        EmotionModel model16 = new EmotionModel(EmotionModelIndex.DEER_MODEL,
                "drink", R.drawable.deer_icon, false);
        emotionModelArrayList.add(model16);

        EmotionModel model17 = new EmotionModel(EmotionModelIndex.MUSIC_NOTE_MODEL,
                "music", R.drawable.music_note_icon, false);
        emotionModelArrayList.add(model17);

        EmotionModel model18 = new EmotionModel(EmotionModelIndex.SHUT_UP_MODEL,
                "music", R.drawable.shut_up, false);
        emotionModelArrayList.add(model18);

        EmotionModel model19 = new EmotionModel(EmotionModelIndex.LOVE_EYE_MODEL,
                "music", R.drawable.love_eye, false);
        emotionModelArrayList.add(model19);
    }


    private void checkPermission() {
        PermissionHelper ph = new PermissionHelper();
        MultiplePermissionsListener mpl = ph.factoryMultiPermissionListener(llRoot);
        PermissionErrorListener pel = new PermissionErrorListener();
        ph.checkAll(this, mpl, pel);

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
    private boolean checkDrawOverlay() {
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

    public void onClickCapture() {
        if (!startedCapturing) {
            View itemView = rvEmotion.getChildAt(0);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.civ_icon);
            imageView.setImageResource(R.drawable.stop);
//            ((EmotionListAdapter.EmotionViewHolder)rvEmotion.findViewHolderForAdapterPosition(0)).btnStopRecording();
            ivCaptureVideo.setImageDrawable(getResources().getDrawable(R.drawable.stop));
            CaptureHelper.fireScreenCaptureIntent(this);
            startedCapturing = true;
        } else {
//            ivCaptureVideo.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_white_36dp));
            try {
                CaptureMessage captureMessage = new CaptureMessage("stop");
                BusStation.getBus().post(captureMessage);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

    }

    @Subscribe
    public void videoInfoBusStation(JoyioVideoMessage joyioVideoMessage) {
        Log.d("oska", joyioVideoMessage.getVideoName());
        Intent intent = new Intent();
        String dir = Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES) + "/JoyioChat/";
        intent.putExtra("videoName", joyioVideoMessage.getVideoName());
        intent.putExtra("audioName", joyioVideoMessage.getAudioName());

        intent.putExtra(ChatRoomDetailActivity.JOYIOMESSAGE_FILE_NAME, dir + joyioVideoMessage.getVideoName());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void initRecyclerView() {
        emotionListAdapter = new EmotionListAdapter(this, emotionModelArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvEmotion.setLayoutManager(layoutManager);
        rvEmotion.setAdapter(emotionListAdapter);
        rvEmotion.setHasFixedSize(true);
        rvEmotion.addOnItemTouchListener(new GeneralRecyclerViewTouchListener(this, rvEmotion, new GeneralRecyclerViewTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //  minus 2 is for spacing item
                selectedModelIndex = position ;
//                ((EmotionListAdapter.EmotionViewHolder)rvEmotion.findViewHolderForAdapterPosition(position)).startLoading(position);
                int realPosition = position;
                if (realPosition == 0) {

                    onClickCapture();
                } else {

                    View itemView = rvEmotion.getChildAt(selectedModelIndex % 6);
                    AVLoadingIndicatorView loadingIndicatorView = (AVLoadingIndicatorView) itemView.findViewById(R.id.loading_view);
                    loadingIndicatorView.setVisibility(View.GONE);

                    mPreview.removeView(surface);
                    surface = new SurfaceView(getApplicationContext());
                    surface.setFrameRate(LOWEST_FPS);
                    surface.setRenderMode(ISurface.RENDERMODE_CONTINUOUSLY);
                    surface.setTransparent(true);
                    Renderer newRenderer = null;
                    switch (emotionModelArrayList.get(position).getIndex()){
                        case EmotionModelIndex.GLASSES_MODEL:
                            newRenderer = new ObjRender(activity);
                            emotionModelArrayList.get(position).setObjRenderer(newRenderer);
                            break;
                        case EmotionModelIndex.QUESTION_MARK_MODEL:
                            newRenderer = new CanvasTextRenderer(activity);
                            break;

                        case EmotionModelIndex.MASK_MODEL:
                            newRenderer = new MaskObjectRender(activity);
                            emotionModelArrayList.get(position).setObjRenderer(newRenderer);
                            break;

                        case  EmotionModelIndex.TEAR_MODEL:
                            newRenderer = new TearObjectRender(activity);
                            emotionModelArrayList.get(position).setObjRenderer(newRenderer);
                            break;
                        case EmotionModelIndex.POKEMON_MODEL:
                            newRenderer = new PokemonBallObjRenderer(activity);
                            emotionModelArrayList.get(position).setObjRenderer(newRenderer);
                            break;
                        case  EmotionModelIndex.ROSE_MODEL:
                            newRenderer = new RoseObjectRenderer(activity);
                            emotionModelArrayList.get(position).setObjRenderer(newRenderer);
                            break;

                        case EmotionModelIndex.DICE_MODEL:
                            newRenderer = new DiceObjectRenderer(activity);
                            emotionModelArrayList.get(position).setObjRenderer(newRenderer);
                            break;
                        case EmotionModelIndex.FOOTBALL_MODEL:
                            newRenderer = new FootballObjectRenderer(activity);
                            emotionModelArrayList.get(position).setObjRenderer(newRenderer);
                            break;

                        case EmotionModelIndex.CLOUD_MODEL:
                            newRenderer = new CloudObjRenderer(activity);
                            emotionModelArrayList.get(position).setObjRenderer(newRenderer);
                            break;

                        case EmotionModelIndex.ANGRY_MODEL:
                            newRenderer = new AngerRenderer(activity);
                            emotionModelArrayList.get(position).setObjRenderer(newRenderer);
                            break;
                        case EmotionModelIndex.LIKE_MODEL:
                            newRenderer = new LikeObjRenderer(activity);
                            emotionModelArrayList.get(position).setObjRenderer(newRenderer);
                            break;
                        case EmotionModelIndex.BIRD_MODEL:
                            newRenderer = new BirdObjRenderer(activity);
                            emotionModelArrayList.get(position).setObjRenderer(newRenderer);
                            break;
                        case EmotionModelIndex.DRINK_MODEL:
                            newRenderer = new CupObjRenderer(activity);
                            emotionModelArrayList.get(position).setObjRenderer(newRenderer);
                            break;
                        case EmotionModelIndex.BIG_CRY_MODEL:
                            newRenderer = new BigCryObjectRender(activity);
                            emotionModelArrayList.get(position).setObjRenderer(newRenderer);
                            break;
                        case EmotionModelIndex.DEER_MODEL:
                            newRenderer = new DeerObjRenderer(activity);
                            emotionModelArrayList.get(position).setObjRenderer(newRenderer);
                            break;
                        case EmotionModelIndex.MUSIC_NOTE_MODEL:
                            newRenderer = new MusicNoteObjectRenderer(activity);
                            emotionModelArrayList.get(position).setObjRenderer(newRenderer);
                            break;
                        case EmotionModelIndex.SHUT_UP_MODEL:
                            newRenderer = new ShutUpTextRenderer(activity);
                            emotionModelArrayList.get(position).setObjRenderer(newRenderer);
                            break;

                        case EmotionModelIndex.LOVE_EYE_MODEL:
                            newRenderer = new LoveEye2ObjRender(activity);
                            emotionModelArrayList.get(position).setObjRenderer(newRenderer);
                            break;
                    }

                    try {
                        surface.setSurfaceRenderer(newRenderer);
                    } catch (IllegalStateException e) {
                        Toast.makeText(getApplicationContext(), " something went wrong", Toast.LENGTH_SHORT).show();
                    }
                    mPreview.addView(surface, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT

                    ));
                    emotionSelectListener.onSelected(position);



                }


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
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
        BusStation.getBus().register(this);
        startCameraSource();

    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        BusStation.getBus().unregister(this);

        mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mCameraSource != null) {
//            mCameraSource.release();
//        }
    }

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


    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            GraphicFaceTracker graphicFaceTracker = new GraphicFaceTracker(mGraphicOverlay, mRefActivity, getApplicationContext(), emotionModelArrayList, mUtils);
            emotionSelectListener = graphicFaceTracker;
            return graphicFaceTracker;
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

    @Override
    public void onRendered() {
//        objRender.setRenderCompleted();
//        emotionListAdapter.completeLoading();
        if(rvEmotion != null && selectedModelIndex != -1) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    View itemView = rvEmotion.getChildAt(selectedModelIndex% 6);
                    AVLoadingIndicatorView loadingIndicatorView = (AVLoadingIndicatorView) itemView.findViewById(R.id.loading_view);
                    loadingIndicatorView.setVisibility(View.GONE);
                }
            });

        }
    }
}
