package tps;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.util.function.UnaryOperator;

public class CancelOrderController {

    private DailySalesController dsc;

    @FXML
    private Button btnClose;

    @FXML
    private ComboBox<String> cboAdd;

    @FXML
    private TextField tfCancel;

    @FXML
    private TextField tfDesc;

    @FXML
    private TextField tfID;

    @FXML
    private TextField tfPcode;

    @FXML
    private TextField tfPrice;

    @FXML
    private TextField tfQty;

    @FXML
    private TextField tfReasons;

    @FXML
    private TextField tfTotal;

    @FXML
    private TextField tfTrans;

    private String addToInv, reasons;
    private int cancel, newQty;
    private double x, y;

    public void initialize(){
        // sets the filter for the input field
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?([0-9]*)?")) {
                return change;
            }
            return null;
        };
        tfCancel.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter));
    }

    public void close(){
        btnClose.getScene().getWindow().hide();
    }

    public void setDSC(DailySalesController dsc){
        // Sets the data for the text fields
        this.dsc = dsc;
        tfID.setText(dsc.id + "");
        tfPcode.setText(dsc.pcode + "");
        tfDesc.setText(dsc.desc);
        tfTrans.setText(dsc.trans);
        tfPrice.setText(dsc.price + "");
        tfQty.setText(dsc.quantity + "");
        tfTotal.setText(dsc.total + "");
        cboAdd.getItems().add("Yes");
        cboAdd.getItems().add("No");
    }

    public void errorMessage(String msg){
        Alert error = new Alert(Alert.AlertType.INFORMATION);
        error.setTitle("Error!");
        error.setHeaderText(null);
        error.setContentText(msg);
        error.showAndWait();
    }

    public void cancelOrder() throws IOException {
        addToInv = cboAdd.getValue();
        cancel = Integer.parseInt(tfCancel.getText());
        reasons = tfReasons.getText();
        newQty = dsc.quantity - cancel;
        if (addToInv == null || reasons.isEmpty() || cancel > dsc.quantity){
            errorMessage("Please fill up the fields properly");
        } else {
            loadStage();
        }
    }

    public void cancelConfirmed(){
        if (addToInv.equals("Yes")){
            dsc.updateStock(cancel);
        }
        if (newQty <= 0){
            // removes sale
            dsc.removeSale(dsc.trans + dsc.pcode);
        } else {
            dsc.updateSale(dsc.trans + dsc.pcode, newQty);
        }
        // Save a record
        Database db = new Database("CancelHistory");
        String hold = dsc.trans + "#" +
                dsc.pcode + "#" +
                dsc.barcode + "#" +
                dsc.desc + "#" +
                dsc.price + "#" +
                cancel + "#" +
                (dsc.price * cancel) + "#" +
                addToInv + "#" +
                reasons + "#" +
                dsc.pos.username + "\n";
        db.addToFile(hold);
        errorMessage("Transaction has been cancelled!");
        close();
    }

    private void loadStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("confirm-cancel.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        // Sends data to other pages
        ConfirmCancelController ccc = loader.getController();
        ccc.setCOC(this);

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

}
