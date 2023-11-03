package tps;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.HashMap;

public class ConfirmCancelController {

    private CancelOrderController coc;

    @FXML
    private Button btnClose;

    @FXML
    private PasswordField pass;

    @FXML
    private TextField user;

    private String username, password;
    private final HashMap<String, String> accounts = new HashMap<>();

    public void initialize(){
        Database db = new Database("Login");
        ArrayList<String[]> arr = db.getFile();
        for (String[] s : arr){
            // Creates a key by combining the username and password
            accounts.put(s[0] + s[1], s[2]);
        }
    }

    public void close(){
        btnClose.getScene().getWindow().hide();
    }

    public void clear(){
        user.setText("");
        pass.setText("");
    }

    public void errorMessage(String msg){
        Alert error = new Alert(Alert.AlertType.INFORMATION);
        error.setTitle("Error!");
        error.setHeaderText(null);
        error.setContentText(msg);
        error.showAndWait();
    }

    public void setCOC(CancelOrderController coc){
        this.coc = coc;
    }

    public void login(){
        String type;

        username = user.getText();
        if (username.isEmpty()) {
            errorMessage("Please enter your username");
            clear();
            return;
        }
        password = pass.getText();
        if (password.isEmpty()) {
            errorMessage("Please enter your password");
            clear();
            return;
        }
        clear();

        if (accounts.containsKey(username + password)){
            type = accounts.get(username + password);
            // Check what kind of account is used
            if (type.equals("Admin")){
                coc.cancelConfirmed();
                close();
            } else {
                errorMessage("The account does not have the authority to confirm cancellation!");
            }
        } else {
            errorMessage("Invalid username or password");
        }
    }

}
