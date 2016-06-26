package oska.joyiochat.module;

/**
 * Created by TheOSka on 22/6/2016.
 */
public class FavPeopleItem {
    Integer favProfilePic;
    String favName;

    public void setFavName(String favName) {
        this.favName = favName;
    }

    public void setFavProfilePic(Integer favProfilePic) {
        this.favProfilePic = favProfilePic;
    }

    public Integer getFavProfilePic() {
        return favProfilePic;
    }

    public String getFavName() {
        return favName;
    }
}
