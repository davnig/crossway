package it.units.crossway.client.remote;

import it.units.crossway.client.GameHandler;
import it.units.crossway.client.model.Intersection;
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
        StonePlacementIntent stonePlacementIntent = (StonePlacementIntent) payload;
        Intersection intersection = new Intersection(stonePlacementIntent.getRow(), stonePlacementIntent.getColumn());
        gameHandler.getBoard().placeStone(intersection, gameHandler.getTurn().getCurrentPlayer());
    }
}
