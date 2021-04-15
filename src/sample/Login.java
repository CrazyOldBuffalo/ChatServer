package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.text.Text;

public class Login extends Application {

    private String Username;
    private String Password;
    private Text ActionTarget;
    private Font AppFont;

    private void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        AppFont = (Font.font("Monospaced", 16));
        GridPane pane = pane(stage);
        Scene scene = new Scene(pane, 500, 250);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();



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
        ActionTarget = new Text();

        pane.add(box, 1,4);
        pane.add(Heading,0,0,2,1);
        pane.add(usernamelabel, 0, 1);
        pane.add(usernamefield, 1,1);
        pane.add(passwordlabel, 0,2);
        pane.add(passwordfield, 1,2);
        pane.add(ActionTarget, 1, 6);

        box.getChildren().add(login);

        login.setOnAction(e -> login(usernamefield, passwordfield, stage));
        quit.setOnAction(e -> quit(stage));
        return pane;
    }

    private void quit(Stage stage) {
        stage.close();
        System.exit(1);
    }

    private void login(TextField usernamefield,TextField passwordfield, Stage stage) {
        String usernamevalue = usernamefield.getText();
        String passwordvalue = passwordfield.getText();
        if (passwordvalue.isEmpty() && usernamevalue.isEmpty()) {
            ActionTarget.setText("Please Enter A Username and Password");
        }
        else if (usernamevalue.isEmpty()) {
            ActionTarget.setText("Please Enter A Username");
        }
        else if (passwordvalue.isEmpty()) {
            ActionTarget.setText("Please Enter A Password");
        }
        else {
            Username = usernamevalue;
            Password = passwordvalue;
            Client client = new Client();
            Stage st = new Stage();
            stage.close();
            client.start(st);

        }

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
        return btn;
    }

    private TextField passwordfield() {
        TextField txt = new TextField();
        txt.setFont(Font.font("Monospaced", 12));
        txt.autosize();
        return txt;
    }

    private Label passwordlbl() {
        Label lbl = new Label();
        lbl.setText("Password: ");
        lbl.setFont(Font.font("Monospaced", 12));
        return lbl;
    }

    private TextField usernamefield() {
        TextField txt = new TextField();
        txt.setFont(Font.font("Monospaced", 12));
        txt.autosize();
        return txt;
    }

    private Label usernamelbl() {
        Label lbl = new Label();
        lbl.setText("UserName: ");
        lbl.setFont(Font.font("Monospaced", 12));
        return lbl;
    }

    private Text HeadingText() {
        Text txt = new Text();
        txt.setText("Chat Server Login");
        txt.setFont(Font.font ("Monospaced", 16));
        return txt;
    }
}
