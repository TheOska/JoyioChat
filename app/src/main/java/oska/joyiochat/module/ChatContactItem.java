package oska.joyiochat.module;

/**
 * Created by TheOSka on 22/6/2016.
 */
public class ChatContactItem {
    Integer chatContactProfilePic;
    String contactName;

    public void setChatContactProfilePic(Integer chatContactProfilePic) {
        this.chatContactProfilePic = chatContactProfilePic;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Integer getChatContactProfilePic() {
        return chatContactProfilePic;
    }

    public String getContactName() {
        return contactName;
    }
}
