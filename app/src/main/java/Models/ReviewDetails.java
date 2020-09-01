package Models;

public class ReviewDetails {

    private String rating;
    private String review;
    private String recommended;
    private String user_name;
    private String user_image;
    private String uid;

    public ReviewDetails() {
    }

    public ReviewDetails(String rating, String review, String recommended, String user_name, String user_image, String uid) {
        this.rating = rating;
        this.review = review;
        this.recommended = recommended;
        this.user_name = user_name;
        this.user_image = user_image;
        this.uid = uid;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getRecommended() {
        return recommended;
    }

    public void setRecommended(String recommended) {
        this.recommended = recommended;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
