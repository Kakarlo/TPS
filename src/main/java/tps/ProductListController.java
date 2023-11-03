package tps;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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

public class ProductListController {

    private POSController pos;

    @FXML
    private Button btnClose;

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, Void> cart_add;

    @FXML
    private TableColumn<Product, String> cart_barcode;

    @FXML
    private TableColumn<Product, String> cart_category;

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
    private TextField search_bar;

    private String barcode;
    private int quantity, totalQty;
    private double x, y;

    public void initialize(){
        productListInitialize();
    }

    public void close(){
        btnClose.getScene().getWindow().hide();
    }

    public void setPos(POSController pos){
        this.pos = pos;
        productTable.setItems(pos.productList);
        // Filters the table
        FilteredList<Product> filteredList = new FilteredList<>(productTable.getItems(), b -> true);
        search_bar.textProperty().addListener(((observable, oldVal, newVal) -> filteredList.setPredicate(product -> {
            if (newVal == null || newVal.isEmpty()){
                return true;
            }

            String lowerCaseFilter = newVal.toLowerCase();
            // Filters the description, pcode, barcode, and category
            if (product.getDescription().toLowerCase().contains(lowerCaseFilter)){
                return true; // filter matches description
            } else if (product.getBarcode().toLowerCase().contains(lowerCaseFilter)){
                return true;
            } else if (String.valueOf(product.getPcode()).contains(lowerCaseFilter)){
                return true;
            } else return product.getCategory().toLowerCase().contains(lowerCaseFilter);
        })));
        SortedList<Product> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(productTable.comparatorProperty());
        productTable.setItems(sortedList);
    }

    public void setQuantity(int qty){
        quantity = qty;
    }

    public int getQuantityTotal(){
        return this.totalQty;
    }

    public void addItem(){
        if (quantity >= 0){
            pos.add(barcode, quantity);
        }
    }

    private void loadStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("add-quantity.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        // Sends data to other pages
        AddQuantityController add = loader.getController();
        add.setPLC(this);

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

    private void productListInitialize(){
        cart_index.setCellValueFactory(new PropertyValueFactory<>("index"));
        cart_pcode.setCellValueFactory(new PropertyValueFactory<>("pcode"));
        cart_description.setCellValueFactory(new PropertyValueFactory<>("description"));
        cart_barcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        cart_category.setCellValueFactory(new PropertyValueFactory<>("category"));
        cart_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        cart_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // Format price
        cart_price.setCellFactory(col ->
                new TableCell<>() {
                    @Override
                    public void updateItem(Double price, boolean empty) {
                        super.updateItem(price, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(String.format("%.2f", price));
                        }
                    }
                });
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
        // Buttons for increasing and decreasing quantity, deletion
        Callback<TableColumn<Product, Void>, TableCell<Product, Void>> add = new Callback<>() {
            @Override
            public TableCell<Product, Void> call(final TableColumn<Product, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("+");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            totalQty = getTableView().getItems().get(getIndex()).getQuantity();
                            barcode = getTableView().getItems().get(getIndex()).getBarcode();
                            quantity = -1;
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

        cart_add.setCellFactory(add);
    }


}
