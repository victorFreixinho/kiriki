import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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

    private void handleClientMessage(Client client, String header, String message) {
        System.out.println("Header: " + header);
        System.out.println("Message: " + message);
    }
}
