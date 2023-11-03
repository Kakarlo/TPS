package tps;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Product {
    private final SimpleStringProperty description;
    private final SimpleStringProperty category;
    private final SimpleStringProperty barcode;
    private SimpleStringProperty trans_no;
    private final SimpleIntegerProperty index;
    private final SimpleIntegerProperty pcode;
    private final SimpleIntegerProperty quantity;
    private final SimpleDoubleProperty price;
    private final SimpleDoubleProperty total;

    public Product(){
        this.index = new SimpleIntegerProperty();
        this.pcode = new SimpleIntegerProperty();
        this.barcode = new SimpleStringProperty();
        this.trans_no = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.category = new SimpleStringProperty();
        this.price = new SimpleDoubleProperty();
        this.quantity = new SimpleIntegerProperty();
        this.total = new SimpleDoubleProperty();
    }

    public Product(String pcode, String barcode, String description, String category, String price, String quantity) {
        this.index = new SimpleIntegerProperty();
        this.description = new SimpleStringProperty(description);
        this.category = new SimpleStringProperty(category);
        this.barcode = new SimpleStringProperty(barcode);
        this.pcode = new SimpleIntegerProperty(Integer.parseInt(pcode));
        this.quantity = new SimpleIntegerProperty(Integer.parseInt(quantity));
        this.price = new SimpleDoubleProperty(Double.parseDouble(price));
        this.total = new SimpleDoubleProperty();
    }

    public String getTrans_no() {
        return trans_no.get();
    }

    public SimpleStringProperty trans_noProperty() {
        return trans_no;
    }

    public void setTrans_no(String trans_no) {
        this.trans_no.set(trans_no);
    }

    public int getIndex() {
        return index.get();
    }

    public SimpleIntegerProperty indexProperty() {
        return index;
    }

    public void setIndex(int index) {
        this.index.set(index);
    }

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public int getQuantity() {
        return quantity.get();
    }

    public SimpleIntegerProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public double getPrice() {
        return price.get();
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public double getTotal() {
        return total.get();
    }

    public SimpleDoubleProperty totalProperty() {
        return total;
    }

    public void setTotal(double total) {
        this.total.set(total);
    }

    public String getCategory() {
        return category.get();
    }

    public SimpleStringProperty categoryProperty() {
        return category;
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public String getBarcode() {
        return barcode.get();
    }

    public SimpleStringProperty barcodeProperty() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode.set(barcode);
    }

    public int getPcode() {
        return pcode.get();
    }

    public SimpleIntegerProperty pcodeProperty() {
        return pcode;
    }

    public void setPcode(int pcode) {
        this.pcode.set(pcode);
    }
}
