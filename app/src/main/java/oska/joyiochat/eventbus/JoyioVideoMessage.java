package oska.joyiochat.eventbus;

import oska.joyiochat.module.JoyioChatMessage;

/**
 * Created by theoska on 4/6/17.
 */

public class JoyioVideoMessage {
    private String videoName;

    public JoyioVideoMessage(String videoName){
        this.videoName =videoName;
    }
    public String getVideoName(){
        return videoName;
    }
}
