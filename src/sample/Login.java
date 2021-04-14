package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.text.Text;

public class Login extends Application {

    private String Username;
    private String Password;
    private Text ActionTarget;

    private void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
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
        Button login = new Button();

        pane.add(Heading,0,0,2,1);
        pane.add(usernamelabel, 0, 1);
        pane.add(usernamefield, 1,1);
        pane.add(passwordlabel, 0,2);
        pane.add(passwordfield, 1,2);

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
