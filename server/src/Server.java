import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Server {

    private static volatile Server serverSingleton;
    private static final int PORT = 5000;
    private static final int MAX_THREADS = 25;
    private ServerSocket serverSocket;
    private List<Client> clientsList;
    private ExecutorService executor;

    private Server() {
        try {
            this.serverSocket = new ServerSocket(PORT);
            this.clientsList = new ArrayList<>();
            this.executor = Executors.newFixedThreadPool(MAX_THREADS);
        } catch (IOException e) {
            System.out.println("Error on server socket initialization:");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unexpected error on server socket initialization:");
            e.printStackTrace();
        }
    }

    public static Server getServer() {
        Server result = serverSingleton;
        if (result != null) {
            return result;
        }
        synchronized(Server.class) {
            if (serverSingleton == null) {
                serverSingleton = new Server();
            }
            return serverSingleton;
        }
    }
    
    public void startServer() {
        Server server = Server.getServer();
        // Cast the object to its class type
        ThreadPoolExecutor pool = (ThreadPoolExecutor) executor;
        System.out.println("Server Started with success!");
        while(true) {
            System.out.println("Waiting for new client connection...");
            try {
                Socket clientSocket = server.serverSocket.accept();
                Client client = new Client(clientSocket);
                clientsList.add(client);
                System.out.println("New client connected.");
                pool.submit(
                        new ClientListenerTask(
                                client,
                                (c, header) ->  message -> server.handleClientMessage(c, header, message)
                        )
                );
            } catch (IOException e) {
                System.out.println("Error creating client connection:");
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("Unexpected error creating client connection:");
                e.printStackTrace();
            }
        }
    }

    private Client searchForOpponent(Client client) {
        for(Client c : clientsList) {
            if(c.getId() != client.getId()) {
                if (c.getClientSocket().isClosed() || !c.getClientSocket().isConnected()) {
                    //TODO: Confirm if this is safe
                    clientsList.remove(c);
                    System.out.println("Removing client from list");
                } else if (!c.isPlaying()) {
                    return c;
                }
            }
        }
        return null;
    }

    private void startGameWithPlayers(Client player1, Client player2) {
        player1.setOpponentID(player2.getId());
        player1.setState(true);
        player2.setOpponentID(player1.getId());
        player2.setState(true);
        boolean isFirstPlayer = Math.random() > 0.5;
        sendGameConfig(player1, player2.getClientName(), isFirstPlayer);
        sendGameConfig(player2, player1.getClientName(), !isFirstPlayer);
    }

    private void sendGameConfig(Client client, String opponentName, boolean isFirstPlayer) {
        try {
            PrintWriter clientWriter = new PrintWriter(client.getClientSocket().getOutputStream());
            JSONObject obj = new JSONObject();
            obj.put("OpponentName", opponentName);
            obj.put("isFirstPlayer", isFirstPlayer);
            clientWriter.println("/login");
            clientWriter.println(obj.toJSONString());
            System.out.println("Sending initial configs to client...");
            clientWriter.flush();
        } catch(IOException e) {
            System.out.println("Error sending Message to client:");
            e.printStackTrace();
        } catch(Exception e) {
            System.out.println("Unexpected error sending Message to client:");
            e.printStackTrace();
        }
    }

    private void handleClientMessage(Client client, String header, String message) {
        switch(header) {
            case "/login":
                try {
                    JSONParser parser = new JSONParser();
                    JSONObject obj = (JSONObject) parser.parse(message);
                    System.out.println("Received client name: " + obj.get("name"));
                    client.setClientName((String) obj.get("name"));
                } catch (ParseException e) {
                    System.out.println("Error Parsing JSON object, to get client name.");
                    e.printStackTrace();
                }
                Client opponent = searchForOpponent(client);
                if(opponent != null) {
                    startGameWithPlayers(client, opponent);
                }
                break;
            default:
                System.out.println("Incorrect Client Message header.");
                break;
        }
    }
}
