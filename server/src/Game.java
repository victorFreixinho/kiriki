import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Game {

    private Client player1;
    private List<Integer> dices1;
    private Client player2;
    private List<Integer> dices2;
    private boolean isGameInProgress;

    Game(Client player1) {
        this.player1 = player1;
        this.player2 = null;
        this.isGameInProgress = false;
    }

    Game(Client player1, Client player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.isGameInProgress = true;
    }

    public boolean isGameInProgress() {
        return isGameInProgress;
    }

    public void setGameInProgress(boolean isGameInProgress){
        this.isGameInProgress = isGameInProgress;
    }

    public Client sortFirstPlayer() {
        if(player1 == null || player2 == null) return null;
        boolean p = Math.random() > 0.5;
        return p ? player1 : player2;
    }

    public void addPlayer(Client client) {
        if(player1 == null && player2 == null) {
            player1 = client;
        } else if (player1 == null || player2 == null) {
            if(player1 == null) {
                player1 = client;
            } else {
                player2 = client;
            }
            setGameInProgress(true);
        }
    }

    public void sendGameConfig(String header) {
        Client firstPlayer = sortFirstPlayer();
        dices1 = sortInitialDices();
        dices2 = sortInitialDices();
        sendGameConfigTo(player1, header, player1.equals(firstPlayer));
        sendGameConfigTo(player2, header, !player1.equals(firstPlayer));
    }

    private void sendGameConfigTo(Client player, String header, boolean isFirstPlayer) {
        try {
            PrintWriter clientWriter = new PrintWriter(player.getClientSocket().getOutputStream());
            JSONObject jsonObj = new JSONObject();
            boolean isPlayer1 = player.equals(player1);
            jsonObj.put("opponentName", isPlayer1 ? player2.getClientName() : player1.getClientName());
            jsonObj.put("opponentSum", isPlayer1 ? getDicesSum(dices2) : getDicesSum(dices1));
            jsonObj.put("isFirstPlayer", isFirstPlayer);
            jsonObj.put("initialDices", isPlayer1 ? dices1 : dices2);
            clientWriter.println(header);
            clientWriter.println(jsonObj.toJSONString());
            System.out.println("Sending initial configs to client...");
            clientWriter.flush();
        } catch(IOException e) {
            System.out.printf("Error sending game initial config to client %s:\n", player.getClientName());
            e.printStackTrace();
        } catch(Exception e) {
            System.out.printf("Unexpected error sending game initial config to client %s:", player.getClientName());
            e.printStackTrace();
        }
    }

    private List<Integer> sortInitialDices() {
        List<Integer> arr = new ArrayList<>();
        for(int i=1; i<=5; i++) {
            arr.add((int) Math.floor(6*Math.random() + 1));
        }
        return arr;
    }

    private int getDicesSum(List<Integer> dices) {
        int sum = 0;
        for(int d : dices) {
            sum += d;
        }
        return sum;
    }
}