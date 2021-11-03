package it.units.crossway.client.remote;

import it.units.crossway.client.GameHandler;
import it.units.crossway.client.IOUtils;
import it.units.crossway.client.Rules;
import it.units.crossway.client.model.StonePlacementIntent;
import lombok.NonNull;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
public class StompMessageHandler implements StompFrameHandler {

    private final GameHandler gameHandler;

    public StompMessageHandler(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    @Override
    @NonNull
    public Type getPayloadType(@NonNull StompHeaders headers) {
        return StonePlacementIntent.class;
    }

    @Override
    public void handleFrame(@NonNull StompHeaders headers, Object payload) {
        if (headers.containsKey("join-event")) {
            System.out.println(headers.getFirst("join-event") + " joined the game");
            gameHandler.startGame();
            return;
        }
        if (headers.containsKey("win-event")) {
            System.out.println("You lose :(\n" + headers.getFirst("win-event") + " win");
            return;
        }
        if (headers.containsKey("pie-rule-event")) {
            String nickname = headers.getFirst("pie-rule-event");
            if (!nickname.equals(gameHandler.getPlayer().getNickname())) {
                System.out.println("The opponent has accepted the pie rule: " +
                        "now " + nickname + " is the BLACK player and you are the WHITE player.");
                Rules.applyPieRule(gameHandler.getPlayer(), gameHandler.getTurn());
                gameHandler.playTurnIfSupposedTo();
                return;
            }
        }
        if (payload instanceof StonePlacementIntent) {
            StonePlacementIntent stonePlacementIntent = (StonePlacementIntent) payload;
            gameHandler.getBoard().placeStone(
                    stonePlacementIntent.getRow(),
                    stonePlacementIntent.getColumn(),
                    gameHandler.getTurn().getTurnColor()
            );
            IOUtils.printBoard(gameHandler.getBoard(), gameHandler.getPlayer());
            gameHandler.endTurn();
            gameHandler.playTurnIfSupposedTo();
        }
    }
}
