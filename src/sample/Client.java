package sample;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

public class Client extends Application {
    private Button SendMessage;
    private Label label;
    private TextArea DisplayMessage;
    private TextField Message;
    private Button Quit;
    private Button Name;
    private Button Read;
    private Button CreateRoom;
    private int port = 12345;
    private String hostName = "localhost";


    @Override
    public void start(Stage MainScreen) {

        BorderPane mainWindow = new BorderPane();
        FlowPane sendmessages = new FlowPane();
        FlowPane Displaymessages = new FlowPane();
        VBox Options = new VBox();
        FlowPane Heading = new FlowPane();


        Options.setSpacing(40);
        sendmessages.setHgap(30);
        Read = new Button("Read");
        Name = Name();
        Quit = Quit();
        CreateRoom = CreateRoom();
        SendMessage = SendMessage();
        Message = new TextField();
        label = SetLabel();
        DisplayMessage = DisplayMessage();

        Displaymessages.getChildren().add(DisplayMessage);
        sendmessages.setPrefHeight(60);
        sendmessages.getChildren().add(label);
        sendmessages.getChildren().add(Message);
        sendmessages.getChildren().add(SendMessage);
        sendmessages.setBackground(new Background(new BackgroundFill(Color.rgb(55,71,79), CornerRadii.EMPTY, Insets.EMPTY)));

        Options.getChildren().add(Quit);
        Options.getChildren().add(Name);
        Options.getChildren().add(CreateRoom);
        Options.getChildren().add(Read);
        Options.setPrefWidth(150);
        Options.setAlignment(Pos.CENTER);
        Options.setBackground(new Background(new BackgroundFill(Color.rgb(55,71,79), CornerRadii.EMPTY, Insets.EMPTY)));
        mainWindow.setRight(Options);
        mainWindow.setBottom(sendmessages);
        sendmessages.setAlignment(Pos.CENTER);
        mainWindow.setCenter(DisplayMessage);

        try {
            Client client = new Client();
            Socket clientSocket = client.ClientSocketBuilder();
            PrintWriter clientOutput = client.ClientPrintWriterBuilder(clientSocket);
            Scanner clientInput = client.ClientScannerBuilder(clientSocket);
            BufferedReader clientStdIn = client.ClientBufferedReaderBuilder();
            WelcomeMessage();

            Scene Window = new Scene(mainWindow, 600, 600);
            MainScreen.setScene(Window);
            MainScreen.setTitle("Chat Server");
            MainScreen.show();
            SendMessage.setOnAction(e -> {
                try {
                    HandleInput(clientOutput,clientInput    );
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });
            CreateRoom.setOnAction(e-> Create(clientInput, clientOutput));
            Quit.setOnAction(e -> QuitApp(clientSocket, MainScreen));
            Read.setOnAction(e -> ReadMessages(clientOutput, clientInput));
            Name.setOnAction(e -> SetName(clientOutput,clientInput));
        } catch (UnknownHostException clientUnknownHostException) {
            System.err.println("Unable to find Host, Exiting");
            System.exit(1);
        } catch (IOException clientIOException) {
            System.err.println("Failed to Setup IO, Exiting");
            System.exit(1);
        } catch (NoSuchElementException clientNSElementException) {
            System.err.println("Connection to Server has Been Closed");
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Create(Scanner clientInput, PrintWriter clientOutput) {
        String Roomoutput;
        TextInputDialog roomname = new TextInputDialog();
        roomname.setContentText("Please Enter A Room Name");
        roomname.setHeaderText(null);
        roomname.setTitle("Room Name Input");
        Optional<String> result = roomname.showAndWait();
        Roomoutput = roomname.getEditor().getText();
        String userinput = "open " + Roomoutput;
        clientOutput.println(userinput);
        int n = clientInput.nextInt();
        clientInput.nextLine();
        for (int i = 0; i < n; i++) {
            DisplayMessage.appendText(clientInput.nextLine() + "\n");
        }
    }


    private Button CreateRoom() {
        Button btn = new Button();
        btn.setPrefHeight(40);
        btn.setPrefWidth(100);
        btn.setText("Create Room");
        return btn;
    }

    private void QuitApp(Socket clientSocket, Stage mainScreen) {
        try {
            clientSocket.close();
            mainScreen.close();
        }
        catch (IOException ioe) {
            System.err.println("Error Closing");
        }
    }


    private void ReadMessages(PrintWriter clientOutput, Scanner clientInput) {
        String userinput = "read";
        clientOutput.println(userinput);
        int n = clientInput.nextInt();
        clientInput.nextLine();
        for (int i = 0; i < n; i++) {
            DisplayMessage.appendText(clientInput.nextLine() + "\n");
        }
    }

    private void SetName(PrintWriter clientOutput, Scanner clientInput) {
        String nameoutput;
        TextInputDialog name = new TextInputDialog();
        name.setContentText("Please Enter A Name");
        name.setHeaderText(null);
        name.setTitle("Name Input");
        Optional<String> result = name.showAndWait();
        nameoutput = name.getEditor().getText();
        if (nameoutput.isEmpty()) {
            Alert alrt = new Alert(Alert.AlertType.ERROR);
            alrt.setContentText("Please Enter a Valid Name");
            alrt.setTitle("Name");
            alrt.setHeaderText(null);
            alrt.initStyle(StageStyle.UTILITY);
            alrt.showAndWait();
        }
        else {
            String userinput = "Name " + nameoutput;
            clientOutput.println(userinput);
            int n = clientInput.nextInt();
            clientInput.nextLine();
            DisplayMessage.appendText(clientInput.nextLine() + "\n");
        }
    }

    private Button Name() {
        Button btn = new Button();
        btn.setPrefHeight(40);
        btn.setPrefWidth(100);
        btn.setText("Name");
        return btn;
    }

    private Button Quit () {
        Button btn = new Button();
        btn.setPrefHeight(40);
        btn.setPrefWidth(100);
        btn.setText("Quit");
        return btn;
    }

    private TextArea DisplayMessage () {
        TextArea txtarea = new TextArea();
        txtarea.setPrefColumnCount(50);
        txtarea.setPrefRowCount(25);
        txtarea.setWrapText(true);
        txtarea.setEditable(false);

        return txtarea;
    }

    private Label SetLabel () {
        Label label = new Label();
        Font f = new Font("Monospaced", Font.PLAIN, 14);
        label.setText("Message");
        return label;
    }

    private Button SendMessage () {
        Button btn = new Button();
        btn.setWrapText(true);
        btn.setText("Send");
        btn.setPrefHeight(40);
        btn.setPrefWidth(100);
        btn.setDefaultButton(true);
        return btn;
    }

    public static void main (String[]args){
        launch(args);
    }

    private void WelcomeMessage () {
        String WelcomeMessage = "Welcome To the Server, To start set your name using the Name Button" + "\n";
        DisplayMessage.appendText(WelcomeMessage);
    }

    private void HandleInput (PrintWriter clientOutput, Scanner clientInput)
            throws IOException {
        if (Message.getText().equals("")) {
            Alert alrt = new Alert(Alert.AlertType.ERROR);
            alrt.setContentText("Please Enter Some Text");
            alrt.setTitle("No Text");
            alrt.setHeaderText(null);
            alrt.initStyle(StageStyle.UTILITY);
            alrt.showAndWait();
        }
        else {
            String userInput = "post " + Message.getText();
            clientOutput.println(userInput);
            int n = clientInput.nextInt();
            clientInput.nextLine();
            for (int i = 0; i < n; i++) {
                DisplayMessage.appendText(clientInput.nextLine() + "\n");
            }
            Message.setText("");
        }
    }

    private BufferedReader ClientBufferedReaderBuilder () {
        return new BufferedReader(new InputStreamReader(System.in));
    }

    private Scanner ClientScannerBuilder (Socket clientSocket) throws IOException {
        return new Scanner(clientSocket.getInputStream());
    }

    private Socket ClientSocketBuilder () throws IOException {
        return new Socket(hostName, port);
    }

    private PrintWriter ClientPrintWriterBuilder (Socket clientSocket) throws IOException {
        return new PrintWriter(clientSocket.getOutputStream(), true);
    }

}


