package kiriki;

import java.net.Socket;

public class Client {

    private static int count = 0;
    private int id;
    private Socket clientSocket;
    private String clientName;

    private Client(){}

    Client(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.clientName = "";
        this.id = ++count;
    }

    Client(Socket clientSocket, String clientName) {
        this.clientSocket = clientSocket;
        this.clientName = clientName;
        this.id = ++count;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public String getClientName() {
        return clientName;
    }

    public int getId() {
        return id;
    }

    public void setClientName(String clientName) {
        this.clientName = (clientName == null || clientName.isBlank()) ? "" : clientName;
    }
}
