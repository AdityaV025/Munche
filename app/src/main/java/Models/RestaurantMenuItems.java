package Models;

public class RestaurantMenuItems {

    private String name;
    private String price;
    private String specification;
    private String category;

    private String is_added;

    public RestaurantMenuItems(String name, String price, String specification, String category, String is_added) {
        this.name = name;
        this.price = price;
        this.specification = specification;
        this.category = category;
        this.is_added = is_added;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public RestaurantMenuItems() {
    }

    public String getIs_added() {
        return is_added;
    }

    public void setIs_added(String is_added) {
        this.is_added = is_added;
    }

}
