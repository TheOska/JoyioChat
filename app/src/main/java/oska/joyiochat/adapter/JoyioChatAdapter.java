package oska.joyiochat.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.golshadi.majid.core.DownloadManagerPro;
import com.golshadi.majid.report.listener.DownloadManagerListener;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import oska.joyiochat.R;
import oska.joyiochat.activity.ChatRoomDetailActivity;
import oska.joyiochat.activity.MainActivity;
import oska.joyiochat.database.VideoModel;
import oska.joyiochat.module.JoyioChatMessage;

import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * Created by theoska on 3/29/17.
 */

public class JoyioChatAdapter extends FirebaseRecyclerAdapter<JoyioChatMessage, JoyioChatAdapter.MessageViewHolder> {

    private static final String TAG = JoyioChatAdapter.class.getName();
    private Activity activity;
    private ProgressBar mProgressBar;
    private List<JoyioChatMessage> joyioChatMessages;
    private DownloadManagerPro downloadManagerPro;
    private MessageViewHolder joyioMessageViewHolder;
    private int clickedPosition;
    int count = 0;
    private Realm realm;
    private Query refQuery;
    public JoyioChatAdapter(Query ref, Activity activity, ProgressBar mProgressBar, Realm realm, DownloadManagerPro downloadManagerPro) {
        super(JoyioChatMessage.class, R.layout.item_message, MessageViewHolder.class, ref);
        this.activity = activity;
        this.mProgressBar = mProgressBar;
        joyioChatMessages = new ArrayList<>();
        refQuery =ref;
        this.realm = realm;
        this.downloadManagerPro = downloadManagerPro;
    }

    @Override
    protected JoyioChatMessage parseSnapshot(DataSnapshot snapshot) {
        JoyioChatMessage joyioChatMessage = super.parseSnapshot(snapshot);
        if (joyioChatMessage != null) {
            joyioChatMessage.setId(snapshot.getKey());
        }
        return joyioChatMessage;
    }



    @Override
    protected void populateViewHolder(final MessageViewHolder viewHolder, JoyioChatMessage joyioChatMessage, int position) {
        ++count;
        Log.d("oska" ,"count " + count);
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        joyioChatMessages.add(joyioChatMessage);
        if (joyioChatMessage.getText() != null) {
            viewHolder.messageTextView.setText(joyioChatMessage.getText());
            viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
            viewHolder.messageImageView.setVisibility(ImageView.GONE);
            viewHolder.rlVideoContainer.setVisibility(View.GONE);

        } else if(joyioChatMessage.getImageUrl() != null){
            String imageUrl = joyioChatMessage.getImageUrl();
            if (imageUrl.startsWith("gs://")) {
                StorageReference storageReference = FirebaseStorage.getInstance()
                        .getReferenceFromUrl(imageUrl);
                storageReference.getDownloadUrl().addOnCompleteListener(
                        new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    String downloadUrl = task.getResult().toString();

                                    Glide.with(viewHolder.messageImageView.getContext())
                                            .load(downloadUrl)
                                            .into(viewHolder.messageImageView);
                                } else {
                                    Log.w(TAG, "Getting download url was not successful.",
                                            task.getException());
                                }
                            }
                        });
            } else {
                Glide.with(viewHolder.messageImageView.getContext())
                        .load(joyioChatMessage.getImageUrl())
                        .into(viewHolder.messageImageView);
            }
            viewHolder.messageImageView.setVisibility(ImageView.VISIBLE);
            viewHolder.messageTextView.setVisibility(TextView.GONE);
            viewHolder.rlVideoContainer.setVisibility(View.GONE);

        }else{


            String thumbnailUrl = joyioChatMessage.getVideoThumbnailUrl();
            if (thumbnailUrl.startsWith("gs://")) {
                Log.d("oska123", " video is start from gs://");
                StorageReference storageReference = FirebaseStorage.getInstance()
                        .getReferenceFromUrl(thumbnailUrl);
                storageReference.getDownloadUrl().addOnCompleteListener(
                        new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    String downloadUrl = task.getResult().toString();

                                    Glide.with(viewHolder.ivVideoThumbnail.getContext())
                                            .load(downloadUrl)
                                            .into(viewHolder.ivVideoThumbnail);
                                } else {
                                    Log.w(TAG, "Getting download url was not successful.",
                                            task.getException());
                                }
                            }
                        });
            } else {
                Log.d("oska123", " inside else");
                VideoModel videoModel = realm.where(VideoModel.class).equalTo("videoUrl", joyioChatMessage.getVideoUrl()).equalTo("localVideoPath", "").findFirst();
                if(videoModel != null) {
                    Log.d("oska123", " video model not null");
                    viewHolder.ivDownload.setVisibility(View.VISIBLE);
                    viewHolder.ivPlay.setVisibility(View.GONE);
                }else{
                    Log.d("oska123", "video model is null R");
                    viewHolder.ivDownload.setVisibility(View.GONE);
                    viewHolder.ivPlay.setVisibility(View.VISIBLE);
                }
                Glide.with(viewHolder.ivVideoThumbnail.getContext())
                        .load(joyioChatMessage.getVideoThumbnailUrl())
                        .into(viewHolder.ivVideoThumbnail);
            }
            viewHolder.messageImageView.setVisibility(ImageView.GONE);
            viewHolder.messageTextView.setVisibility(TextView.GONE);
            viewHolder.rlVideoContainer.setVisibility(View.VISIBLE);
        }


        // SET username on screen conand i
        viewHolder.messengerTextView.setText(joyioChatMessage.getName());
        if (joyioChatMessage.getPhotoUrl() == null) {
            viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(activity,
                    R.drawable.ic_account_circle_black_36dp));
        } else {
            Glide.with(activity)
                    .load(joyioChatMessage.getPhotoUrl())
                    .into(viewHolder.messengerImageView);
        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final MessageViewHolder messageViewHolder = super.onCreateViewHolder(parent, viewType);
        messageViewHolder.setOnClickListener(new MessageViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                getRef(position).child("videoUrl").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        VideoModel videoModel = realm.where(VideoModel.class).equalTo("videoUrl", dataSnapshot.getValue().toString()).equalTo("localVideoPath", "").findFirst();
                        if(videoModel != null){
                            try {
                                int token = downloadManagerPro.
                                        addTask("joyiochat_video_"+videoModel.getId()+".mp4", videoModel.getVideoUrl(),
                                                true,true);

                                downloadManagerPro.startDownload(token);
                                Log.d("oska", "video id " + videoModel.getId());
                                realm.beginTransaction();
                                videoModel.setLocalVideoPath(ChatRoomDetailActivity.LOCAL_DIR_JOYIOCHAT+
                                        ChatRoomDetailActivity.LOCAL_FOLDER+
                                        "joyiochat_video_"+videoModel.getId()+".mp4."
                                );
                                realm.insertOrUpdate(videoModel);
                                realm.commitTransaction();
                                messageViewHolder.ivPlay.setVisibility(View.GONE);
                                messageViewHolder.ivDownload.setVisibility(View.GONE);
                                messageViewHolder.loadingIndicatorView.setVisibility(View.VISIBLE);

                                clickedPosition = position;
                                joyioMessageViewHolder = messageViewHolder;
                                Log.d("oska123", downloadManagerPro.singleDownloadStatus(token).name);
                                Log.d("oska123", downloadManagerPro.singleDownloadStatus(token).type);
                                Log.d("oska123", downloadManagerPro.singleDownloadStatus(token).fileSize+"");
                                Log.d("oska123", downloadManagerPro.singleDownloadStatus(token).downloadLength+"");
                                Log.d("oska123", downloadManagerPro.singleDownloadStatus(token).saveAddress+"");
                                Log.d("oska123", downloadManagerPro.singleDownloadStatus(token).name);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }else{
                            VideoModel localVideoModel = realm.where(VideoModel.class).equalTo("videoUrl", dataSnapshot.getValue().toString()).findFirst();
//                            Uri localVideoUrl = getVideoContentUri(activity,new File(localVideoModel.getLocalVideoPath()));
                            Log.d("videoPath" , localVideoModel.getLocalVideoPath());
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(localVideoModel.getLocalVideoPath()));
                            intent.setDataAndType(Uri.parse(localVideoModel.getLocalVideoPath()), "video/mp4");
                            activity.startActivity(intent);

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
        return messageViewHolder;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public ImageView messageImageView;
        public TextView messengerTextView;
        public CircleImageView messengerImageView;
        public RelativeLayout rlVideoContainer;
        public ImageView ivVideoThumbnail, ivPlay, ivDownload;
        public AVLoadingIndicatorView loadingIndicatorView;
        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
            rlVideoContainer = (RelativeLayout) itemView.findViewById(R.id.rl_video_container);
            ivVideoThumbnail = (ImageView) itemView.findViewById(R.id.iv_video_thumbnail);
            ivPlay = (ImageView) itemView.findViewById(R.id.iv_play);
            ivDownload = (ImageView) itemView.findViewById(R.id.iv_download);
            loadingIndicatorView = (AVLoadingIndicatorView) itemView.findViewById(R.id.loading_view);
            ivVideoThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListener.onItemClick(view, getAdapterPosition());
                }
            });

        }
        private MessageViewHolder.ClickListener mClickListener;

        public interface ClickListener{
            void onItemClick(View view, int position);
        }

        public void setOnClickListener(MessageViewHolder.ClickListener clickListener){
            mClickListener = clickListener;
        }

    }

    public void downloadCompleteChange(){

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                joyioMessageViewHolder.ivPlay.setVisibility(View.VISIBLE);
                joyioMessageViewHolder.loadingIndicatorView.setVisibility(View.GONE);
                notifyItemChanged(clickedPosition);

            }
        });


    }


    public Uri getVideoContentUri(Context context, File videoFile) {
        String filePath = videoFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Video.Media._ID },
                MediaStore.Video.Media.DATA + "=? ",
                new String[] { filePath }, null);
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
}
