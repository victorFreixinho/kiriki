package kiriki;

import kiriki.Message.ServerMessageHandler;

import java.util.ArrayList;
import java.util.List;

public class Game extends ServerMessageHandler {

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
        this.isGameInProgress = false;
    }

    private int getDicesSum(List<Integer> dices) {
        int sum = 0;
        for(int d : dices) { sum += d; }
        return sum;
    }

    public String getPlayer1Name() { return this.player1 == null ? null : this.player1.getClientName(); }

    public String getPlayer2Name() {
        return this.player2 == null ? null : this.player2.getClientName();
    }

    public boolean isGameInProgress() {
        return isGameInProgress;
    }

    public Client sortFirstPlayer() {
        if(player1 == null || player2 == null) return null;
        return Math.random() > 0.5 ? player1 : player2;
    }

    private List<Integer> sortInitialDices() {
        List<Integer> arr = new ArrayList<>();
        for(int i=1; i<=5; i++) {
            arr.add((int) Math.floor(6*Math.random() + 1));
        }
        return arr;
    }

    public void addPlayer(Client client) {
        if (player1 == null) {
            player1 = client;
        } else if(player2 == null) {
            player2 = client;
        }
    }

    public void handleStartGame() {
        if(isGameInProgress) {
            // TODO
            return;
        }

        Client firstPlayer = sortFirstPlayer();
        dices1 = sortInitialDices();
        dices2 = sortInitialDices();
        isGameInProgress = true;
        sendGameConfigTo(
                player1,
                player2.getClientName(),
                getDicesSum(dices2),
                player1.equals(firstPlayer),
                dices1
        );
        sendGameConfigTo(
                player2,
                player1.getClientName(),
                getDicesSum(dices1),
                player2.equals(firstPlayer),
                dices2
        );
    }

    public void handlePlayerGuess(Client player, Object[] guess) {
        if(!isGameInProgress || guess == null) {
            // TODO
            return;
        }

        boolean isPlayer1 = player.getId() == player1.getId();
        Client opp = isPlayer1 ? player2 : player1;
        ArrayList<Integer> dices = new ArrayList<>((isPlayer1 ? dices1 : dices2));
        ArrayList<Integer> oppDicesCopy = new ArrayList<>((isPlayer1 ? dices2 : dices1));
        int total = oppDicesCopy.size();

        int points = 0;
        for(Object obj : guess) {
            try {
                int i = (int) (long) (Long) obj;
                if(oppDicesCopy.contains(i)) {
                    oppDicesCopy.remove((Integer) i);
                    points++;
                }
            } catch (Exception e) {
                System.out.println(
                    "Error in guess casting. \n" +
                    "The client \"" + player.getClientName()+ "\" gave an incorrect guess type:"
                );
                e.printStackTrace();
            }
        }

        if(total == points) {
            System.out.println("Client \"" + player.getClientName() + "\" won the game!");
            handleFinishGame(player);
        } else if(dices.size()<=2) {
            System.out.println("Client \"" + opp.getClientName() + "\" won the game!");
            handleFinishGame(opp);
        } else {
            if(total < 2 * points) {
                System.out.println("Client \"" + player.getClientName() + "\" got more than half right ("+points+"/"+total+") so he didn't miss any dice.");
                sendWrongGuessMessageTo(player, false);
                sendStartRoundMessageTo(opp, false, getDicesSum(dices));
            } else {
                System.out.println("Client \"" + player.getClientName() + "\" got less than half right ("+points+"/"+total+") so missed a die.");
                sendWrongGuessMessageTo(player, true);
            }
        }
    }

    public void handleExcludePlayerDice(Client player, int value) {
        if(!isGameInProgress) {
            // TODO
            return;
        }

        if(player.getId() == player1.getId()) {
            dices1.remove((Integer) value);
            sendStartRoundMessageTo(player2, true, getDicesSum(dices1));
        } else {
            dices2.remove((Integer) value);
            sendStartRoundMessageTo(player1, true, getDicesSum(dices2));
        }
    }

    public void handleFinishGame(Client winner) {
        if(!isGameInProgress) {
            // TODO
            return;
        }
        boolean isPlayer1Winner = winner.getId() == player1.getId();
        sendFinishGameMessageTo(player1, isPlayer1Winner);
        sendFinishGameMessageTo(player2, !isPlayer1Winner);
        isGameInProgress = false;
    }
}
