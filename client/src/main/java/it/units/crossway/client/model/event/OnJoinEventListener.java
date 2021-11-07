package it.units.crossway.client.model.event;

@FunctionalInterface
public interface OnJoinEventListener {
    void onJoinEvent(String joined);
}
