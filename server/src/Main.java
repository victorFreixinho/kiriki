import kiriki.Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Starting server...");
            Server server = Server.getServer();
            server.startServer();
        }  catch (IOException e) {
            System.out.println("Error on server socket initialization:");
            e.printStackTrace();
        }  catch (Exception e) {
            System.out.println("Unexpected error on server socket initialization:");
            e.printStackTrace();
        } finally {
            System.out.println("Closing server...");
        }
    }
}
