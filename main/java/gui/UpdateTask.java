package gui;

import com.google.gson.JsonObject;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class UpdateTask extends Task<JsonObject[]> {
	String query1;
	String query2;

	public UpdateTask() {
		super();
		query1 = CenterPane.get().canvas.getSubWorld();
		query2 = "&from_row=" + CenterPane.get().canvas.highlight_row + "&from_col="
				+ CenterPane.get().canvas.highlight_col + "&height=" + 1 + "&width=" + 1;
		this.setOnSucceeded(ex -> {
			Platform.runLater(() -> {
				try {
					GUIController.updateEverything(this.get()[0], this.get()[1]);
				} catch (Exception hehe) {
					hehe.printStackTrace();
				}
			});
		});
	}

	public JsonObject[] call() {
		JsonObject[] array = new JsonObject[2];
		array[0] = Client.get("world", query1).getAsJsonObject();
		array[1] = Client.get("world", query2).get("state").getAsJsonArray().get(0).getAsJsonObject();
		return array;
	}
}
