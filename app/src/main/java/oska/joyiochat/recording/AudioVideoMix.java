package oska.joyiochat.recording;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.googlecode.mp4parser.BasicContainer;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AACTrackImpl;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.DIRECTORY_MOVIES;

/**
 * Created by TheOskaCKH on 12/18/2016.
 */

public class AudioVideoMix {
    private Context context;
    private final String TAG = "AudioVideoMix";
    private String rawPath;
    private static final String LOCAL_DIR_MOV = (Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES)) + "";
    private static final String LOCAL_FOLDER = "/JoyioChat/";
    String fadedAcc = "faded.aac";
    String screenShot1 = "screen1.mp4";

    public AudioVideoMix(Context context) {
        this.context = context;
        rawPath = LOCAL_DIR_MOV + LOCAL_FOLDER;
    }

    public void merge(final AudioVideoMixListener listener) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // get mp4 file
                    MediaPlayer mpVideo = MediaPlayer.create(context, Uri.parse(rawPath + screenShot1));
                    int videoDuration = mpVideo.getDuration();
                    MediaPlayer mpAudio = MediaPlayer.create(context, Uri.parse(rawPath + fadedAcc));
                    int audioDuration = mpAudio.getDuration();

                    AACTrackImpl audioTrack = new AACTrackImpl(new FileDataSourceImpl(rawPath + fadedAcc));
//                    int audioDuration = (int) Math.ceil(audioTrack.getSamples().size());
                    Log.d("AudioVideoMix", "video duration " +  videoDuration);
                    Log.d("AudioVideoMix", "audio duration " + audioDuration);
                    // mobile local storage file path
                    Movie m = MovieCreator.build(rawPath + screenShot1);
                    List<Track> nuTracks = new ArrayList<Track>();
                    // extract all MPEG layer
                    for (Track track : m.getTracks()) {
                        if (!"soun".equals(track)) {
                            nuTracks.add(track);
                        }
                    }

                    if(audioDuration < videoDuration){
                        Log.d("AudioVideoMix", "one");
                        double exactlyDuration = (double) videoDuration / audioDuration ;
                        int minDuration = (int) Math.floor(exactlyDuration);
                        double offsetDuration = exactlyDuration - minDuration ;
                        Track[] audioTracks = new Track[minDuration+1];
                        for (int i=0;i<minDuration;i++){
                            audioTracks[i] = audioTrack ;
                        }
                        int offsetSize = (int) (audioTrack.getSamples().size()* offsetDuration);
                        audioTracks[minDuration] = new CroppedTrack(audioTrack,0,offsetSize);
                        nuTracks.add(new AppendTrack(audioTracks));
                    }else if(audioDuration > videoDuration){
                        Log.d("AudioVideoMix", "two");
                        double offsetDuration = (double) videoDuration / audioDuration ;
                        int offsetSize = (int) (audioTrack.getSamples().size()* offsetDuration);
                        CroppedTrack croppedTrack = new CroppedTrack(audioTrack,0,offsetSize);
                        nuTracks.add(croppedTrack);
                    }else {
                        Log.d("AudioVideoMix", "three");
                        nuTracks.add(audioTrack);
                    }

                    m.setTracks(nuTracks);
                    BasicContainer out2 = (BasicContainer) new DefaultMp4Builder().build(m);
                    //
                    FileChannel fc2 = new RandomAccessFile(rawPath + "output8.mp4", "rw").getChannel();
                    out2.writeContainer(fc2);
                    fc2.close();
                    mpVideo.release();
                    mpAudio.release();

                } catch (IOException e) {
                    Log.e(TAG, e + "");
                    e.printStackTrace();
                }finally {
                    listener.onFinishMixing();

                }
            }
        });


    }

}
