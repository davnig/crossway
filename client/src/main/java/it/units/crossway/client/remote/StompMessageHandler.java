package it.units.crossway.client.remote;

import it.units.crossway.client.model.StonePlacementIntent;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

public class StompMessageHandler implements StompFrameHandler {
    @Override
    public Type getPayloadType(StompHeaders headers) {
        return StonePlacementIntent.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        // TODO
    }
}
