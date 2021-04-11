package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Client extends Application {
    private Button SendMessage;
    private Label label;
    private TextArea DisplayMessage;
    private TextField Message;
    private Button Quit;
    private int port = 12345;
    private String hostName = "localhost";


    @Override
    public void start(Stage MainScreen) throws Exception {
        BorderPane mainWindow = new BorderPane();
        FlowPane sendmessages = new FlowPane();
        FlowPane Displaymessages = new FlowPane();
        FlowPane Options = new FlowPane();
        FlowPane Heading = new FlowPane();


        Options.setHgap(20);
        sendmessages.setHgap(30);
        Quit = Quit();
        SendMessage = SendMessage();
        Message = new TextField();
        label = SetLabel();
        DisplayMessage = DisplayMessage();

        sendmessages.setPrefHeight(60);
        sendmessages.getChildren().add(label);
        sendmessages.getChildren().add(Message);
        sendmessages.getChildren().add(SendMessage);
        Options.getChildren().add(Quit);
        Options.setPrefWidth(100);
        SendMessage.setOnAction(e -> getMessage());
        mainWindow.setRight(Options);
        mainWindow.setBottom(sendmessages);
        sendmessages.setAlignment(Pos.CENTER);
        mainWindow.setCenter(DisplayMessage);
        Scene Window = new Scene(mainWindow, 600, 600);
        MainScreen.setScene(Window);
        MainScreen.setTitle("Chat Server");
        MainScreen.show();

        try {
            Client client = new Client();
            Socket clientSocket = client.ClientSocketBuilder();
            PrintWriter clientOutput = client.ClientPrintWriterBuilder(clientSocket);
            Scanner clientInput = client.ClientScannerBuilder(clientSocket);
            BufferedReader clientStdIn = client.ClientBufferedReaderBuilder();
            client.WelcomeMessage();
            client.HandleInput(clientOutput, clientInput, clientStdIn);
        } catch (UnknownHostException clientUnknownHostException) {
            System.err.println("Unable to find Host, Exiting");
            System.exit(1);
        } catch (IOException clientIOException) {
            System.err.println("Failed to Setup IO, Exiting");
            System.exit(1);
        } catch (NoSuchElementException clientNSElementException) {
            System.err.println("Connection to Server has Been Closed");
            System.exit(1);
        }
    }

    private Button Quit () {
        Button btn = new Button();
        btn.setText("Quit");
        return btn;
    }

    private void getMessage () {
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
        btn.setDefaultButton(true);
        return btn;
    }


    public static void main (String[]args){
        launch(args);
    }

    private void WelcomeMessage () {
        System.out.println("Welcome to the Server, Before Starting Set Your Name with the Command: " + "name " + "Followed by you username");
        System.out.println("For More Commands & Their Usage type: help");
    }

    private void HandleInput (PrintWriter clientOutput, Scanner clientInput, BufferedReader clientStdIn)
            throws IOException {
        String userInput;
        while ((userInput = clientStdIn.readLine()) != null) {
            clientOutput.println(userInput);
            int n = clientInput.nextInt();
            clientInput.nextLine();
            for (int i = 0; i < n; i++) {
                System.out.println(clientInput.nextLine());
            }
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


