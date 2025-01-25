package gui;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class CenterPane extends StackPane {
	private static CenterPane singleton;

	public final SmartCanvas canvas;
	public final Label aliveCritters;
	public final Label timeStep;

	private CenterPane() {
		BorderPane bottomLayer = new BorderPane();

		HBox dataTab = new HBox();

		Label aliveCrittersTitle = new Label("Number of Critters Alive: ");
		aliveCritters = new Label("0");
		Label timeStepTitle = new Label("Current Time Step: ");
		timeStep = new Label("0");
		dataTab.setPadding(new Insets(0, 50, 0, 50));
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		dataTab.getChildren().addAll(aliveCrittersTitle, aliveCritters, spacer, timeStepTitle, timeStep);
		dataTab.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 1 0;");
		dataTab.setAlignment(Pos.CENTER);
		dataTab.setMinHeight(25);
		bottomLayer.setTop(dataTab);

		canvas = new SmartCanvas();
		Pane pane = new Pane(canvas);
		canvas.widthProperty().bind(pane.widthProperty());
		canvas.heightProperty().bind(pane.heightProperty());
		bottomLayer.setCenter(pane);

		Slider verticalSlider = new Slider();
		verticalSlider.setOrientation(Orientation.VERTICAL);
		verticalSlider.setMaxWidth(20);
		verticalSlider.setMinWidth(20);
		verticalSlider.setPrefWidth(20);
		bottomLayer.setRight(verticalSlider);

		HBox hbox = new HBox();
		Region spacer2 = new Region();
		Slider horizontalSlider = new Slider();
		horizontalSlider.setMaxHeight(20);
		horizontalSlider.setMinHeight(20);
		horizontalSlider.setPrefHeight(20);
		spacer2.setMinSize(12, 12);
		spacer2.setMaxSize(12, 12);
		spacer2.setPrefSize(12, 12);
		horizontalSlider.setOrientation(Orientation.HORIZONTAL);
		hbox.getChildren().addAll(horizontalSlider, spacer2);
		HBox.setHgrow(horizontalSlider, Priority.ALWAYS);
		bottomLayer.setBottom(hbox);

		VBox vbox = new VBox();
		vbox.setFillWidth(true);
		Button zoomIn = new Button("+");
		zoomIn.setFont(Font.font("Impact", 14));
		zoomIn.setMinSize(30, 30);
		zoomIn.setMaxSize(30, 30);
		zoomIn.setPrefSize(30, 30);
		zoomIn.setStyle("-fx-border-radius: 5 5 0 0; -fx-background-radius: 5 5 0 0;");

		Button zoomOut = new Button("-");
		zoomOut.setFont(Font.font("Impact", 16));
		zoomOut.setStyle("-fx-border-radius: 0 0 5 5; -fx-background-radius: 0 0 5 5;");
		zoomOut.setMinSize(30, 30);
		zoomOut.setMaxSize(30, 30);
		zoomOut.setPrefSize(30, 30);
		vbox.getChildren().addAll(zoomIn, zoomOut);

		AnchorPane topLayer = new AnchorPane();
		AnchorPane.setBottomAnchor(vbox, 30.0);
		AnchorPane.setRightAnchor(vbox, 30.0);
		topLayer.setPickOnBounds(false);
		topLayer.getChildren().add(vbox);

		this.getChildren().addAll(bottomLayer, topLayer);

		////////////////////////////////////////////////////////////////
		zoomIn.setOnAction(e -> canvas.ratio.set(canvas.ratio.get() * GUI.ZOOM_SPEED));
		zoomOut.setOnAction(e -> canvas.ratio.set(canvas.ratio.get() / GUI.ZOOM_SPEED));

		horizontalSlider.maxProperty().bindBidirectional(canvas.translateX.max);
		horizontalSlider.minProperty().bindBidirectional(canvas.translateX.min);
		horizontalSlider.valueProperty().bindBidirectional(canvas.translateX);

		verticalSlider.maxProperty().bindBidirectional(canvas.translateY.max);
		verticalSlider.minProperty().bindBidirectional(canvas.translateY.min);
		verticalSlider.valueProperty().bindBidirectional(canvas.translateY);

		this.setOnScroll(event -> {
			verticalSlider.setValue(verticalSlider.valueProperty().get() + event.getDeltaY());
			horizontalSlider.setValue(horizontalSlider.valueProperty().get() - event.getDeltaX());
		});
	}

	public static CenterPane get() {
		if (singleton == null) {
			singleton = new CenterPane();
		}
		return singleton;
	}
}
