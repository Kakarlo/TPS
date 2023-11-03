package tps;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

import java.util.ArrayList;
import java.util.HashMap;

public class ChangePasswordController {

    @FXML
    private Button btnClose;

    @FXML
    private PasswordField confirmPass;

    @FXML
    private PasswordField newPass;

    @FXML
    private PasswordField oldPass;

    public String username;

    private final HashMap<String, Integer> accounts = new HashMap<>();
    private ArrayList<String[]> arr;
    private Database db;

    public void initialize(){
        db = new Database("Login");
        arr = db.getFile();
        for (int i = 0; i < arr.size(); i++){
            // Creates a key by combining the username and password
            accounts.put(arr.get(i)[0] + arr.get(i)[1], i);
        }
    }

    public void setUser(String username){
        this.username = username;
    }

    public void close(){
        btnClose.getScene().getWindow().hide();
    }

    public void clear(){
        oldPass.setText("");
        newPass.setText("");
        confirmPass.setText("");
    }

    public void errorMessage(String msg){
        Alert error = new Alert(Alert.AlertType.INFORMATION);
        error.setTitle("Error!");
        error.setHeaderText(null);
        error.setContentText(msg);
        error.showAndWait();
    }

    public void saveAccounts(){
        String hold = "";
        for (String[] s: arr){
            hold += s[0] + "#" + s[1] + "#" + s[2] + "\n";
        }
        db.storeToFile(hold);
    }

    public void changePass(){
        String oldP, newP, confirmP;

        oldP = oldPass.getText();
        newP = newPass.getText();
        confirmP = confirmPass.getText();
        if (oldP.isEmpty() || newP.isEmpty() || confirmP.isEmpty()) {
            errorMessage("Please fill up all the fields");
            clear();
            return;
        }
        clear();

        if (accounts.containsKey(username + oldP)){
            if (newP.equals(confirmP)){
                int index = accounts.get(username + oldP);
                arr.get(index)[1] = newP;
                saveAccounts();
                errorMessage("Password has been updated!");
                close();
            } else {
                errorMessage("New passwords does not match!");
            }
        } else {
            errorMessage("Wrong old password!");
        }
    }

}
