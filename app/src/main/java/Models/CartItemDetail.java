package Models;

public class CartItemDetail {

    private String select_name;
    private String select_price;
    private String select_specification;
    private String item_count;

    public CartItemDetail() {
    }

    public CartItemDetail(String select_name, String select_price, String select_specification, String item_count) {
        this.select_name = select_name;
        this.select_price = select_price;
        this.select_specification = select_specification;
        this.item_count = item_count;
    }

    public String getSelect_name() {
        return select_name;
    }

    public void setSelect_name(String select_name) {
        this.select_name = select_name;
    }

    public String getSelect_price() {
        return select_price;
    }

    public void setSelect_price(String select_price) {
        this.select_price = select_price;
    }

    public String getSelect_specification() {
        return select_specification;
    }

    public void setSelect_specification(String select_specification) {
        this.select_specification = select_specification;
    }

    public String getItem_count() {
        return item_count;
    }

    public void setItem_count(String item_count) {
        this.item_count = item_count;
    }

}
