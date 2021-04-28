package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.text.Text;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.HashMap;

public class Login extends Application {


    private String Username;
    private String Password;
    private Text ActionTarget;
    private Font AppFont;
    private final byte[]  salt = new byte[16];
    SecureRandom random = GenerateRandom();
    private HashMap<String, String> logins = new HashMap<>();

    private void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        AppFont = (Font.font("Monospaced", 16));
        CreateLogins();
        GridPane pane = pane(stage);
        Background bkground = new Background(new BackgroundFill(Color.rgb(55,71,79), CornerRadii.EMPTY, Insets.EMPTY));
        pane.setBackground(bkground);
        Scene scene = new Scene(pane, 500, 250);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();

    }

    private void CreateLogins() {
        try {
            KeySpec spec = new PBEKeySpec("password".toCharArray(), salt, 65536, 128);
            KeySpec specz = new PBEKeySpec("HiGuyz".toCharArray(), salt, 65536, 128);
            KeySpec testspec = new PBEKeySpec("testpass1".toCharArray(), salt, 65536, 128);
            KeySpec stronkpass = new PBEKeySpec("StonkPassword1".toCharArray(), salt, 65536, 128);
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = f.generateSecret(spec).getEncoded();
            byte[] hashz = f.generateSecret(specz).getEncoded();
            byte[] testpass = f.generateSecret(testspec).getEncoded();
            byte[] stronkpassword = f.generateSecret(stronkpass).getEncoded();
            Base64.Encoder enc = Base64.getEncoder();
            logins.put("Harry", enc.encodeToString(hash));
            logins.put("Jack", enc.encodeToString(testpass));
            logins.put("Kate", enc.encodeToString(hashz));
            logins.put("Sammy", enc.encodeToString(stronkpassword));
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException NSAException) {
            System.err.println("Ooopsie poopsie");
        }
    }

    private SecureRandom GenerateRandom() {
        SecureRandom rnd = new SecureRandom();
        rnd.nextBytes(salt);
        return rnd;
    }

    private GridPane pane(Stage stage) {
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(30);
        pane.setVgap(10);
        pane.setPadding(new Insets(25,25,25,25));
        Text Heading = HeadingText();
        Label usernamelabel = usernamelbl();
        TextField usernamefield = usernamefield();
        Label passwordlabel = passwordlbl();
        TextField passwordfield = passwordfield();
        HBox box = new HBox(10);
        Button login = loginButton();
        Button quit = quitButton();
        ActionTarget = ActionTgt();


        pane.add(box, 1,4);
        pane.add(Heading,0,0,2,1);
        pane.add(usernamelabel, 0, 1);
        pane.add(usernamefield, 1,1);
        pane.add(passwordlabel, 0,2);
        pane.add(passwordfield, 1,2);
        pane.add(ActionTarget, 1, 6);

        box.getChildren().add(login);
        box.getChildren().add(quit);

            login.setOnAction(e -> {
                try {
                    login(usernamefield, passwordfield, stage);
                } catch (NoSuchAlgorithmException | InvalidKeySpecException noSuchAlgorithmException) {
                    noSuchAlgorithmException.printStackTrace();
                }
            });
        quit.setOnAction(e -> quit(stage));
        return pane;
    }

    private Text ActionTgt() {
        Text txt = new Text();
        txt.setFont(AppFont);
        txt.setFill(Color.RED);
        return txt;
    }

    private void quit(Stage stage) {
        stage.close();
        System.exit(1);
    }

    private void login(TextField usernamefield,TextField passwordfield, Stage stage) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String usernamevalue = usernamefield.getText();
        String passwordvalue = passwordfield.getText();
        if (passwordvalue.isEmpty() && usernamevalue.isEmpty()) {
            ActionTarget.setText("Enter A Username & Password");
        }
        else if (usernamevalue.isEmpty()) {
            ActionTarget.setText("Enter A Username");
        }
        else if (passwordvalue.isEmpty()) {
            ActionTarget.setText("Enter A Password");
        }
        else {
            Username = usernamevalue;
            Password = passwordvalue;
            if (PasswordCheck()) {
                Client clt = new Client(Username);
                Stage stg = new Stage();
                clt.start(stg);
                stage.close();
            }
            else {
                ActionTarget.setText("Login Failed");
            }
        }

    }

    private boolean PasswordCheck() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec kspec = new PBEKeySpec(Password.toCharArray(), salt,65536, 128);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = f.generateSecret(kspec).getEncoded();
        Base64.Encoder enc = Base64.getEncoder();
        String Encoded = enc.encodeToString(hash);
        if (logins.containsKey(Username)) {
            return logins.get(Username).equals(Encoded);
        }
        return false;
    }


    private Button quitButton() {
        Button btn = new Button();
        btn.setText("Quit");
        btn.setFont(AppFont);
        return btn;
    }


    private Button loginButton() {
        Button btn = new Button();
        btn.setText("Login");
        btn.setFont(AppFont);
        return btn;
    }

    private TextField passwordfield() {
        TextField txt = new TextField();
        txt.setFont(AppFont);
        txt.setPrefColumnCount(15);
        return txt;
    }

    private Label passwordlbl() {
        Label lbl = new Label();
        lbl.setText("Password: ");
        lbl.setFont(AppFont);
        lbl.setTextFill(Color.WHITE);
        return lbl;
    }

    private TextField usernamefield() {
        TextField txt = new TextField();
        txt.setFont(AppFont);
        txt.setPrefColumnCount(15);
        return txt;
    }

    private Label usernamelbl() {
        Label lbl = new Label();
        lbl.setText("UserName: ");
        lbl.setFont(AppFont);
        lbl.setTextFill(Color.WHITE);
        return lbl;
    }

    private Text HeadingText() {
        Text txt = new Text();
        txt.setText("Chat Server Login");
        txt.setFont(AppFont);
        txt.setFill(Color.WHITE);
        return txt;
    }
}
