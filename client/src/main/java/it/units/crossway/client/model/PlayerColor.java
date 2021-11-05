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
}
