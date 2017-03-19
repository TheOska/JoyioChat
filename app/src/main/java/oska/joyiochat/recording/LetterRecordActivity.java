package oska.joyiochat.recording;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.TaskDescription;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;


import com.github.ybq.android.spinkit.style.Wave;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import oska.joyiochat.R;
import oska.joyiochat.permission.TempMultiplePermissionListener;

import static android.graphics.Bitmap.Config.ARGB_8888;

public final class LetterRecordActivity extends AppCompatActivity {
    int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    @BindView(R.id.spinner_video_size_percentage)
    Spinner videoSizePercentageView;

    @BindView(R.id.switch_show_countdown)
    Switch showCountdownView;

    @BindView(R.id.switch_hide_from_recents)
    Switch hideFromRecentsView;

    @BindView(R.id.switch_recording_notification)
    Switch recordingNotificationView;

    @BindView(R.id.switch_show_touches)
    Switch showTouchesView;

    @BindView(R.id.launch)
    View launchView;

    @BindView(R.id.merge)
    Button btnMerge;

    @BindString(R.string.app_name)
    String appName;

    @BindColor(R.color.primary_normal)
    int primaryNormal;

    @Inject
    @VideoSizePercentage
    IntPreference videoSizePreference;

    @Inject
    @ShowCountdown
    BooleanPreference showCountdownPreference;

    @Inject
    @HideFromRecents
    BooleanPreference hideFromRecentsPreference;

    @Inject
    @RecordingNotification
    BooleanPreference recordingNotificationPreference;

    @Inject
    @ShowTouches
    BooleanPreference showTouchesPreference;

    @BindView(R.id.frame_root)
    FrameLayout rootView;

    private MultiplePermissionsListener allPermissionsListener;
    private VideoSizePercentageAdapter videoSizePercentageAdapter;
    private Wave mWaveDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ("true".equals(getIntent().getStringExtra("crash"))) {
            throw new RuntimeException("Crash! Bang! Pow! This is only a test...");
        }

        ((TelecineApplication) getApplication()).injector().inject(this);

        setContentView(R.layout.activity_recording);
        ButterKnife.bind(this);
        Dexter.initialize(this);
        createPermissionListeners();
        initViews();
        Log.d("oska", "video size preference " + videoSizePreference.get());
        Log.d("oska", "showCountdownPreference preference " + showCountdownPreference.get());
        Log.d("oska", "hideFromRecentsPreference preference " + hideFromRecentsPreference.get());
        Log.d("oska", "recordingNotificationPreference preference " + recordingNotificationPreference.get());
        Log.d("oska", "showTouchesPreference preference " + showTouchesPreference.get());

        setTaskDescription(new TaskDescription(appName, rasterizeTaskIcon(), primaryNormal));

        videoSizePercentageAdapter = new VideoSizePercentageAdapter(this);

        videoSizePercentageView.setAdapter(videoSizePercentageAdapter);
        videoSizePercentageView.setSelection(
                VideoSizePercentageAdapter.getSelectedPosition(videoSizePreference.get()));

        showCountdownView.setChecked(showCountdownPreference.get());
        hideFromRecentsView.setChecked(hideFromRecentsPreference.get());
        recordingNotificationView.setChecked(recordingNotificationPreference.get());
        showTouchesView.setChecked(showTouchesPreference.get());

        /* fire capture video */
        if (checkDrawOverlay()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CaptureHelper.CREATE_SCREEN_CAPTURE);
        }

        if (ContextCompat.checkSelfPermission(LetterRecordActivity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LetterRecordActivity.this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(LetterRecordActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
//        hideStatusBar();
    }

    private void initViews() {
        mWaveDrawable = new Wave();
        mWaveDrawable.setBounds(0,0,100,100);
        mWaveDrawable.setColor(getResources().getColor(R.color.colorAccent));

    }

    private void createPermissionListeners() {

        MultiplePermissionsListener feedbackViewMultiplePermissionListener =
                new TempMultiplePermissionListener(this);
        allPermissionsListener =
                new CompositeMultiplePermissionsListener(feedbackViewMultiplePermissionListener,
                        SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.with(rootView,
                                R.string.all_permissions_denied_feedback)
                                .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                                .build());

        if (Dexter.isRequestOngoing()) {
            return;
        }
        Dexter.checkPermissions(allPermissionsListener, Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_SETTINGS);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkDrawOverlay(){
        return !Settings.canDrawOverlays(this);
    }
    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
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

    @OnClick(R.id.launch)
    void onLaunchClicked() {
        CaptureHelper.fireScreenCaptureIntent(this);
    }

    @OnClick(R.id.merge)
    void onBtnMergeClicked() {
        btnMerge.setCompoundDrawables(mWaveDrawable,null , null, null);
        mWaveDrawable.start();
        AudioVideoMix audioVideoMix = new AudioVideoMix(this);
        AudioVideoMixListener avml = new AudioVideoMixListener() {
            @Override
            public void onFinishMixing() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mWaveDrawable.stop();
                        btnMerge.setCompoundDrawables(null, null, null, null);
                        btnMerge.setText("Finish");
                    }
                });
            }
        };
        audioVideoMix.merge(avml);
    }

    @OnItemSelected(R.id.spinner_video_size_percentage)
    void onVideoSizePercentageSelected(
            int position) {
        int newValue = videoSizePercentageAdapter.getItem(position);
        int oldValue = videoSizePreference.get();
        if (newValue != oldValue) {
            videoSizePreference.set(newValue);

        }
    }

    @OnCheckedChanged(R.id.switch_show_countdown)
    void onShowCountdownChanged() {
        boolean newValue = showCountdownView.isChecked();
        boolean oldValue = showCountdownPreference.get();
        if (newValue != oldValue) {
            showCountdownPreference.set(newValue);


        }
    }

    @OnCheckedChanged(R.id.switch_hide_from_recents)
    void onHideFromRecentsChanged() {
        boolean newValue = hideFromRecentsView.isChecked();
        boolean oldValue = hideFromRecentsPreference.get();
        if (newValue != oldValue) {
            hideFromRecentsPreference.set(newValue);


        }
    }

    @OnCheckedChanged(R.id.switch_recording_notification)
    void onRecordingNotificationChanged() {
        boolean newValue = recordingNotificationView.isChecked();
        boolean oldValue = recordingNotificationPreference.get();
        if (newValue != oldValue) {
            recordingNotificationPreference.set(newValue);


        }
    }

    @OnCheckedChanged(R.id.switch_show_touches)
    void onShowTouchesChanged() {
        boolean newValue = showTouchesView.isChecked();
        boolean oldValue = showTouchesPreference.get();
        if (newValue != oldValue) {
            showTouchesPreference.set(newValue);

        }
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
    protected void onStop() {
        super.onStop();
        if (hideFromRecentsPreference.get() && !isChangingConfigurations()) {
            finishAndRemoveTask();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

    }
}
