package oska.joyiochat.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by theoska on 3/30/17.
 */

public class VideoModel extends RealmObject {
    @PrimaryKey
    private int id;

    @Required
    private String videoUrl;
    private String localVideoPath;
    public static String TAG_ID = "id";
    public static String TAG_LOCAL_VIDEO_PATH = "localVideoPath";
    public static String TAG_VIDEO_URL = "videoUrl";
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getLocalVideoPath() {
        return localVideoPath;
    }

    public void setLocalVideoPath(String localVideoPath) {
        this.localVideoPath = localVideoPath;
    }
}
