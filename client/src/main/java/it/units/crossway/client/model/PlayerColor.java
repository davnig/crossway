package it.units.crossway.client.model;

public enum PlayerColor {
	WHITE,
	BLACK,
	NONE;

    public PlayerColor getOpposite() {
        switch (this) {
            case WHITE:
                return BLACK;
            case BLACK:
                return WHITE;
            default:
                return NONE;
        }
    }

    public String asSymbol() {
        switch (this) {
            case BLACK:
                return "O";
            case WHITE:
                return "x";
            default:
                return " ";
        }
    }
}
