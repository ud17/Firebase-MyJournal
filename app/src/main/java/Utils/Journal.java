package Utils;

import com.google.firebase.Timestamp;

public class Journal {

    private String title;
    private String thought;
    private String imageURL;
    private String username;
    private String userId;
    private Timestamp timeAdded;

    public Journal() {
    }

    public Journal(String title, String thought, String imageURL, String username, String userId, Timestamp timeAdded) {
        this.title = title;
        this.thought = thought;
        this.imageURL = imageURL;
        this.username = username;
        this.userId = userId;
        this.timeAdded = timeAdded;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThought() {
        return thought;
    }

    public void setThought(String thought) {
        this.thought = thought;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }
}
