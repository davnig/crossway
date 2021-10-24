package it.units.crossway.client.remote;

import it.units.crossway.client.model.StonePlacementIntent;
import lombok.NonNull;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

public class StompMessageHandler implements StompFrameHandler {
    @Override
    @NonNull
    public Type getPayloadType(@NonNull StompHeaders headers) {
        return StonePlacementIntent.class;
    }

    @Override
    public void handleFrame(@NonNull StompHeaders headers, Object payload) {
        // TODO
    }
}
