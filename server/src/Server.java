import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Server {

    public static final String HEADER_GAME_CONFIG = "/login";
    public static final String HEADER_PLAYER_GUESS = "/guess";
    public static final String HEADER_START_ROUND = "/startRound";
    public static final String HEADER_FINISH_GAME = "/finish";
    public static final String HEADER_EXCLUDE_DICE = "/excludeDice";

    private static final int PORT = 5000;
    private static final int MAX_THREADS = 25;
    private static volatile Server serverSingleton;
    private ServerSocket serverSocket;
//    private List<Client> clientsList;
    private Map<Integer, Game> clientGameMap;
    private ExecutorService executor;

    private Server() {
        try {
            this.serverSocket = new ServerSocket(PORT);
//            this.clientsList = new ArrayList<>();
            this.clientGameMap = new HashMap<>();
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
        ThreadPoolExecutor pool = (ThreadPoolExecutor) executor;
        System.out.println("Server Started with success!");
        while(true) {
            System.out.println("Waiting for new client connection...");
            try {
                Socket clientSocket = server.serverSocket.accept();
                Client client = new Client(clientSocket);
//                clientsList.add(client);
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

    private void addClientToGame(Client client) {
        if(clientGameMap.get(client.getId()) == null){
            for(Game game: clientGameMap.values()) {
                if(!game.isGameInProgress()) {
                    game.addPlayer(client);
                    clientGameMap.put(client.getId(), game);
                    game.sendGameConfig(HEADER_GAME_CONFIG);
                    return;
                }
            }
            clientGameMap.put(client.getId(), new Game(client));
        }
    }

    private void handleClientMessage(Client client, String header, String message) {
        switch(header) {
            case HEADER_GAME_CONFIG:
                try {
                    JSONParser parser = new JSONParser();
                    JSONObject obj = (JSONObject) parser.parse(message);
                    System.out.println("Received client name: " + obj.get("userName"));
                    client.setClientName((String) obj.get("userName"));
                } catch (ParseException e) {
                    System.out.println("Error Parsing JSON object, to get client name.");
                    e.printStackTrace();
                }
                addClientToGame(client);
                break;
            case HEADER_PLAYER_GUESS:
                try {
                    JSONParser parser = new JSONParser();
                    JSONObject obj = (JSONObject) parser.parse(message);
                    ArrayList<Long> guess = (ArrayList<Long>) obj.get("guess");
                    System.out.println("Received client guess: " + guess.toString());
                    Game game = clientGameMap.get(client.getId());
                    if(game != null) {
                        System.out.println("ta caindo aqui?");
                        game.handlePlayerGuess(client, guess);
                    }
                } catch (ParseException e) {
                    System.out.println("Error Parsing JSON object, to get client guess.");
                    e.printStackTrace();
                }
                break;
            case HEADER_EXCLUDE_DICE:
                try {
                    JSONParser parser = new JSONParser();
                    JSONObject obj = (JSONObject) parser.parse(message);
                    clientGameMap.get(client.getId()).handleExcludePlayerDice(client, (long) obj.get("value"));
                } catch (ParseException e) {
                    System.out.println("Error Parsing JSON object, to get client guess.");
                    e.printStackTrace();
                }
                break;
            default:
                System.out.println("Incorrect Client Message header.");
                break;

        }
    }
}
