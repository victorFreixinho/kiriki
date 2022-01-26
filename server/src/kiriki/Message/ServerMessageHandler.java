package kiriki.Message;

import kiriki.Client;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerMessageHandler extends MessageHandler {

    private static void sendMessage(Client player, String header, String message) throws IOException {
        System.out.println("Sending message to \""+ player.getClientName() +"\":");
        System.out.println(header);
        System.out.println(message);
        System.out.println("Message sent successfully.\n");

        PrintWriter clientWriter = new PrintWriter(player.getClientSocket().getOutputStream());
        clientWriter.println(header);
        clientWriter.println(message);
        clientWriter.flush();
    }

    protected static String getMessage( Map<String, Object> configs) {
        return new JSONObject(configs).toJSONString();
    }

    //    /login
    //    {"opponentName": "Foo", "opponentSum": 13, "isFirstPlayer": true, "initialDices": [1,2,4,2,3]}
    protected static void sendGameConfigTo(Client player, String oppName, int oppSum, boolean isFst, List<Integer> dices) {
        HashMap<String, Object> configs = new HashMap<>();
        configs.put("opponentName", oppName);
        configs.put("opponentSum", oppSum);
        configs.put("isFirstPlayer", isFst);
        configs.put("initialDices", dices);

        try {
            sendMessage(player, GAME_CONFIG_HEADER, getMessage(configs));
        } catch(IOException e) {
            System.out.println("Error sending initial configs message to client \"" + player.getClientName() + "\":");
            e.printStackTrace();
        } catch(Exception e) {
            System.out.println("Unexpected error sending initial configs message to client \"" + player.getClientName() + "\":");
            e.printStackTrace();
        }
    }

    //    /guess
    //    {"loseDice": true}
    protected static void sendWrongGuessMessageTo(Client player, boolean loseDice) {
        try {
            sendMessage(player, PLAYER_GUESS_HEADER, getMessage(Collections.singletonMap("loseDice", loseDice)));
        } catch(IOException e) {
            System.out.println("Error sending initial configs message to client \"" + player.getClientName() + "\":");
            e.printStackTrace();
        } catch(Exception e) {
            System.out.println("Unexpected error sending initial configs message to client \"" + player.getClientName() + "\":");
            e.printStackTrace();
        }
    }

    //    /startRound
    //    {"loseDice": true, "sum": 13}
    protected static void sendStartRoundMessageTo(Client player, boolean loseDice, int sum) {
        HashMap<String, Object> configs = new HashMap<>();
        configs.put("loseDice", loseDice);
        configs.put("sum", sum);

        try {
            sendMessage(player, START_ROUND_HEADER, getMessage(configs));
        } catch(IOException e) {
            System.out.println("Error sending start round message to client \"" + player.getClientName() + "\":");
            e.printStackTrace();
        } catch(Exception e) {
            System.out.println("Unexpected error sending start round message to client \"" + player.getClientName() + "\":");
            e.printStackTrace();
        }
    }

    //    /finish
    //    {"winner": true}
    protected static void sendFinishGameMessageTo(Client player, boolean winner) {
        try {
            sendMessage(player, FINISH_GAME_HEADER, getMessage(Collections.singletonMap("winner", winner)));
        } catch(IOException e) {
            System.out.println("Error sending start round message to client \"" + player.getClientName() + "\":");
            e.printStackTrace();
        } catch(Exception e) {
            System.out.println("Unexpected error sending start round message to client \"" + player.getClientName() + "\":");
            e.printStackTrace();
        }
    }
}
