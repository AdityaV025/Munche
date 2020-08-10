package Models;

import java.util.ArrayList;

public class OrderedItemDetail {

    private ArrayList<String> ordered_items;
    private String ordered_restaurant_name;
    private String ordered_time;
    private String total_amount;

    public OrderedItemDetail(ArrayList<String> ordered_items, String ordered_restaurant_name, String ordered_time, String total_amount) {
        this.ordered_items = ordered_items;
        this.ordered_restaurant_name = ordered_restaurant_name;
        this.ordered_time = ordered_time;
        this.total_amount = total_amount;
    }

    public OrderedItemDetail() {
    }

    public ArrayList<String> getOrdered_items() {
        return ordered_items;
    }

    public void setOrdered_items(ArrayList<String> ordered_items) {
        this.ordered_items = ordered_items;
    }

    public String getOrdered_restaurant_name() {
        return ordered_restaurant_name;
    }

    public void setOrdered_restaurant_name(String ordered_restaurant_name) {
        this.ordered_restaurant_name = ordered_restaurant_name;
    }

    public String getOrdered_time() {
        return ordered_time;
    }

    public void setOrdered_time(String ordered_time) {
        this.ordered_time = ordered_time;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

}
