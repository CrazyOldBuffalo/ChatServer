package sample;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private int portNumber = 12345;
    private ArrayList<ClientHandler> clientThreads = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        try {
            ServerSocket serverSocket = server.serverSocketBuilder();
            server.AcceptConnections(serverSocket);
        }
        catch (IOException e) {
            System.err.println("Error Occurred when Listening on port, Exiting");
            System.exit(1);
        }
    }

    private ServerSocket serverSocketBuilder() throws IOException {
        return new ServerSocket(portNumber);
    }

    private void AcceptConnections(ServerSocket serverSocket) throws IOException {
        while (true)
        {
            Socket clientSocket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(clientSocket, this);
            clientThreads.add(clientHandler);
            clientHandler.start();
        }
    }

    public void EndConnection(ClientHandler clientHandler, Socket clientSocket) {
        try {
            clientThreads.remove(clientHandler);
            clientSocket.close();
        }
        catch (IOException clientIOException) {
            System.err.println("Error in Closing Client Connection");
        }
    }


}