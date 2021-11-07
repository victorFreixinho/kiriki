import java.net.Socket;

public class Client {
    private Socket clientSocket;
    private String clientName;

    private Client(){}

    Client(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.clientName = "";
    }
    Client(Socket clientSocket, String clientName) {
        this.clientSocket = clientSocket;
        this.clientName = clientName;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}
