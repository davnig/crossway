package it.units.crossway.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Turn {

    private int turnNumber;
    private PlayerColor currentPlayer;

    public void initFirstTurn() {
        turnNumber = 1;
        currentPlayer = PlayerColor.BLACK;
        printTurnInfo();
    }

    public void nextTurn() {
        turnNumber++;
        switchCurrentPlayer();
        printTurnInfo();
    }

    public void switchCurrentPlayer() {
        currentPlayer = getCurrentPlayerOpponent();
    }

    public void applyPieRule() {
        switchCurrentPlayer();
        nextTurn();
    }

    public PlayerColor getCurrentPlayerOpponent() {
        switch (currentPlayer) {
            case BLACK: return PlayerColor.WHITE;
            case WHITE: return PlayerColor.BLACK;
            default: return PlayerColor.NONE;
        }
    }

    private void printTurnInfo() {
        System.out.printf("it.units.crossway.client.model.Turn %d: %s plays%n", turnNumber, currentPlayer);
    }
}
