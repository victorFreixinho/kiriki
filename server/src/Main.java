public class Main {
    public static void main(String[] args) {
        Server server = Server.getServer();
        server.startServer();
        System.out.println("Closing server...");
    }
}
