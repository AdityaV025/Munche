package Models;

public class RestaurantDetail {

    private String restaurant_name;
    private String restaurant_spotimage;

    public RestaurantDetail() {
    }

    public RestaurantDetail(String restaurant_name, String restaurant_spotimage) {
        this.restaurant_name = restaurant_name;
        this.restaurant_spotimage = restaurant_spotimage;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }

    public String getRestaurant_spotimage() {
        return restaurant_spotimage;
    }

    public void setRestaurant_spotimage(String restaurant_spotimage) {
        this.restaurant_spotimage = restaurant_spotimage;
    }

}
