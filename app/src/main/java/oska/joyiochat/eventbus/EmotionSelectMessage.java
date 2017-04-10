package oska.joyiochat.eventbus;

import oska.joyiochat.module.EmotionModel;

/**
 * Created by theoska on 4/10/17.
 */

public class EmotionSelectMessage {
    private int selectedModel;

    public EmotionSelectMessage(int selectedModel){
        this.selectedModel = selectedModel;
    }

    public int getSelectedModel(){
        return selectedModel;
    }
}
