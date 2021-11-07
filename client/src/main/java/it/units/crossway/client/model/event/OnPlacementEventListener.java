package it.units.crossway.client.model.event;

import it.units.crossway.client.model.StonePlacementIntent;

@FunctionalInterface
public interface OnPlacementEventListener {
    void onPlacementEvent(StonePlacementIntent stonePlacementIntent);
}
