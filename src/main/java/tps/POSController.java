package tps;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class POSController {

    @FXML
    private Button btnF1;

    @FXML
    private Button btnF10;

    @FXML
    private Button btnF2;

    @FXML
    private Button btnF3;

    @FXML
    private Button btnF4;

    @FXML
    private Button btnF5;

    @FXML
    private Button btnF6;

    @FXML
    private TableView<Product> cart;

    @FXML
    private TableColumn<Product, String> cart_description;

    @FXML
    private TableColumn<Product, Integer> cart_index;

    @FXML
    private TableColumn<Product, Double> cart_price;

    @FXML
    private TableColumn<Product, Integer> cart_quantity;

    @FXML
    private TableColumn<Product, Double> cart_total;

    @FXML
    private TableColumn<Product, Void> cart_increase;

    @FXML
    private TableColumn<Product, Void> cart_decrese;

    @FXML
    private TableColumn<Product, Void> cart_delete;

    @FXML
    private Label cashier_name;

    @FXML
    private Label current_date;

    @FXML
    private Label current_time;

    @FXML
    private TextField search_bar;

    @FXML
    private Label total_amount;

    @FXML
    private Label total_amount2;

    @FXML
    private Label total_vat;

    @FXML
    private Label total_vatable;

    @FXML
    private Label trans_date;

    @FXML
    private Label trans_no;

    public ObservableList<Product> productList = FXCollections.observableArrayList();
    public final HashMap<String, Integer> productMap = new HashMap<>();
    private Database db;
    private String recentTrans;
    public String username;
    private int index = 1, transaction = 0;
    private double x, y;
    private boolean currentTrans = false;

    @FXML
    public void initialize(){
        // sets the filter for the search bar
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?([0-9]*)?")) {
                return change;
            }
            return null;
        };
        search_bar.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter));
        checkDate();
        inactive();
        cartInitialize();
        productList();
        startTime();
    }

    public void checkDate(){
        Date time = new Date();
        DateFormat number = new SimpleDateFormat("yyyyMMdd");
        // Check the recent daily sales
        db = new Database("DailySalesRecent");
        try {
            recentTrans = db.getFile().get(0)[0];
        } catch (IndexOutOfBoundsException e){
            return;
        }

        if (recentTrans.substring(0,8).equals(number.format(time))){
            transaction = Integer.parseInt(recentTrans.substring(9));
        } else {
            // Resets the daily sales text file and adds sales to total sales
            db = new Database("DailySales");
            String daily = db.getFileText();
            db.storeToFile("");
            db = new Database("DailySalesRecent");
            db.storeToFile("");
            db = new Database("TotalSales");
            db.addToFile(daily);
        }
    }

    public void setCashier_name(String user){
        username = user;
        cashier_name.setText(user + " | Cashier");
    }

    public void enterSearch(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER){
            add(search_bar.getText(), 1);
            search_bar.setText("");
        }
    }

    public int contains(String bar){
        for(int i = 0; i < cart.getItems().size(); i++){
            if (cart.getItems().get(i).getBarcode().equals(bar)){
                return i;
            }
        }
        return -1;
    }

    public void add(String barcode, int quantity){
        // Checks if item exist in the cart already
        int itemIndex = contains(barcode);
        if (!(itemIndex < 0)){
            Product p = cart.getItems().get(itemIndex);
            int newQty = p.getQuantity() + quantity;

            int totalQty = productList.get(productMap.get(barcode)).getQuantity();
            if (newQty <= 0){ // Checks if the item is getting removed
                cart.getItems().remove(itemIndex);
                updateInd();
            } else if (totalQty >= newQty) { // Checks if the total stocks is greater than quantity
                p.setQuantity(p.getQuantity() + quantity);
                p.setTotal(p.getPrice() * p.getQuantity());
            } else {
                errorMessage("Not enough stock");
            }
            update();
            return;
        }
        // adds a product based on the list
        if (productMap.containsKey(barcode)){
            Product copy = productList.get(productMap.get(barcode));
            // Checks if the product is in stock
            if (copy.getQuantity() <= 0) {
                errorMessage("Product is out of stock");
                return;
            }
            // Adds the product
            Product p = new Product();
            p.setIndex(index++);
            p.setPcode(copy.getPcode());
            p.setBarcode(copy.getBarcode());
            p.setDescription(copy.getDescription());
            p.setPrice(copy.getPrice());
            p.setQuantity(quantity);
            p.setTotal(p.getPrice() * p.getQuantity());
            cart.getItems().add(p);
            update();
            return;
        }
        errorMessage("Barcode does not exist");
    }

    private void update(){
        // update the data field for total and whatnot
        ObservableList<Product> ob = cart.getItems();
        double total = 0d, vat, vatable;
        for (Product c : ob){
            total += c.getTotal();
        }
        vat = total * .12;
        vatable = total - vat;
        total_amount.setText(String.format("%,.2f", total));
        total_amount2.setText(String.format("%,.2f", total));
        total_vat.setText(String.format("%,.2f", vat));
        total_vatable.setText(String.format("%,.2f", vatable));
    }

    public void errorMessage(String msg){
        Alert error = new Alert(Alert.AlertType.INFORMATION);
        error.setTitle("Error!");
        error.setHeaderText(null);
        error.setContentText(msg);
        error.showAndWait();
    }

    public boolean confirmationMessage(String msg){
        Alert error = new Alert(Alert.AlertType.CONFIRMATION);
        error.setTitle("Confirmation");
        error.setHeaderText(null);
        error.setContentText(msg);
        Optional<ButtonType> result = error.showAndWait();
        return result.get() == ButtonType.OK;
    }

    public void inactive(){
        // state when there is no transaction
        trans_no.setText("");
        trans_date.setText("");
        btnF2.setDisable(true);
        btnF3.setDisable(true);
        btnF4.setDisable(true);
        search_bar.setDisable(true);
        cart.setDisable(true);
        cart.getItems().clear();
        index = 1;
        currentTrans = false;
        update();
    }

    private void startTime(){
        // Starts the time
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                DateFormat formatTime = new SimpleDateFormat("hh:mm:ss aa");
                DateFormat formatDate = new SimpleDateFormat("E, dd MMM yyyy");
                Date time = new Date();
                current_time.setText(formatTime.format(time));
                current_date.setText(formatDate.format(time));
            }
        };
        timer.start();
    }

    public void newTransaction(){
        // Sets up the app for a transaction
        currentTrans = true;
        if (btnF2.isDisabled()) {
            Date time = new Date();
            DateFormat number = new SimpleDateFormat("yyyyMMdd");
            DateFormat formatDate = new SimpleDateFormat("E, dd MMM yyyy");
            trans_no.setText(String.format("%s%04d", number.format(time), ++transaction));
            trans_date.setText(formatDate.format(time));
            btnF2.setDisable(false);
            btnF3.setDisable(false);
            btnF4.setDisable(false);
            search_bar.setDisable(false);
            cart.setDisable(false);
        } else {
            errorMessage("Please finish the current transaction");
        }
    }

    public void searchProduct() throws IOException {
        loadStage("product-list.fxml");
    }

    public void settlePayment() throws IOException {
        if (cart.getItems().size() == 0){
            errorMessage("Please enter a product");
        } else {
            loadStage("settle-payment.fxml");
        }
    }

    public void saveSales(){
        // Saves the sales
        String hold = "";
        ObservableList<Product> p = cart.getItems();
        for (Product prod : p) {
            hold += trans_no.getText() + "#" +
                    prod.getPcode() + "#" +
                    prod.getBarcode() + "#" +
                    prod.getDescription() + "#" +
                    prod.getPrice() + "#" +
                    prod.getQuantity() + "#" +
                    prod.getTotal() + "\n";
            // Updates the total stocks
            Product stocks = productList.get(productMap.get(prod.getBarcode()));
            stocks.setQuantity(stocks.getQuantity() - prod.getQuantity());
        }
        db = new Database("DailySales");
        db.addToFile(hold);
        db = new Database("DailySalesRecent");
        db.storeToFile(trans_no.getText());

        updateStock();
    }

    public void updateStock(){
        // Updates the stock
        String hold = "";
        for (Product prod : productList) {
            hold += prod.getPcode() + "#" +
                    prod.getBarcode() + "#" +
                    prod.getDescription() + "#" +
                    prod.getCategory() + "#" +
                    prod.getPrice() + "#" +
                    prod.getQuantity() + "\n";
        }
        db = new Database("Items");
        db.storeToFile(hold);
        productList();
    }

    public String getTotal(){
        return total_amount.getText().replaceAll(",", "");
    }

    public void clearSales(){
        if (confirmationMessage("Would you like to clear the cart?")) {
            cart.getItems().clear();
            index = 1;
            update();
        }
    }

    public void dailySales() throws IOException {
        loadStage("daily-sales.fxml");
    }

    public void changePass() throws IOException {
        loadStage("change-password.fxml");
    }

    public void logout() throws IOException {
        if (confirmationMessage("Do you want to log-out?")) {
            btnF10.getScene().getWindow().hide();
            loadStage("login-view.fxml");
        }
    }

    private void loadStage(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = loader.load();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        // Sends data to other pages
        switch (fxml) {
            case "POS.fxml" -> {
                POSController pos = loader.getController();
                pos.setCashier_name(username);
            }
            case "product-list.fxml" -> {
                ProductListController pro = loader.getController();
                pro.setPos(this);
            }
            case "settle-payment.fxml" -> {
                SettlePaymentController sp = loader.getController();
                sp.setPos(this);
            }
            case "daily-sales.fxml" -> {
                DailySalesController dsc = loader.getController();
                dsc.setPos(this);
            }
            case "change-password.fxml" -> {
                ChangePasswordController cpc = loader.getController();
                cpc.setUser(username);
            }
        }

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

    private void updateInd(){
        ObservableList<Product> c = cart.getItems();
        for (int i = 0; i < c.size(); i++){
            c.get(i).setIndex(i + 1);
        }
        index = c.size() + 1;
    }

    public void f1Keys(KeyEvent event) throws IOException {
        switch (event.getCode()){
            case F1 -> newTransaction();
            case F2 -> {
                if (currentTrans) searchProduct();
            }
            case F3 -> {
                if (currentTrans)  settlePayment();
            }
            case F4 -> {
                if (currentTrans)  clearSales();
            }
            case F5 -> dailySales();
            case F6 -> changePass();
            case F10 -> logout();
        }
    }

    private void cartInitialize(){
        //cart_index.setCellValueFactory(cart -> cart.getValue().indexProperty().asString());
        cart_index.setCellValueFactory(new PropertyValueFactory<>("index"));
        cart_description.setCellValueFactory(new PropertyValueFactory<>("description"));
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

        // Buttons for increasing and decreasing quantity, deletion
        Callback<TableColumn<Product, Void>, TableCell<Product, Void>> increase = new Callback<>() {
            @Override
            public TableCell<Product, Void> call(final TableColumn<Product, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("<");

                    {
                        btn.setOnAction((ActionEvent event) -> add(getTableView().getItems().get(getIndex()).getBarcode(), 1));
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

        Callback<TableColumn<Product, Void>, TableCell<Product, Void>> decrease = new Callback<>() {
            @Override
            public TableCell<Product, Void> call(final TableColumn<Product, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button(">");

                    {
                        btn.setOnAction((ActionEvent event) -> add(getTableView().getItems().get(getIndex()).getBarcode(), -1));
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

        Callback<TableColumn<Product, Void>, TableCell<Product, Void>> delete = new Callback<>() {
            @Override
            public TableCell<Product, Void> call(final TableColumn<Product, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("x");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            getTableView().getItems().remove(getIndex());
                            update();
                            updateInd();
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

        cart_increase.setCellFactory(increase);
        cart_decrese.setCellFactory(decrease);
        cart_delete.setCellFactory(delete);
    }

    private void productList(){
        productList.clear();
        db = new Database("Items");
        ArrayList<String[]> arr = db.getFile();
        int i = 0;
        for (String[] s : arr){
            Product p = new Product(s[0], s[1], s[2], s[3], s[4], s[5]);
            productMap.put(p.getBarcode(), i);
            p.setIndex(++i);
            productList.add(p);
        }
    }

}
