package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

import static javafx.scene.paint.Color.WHITE;
import static javafx.scene.paint.Color.color;

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
    private Button SendMsgToRoom;
    private Button ImageTest;
    private Button RecieveImage;
    private Button UIMode;
    private ComboBox<String> UIList;
    private Text Title;
    Integer imgref = 0;

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
        FlowPane Heading = Heading();
        VBox Options = createOptions();
        DisplayMessage = DisplayMessage();
        Displaymessages.getChildren().add(DisplayMessage);
        mainWindow.setTop(Heading);
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

            Scene Window = new Scene(mainWindow, 600, 700);
            MainScreen.setScene(Window);
            MainScreen.setTitle("Chat Server");
            MainScreen.show();
            SendMessage.setOnAction(e -> {
                try {
                    HandleInput(clientOutput,clientInput);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });
            SendMsgToRoom.setOnAction(e -> SendToRoom(clientInput, clientOutput));
            ReadRoom.setOnAction(e -> ReamRoomMsg(clientInput, clientOutput));
            Search.setOnAction(e -> Search(clientInput, clientOutput));
            Subscribe.setOnAction(e -> Scribing("sub ",clientInput, clientOutput));
            Unsubscribe.setOnAction(e -> Scribing("unsub ",clientInput, clientOutput));
            CreateRoom.setOnAction(e -> Create(clientInput, clientOutput));
            Quit.setOnAction(e -> QuitApp(clientSocket, MainScreen));
            Read.setOnAction(e -> ReadMessages(clientOutput, clientInput));
            ImageTest.setOnAction(e -> {
                try {
                    Image(clientSocket, clientOutput, clientInput);
                } catch (Exception ioException) {
                    ioException.printStackTrace();
                }
            });
            RecieveImage.setOnAction(e -> {
                try {
                    GetImages(clientOutput,clientInput, clientSocket);
                } catch (IOException | ClassNotFoundException ioException) {
                    ioException.printStackTrace();
                }
            });
            UIMode.setOnAction(e -> ChangeMode(Options, Heading, sendmessages, Displaymessages));
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


    private void ChangeMode(VBox options, FlowPane heading, FlowPane sendmessages, FlowPane displayMessages) {
        if (UIList.getValue().equalsIgnoreCase("darkmode")) {
            options.setBackground(new Background(new BackgroundFill(Color.rgb(55,71,79), CornerRadii.EMPTY, Insets.EMPTY)));
            heading.setBackground(new Background(new BackgroundFill(Color.rgb(45, 51, 59), CornerRadii.EMPTY, Insets.EMPTY)));
            sendmessages.setBackground(new Background(new BackgroundFill(Color.rgb(55,71,79), CornerRadii.EMPTY, Insets.EMPTY)));
            displayMessages.setBackground(new Background(new BackgroundFill(Color.rgb(75, 89, 97),CornerRadii.EMPTY, Insets.EMPTY)));
            Title.setFill(Color.rgb(173, 186, 197));
            label.setTextFill(Color.rgb(173, 186, 197));
        }
        else if (UIList.getValue().equalsIgnoreCase("lightmode")) {
            options.setBackground(new Background(new BackgroundFill(Color.rgb(205, 217, 229), CornerRadii.EMPTY, Insets.EMPTY)));
            heading.setBackground(new Background(new BackgroundFill(Color.rgb(45, 51, 59), CornerRadii.EMPTY, Insets.EMPTY)));
            sendmessages.setBackground(new Background(new BackgroundFill(Color.rgb(205, 217, 229), CornerRadii.EMPTY, Insets.EMPTY)));
            Title.setFill(Color.rgb(36, 41, 72));
            label.setTextFill(Color.rgb(36, 41, 72));
        }
        else {
            System.out.println("Default");
        }
    }

    private void GetImages(PrintWriter clientOutput, Scanner clientInput, Socket clientSocket) throws IOException, ClassNotFoundException {
        String filename = "test.jpeg";
        FileOutputStream media = new FileOutputStream(filename);
        String Request = "RImage";
        clientOutput.println(Request);
        int n = clientInput.nextInt();
        for (int i = 0; i < n; i++) {
            ObjectInputStream inStream = new ObjectInputStream(clientSocket.getInputStream());
            byte[] barray = (byte[])inStream.readObject();
            media.write(barray);

        }
        media.close();
        Desktop.getDesktop().open(new File(filename));
    }

    private void Image(Socket clientSocket, PrintWriter clientOutput, Scanner clientInput) throws IOException {
        Stage stg = new Stage();
        FileChooser flchooser = new FileChooser();
        flchooser.setTitle("Open File");
        File file = flchooser.showOpenDialog(stg);
        if (file != null) {
            String Request = "SImage " + file;
            clientOutput.println(Request);
            int n = clientInput.nextInt();
            clientInput.nextLine();
            for (int i = 0; i < n; i++) {
                DisplayMessage.appendText(clientInput.nextLine() + "\n");
            }
        }
        else {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("No Image Selected");
            err.setContentText("You have not selected an error");
            err.setHeaderText(null);
            err.initStyle(StageStyle.UTILITY);
            err.showAndWait();
        }
    }

    //Button Functions Are All Here, In the Order they Appear
    private void QuitApp(Socket clientSocket, Stage mainScreen) {
        try {
            clientSocket.close();
            mainScreen.close();
        }
        catch (IOException ioe) {
            System.err.println("Error Closing");
        }
    }

    private void Search(Scanner clientInput, PrintWriter clientOutput) {
        String Output;
        String type = "search ";
        TextInputDialog Search = new TextInputDialog();
        Search.setContentText("Please Enter A Search Term");
        Search.setTitle("Search");
        Search.setHeaderText(null);
        Optional<String> result = Search.showAndWait();
        if (result.isEmpty()) {
            Alert NullTextErrorBox = NullTextErrorBox();
            NullTextErrorBox.showAndWait();
        }
        else {
            Output = "search " + Search.getEditor().getText();
            HandleSubmit(type, Output, clientInput, clientOutput);
        }

    }

    private void ReamRoomMsg(Scanner clientInput, PrintWriter clientOutput) {
        String Output;
        String Type = "read ";
        TextInputDialog Search = new TextInputDialog();
        Search.setContentText("Please Enter A Room To Read");
        Search.setTitle("Read Room");
        Search.setHeaderText(null);
        Optional<String> result = Search.showAndWait();
        if (result.isEmpty()) {
            Alert NullTextErrorBox = NullTextErrorBox();
            NullTextErrorBox.showAndWait();
        }
        else {
            Output = Search.getEditor().getText();
            HandleSubmit(Type, Output, clientInput, clientOutput);
        }
    }

    private void SendToRoom(Scanner clientInput, PrintWriter clientOutput) {
        String Output;
        String Type = "postto ";
        TextInputDialog Search = new TextInputDialog();
        Search.setContentText("Please Enter A Room To Post To");
        Search.setTitle("Post To Room");
        Search.setHeaderText(null);
        Optional<String> result = Search.showAndWait();
        if (result.isEmpty() || Message.getText().isEmpty()) {
            Alert NullTextErrorBox = NullTextErrorBox();
            NullTextErrorBox.showAndWait();
        }
        else {
            Output = Search.getEditor().getText() + " " + Message.getText();
            HandleSubmit(Type, Output, clientInput, clientOutput);
            Message.setText("");
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

    private void Scribing(String Type, Scanner clientInput, PrintWriter clientOutput) {
        String Output;
        TextInputDialog subscribename = new TextInputDialog();
        subscribename.setContentText("Please Enter A Room Name");
        subscribename.setTitle("Subscribe ");
        subscribename.setHeaderText(null);
        Optional<String> result = subscribename.showAndWait();
        Output = subscribename.getEditor().getText();
        if (subscribename.getEditor().getText().isEmpty()) {
            Alert NullTextErrorBox = NullTextErrorBox();
            NullTextErrorBox.showAndWait();
        }
        else {
            HandleSubmit(Type, Output, clientInput, clientOutput);
        }
    }

    private void Create(Scanner clientInput, PrintWriter clientOutput) {
        String Output;
        String Type = "open ";
        TextInputDialog roomname = new TextInputDialog();
        roomname.setContentText("Please Enter A Room Name");
        roomname.setHeaderText(null);
        roomname.setTitle("Room Name Input");
        Optional<String> result = roomname.showAndWait();
        if (result.isEmpty() || roomname.getEditor().getText().equals("")) {
            Alert NullTextErrorBox = NullTextErrorBox();
            NullTextErrorBox.showAndWait();
        }
        else {
            Output = roomname.getEditor().getText();
            HandleSubmit(Type, Output, clientInput, clientOutput);
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
            Alert NullTextErrorBox = NullTextErrorBox();
            NullTextErrorBox.showAndWait();
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

    private void HandleSubmit(String Type, String Output, Scanner clientInput, PrintWriter clientOutput) {
        clientOutput.println(Type + Output);
        int n = clientInput.nextInt();
        clientInput.nextLine();
        for (int i = 0; i < n; i++) {
            DisplayMessage.appendText(clientInput.nextLine() + "\n");
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
        CreateRoom = CreateRoom();
        Unsubscribe = UnsubtoRoom();
        Subscribe  = SubtoRoom();
        ReadRoom = ReadRoom();
        Search = Searchbtn();
        ImageTest = ImageTest();
        RecieveImage = RecieveImage();
        vb.getChildren().add(Subscribe);
        vb.getChildren().add(Unsubscribe);
        vb.getChildren().add(Search);
        vb.getChildren().add(CreateRoom);
        vb.getChildren().add(ReadRoom);
        vb.getChildren().add(Read);
        vb.getChildren().add(ImageTest);
        vb.getChildren().add(RecieveImage);
        vb.setPrefWidth(150);
        vb.setAlignment(Pos.CENTER);
        vb.setBackground(new Background(new BackgroundFill(Color.rgb(55,71,79), CornerRadii.EMPTY, Insets.EMPTY)));
        return vb;
    }

    private Button RecieveImage() {
        Button btn = new Button();
        btn.setText("Get Images");
        btn.setWrapText(true);
        btn.setPrefHeight(40);
        btn.setPrefWidth(130);
        btn.setFont(AppFont);
        return btn;
    }

    private FlowPane sendMessage() {
        FlowPane fp = new FlowPane();
        SendMessage = SendMessage();
        Message = new TextField();
        label = SetLabel();
        SendMsgToRoom = SendMsgToRoom();
        fp.setHgap(30);
        fp.setPrefHeight(60);
        fp.getChildren().add(label);
        fp.getChildren().add(Message);
        fp.getChildren().add(SendMessage);
        fp.getChildren().add(SendMsgToRoom);
        fp.setBackground(new Background(new BackgroundFill(Color.rgb(55,71,79), CornerRadii.EMPTY, Insets.EMPTY)));

        return fp;
    }

    private Button ImageTest() {
        Button btn = new Button();
        btn.setWrapText(true);
        btn.setText("Image");
        btn.setPrefHeight(40);
        btn.setPrefWidth(130);
        btn.setFont(AppFont);
        return btn;
    }

    private Button SendMsgToRoom() {
        Button btn = new Button();
        btn.setWrapText(true);
        btn.setText("Send To");
        btn.setPrefHeight(40);
        btn.setPrefWidth(130);
        btn.setFont(AppFont);
        return btn;
    }

    private Alert NullTextErrorBox() {
        Alert alrt = new Alert(Alert.AlertType.ERROR);
        alrt.setContentText("Please Enter Some Text");
        alrt.setTitle("No Text");
        alrt.setHeaderText(null);
        alrt.initStyle(StageStyle.UTILITY);
        return alrt;
    }

    private FlowPane Heading() {
        FlowPane pane = new FlowPane();
        Title = Title();
        UIMode = ModeButton();
        Quit = Quit();
        ObservableList<String> options = FXCollections.observableArrayList(
                "LightMode",
                "DarkMode"
        );

        UIList = new ComboBox<>(options);
        UIList.setValue("DarkMode");
        pane.getChildren().add(Quit);
        pane.setHgap(20);
        pane.getChildren().add(Title);
        pane.getChildren().add(UIList);
        pane.getChildren().add(UIMode);
        pane.setPrefHeight(50);
        pane.setAlignment(Pos.CENTER);
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(45, 51, 59), CornerRadii.EMPTY, Insets.EMPTY)));
        return pane;
    }

    private Button ModeButton() {
        Button btn = new Button();
        btn.setText("Set Mode");
        btn.setPrefHeight(30);
        btn.setPrefWidth(100);
        btn.setFont(AppFont);
        btn.setWrapText(true);
        return btn;
    }

    private Text Title() {
        Text txt = new Text();
        txt.setText("Chat Server V0.1");
        txt.setFont(AppFont);
        txt.setFill(WHITE);
        return txt;
    }

    private Button Quit () {
        Button btn = new Button();
        btn.setPrefHeight(30);
        btn.setPrefWidth(90);
        btn.setFont(AppFont);
        btn.setText("Quit");
        btn.setWrapText(true);
        return btn;
    }
    //---------------------------------------------------------------------------------------
}


