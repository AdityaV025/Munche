package Models;

public class FavoriteRestaurantDetails {

    private String restaurant_name;
    private String restaurant_image;
    private String restaurant_uid;
    private String restaurant_price;

    public FavoriteRestaurantDetails() {
    }

    public FavoriteRestaurantDetails(String restaurant_name, String restaurant_image, String restaurant_uid, String restaurant_price) {
        this.restaurant_name = restaurant_name;
        this.restaurant_image = restaurant_image;
        this.restaurant_uid = restaurant_uid;
        this.restaurant_price = restaurant_price;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }

    public String getRestaurant_image() {
        return restaurant_image;
    }

    public void setRestaurant_image(String restaurant_image) {
        this.restaurant_image = restaurant_image;
    }

    public String getRestaurant_uid() {
        return restaurant_uid;
    }

    public void setRestaurant_uid(String restaurant_uid) {
        this.restaurant_uid = restaurant_uid;
    }

    public String getRestaurant_price() {
        return restaurant_price;
    }

    public void setRestaurant_price(String restaurant_price) {
        this.restaurant_price = restaurant_price;
    }

}
