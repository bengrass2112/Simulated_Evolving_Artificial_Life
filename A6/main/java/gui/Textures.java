package gui;

import javafx.scene.image.Image;

public class Textures {
	private static Image water;
	private static Image lily;

	public static Image water() {
		if (water == null) {
			water = new Image("water.png");
		}
		return water;
	}

	public static Image lily() {
		if (lily == null) {
			lily = new Image("water.png");
		}
		return lily;
	}
}
