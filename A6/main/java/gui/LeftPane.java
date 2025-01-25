package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class LeftPane extends VBox {

	public final Button selectWorld;
	public final Button addCritter;
	public final Label worldInfo;
	public final Label crittersList;

	private static LeftPane singleton;

	public static LeftPane get() {
		if (singleton == null) {
			singleton = new LeftPane();
		}
		return singleton;
	}

	private LeftPane() {
		super();
		this.setFillWidth(true);
		HBox world = new HBox();
		world.setPadding(new Insets(3, 3, 3, 3));
		Label worldLabel = new Label("     World");
		selectWorld = new Button("+");
		Region spacer = new Region();

		world.getChildren().addAll(worldLabel, spacer, selectWorld);
		HBox.setHgrow(spacer, Priority.ALWAYS);
		world.setAlignment(Pos.CENTER);

		worldInfo = new Label("Default");
		world.setStyle("-fx-border-color: black; -fx-border-width: 1 0 0.5 0 ;");

		// the thing with "critters" plus the button
		HBox crittersTitle = new HBox();
		crittersTitle.setPadding(new Insets(3, 3, 3, 3));
		Label crittersLabel = new Label("  Critters");
		addCritter = new Button("+");
		Region spacer2 = new Region();
		crittersTitle.getChildren().addAll(crittersLabel, spacer2, addCritter);
		HBox.setHgrow(spacer2, Priority.ALWAYS);
		crittersTitle.setAlignment(Pos.CENTER);

		crittersList = new Label("");
		crittersTitle.setStyle("-fx-border-color: black; -fx-border-width: 1 0 0.5 0 ;");
		ScrollPane scroll = new ScrollPane(crittersList);
		scroll.setPadding(new Insets(10, 10, 5, 5));
		scroll.setStyle("-fx-box-border: transparent;");

		this.getChildren().addAll(world, worldInfo, crittersTitle, scroll);
		VBox.setVgrow(scroll, Priority.ALWAYS);
		this.setStyle("-fx-border-color: grey; -fx-border-width: 0 1 0 0;");
	}
}
