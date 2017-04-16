package oska.joyiochat.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.coremedia.iso.boxes.VideoMediaHeaderBox;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.golshadi.majid.core.DownloadManagerPro;
import com.golshadi.majid.report.listener.DownloadManagerListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import oska.joyiochat.R;
import oska.joyiochat.adapter.JoyioChatAdapter;
import oska.joyiochat.database.VideoModel;
import oska.joyiochat.listener.VideoThumbnailListener;
import oska.joyiochat.module.JoyioChatMessage;
import oska.joyiochat.permission.PermissionErrorListener;
import oska.joyiochat.permission.PermissionHelper;
import oska.joyiochat.recording.AudioVideoMix;
import oska.joyiochat.recording.AudioVideoMixListener;

import static android.os.Environment.DIRECTORY_MOVIES;
import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * Created by theoska on 3/20/17.
 */

public class ChatRoomDetailActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, VideoThumbnailListener, DownloadManagerListener {

    private static final String TAG = "MainActivity";
    public static final String MESSAGES_CHILD = "messages";
    private static final int REQUEST_INVITE = 1;
    private static final int REQUEST_MEDIA = 2;
    private static final int EXTRA_JOYIOCHAT = 5;
    private int selectedType;
    private static final int EXTRA_IMAGE = 3;
    private static final int EXTRA_VIDEO = 4;
    private static final int JOYIOCHAT_REQUEST_CODE = 111;
    public static final String JOYIOMESSAGE_FILE_NAME = "joyioMessageFileName";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private static final String MESSAGE_URL = "http://friendlychat.firebase.google.com/message/";
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    public static final String LOCAL_DIR_JOYIOCHAT = (Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES)) + "";
    public static final String LOCAL_FOLDER = "/JoyioChat/";
    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;

    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    //    private FirebaseRecyclerAdapter<JoyioChatMessage, MessageViewHolder> mFirebaseAdapter;
    private JoyioChatAdapter joyioChatAdapter;
    private ProgressBar mProgressBar;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private EditText mMessageEditText;
    private ImageView mAddMessageImageView;
    //    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private GoogleApiClient mGoogleApiClient;
    private RelativeLayout relativeLayout;
    private Realm realm;
    private DownloadManagerPro downloadManagerPro;
    private String selectedVideoName;
    private String fileFullPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom_detail);
        relativeLayout = (RelativeLayout) findViewById(R.id.rl_root);
        selectedVideoName = "";
        fileFullPath = "";
        checkPermission();
        initDownloadManger();
        getUserInfo();
        initChatRoomMsg();
        initSendMsg();
    }

    private void initDownloadManger() {
        downloadManagerPro = new DownloadManagerPro(this.getApplicationContext());
        downloadManagerPro.init("Movies/JoyioChat/", 3, this);
    }

    private void checkPermission() {
        PermissionHelper ph = new PermissionHelper();
        MultiplePermissionsListener mpl = ph.factoryMultiPermissionListener(relativeLayout);
        PermissionErrorListener pel = new PermissionErrorListener();
        ph.checkAll(this, mpl, pel);

    }


    private void getUserInfo() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUsername = ANONYMOUS;

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

    }

    private void initChatRoomMsg() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .build();
        realm = Realm.getInstance(config);


        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = mFirebaseDatabaseReference.child(MESSAGES_CHILD);
        updateRealm(query);
        joyioChatAdapter = new JoyioChatAdapter(mFirebaseDatabaseReference.child(MESSAGES_CHILD), this, mProgressBar, realm, downloadManagerPro);
        joyioChatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = joyioChatAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(joyioChatAdapter);


    }

    private void updateRealm(Query query) {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        if (issue.hasChild("videoUrl")) {

                            VideoModel videoModel = realm.where(VideoModel.class).equalTo("videoUrl", issue.child("videoUrl").getValue().toString()).findFirst();
                            if (videoModel == null) { // don't have this record, than insert
//                                Log.d("oska123" , "missed video url " +videoModel.getVideoUrl());
                                insertToDB(issue.child("videoUrl").getValue().toString());

                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void initSendMsg() {
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mAddMessageImageView = (ImageView) findViewById(R.id.addMessageImageView);
        mAddMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(ChatRoomDetailActivity.this)
                        .title(R.string.choose_platform)
                        .items(R.array.platform_array)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                intent.addCategory(Intent.CATEGORY_OPENABLE);

                                switch (position) {
                                    case 0:
                                        selectedType = EXTRA_JOYIOCHAT;
                                        startActivityForResult(new Intent(ChatRoomDetailActivity.this, FaceTrackerActivity.class), JOYIOCHAT_REQUEST_CODE);
                                        break;
                                    case 1:
                                        intent.setType("image/*");
                                        selectedType = EXTRA_IMAGE;
                                        startActivityForResult(intent, REQUEST_MEDIA);

                                        break;
                                    case 2:
                                        intent.setType("video/*");
                                        selectedType = EXTRA_VIDEO;
                                        startActivityForResult(intent, REQUEST_MEDIA);


                                        break;
                                    default:
                                        intent.setType("video/*");
                                        selectedType = EXTRA_VIDEO;
                                        startActivityForResult(intent, REQUEST_MEDIA);


                                        break;
                                }

                            }
                        })
                        .show();


            }
        });

        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JoyioChatMessage joyioChatMessage = new JoyioChatMessage(mMessageEditText.getText().toString(), mUsername,
                        mPhotoUrl, null);
                mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(joyioChatMessage);
                mMessageEditText.setText("");
//                mFirebaseAnalytics.logEvent(MESSAGE_SENT_EVENT, null);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_MEDIA) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Log.d("selected", selectedType + "");
                    final Uri uri = data.getData();
                    if (selectedType == EXTRA_VIDEO) {
                        Uri videoThumbnailUri = saveThumbnailBitmap("temp", uri);
                        this.onVideoThumbnailComplete(uri, videoThumbnailUri);

                    } else if (selectedType == EXTRA_IMAGE) {


                        JoyioChatMessage tempMessage = new JoyioChatMessage(null, mUsername, mPhotoUrl,
                                LOADING_IMAGE_URL);
                        mFirebaseDatabaseReference.child(MESSAGES_CHILD).push()
                                .setValue(tempMessage, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError,
                                                           DatabaseReference databaseReference) {
                                        if (databaseError == null) {
                                            String key = databaseReference.getKey();
                                            StorageReference storageReference =
                                                    FirebaseStorage.getInstance()
                                                            .getReference(mFirebaseUser.getUid())
                                                            .child(key)
                                                            .child(uri.getLastPathSegment());
                                            putImageInStorage(storageReference, uri, key);

                                        } else {
                                            Log.w(TAG, "Unable to write message to database.",
                                                    databaseError.toException());
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Something When Wrong", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        } else if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Use Firebase Measurement to log that invitation was sent.
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "inv_sent");

                // Check how many invitations were sent and log.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                Log.d(TAG, "Invitations sent: " + ids.length);
            } else {
                // Use Firebase Measurement to log that invitation was not sent
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "inv_not_sent");
//                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, payload);

                // Sending failed or it was canceled, show failure message to the user
                Log.d(TAG, "Failed to send invitation.");
            }
        } else if (requestCode == JOYIOCHAT_REQUEST_CODE) {
            final String filePath;
            try {
                filePath = data.getStringExtra(JOYIOMESSAGE_FILE_NAME);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            selectedVideoName = data.getStringExtra("videoName");


            final MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                    .title(R.string.progress_dialog)
                    .content(R.string.please_wait)
                    .progress(true, 0)
                    .progressIndeterminateStyle(true)
                    .show();

//
            AudioVideoMix audioVideoMix = new AudioVideoMix(this,LOCAL_DIR_JOYIOCHAT + LOCAL_FOLDER +data.getStringExtra("audioName"),
                    LOCAL_DIR_JOYIOCHAT + LOCAL_FOLDER + data.getStringExtra("videoName"),
                    LOCAL_DIR_JOYIOCHAT + LOCAL_FOLDER + "MIX_"+ data.getStringExtra("videoName"));
            AudioVideoMixListener avml = new AudioVideoMixListener() {
                @Override
                public void onFinishMixing() {

                    Log.d("oska123", "onFinishMixing");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("oska123", "dismiss dialog");
                            materialDialog.dismiss();
                            fileFullPath = LOCAL_DIR_JOYIOCHAT + LOCAL_FOLDER + "MIX_"+ data.getStringExtra("videoName");
                            Uri videoThumbnailUri = saveThumbnailBitmapFromJoyioMessage("temp", filePath);
                            onVideoThumbnailComplete(Uri.parse(filePath), videoThumbnailUri);
                        }
                    });
                }
            };
            audioVideoMix.merge(avml);


        }
    }

    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {

        storageReference.putFile(uri).addOnCompleteListener(ChatRoomDetailActivity.this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            @SuppressWarnings("VisibleForTests")
                            JoyioChatMessage joyioChatMessage =
                                    new JoyioChatMessage(null, mUsername, mPhotoUrl,
                                            task.getResult().getMetadata().getDownloadUrl()
                                                    .toString());
                            mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(key)
                                    .setValue(joyioChatMessage);
                        } else {
                            Log.w(TAG, "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }

    private void putVideoInStorage(final DatabaseReference databaseReference, final StorageReference storageReference, Uri videoUri, final Uri videoThumbnailUri, final String key) {
        final JoyioChatMessage joyioChatMessage = new JoyioChatMessage();
        if (selectedType == EXTRA_JOYIOCHAT) {
            Log.d("oska", "run the extra joyioChat");
            videoUri = getVideoContentUri(this, new File(videoUri.getPath()));
        }
        storageReference.putFile(videoUri).addOnCompleteListener(ChatRoomDetailActivity.this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        @SuppressWarnings("VisibleForTests") Uri downloadUrl = task.getResult().getMetadata().getDownloadUrl();
                        if (task.isSuccessful()) {
                            joyioChatMessage.setText(null);
                            joyioChatMessage.setName(mUsername);
                            joyioChatMessage.setPhotoUrl(mPhotoUrl);
                            joyioChatMessage.setVideoUrl(downloadUrl.toString());
                            joyioChatMessage.setImageUrl(null);
                            uploadVideoThumbnail(videoThumbnailUri, joyioChatMessage, key, downloadUrl.toString());
//                            mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(key)
//                                    .setValue(joyioChatMessage);
                        } else {
                            Log.w(TAG, "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }

    private void uploadVideoThumbnail(Uri videoThumbnailUri, final JoyioChatMessage joyioChatMessage, final String key, final String strDownloadUrl) {
        StorageReference storageThumbnailRef =
                FirebaseStorage.getInstance()
                        .getReference(mFirebaseUser.getUid())
                        .child(key)
                        .child(videoThumbnailUri.getLastPathSegment());

        storageThumbnailRef.putFile(videoThumbnailUri).addOnCompleteListener(ChatRoomDetailActivity.this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            @SuppressWarnings("VisibleForTests") Uri thumbnailUrl = task.getResult().getMetadata().getDownloadUrl();
                            joyioChatMessage.setVideoThumbnailUrl(thumbnailUrl.toString());
                            mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(key)
                                    .setValue(joyioChatMessage);
                            insertToDB(strDownloadUrl);
                        } else
                            Log.w(TAG, "thumbnail image upload task was not successful.", task.getException());
                    }
                });

    }

    private void insertToDB(final String strDownloadUrl) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Number currentIdNum = realm.where(VideoModel.class).max(VideoModel.TAG_ID);
                int maxID;
                if (currentIdNum != null) {
                    maxID = currentIdNum.intValue() + 1;
                } else
                    maxID = 1;
                VideoModel videoModel = new VideoModel();
                videoModel.setId(maxID);
                videoModel.setVideoUrl(strDownloadUrl);
                videoModel.setLocalVideoPath(fileFullPath);
                fileFullPath = "";
                realm.insertOrUpdate(videoModel);
            }
        });

    }

    private Uri saveThumbnailBitmap(String filename, Uri uri) {

        // video URI
        fileFullPath = getRealPathFromURI_API19(uri);
        Log.d("oska123", fileFullPath);

        Bitmap videoThumbnailBitmap = ThumbnailUtils.createVideoThumbnail(fileFullPath,
                MediaStore.Images.Thumbnails.MINI_KIND);


        // video thumbnail path
        String extStorageDirectory = LOCAL_DIR_JOYIOCHAT + LOCAL_FOLDER;
        OutputStream outStream = null;

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(extStorageDirectory);
        myDir.mkdirs();
        File file = new File(myDir, "temp.jpg");
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            videoThumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            return Uri.fromFile(file);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private Uri saveThumbnailBitmapFromJoyioMessage(String filename, String fileFullPath) {

        Bitmap videoThumbnailBitmap = ThumbnailUtils.createVideoThumbnail(fileFullPath,
                MediaStore.Images.Thumbnails.MINI_KIND);


        // video thumbnail path
        String extStorageDirectory = LOCAL_DIR_JOYIOCHAT + LOCAL_FOLDER;
        OutputStream outStream = null;

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(extStorageDirectory);
        myDir.mkdirs();
        File file = new File(myDir, "temp.jpg");
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            videoThumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            return Uri.fromFile(file);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getRealPathFromURI_API19(Uri uri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Video.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Video.Media._ID + "=?";

        Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        cursor.close();

        return filePath;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onVideoThumbnailComplete(final Uri videoUri, final Uri thumbnailUri) {
        Log.d("oska123", "videoUri " + videoUri);
        // Case that browser data from local
        JoyioChatMessage tempMessage = new JoyioChatMessage(null, mUsername, mPhotoUrl,
                LOADING_IMAGE_URL);
        mFirebaseDatabaseReference.child(MESSAGES_CHILD).push()
                .setValue(tempMessage, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError,
                                           DatabaseReference databaseReference) {
                        if (databaseError == null) {

                            String key = databaseReference.getKey();
                            StorageReference storageReference =
                                    FirebaseStorage.getInstance()
                                            .getReference(mFirebaseUser.getUid())
                                            .child(key)
                                            .child(videoUri.getLastPathSegment());
                            putVideoInStorage(databaseReference, storageReference, videoUri, thumbnailUri, key);
                        } else {
                            Log.w(TAG, "Unable to write message to database.",
                                    databaseError.toException());
                        }
                    }
                });
    }


    public Uri getVideoContentUri(Context context, File videoFile) {
        String filePath = videoFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Video.Media._ID},
                MediaStore.Video.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/video/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (videoFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Video.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    @Override
    public void OnDownloadStarted(long taskId) {
        Log.d(TAG, "OnDownloadStarted");

    }

    @Override
    public void OnDownloadPaused(long taskId) {
        Log.d(TAG, "OnDownloadPaused");

    }

    @Override
    public void onDownloadProcess(long taskId, double percent, long downloadedLength) {
        Log.d(TAG, "onDownloadProcess");

    }

    @Override
    public void OnDownloadFinished(long taskId) {
        Log.d(TAG, "OnDownloadFinished");
        joyioChatAdapter.downloadCompleteChange();
    }

    @Override
    public void OnDownloadRebuildStart(long taskId) {
        Log.d(TAG, "OnDownloadRebuildStart");
    }

    @Override
    public void OnDownloadRebuildFinished(long taskId) {
//        Log.d(TAG, "OnDownloadRebuildFinished " +downloadManagerPro.singleDownloadStatus());


    }

    @Override
    public void OnDownloadCompleted(long taskId) {
        Log.d(TAG, "OnDownloadCompleted");

    }

    @Override
    public void connectionLost(long taskId) {
        Log.d(TAG, "connectionLost");

    }
}
