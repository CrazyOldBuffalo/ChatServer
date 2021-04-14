package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
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
    public void start(Stage stage) throws Exception {
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(30);
        pane.setVgap(10);

        pane.setPadding(new Insets(25,25,25,25));
    }
}
