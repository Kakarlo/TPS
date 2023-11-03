package tps;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DailySalesController {

    POSController pos;

    @FXML
    private Button btnClose;

    @FXML
    private TableView<Product> dailySales;

    @FXML
    private TableColumn<Product, Void> cart_edit;

    @FXML
    private TableColumn<Product, String> cart_invoice;

    @FXML
    private TableColumn<Product, String> cart_description;

    @FXML
    private TableColumn<Product, Integer> cart_index;

    @FXML
    private TableColumn<Product, Integer> cart_pcode;

    @FXML
    private TableColumn<Product, Double> cart_price;

    @FXML
    private TableColumn<Product, Integer> cart_quantity;

    @FXML
    private TableColumn<Product, Double> cart_total;

    @FXML
    private Label total_amount;

    private final ObservableList<Product> ds = FXCollections.observableArrayList();
    private Database db;
    private double x, y;
    public HashMap<String, Integer> salesMap = new HashMap<>();
    public double price, total;
    public int id, quantity, pcode;
    public String desc, trans, barcode;

    public void initialize(){
        initializeTable();
        loadDailySales();
    }

    public void close(){
        btnClose.getScene().getWindow().hide();
    }

    public void setPos(POSController pos){
        this.pos = pos;
    }

    public void loadDailySales(){
        db = new Database("DailySales");
        ArrayList<String[]> arr = db.getFile();
        int i = 0;
        for (String[] s : arr){
            Product p = new Product();
            p.setIndex(++i);
            p.setTrans_no(s[0]);
            p.setPcode(Integer.parseInt(s[1]));
            p.setBarcode(s[2]);
            p.setDescription(s[3]);
            p.setPrice(Double.parseDouble(s[4]));
            p.setQuantity(Integer.parseInt(s[5]));
            p.setTotal(Double.parseDouble(s[6]));
            salesMap.put(s[0] + s[1], i - 1);
            ds.add(p);
        }
        dailySales.setItems(ds);
        updateTotal();
    }

    public void saveDailySales(){
        String hold = "";
        ObservableList<Product> p = dailySales.getItems();
        for (Product prod : p) {
            hold += prod.getTrans_no() + "#" +
                    prod.getPcode() + "#" +
                    prod.getBarcode() + "#" +
                    prod.getDescription() + "#" +
                    prod.getPrice() + "#" +
                    prod.getQuantity() + "#" +
                    prod.getTotal() + "\n";
        }
        db = new Database("DailySales");
        db.storeToFile(hold);
    }

    public void removeSale(String code){
        int index = salesMap.get(code);
        dailySales.getItems().remove(index);
        saveDailySales();
        updateTotal();
        updateInd();
    }

    public void updateSale(String code, int quantity){
        int index = salesMap.get(code);
        Product p = dailySales.getItems().get(index);
        p.setQuantity(quantity);
        p.setTotal(p.getPrice() * p.getQuantity());
        updateTotal();
        saveDailySales();
    }

    public void updateStock(int returnStock){
        Product p = pos.productList.get(pos.productMap.get(barcode));
        p.setQuantity(p.getQuantity() + returnStock);
        pos.updateStock();
    }

    public void updateTotal(){
        double total = 0;
        for (Product c : ds){
            total += c.getTotal();
        }
        total_amount.setText(String.format("%,.2f", total));
    }

    private void updateInd(){
        ObservableList<Product> c = dailySales.getItems();
        for (int i = 0; i < c.size(); i++){
            c.get(i).setIndex(i + 1);
        }
    }

    private void loadStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("cancel-order.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        // Sends data to other pages
        CancelOrderController cs = loader.getController();
        cs.setDSC(this);

        root.setOnMousePressed((MouseEvent event) ->{
            x = event.getSceneX();
            y = event.getSceneY();
        });

        root.setOnMouseDragged((MouseEvent event) ->{
            stage.setX(event.getScreenX()  - x);
            stage.setY(event.getScreenY()  - y);

            stage.setOpacity(.9);
        });

        root.setOnMouseReleased((MouseEvent event) -> stage.setOpacity(1));

        stage.setResizable(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    public void initializeTable(){
        cart_index.setCellValueFactory(new PropertyValueFactory<>("index"));
        cart_pcode.setCellValueFactory(new PropertyValueFactory<>("pcode"));
        cart_description.setCellValueFactory(new PropertyValueFactory<>("description"));
        cart_invoice.setCellValueFactory(new PropertyValueFactory<>("trans_no"));
        cart_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        cart_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        cart_total.setCellValueFactory(new PropertyValueFactory<>("total"));


        // Format price
        cart_price.setCellFactory(col ->
                new TableCell<>() {
                    @Override
                    public void updateItem(Double price, boolean empty) {
                        super.updateItem(price, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(String.format("%,.2f", price));
                        }
                    }
                });
        // format total
        cart_total.setCellFactory(col ->
                new TableCell<>() {
                    @Override
                    public void updateItem(Double total, boolean empty) {
                        super.updateItem(total, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(String.format("%,.2f", total));
                        }
                    }
                });

        // Format pcode
        cart_pcode.setCellFactory(col ->
                new TableCell<>() {
                    @Override
                    public void updateItem(Integer pcode, boolean empty) {
                        super.updateItem(pcode, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(String.format("P%05d", pcode));
                        }
                    }
                });
        // Button for editing sale
        Callback<TableColumn<Product, Void>, TableCell<Product, Void>> edit = new Callback<>() {
            @Override
            public TableCell<Product, Void> call(final TableColumn<Product, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("-");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Product current = getTableView().getItems().get(getIndex());
                            id = current.getIndex();
                            pcode = current.getPcode();
                            barcode = current.getBarcode();
                            desc = current.getDescription();
                            trans = current.getTrans_no();
                            price = current.getPrice();
                            quantity = current.getQuantity();
                            total = current.getTotal();
                            try {
                                loadStage();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };

        cart_edit.setCellFactory(edit);
    }

}
