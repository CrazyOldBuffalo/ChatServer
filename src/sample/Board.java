package sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Board {
    private String boardName;
    private static ArrayList<ClientHandler> Clients = new ArrayList<>();
    private static ArrayList<Message> messages = new ArrayList<>();
    private HashMap<ClientHandler, Integer> unread = new HashMap<>();
    private File room;

    public Board(String sName, ClientHandler client) {
        this.boardName = sName;
        Clients.add(client);
        room = CreateRoomFile();
        if (room.exists()) {
            restoreBoard();
        }
        unread.put(client, 0);
    }

    private File CreateRoomFile() {
        return new File(boardName + ".txt");
    }

    public void AddClient(ClientHandler client) {
        Clients.add(client);
        unread.put(client, 0);
    }

    public void RemoveClient(ClientHandler client) {
        unread.remove(client);
        Clients.remove(client);
    }

    public void RoomPost(String clientName, String message) throws IOException {
        FileWriter fWriter = new FileWriter(room, true);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Message json = new Message(clientName, message, format.format(date));
        messages.add(json);
        fWriter.append(json.getClientName()).append(" ").append(json.getMessage()).append(" ").append(json.getDate()).append("\n");
        fWriter.close();
    }

    public int getRoomunread(ClientHandler client) {
        return unread.get(client);
    }

    public void setRoomunread(ClientHandler client) {
        Integer unreadvalue = unread.get(client);
        unread.put(client, unreadvalue = messages.size());
    }

    private void restoreBoard() {
        try {
            BufferedReader fr = new BufferedReader(new FileReader(boardName + ".txt"));
            String line;
            while ((line = fr.readLine()) != null) {
                String[] archives = parse(line);
                String username = archives[0];
                String Message = archives[1];
                String date = archives[2];
                String time = archives[3];
                Message archiveJson = new Message(username, Message, date + " " + time);
                messages.add(archiveJson);
            }
            fr.close();
        }
        catch (FileNotFoundException fNotFoundException) {
            System.err.println("File Does Not Exist");
        }
        catch (IOException BoardIOException) {
            System.err.println("Failed to Establish I/O for board");
        }
    }

    private String[] parse(String line) {
        return line.split("\\s+");
    }

    public String boardname() {
        return boardName;
    }

    public ArrayList<Message> ReadRoom() {
        return messages;
    }

    public boolean Subscribed(ClientHandler clientHandler) {
        return Clients.contains(clientHandler);
    }

    public int GetMessageCount() {
        return messages.size();
    }
}