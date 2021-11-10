import java.net.Socket;

public class Client {

    private static int count = 0;
    private int id;
    private Socket clientSocket;
    private String clientName;
    private boolean isPlaying;
    private int opponentID;

    private Client(){}

    Client(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.clientName = "";
        this.isPlaying = false;
        this.opponentID = -1;
        this.id = ++count;
    }
    Client(Socket clientSocket, String clientName) {
        this.clientSocket = clientSocket;
        this.clientName = clientName;
        this.isPlaying = false;
        this.opponentID = -1;
        this.id = ++count;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public String getClientName() {
        return clientName;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public int getOpponentID() {
        return opponentID;
    }

    public int getId() {
        return id;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setState(boolean isPlaying) {
        if(!isPlaying) {
            opponentID = -1;
        }
        this.isPlaying = isPlaying;

    }

    public void setOpponentID(int opponentID) {
        this.opponentID = opponentID;
    }
}
