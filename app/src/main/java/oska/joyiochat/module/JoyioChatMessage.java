package oska.joyiochat.module;

/**
 * Created by theoska on 3/20/17.
 */

public class JoyioChatMessage {


    private String id;
    private String text;
    private String name;
    // photoUrl is refer to user icon url
    private String photoUrl;
    private String imageUrl;
    private String videoUrl;
    private String videoThumbnailUrl;


    public JoyioChatMessage(String text, String name, String photoUrl, String imageUrl) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.imageUrl = imageUrl;
    }

    public JoyioChatMessage(String text, String name, String photoUrl, String videoUrl,
                            String videoThumbnailUrl) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.videoUrl = videoUrl;
        this.videoThumbnailUrl = videoThumbnailUrl;
    }

    public String getVideoThumbnailUrl() {
        return videoThumbnailUrl;
    }

    public void setVideoThumbnailUrl(String videoThumbnailUrl) {
        this.videoThumbnailUrl = videoThumbnailUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public JoyioChatMessage() {
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
