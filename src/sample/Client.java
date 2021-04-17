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

import static javafx.scene.paint.Color.WHITE;

public class Client extends Application {
    private final String username;
    private final int port = 12345;
    private final String hostName = "localhost";

    private final Font AppFont = (Font.font("Monospaced", 16));
    private Label label;
    private TextArea DisplayMessage;
    private TextField Message;
    private Button Unsubscribe;
    private Button Subscribe;
    private Button Search;
    private Button ReadRoom;
    private Button Quit;
    private Button Read;
    private Button CreateRoom;
    private Button SendMessage;

    public Client(String username) {
        this.username = username;
    }

    public static void main (String[]args){
        launch(args);
    }

    @Override
    public void start(Stage MainScreen) {
        BorderPane mainWindow = new BorderPane();
        FlowPane sendmessages = sendMessage();
        FlowPane Displaymessages = new FlowPane();
        FlowPane Heading = new FlowPane();
        VBox Options = createOptions();
        DisplayMessage = DisplayMessage();
        Displaymessages.getChildren().add(DisplayMessage);

        mainWindow.setRight(Options);
        mainWindow.setBottom(sendmessages);
        sendmessages.setAlignment(Pos.CENTER);
        mainWindow.setCenter(DisplayMessage);

        try {
            Socket clientSocket = ClientSocketBuilder();
            PrintWriter clientOutput = ClientPrintWriterBuilder(clientSocket);
            Scanner clientInput = ClientScannerBuilder(clientSocket);
            BufferedReader clientStdIn = ClientBufferedReaderBuilder();
            SetUsername(clientInput, clientOutput);

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
            Search.setOnAction(e -> Search(clientInput, clientOutput));
            Subscribe.setOnAction(e -> Scribing("sub",clientInput, clientOutput));
            Unsubscribe.setOnAction(e -> Scribing("unsub",clientInput, clientOutput));
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

    private void SetUsername(Scanner clientInput, PrintWriter clientOutput) {
        clientOutput.println("Name " + username);
        int n = clientInput.nextInt();
        clientInput.nextLine();
        for (int i = 0; i < n; i++) {
            System.out.println(clientInput.nextLine() + "\n");
        }
    }

    private void Search(Scanner clientInput, PrintWriter clientOutput) {
        String Output;
        TextInputDialog Search = new TextInputDialog();
        Search.setContentText("Please Enter A Search Term");
        Search.setTitle("Search");
        Search.setHeaderText(null);
        Optional<String> result = Search.showAndWait();
        Output = "search " + Search.getEditor().getText();
        clientOutput.println(Output);
        int n = clientInput.nextInt();
        clientInput.nextLine();
        for (int i = 0; i < n; i++) {
            DisplayMessage.appendText(clientInput.nextLine() + "\n");
        }
    }

    private void Scribing(String Type, Scanner clientInput, PrintWriter clientOutput) {
        String Output;
        TextInputDialog subscribename = new TextInputDialog();
        subscribename.setContentText("Please Enter A Room Name");
        subscribename.setTitle("Subscribe");
        subscribename.setHeaderText(null);
        Optional<String> result = subscribename.showAndWait();
        Output = Type + " " + subscribename.getEditor().getText();
        clientOutput.println(Output);
        int n = clientInput.nextInt();
        clientInput.nextLine();
        for (int i = 0; i < n; i++) {
            DisplayMessage.appendText(clientInput.nextLine() + "\n");
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

    private void WelcomeMessage () {
        String WelcomeMessage = "Welcome To the Server: " + this.username + "\n";
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

    //Socket Constructor methods, used for making the sockets and Readers/Scanners for I/O
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
    // ---------------------------------------------------------------------------------------

    //Element Creation Section - Buttons, Fields, Labels are all made here
    private Button Searchbtn() {
        Button btn = new Button();
        btn.setPrefHeight(40);
        btn.setPrefWidth(130);
        btn.setFont(AppFont);
        btn.setText("Search");
        return btn;
    }

    private Button ReadRoom() {
        Button btn = new Button();
        btn.setPrefHeight(40);
        btn.setPrefWidth(130);
        btn.setFont(AppFont);
        btn.setText("Read Room");
        btn.setWrapText(true);
        return btn;
    }

    private Button Quit () {
        Button btn = new Button();
        btn.setPrefHeight(40);
        btn.setPrefWidth(130);
        btn.setFont(AppFont);
        btn.setText("Quit");
        btn.setWrapText(true);
        return btn;
    }

    private TextArea DisplayMessage () {
        TextArea txtarea = new TextArea();
        txtarea.setPrefColumnCount(50);
        txtarea.setPrefRowCount(25);
        txtarea.setWrapText(true);
        txtarea.setEditable(false);
        txtarea.setFont(AppFont);
        return txtarea;
    }

    private Label SetLabel () {
        Label label = new Label();
        label.setFont(AppFont);
        label.setTextFill(WHITE);
        label.setText("Message");
        return label;
    }

    private Button Read() {
        Button btn = new Button();
        btn.setPrefHeight(40);
        btn.setPrefWidth(130);
        btn.setText("Read");
        btn.setFont(AppFont);
        return btn;
    }

    private Button SendMessage () {
        Button btn = new Button();
        btn.setWrapText(true);
        btn.setText("Send");
        btn.setPrefHeight(40);
        btn.setPrefWidth(130);
        btn.setDefaultButton(true);
        btn.setFont(AppFont);
        return btn;
    }

    private Button CreateRoom() {
        Button btn = new Button();
        btn.setPrefHeight(40);
        btn.setPrefWidth(130);
        btn.setText("Create Room");
        btn.setWrapText(true);
        btn.setFont(AppFont);
        return btn;
    }

    private Button SubtoRoom() {
        Button btn = new Button();
        btn.setPrefHeight(40);
        btn.setPrefWidth(130);
        btn.setText("Subscribe");
        btn.setWrapText(true);
        btn.setFont(AppFont);
        return btn;
    }

    private Button UnsubtoRoom() {
        Button btn = new Button();
        btn.setPrefHeight(40);
        btn.setPrefWidth(130);
        btn.setText("Unsubscribe");
        btn.setWrapText(true);
        btn.setFont(AppFont);
        return btn;
    }

    private VBox createOptions() {
        VBox vb = new VBox();
        vb.setSpacing(20);
        Read = Read();
        Quit = Quit();
        CreateRoom = CreateRoom();
        Unsubscribe = UnsubtoRoom();
        Subscribe  = SubtoRoom();
        ReadRoom = ReadRoom();
        Search = Searchbtn();
        vb.getChildren().add(Quit);
        vb.getChildren().add(Subscribe);
        vb.getChildren().add(Unsubscribe);
        vb.getChildren().add(Search);
        vb.getChildren().add(CreateRoom);
        vb.getChildren().add(ReadRoom);
        vb.getChildren().add(Read);
        vb.setPrefWidth(150);
        vb.setAlignment(Pos.CENTER);
        vb.setBackground(new Background(new BackgroundFill(Color.rgb(55,71,79), CornerRadii.EMPTY, Insets.EMPTY)));
        return vb;
    }

    private FlowPane sendMessage() {
        FlowPane fp = new FlowPane();
        SendMessage = SendMessage();
        Message = new TextField();
        label = SetLabel();
        fp.setHgap(30);
        fp.setPrefHeight(60);
        fp.getChildren().add(label);
        fp.getChildren().add(Message);
        fp.getChildren().add(SendMessage);
        fp.setBackground(new Background(new BackgroundFill(Color.rgb(55,71,79), CornerRadii.EMPTY, Insets.EMPTY)));

        return fp;
    }
    //---------------------------------------------------------------------------------------
}


