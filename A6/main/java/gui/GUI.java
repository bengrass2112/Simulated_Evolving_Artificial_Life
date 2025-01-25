package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.World;

public class GUI extends Application {

	final static double ASPECT_RATIO = 0.667;
	final static double ZOOM_SPEED = 1.15;

	final static double DEFAULT_WIDTH = 1000;
	final static double DEFAULT_HEIGHT = DEFAULT_WIDTH * ASPECT_RATIO;

	public static final double MIN_WIDTH = 1000;
	public static final double MIN_HEIGHT = MIN_WIDTH * ASPECT_RATIO;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
		World.createWorld(50, 87);
        primaryStage.setTitle("Critter World");
        StackPane root = new StackPane();
        root.setVisible(true);
//		root.getChildren().add(ComponentFactory.titleScreen());
		root.getChildren().add(GUIController.titleScreen());
        Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setMinWidth(MIN_WIDTH);
		primaryStage.setOnCloseRequest(e -> {
			Platform.exit();
			GUIController.scheduler.cancel();
		});

        primaryStage.minHeightProperty().bind(primaryStage.widthProperty().multiply(ASPECT_RATIO));
        primaryStage.maxHeightProperty().bind(primaryStage.widthProperty().multiply(ASPECT_RATIO));
        primaryStage.show();
    }

}
