package sample;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageFile extends Application {

    private final Scanner Input;
    private final PrintWriter Output;
    private final TextArea MsgArea;
    Desktop desktop = Desktop.getDesktop();
    FileChooser flchooser = new FileChooser();

    public ImageFile(PrintWriter clientOutput, Scanner clientInput, TextArea displayMessage) {
        Input = clientInput;
        Output = clientOutput;
        MsgArea = displayMessage;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Button Open = new Button("Open");
        BorderPane pane = new BorderPane();
        Button Cancel = new Button("Cancel");
        VBox box = new VBox();
        box.setSpacing(20);
        box.setAlignment(Pos.CENTER);
        flchooser.setTitle("Open File");
        box.getChildren().add(Open);
        box.getChildren().add(Cancel);
        pane.setCenter(box);

        stage.setTitle("Chose an Image");
        stage.setScene(new Scene(pane, 300, 300));
        stage.show();

        Open.setOnAction(e -> LoadFile(stage));
        Cancel.setOnAction(e -> CloseImage(stage));
    }

    private void CloseImage(Stage stage) {
        stage.close();
    }

    private void LoadFile(Stage stage) {
        File file = flchooser.showOpenDialog(stage);
        if (file != null) {
            try {
                long fl = file.length();
                int filelen = (int) fl;
                byte[] barray = new byte[filelen];
                FileInputStream stream = new FileInputStream(file);
                String Request = "SImage " + stream.read(barray);
                Output.println(Request);
                int n = Input.nextInt();
                Input.nextLine();
                for (int i = 0; i < n; i++) {
                    Input.nextLine();
                }
                stage.close();
            }
            catch (IOException e) {
                Logger.getLogger(ImageFile.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        else {
            stage.close();
        }
    }
}
