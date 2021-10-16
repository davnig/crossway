package it.units.crossway.server.exception;

public class DuplicatePlayerException extends ServerException {
    public DuplicatePlayerException(String field, String duplicateNickname) {
        super("A player with {" + field + " = " + duplicateNickname + "} already exists");
    }
}
