package oska.joyiochat.eventbus;

import oska.joyiochat.module.JoyioChatMessage;

/**
 * Created by theoska on 4/6/17.
 */

public class JoyioVideoMessage {
    private String videoName;
    private String audioName;
    public JoyioVideoMessage(String videoName, String audioName){
        this.videoName =videoName;
        this.audioName = audioName;
    }
    public String getVideoName(){
        return videoName;
    }
    public String getAudioName(){
        return audioName;
    }
}
