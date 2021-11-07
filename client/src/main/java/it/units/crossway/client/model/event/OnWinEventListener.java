package it.units.crossway.client.model.event;

@FunctionalInterface
public interface OnWinEventListener {
    void onWinEvent(String winner);
}
