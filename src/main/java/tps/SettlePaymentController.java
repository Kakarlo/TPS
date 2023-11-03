package tps;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class SettlePaymentController {

    private POSController pos;

    @FXML
    private Button btnClose;

    @FXML
    private TextField change;

    @FXML
    private TextField input;

    @FXML
    private TextField total;

    private int inputAmount;
    private double totalAmount, changeAmount;

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

    public void setPos(POSController pos){
        this.pos = pos;
        total.setText(pos.getTotal());
        totalAmount = Double.parseDouble(pos.getTotal());
        updateField("");
    }

    public void updateField(String userInput){
        if (userInput.isEmpty()){
            inputAmount = 0;
        } else {
            try {
                inputAmount = Integer.parseInt(userInput);
            } catch (NumberFormatException e){
                errorMessage("Number is too big!");
            }
        }
        input.setText(String.format("%,d", inputAmount));
        changeAmount = inputAmount - totalAmount;
        change.setText(String.format("%,.2f", changeAmount));
    }

    public void add(ActionEvent event){
        Button add = (Button) event.getSource();
        updateField(input.getText().replaceAll(",", "") + add.getText());
    }

    public void clear(){
        String clr = input.getText().replaceAll(",", "");
        clr = clr.substring(0,clr.length()-1);
        updateField(clr);
    }

    public void enter(){
        if (changeAmount < 0){
            errorMessage("Enter an amount greater than the total");
        } else {
            pos.saveSales();
            pos.inactive();
            close();
        }
    }
}
