import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ClientListenerTask implements Runnable {

    private Client client;
    private BiFunction<Client, String, Consumer<String>> onReceiveClientMessage;
    private ClientListenerTask(){}

    public ClientListenerTask(Client client, BiFunction<Client, String, Consumer<String>> onReceiveClientMessage) {
        this.client = client;
        this.onReceiveClientMessage = onReceiveClientMessage;
    }

    @Override
    public void run() {
        try {
            InputStreamReader reader = new InputStreamReader(client.getClientSocket().getInputStream());
            BufferedReader buff = new BufferedReader(reader);
            String line;
            String header = "";
            boolean isHeader = true;
            while((line = buff.readLine()) != null) {
                if(isHeader) {
                    header = line;
                } else {
                    this.onReceiveClientMessage.apply(client, header).accept(line);
                }
                isHeader = !isHeader;
            }

        } catch(IOException e) {
            System.out.println("Error on reading client message:");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unexpected error on reading client message:");
            e.printStackTrace();
        }
    }
}
