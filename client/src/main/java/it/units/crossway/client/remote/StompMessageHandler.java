package it.units.crossway.client.remote;

import it.units.crossway.client.model.StonePlacementIntent;
import it.units.crossway.client.model.event.OnJoinEventListener;
import it.units.crossway.client.model.event.OnPieRuleEventListener;
import it.units.crossway.client.model.event.OnPlacementEventListener;
import it.units.crossway.client.model.event.OnWinEventListener;
import lombok.Data;
import lombok.NonNull;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

@Data
public class StompMessageHandler implements StompFrameHandler {

    private OnJoinEventListener joinEventListener;
    private OnPieRuleEventListener pieRuleEventListener;
    private OnWinEventListener winEventListener;
    private OnPlacementEventListener placementEventListener;

    @Override
    @NonNull
    public Type getPayloadType(@NonNull StompHeaders headers) {
        return StonePlacementIntent.class;
    }

    @Override
    public void handleFrame(@NonNull StompHeaders headers, Object payload) {
        if (headers.containsKey("join-event")) {
            joinEventListener.onJoinEvent(headers.getFirst("join-event"));
            return;
        }
        if (headers.containsKey("pie-rule-event")) {
            pieRuleEventListener.onPieRuleEvent(headers.getFirst("pie-rule-event"));
            return;
        }
        if (headers.containsKey("win-event")) {
            winEventListener.onWinEvent(headers.getFirst("win-event"));
            return;
        }
        if (payload instanceof StonePlacementIntent) {
            placementEventListener.onPlacementEvent((StonePlacementIntent) payload);
        }
    }
}
