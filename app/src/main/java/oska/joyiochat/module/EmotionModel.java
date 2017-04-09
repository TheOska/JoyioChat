package oska.joyiochat.module;

import org.rajawali3d.renderer.Renderer;

/**
 * Created by theoska on 4/9/17.
 */

public class EmotionModel {
    int index;
    String name;
    Renderer objRenderer;
    int icon;
    boolean isloaded;
    public EmotionModel(){}
    public EmotionModel(int index, String name, Renderer objRenderer, int icon, boolean isloaded) {
        this.index = index;
        this.name = name;
        this.objRenderer = objRenderer;
        this.icon = icon;
        this.isloaded = isloaded;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Renderer getObjRenderer() {
        return objRenderer;
    }

    public void setObjRenderer(Renderer objRenderer) {
        this.objRenderer = objRenderer;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isloaded() {
        return isloaded;
    }

    public void setIsloaded(boolean isloaded) {
        this.isloaded = isloaded;
    }
}
