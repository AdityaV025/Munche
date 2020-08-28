package Models;

public class RestaurantDetail {

    private String restaurant_name;
    private String restaurant_spotimage;
    private String restaurant_prep_time;
    private String average_price;
    private String restaurant_uid;
    private Double latitude;
    private Double longitude;

    public RestaurantDetail() {
    }

    public RestaurantDetail(String restaurant_name, String restaurant_spotimage, String average_price, String restaurant_uid, Double latitude, Double longitude, String restaurant_prep_time) {
        this.restaurant_name = restaurant_name;
        this.restaurant_spotimage = restaurant_spotimage;
        this.average_price = average_price;
        this.restaurant_uid = restaurant_uid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.restaurant_prep_time = restaurant_prep_time;
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

    public String getAverage_price() {
        return average_price;
    }

    public void setAverage_price(String average_price) {
        this.average_price = average_price;
    }

    public String getRestaurant_uid() {
        return restaurant_uid;
    }

    public void setRestaurant_uid(String restaurant_uid) {
        this.restaurant_uid = restaurant_uid;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getRestaurant_prep_time() {
        return restaurant_prep_time;
    }

    public void setRestaurant_prep_time(String restaurant_prep_time) {
        this.restaurant_prep_time = restaurant_prep_time;
    }

}
