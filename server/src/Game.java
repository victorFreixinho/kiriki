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

    private List<Integer> sortInitialDices() {
        List<Integer> arr = new ArrayList<>();
        for(int i=1; i<=5; i++) {
            arr.add((int) Math.floor(6*Math.random() + 1));
        }
        return arr;
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

//    /login
//    {"opponentName": "Foo", "opponentName": 13, "isFirstPlayer": true, "initialDices": [1,2,4,2,3]}
    private void sendGameConfigTo(Client player, String header, boolean isFirstPlayer) {
        boolean isPlayer1 = player.equals(player1);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("opponentName", isPlayer1 ? player2.getClientName() : player1.getClientName());
        jsonObj.put("opponentSum", isPlayer1 ? getDicesSum(dices2) : getDicesSum(dices1));
        jsonObj.put("isFirstPlayer", isFirstPlayer);
        jsonObj.put("initialDices", isPlayer1 ? dices1 : dices2);

        sendMessage(player, Server.HEADER_GAME_CONFIG, jsonObj.toJSONString(), "initial configs message");
    }

    private int getDicesSum(List<Integer> dices) {
        int sum = 0;
        for(int d : dices) {
            sum += d;
        }
        return sum;
    }

    public void handlePlayerGuess(Client player, ArrayList<Long> guess) {
        if(!isGameInProgress) return;
        boolean isPlayer1 = player.getId() == player1.getId();
        ArrayList<Integer> opponentDicesCopy = new ArrayList<>((isPlayer1 ? dices2 : dices1));
        int points = 0;

        for(long i : guess) {

            if(opponentDicesCopy.contains((int) i)) {
                opponentDicesCopy.remove((Integer) (int) i);
                points++;
            }
        }
        System.out.println("op:"+opponentDicesCopy);
        System.out.printf("Guess: "+guess);
        System.out.println("Points: "+points);
        if(guess.size() == points) {
            System.out.println("Dices1: "+(isPlayer1 ? dices1.toString() : dices2.toString()));
            sendFinishGameMessage(isPlayer1);
        } else {
            if(isPlayer1 ? dices1.size()>2 : dices2.size()>2) {
                if(guess.size() < 2 * points) {
                    sendWrongGuessMessage(isPlayer1, false);
                    sendStartRoundMessage(!isPlayer1, false);
                } else {
                    sendWrongGuessMessage(isPlayer1, true);
                }
            } else {
                System.out.println("Dices2: "+(isPlayer1 ? dices1.toString() : dices2.toString()));
                sendFinishGameMessage(!isPlayer1);
            }
        }
    }

//    /guess
//    {"loseDice": true}
    private void sendWrongGuessMessage(boolean isPlayer1, boolean loseDice) {
        if(!isGameInProgress) return;
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("loseDice", loseDice);

        sendMessage(
                isPlayer1 ? player1 : player2,
                Server.HEADER_PLAYER_GUESS,
                jsonObj.toJSONString(),
            "wrong guess message"
        );
    }

    public void handleExcludePlayerDice(Client player, long value) {
        if(!isGameInProgress) return;
        boolean isPlayer1 = player1.getId() == player.getId();
        if(isPlayer1 && dices1.contains((int)value)) {
            dices1.remove((Integer) (int) value);
        } else if (!isPlayer1 && dices2.contains((int)value)) {
            dices2.remove((Integer) (int) value);
        }
        sendStartRoundMessage(!isPlayer1, true);
    }

//    /startRound
//    {"loseDice": true, "sum": 13}
    private void sendStartRoundMessage(boolean isPlayer1, boolean loseDice) {
        if(!isGameInProgress) return;
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("loseDice", loseDice);
        jsonObj.put("sum", getDicesSum(isPlayer1 ? dices2 : dices1));

        sendMessage(
                isPlayer1 ? player1 : player2,
                Server.HEADER_START_ROUND,
                jsonObj.toJSONString(),
                "start round message"
        );
    }

    //    /finish
    //    {"winner": true}
    private void sendFinishGameMessage(boolean isPlayer1Winner) {
        JSONObject winnerJson = new JSONObject();
        JSONObject loserJson = new JSONObject();
        winnerJson.put("winner", true);
        loserJson.put("winner", false);

        sendMessage(
                player1,
                Server.HEADER_FINISH_GAME,
                isPlayer1Winner ? winnerJson.toJSONString() : loserJson.toJSONString(),
                "finish game message"
        );

        sendMessage(
                player2,
                Server.HEADER_FINISH_GAME,
                !isPlayer1Winner ? winnerJson.toJSONString() : loserJson.toJSONString(),
                "finish game message"
        );
        setGameInProgress(false);
    }

    private void sendMessage(Client player, String header, String message, String messageDescription) {
        try {
            PrintWriter clientWriter = new PrintWriter(player.getClientSocket().getOutputStream());
            clientWriter.println(header);
            clientWriter.println(message);
            System.out.println("Sending " + messageDescription + " to client...");
            clientWriter.flush();
        } catch(IOException ex) {
            System.out.printf("Error sending %s to client %s:\n", messageDescription, player.getClientName());
            ex.printStackTrace();
        } catch(Exception ex) {
            System.out.printf("Unexpected error sending %s to client %s:", messageDescription, player.getClientName());
            ex.printStackTrace();
        }
    }
}
