package oska.joyiochat.listener;

import android.net.Uri;

/**
 * Created by theoska on 3/29/17.
 */

public interface VideoThumbnailListener {
    void onVideoThumbnailComplete(Uri videoUri, Uri thumbnailUri);
}
