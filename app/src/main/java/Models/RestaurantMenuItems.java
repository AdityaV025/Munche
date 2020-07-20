package Models;

public class RestaurantMenuItems {

    private String name;
    private String price;
    private String specification;
    private String category;

    public RestaurantMenuItems(String name, String price, String specification, String category) {
        this.name = name;
        this.price = price;
        this.specification = specification;
        this.category = category;
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
}
