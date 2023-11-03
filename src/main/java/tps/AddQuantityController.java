package tps;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.converter.IntegerStringConverter;

import java.util.function.UnaryOperator;

public class AddQuantityController {

    ProductListController plc;

    @FXML
    private Button btnClose;

    @FXML
    private TextField input;

    public void initialize(){
        // Sets the filter for the input field
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?([0-9]*)?")) {
                return change;
            }
            return null;
        };
        input.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter));
    }

    public void limitQty(){
        // Limits the amount the user can add to the cart
        String ipt = input.getText();
        int qty;
        if (ipt == null || ipt.isEmpty()){
            qty = 0;
        } else {
            qty = Integer.parseInt(ipt);
        }
        if (qty > plc.getQuantityTotal()){
            errorMessage("Amount is greater than current stocks");
            input.setText("0");
        }
    }

    public void enterQty(KeyEvent event){
        // Add
        if (event.getCode() == KeyCode.ENTER) {
            int qty = Integer.parseInt(input.getText());
            if (qty == 0) {
                errorMessage("Please enter an amount");
            } else {
                plc.setQuantity(qty);
                plc.addItem();
                close();
            }
        }
    }

    public void errorMessage(String msg){
        Alert error = new Alert(Alert.AlertType.INFORMATION);
        error.setTitle("Error!");
        error.setHeaderText(null);
        error.setContentText(msg);
        error.showAndWait();
    }

    public void close(){
        btnClose.getScene().getWindow().hide();
    }

    public void setPLC(ProductListController plc){
        this.plc = plc;
    }

}
