package sample;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.FileWriter;

public class ClientHandler extends Thread {
    private Socket client;
    private PrintWriter toClient;
    private BufferedReader fromClient;
    private String clientName = "client";
    private Server server;
    private static File file;
    private static ArrayList<Message> open = new ArrayList<>();
    private static ArrayList<String> clientNames = new ArrayList<>();
    private static HashMap<String, Board> boards = new HashMap<>();


    public ClientHandler(Socket clientSocket, Server server) throws IOException{
        client = clientSocket;
        toClient = ToClientBuilder();
        fromClient = FromClientBuilder();
        this.server = server;
        file = FileCreator();
        if (file.exists()) {
            Restore();
        }
    }

    private File FileCreator() {
        return new File("main.txt");
    }


    @Override
    public void run() {
        try {
            int unread = 0;
            String inputLine;
            while ((inputLine = fromClient.readLine()) != null) {
                System.out.println(inputLine);
                Scanner clientHandlerScanner = new Scanner(inputLine);
                String command  = "";
                String argument = "";
                String room = "";
                try {
                    command  = clientHandlerScanner.next();
                    if (command.equalsIgnoreCase("postto")) {
                        room = clientHandlerScanner.skip(" ").next();
                    }
                    argument = clientHandlerScanner.skip(" ").nextLine();
                }
                catch (NoSuchElementException e) {
                    System.err.println("Error");
                }
                if (command.equalsIgnoreCase("name") || command.equalsIgnoreCase("n")){
                    synchronized(ClientHandler.class) {
                        ClientNameSetter(argument);
                    }
                }
                else if ((command.equalsIgnoreCase("post") || command.equalsIgnoreCase("p")) && argument.length() > 1) {
                    if (argument.length() > 25) {
                        toClient.println(1);
                        toClient.println("Too Many Characters, max = 25");
                    }
                    else {
                        synchronized(ClientHandler.class) {
                            PostMessage(argument);
                        }
                    }
                }
                else if ((command.equalsIgnoreCase("postto") || command.equalsIgnoreCase("pt")) && (room.length() > 1 && argument.length() > 1)) {
                    if (argument.length() > 25) {
                        toClient.println(1);
                        toClient.println("Too Many Characters, max = 25");
                    }
                    else if (boards.containsKey(room)) {
                        PostToRoom(room, argument);
                    }
                    else {
                        toClient.println(1);
                        toClient.println("Room Doesn't exist");
                    }
                }
                else if ((command.equalsIgnoreCase("read") || command.equalsIgnoreCase("r"))) {
                    if (argument.length() == 0) {
                        ReadMessages(unread);
                        unread = open.size();
                    }
                    else if (argument.length() > 1 && boards.containsKey(argument)) {
                        ReadRoomMessages(argument);
                    }
                    else {
                        toClient.println(1);
                        toClient.println("Room Doesn't Exist");
                    }
                }
                else if (command.equalsIgnoreCase("open") && argument.length() > 1) {
                    CreateBoard(argument);
                }
                else if (command.equalsIgnoreCase("sub") && argument.length() > 1) {
                    SubscribetoRoom(argument);
                }
                else if(command.equalsIgnoreCase("unsub") && argument.length() > 1) {
                    Unsubscribe(argument);
                }
                else if (command.equalsIgnoreCase("help") || command.equalsIgnoreCase("h")) {
                    Help();
                }
                else if (command.equalsIgnoreCase("quit")) {
                    ClientQuit(clientHandlerScanner);
                    return;
                }
                else {
                    toClient.println(1);
                    toClient.println("Please Enter a Valid Command and/or Argument");
                }
            }
        }
        catch (IOException clientHanlderIoException) {
            System.err.println("Exception while connected");
        }
    }

    private void Unsubscribe(String argument) {
        if (boards.containsKey(argument)) {
            if (boards.get(argument).Subscribed(this)) {
                boards.get(argument).RemoveClient(this);
                toClient.println(1);
                toClient.println("You have unsubscribed from Room: " + argument);
            }
            else {
                toClient.println(1);
                toClient.println("You're Not Subscribed to This room");
            }
        }
        else {
            toClient.println(1);
            toClient.println("Room doesn't exist");
        }
    }

    private void CreateBoard(String argument) {
        if (boards.containsKey(argument)) {
            toClient.println(1);
            toClient.println("Board Already Exists with this name");
        }
        else {
            boards.put(argument, new Board(argument, this));
            toClient.println(1);
            toClient.println("Board " + argument + " Created");
        }
    }

    private void SubscribetoRoom(String argu) {
        if (boards.containsKey(argu)) {
            boards.get(argu).AddClient(this);
            toClient.println(1);
            toClient.println("Board " + argu + " Subscribed");
        }
        else if (boards.get(argu).Subscribed(this)) {
            toClient.println(1);
            toClient.println("You're already Subscribed");
        }
        else {
            toClient.println(1);
            toClient.println("Board Doesn't Exist");
        }
    }

    private void ReadRoomMessages(String argu) {
        if(boards.get(argu).Subscribed(this)) {
            ArrayList<Message> roomboard = boards.get(argu).ReadRoom();
            int roomunread = boards.get(argu).getRoomunread(this);
            if (roomunread == roomboard.size()) {
                toClient.println(1);
                toClient.println("You're Up to Date");
            }
            else {
                toClient.println(roomboard.size() - roomunread);
                for (int i = roomunread; i < roomboard.size(); i++) {
                    toClient.println(roomboard.get(i).getClientName() + " Says: " + roomboard.get(i).getMessage() + " on " + roomboard.get(i).getDate());
                }
                boards.get(argu).setRoomunread(this);
            }
        }
        else {
            toClient.println(1);
            toClient.println("You're not Subscribed, please subscribe to Post & Read to Channel" + argu);
        }
    }

    private void PostToRoom(String room, String argu) throws IOException{
        if (boards.get(room).Subscribed(this)) {
            synchronized(ClientHandler.class) {
                boards.get(room).RoomPost(clientName, argu);
                toClient.println(1);
                toClient.println("Posted to Board " + room);
            }
        }
        else {
            toClient.println(1);
            toClient.println("You're not Subscribed, please subscribe to Post & Read to Channel " + room);
        }
    }

    private void Restore() throws IOException {
        BufferedReader fr = new BufferedReader(new FileReader("main.txt"));
        String line;
        while ((line = fr.readLine()) != null) {

            String[] archives = parse(line);
            String username = archives[0];
            String Message = archives[1];
            String date = archives[2];
            String time = archives[3];
            Message archiveJson = new Message(username, Message, date + " " + time);
            open.add(archiveJson);
        }
        fr.close();
    }


    private void Help() {
        toClient.println(4);
        toClient.println("name/n - For Setting your UserName, follow this with your specified username.      e.g: name Harry");
        toClient.println("post/p - To Post a Message to the MessageBoard: to Post to A room enter the roomname to post to the Default Room Enter Global, follow this with your message.     e.g: post global Hello There");
        toClient.println("read/r - To Read all Posted Messages on the server.                                e.g: read");
        toClient.println("archive/a - Read all Stored Messages from the archive.                             e.g: archive");
        toClient.println("quit/q - To Exit from the server.                                                  e.g: quit");
    }

    private void ClientQuit(Scanner clientHandlerScanner) throws IOException {
        toClient.println(1);
        toClient.println("Exiting");
        clientNames.remove(this.clientName);
        fromClient.close();
        toClient.close();
        clientHandlerScanner.close();
    }

    private void ReadMessages(int unread) {
        if (unread == open.size()) {
            toClient.println(1);
            toClient.println("You're up to date");
        }
        else {
            toClient.println(open.size() - unread);
            for (int i = unread; i < open.size(); i++) {
                toClient.println(open.get(i).getClientName() + " Says: " + open.get(i).getMessage() + " On [ " + open.get(i).getDate() + " ]");
            }
        }
    }

    private void PostMessage(String argument) throws IOException{
        FileWriter fWriter = new FileWriter(file, true);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Message json = new Message(clientName, argument, format.format(date));
        fWriter.append(json.getClientName()).append(" ").append(json.getMessage()).append(" ").append(json.getDate()).append("\n");
        open.add(json);
        fWriter.close();
        toClient.println(1);
        toClient.println("Message Has been Posted");
    }

    private void ClientNameSetter(String argument) {
        if (argument.length() > 2 && !clientNames.contains(argument)) {
            clientName = argument;
            clientNames.add(argument);
            toClient.println(1);
            toClient.println("Your Name is now Set to: " + clientName);
        }
        else {
            toClient.println(1);
            toClient.println("Name is not Acceptable or Taken");
        }
    }

    private String[] parse(String line) {
        return line.split("\\s+");
    }

    private BufferedReader FromClientBuilder() throws IOException {
        return new BufferedReader(new InputStreamReader(client.getInputStream()));
    }

    private PrintWriter ToClientBuilder() throws IOException {
        return new PrintWriter(client.getOutputStream(), true);
    }
}
