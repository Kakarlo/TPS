package tps;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class LoginController {

    @FXML
    private Button btnLogin;

    @FXML
    private PasswordField pfPass;

    @FXML
    private TextField tfUser;

    @FXML
    private void pfPassKeyPressed(KeyEvent event) throws IOException {
        KeyCode key = event.getCode();
        if (key.equals(KeyCode.ENTER)){
            login();
        }
    }

    private String username, password;
    private final HashMap<String, String> accounts = new HashMap<>();
    private double x = 0, y = 0;

    public void initialize(){
        Database db = new Database("Login");
        ArrayList<String[]> arr = db.getFile();
        for (String[] s : arr){
            // Creates a key by combining the username and password
            accounts.put(s[0] + s[1], s[2]);
        }
    }

    public void close(){
        System.exit(0);
    }

    public void clear(){
        tfUser.setText("");
        pfPass.setText("");
    }

    public void errorMessage(String msg){
        Alert error = new Alert(Alert.AlertType.INFORMATION);
        error.setTitle("Error!");
        error.setHeaderText(null);
        error.setContentText(msg);
        error.showAndWait();
    }

    public void login() throws IOException {
        String type;

        username = tfUser.getText();
        // Check if the text field are empty
        if (username.isEmpty()) {
            errorMessage("Please enter your username");
            clear();
            return;
        }
        password = pfPass.getText();
        if (password.isEmpty()) {
            errorMessage("Please enter your password");
            clear();
            return;
        }
        // Clear fields
        clear();

        // Check for valid username and password
        if (accounts.containsKey(username + password)){
            type = accounts.get(username + password);
            // Check what kind of account is used
            if (type.equals("Admin")){
                // Run inventory
                // Soon to come
            } else if (type.equals("Cashier")){
                // Run POS
                loadStage();
            } else {
                errorMessage("Invalid type of account");
            }
        } else {
            errorMessage("Invalid username or password");
        }
    }

    private void loadStage() throws IOException {
        btnLogin.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("POS.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        // Sends data to other stages
        POSController pos = loader.getController();
        pos.setCashier_name(username);

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
        stage.setTitle("Marthinna Karla");
        stage.setScene(scene);
        stage.show();
    }

}
