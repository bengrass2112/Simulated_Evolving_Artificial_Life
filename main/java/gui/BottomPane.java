package gui;

import com.google.gson.JsonObject;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class BottomPane extends HBox {

	private static BottomPane singleton;

	public final Button play;
	public final Button pause;
	public final TextField stepsPerSec;

	public final Button advance;
	public final TextField steps;

	private BottomPane() {
		ImageView img = new ImageView(new Image("play.png"));
		play = new Button();
		play.setGraphic(img);
		play.setMaxSize(15, 15);
		pause = new Button();
		pause.setMaxSize(15, 15);
		pause.setGraphic(new ImageView(new Image("pause.png")));
		Label label = new Label("   Steps per second:   ");
		stepsPerSec = new TextField();
		stepsPerSec.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.length() != 0) {
				try {
					int rate = Integer.parseInt(newValue);
					if (1 <= rate && rate <= 500) {
						GUIController.framerate = rate;
					} else {
						stepsPerSec.setText(Math.min(500, Math.max(1, rate)) + "");
					}
				} catch (NumberFormatException e) {
					stepsPerSec.setText("1");
				}
			} else {
				GUIController.framerate = 1;
			}
		});
		stepsPerSec.setMaxWidth(50);

		advance = new Button("Advance ");
		steps = new TextField();
		steps.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.length() != 0) {
				try {
					int step = Integer.parseInt(newValue);
					if (!(1 <= step && step <= 100)) {
						steps.setText(Math.min(100, Math.max(1, step)) + "");
					}
				} catch (NumberFormatException e) {
					stepsPerSec.setText("1");
				}
			}
		});
		advance.setOnAction(e -> {
			try {

				int step = Integer.parseInt(steps.textProperty().get());

				JsonObject json = new JsonObject();
				json.addProperty("count", step);
				Client.post(json, "step");

				// TODO: GUIController.controller.advanceTime(step);
				// TODO: GUIController.updateAfterStep();
			} catch (NumberFormatException e1) {
				stepsPerSec.setText("1");
			}
		});
		steps.setMaxWidth(50);
		Label hehe = new Label(" steps");

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		this.setPadding(new Insets(10, 100, 10, 100));
		this.getChildren().addAll(play, pause, label, stepsPerSec, spacer, advance, steps, hehe);
		this.setAlignment(Pos.CENTER);
		this.setMaxHeight(25);
	}

	public static BottomPane get() {
		if (singleton == null) {
			singleton = new BottomPane();
		}
		return singleton;
	}
}
