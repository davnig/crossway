package it.units.crossway.server.exception;

public abstract class ServerException extends RuntimeException {
    public ServerException(String message) {
        super(message);
    }
}
