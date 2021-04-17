package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
    private final String username;
    private Button SendMessage;
    private Label label;
    private TextArea DisplayMessage;
    private TextField Message;
    private Button Quit;
    private Button Read;
    private Button CreateRoom;
    private final int port = 12345;
    private final String hostName = "localhost";
    private Button Unsubscribe;
    private Button Subscribe;
    private final Font AppFont = (Font.font("Monospaced", 16));
    private Button ReadRoom;

    public Client(String username) {
        this.username = username;
    }


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
        Quit = Quit();
        CreateRoom = CreateRoom();
        SendMessage = SendMessage();
        Message = new TextField();
        label = SetLabel();
        DisplayMessage = DisplayMessage();
        Unsubscribe = UnsubtoRoom();
        Subscribe  = SubtoRoom();
        ReadRoom = ReadRoom();

        Displaymessages.getChildren().add(DisplayMessage);
        sendmessages.setPrefHeight(60);
        sendmessages.getChildren().add(label);
        sendmessages.getChildren().add(Message);
        sendmessages.getChildren().add(SendMessage);
        sendmessages.setBackground(new Background(new BackgroundFill(Color.rgb(55,71,79), CornerRadii.EMPTY, Insets.EMPTY)));

        Options.getChildren().add(Quit);
        Options.getChildren().add(CreateRoom);
        Options.getChildren().add(Subscribe);
        Options.getChildren().add(Unsubscribe);
        Options.getChildren().add(Read);
        Options.getChildren().add(ReadRoom);
        Options.setPrefWidth(150);
        Options.setAlignment(Pos.CENTER);
        Options.setBackground(new Background(new BackgroundFill(Color.rgb(55,71,79), CornerRadii.EMPTY, Insets.EMPTY)));
        mainWindow.setRight(Options);
        mainWindow.setBottom(sendmessages);
        sendmessages.setAlignment(Pos.CENTER);
        mainWindow.setCenter(DisplayMessage);

        try {
            Socket clientSocket = ClientSocketBuilder();
            PrintWriter clientOutput = ClientPrintWriterBuilder(clientSocket);
            Scanner clientInput = ClientScannerBuilder(clientSocket);
            BufferedReader clientStdIn = ClientBufferedReaderBuilder();
            clientOutput.println("Name " + username);
            int n = clientInput.nextInt();
            clientInput.nextLine();
            for (int i = 0; i < n; i++) {
                DisplayMessage.appendText(clientInput.nextLine() + "\n");
            }
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
            Subscribe.setOnAction(e -> Subscribe(clientInput, clientOutput));
            CreateRoom.setOnAction(e -> Create(clientInput, clientOutput));
            Quit.setOnAction(e -> QuitApp(clientSocket, MainScreen));
            Read.setOnAction(e -> ReadMessages(clientOutput, clientInput));
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

    private void Subscribe(Scanner clientInput, PrintWriter clientOutput) {
        String Subscribeoutput;

    }

    private Button ReadRoom() {
        Button btn = new Button();
        btn.setPrefHeight(40);
        btn.setPrefWidth(100);
        btn.setText("Read Room");
        return btn;
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

    private Button SubtoRoom() {
        Button btn = new Button();
        btn.setPrefHeight(40);
        btn.setPrefWidth(100);
        btn.setText("Subscribe");
        return btn;
    }

    private Button UnsubtoRoom() {
        Button btn = new Button();
        btn.setPrefHeight(40);
        btn.setPrefWidth(100);
        btn.setText("Unsubscribe");
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
        Font f = new Font("Monospaced", 14);
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


