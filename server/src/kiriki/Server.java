package kiriki;

import kiriki.Message.ClientMessageHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class Server extends ClientMessageHandler {

    private static final int PORT = 5000;
    private static final int MAX_THREADS = 25;

    private static volatile Server serverSingleton;
    private final ServerSocket serverSocket;
    private final ExecutorService executor;

    private final Map<Integer, Game> clientGameMap;
    private boolean isRunning;

    private Server() throws IOException {
            this.serverSocket = new ServerSocket(PORT);
            this.clientGameMap = new HashMap<>();
            this.executor = Executors.newFixedThreadPool(MAX_THREADS);
            this.isRunning = false;
    }

    public static Server getServer() throws IOException {
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
    
    public void startServer() throws IOException {
        Server server = Server.getServer();
        ThreadPoolExecutor pool = (ThreadPoolExecutor) executor;
        isRunning = true;
        System.out.println("Server Started with success!\n");
        while(isRunning) {
            try {
                System.out.println("Waiting for new client connection...");
                Socket clientSocket = server.serverSocket.accept();
                Client client = new Client(clientSocket);
                System.out.println("New client connected.\n");
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
        System.out.println("\nAdding client \"" + client.getClientName() +"\" to a game...");
        if(clientGameMap.get(client.getId()) == null){
            for(Game game: clientGameMap.values()) {
                if(!game.isGameInProgress()) {
                    game.addPlayer(client);
                    System.out.println("Starting new game: \"" + game.getPlayer1Name() +"\" vs \"" + game.getPlayer2Name() + "\".\n");
                    clientGameMap.put(client.getId(), game);
                    game.handleStartGame();
                    return;
                }
            }
            System.out.println("Created a new game for client \"" + client.getClientName() + "\".\n");
            clientGameMap.put(client.getId(), new Game(client));
        } else {
            System.out.println("Client \"" + client.getClientName() +"\" is already in a game.");
        }
    }

    private void handleClientMessage(Client client, String header, String message) {
        switch (header) {
            case GAME_CONFIG_HEADER: {
                String userName = getName(message);
                client.setClientName(userName);
                addClientToGame(client);
                break;
            }
            case PLAYER_GUESS_HEADER: {
                Object[] guess = getGuess(message);
                Game game = clientGameMap.get(client.getId());
                if (game == null) {
                    // TODO
                    return;
                }
                System.out.println("Received guess (" + Arrays.toString(guess) + ") from client \"" + client.getClientName() + "\".");
                game.handlePlayerGuess(client, guess);
                break;
            }
            case EXCLUDE_DICE_HEADER: {
                int value = getExcludeDiceValue(message);
                Game game = clientGameMap.get(client.getId());
                if (game == null) {
                    // TODO
                    return;
                }
                System.out.println("Received exclude player dice (value = " + value + ") from client \"" + client.getClientName() + "\".");
                game.handleExcludePlayerDice(client, value);
                break;
            }
            default: System.out.println("Incorrect Client Message header.");
        }
    }
}
