package gui;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import controller.ControllerImpl;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import model.Critter;
import model.Tile;
import model.World;

public class GUIController {

    public volatile static int framerate; // the maximum is 30

    public static BooleanProperty running = new SimpleBooleanProperty(false);

    public static ControllerImpl controller = new ControllerImpl();

    public static TimerTask worker = new BackgroundTask();

    public static Timer scheduler = new Timer();

    public static Object mutex = new Object();

    private volatile static long count;

    public static StackPane titleScreen() {

        VBox vbox = new VBox();
        vbox.setSpacing(20);
        vbox.setPadding(new Insets(50));
        vbox.setAlignment(Pos.CENTER);

        String title = " CRITTER WORLD";
        Label label = new Label(title);
        label.setMinSize(360, 40);
        label.setFont(new Font("Impact", 50));

        // animation for typing out Critter World
        final IntegerProperty substr = new SimpleIntegerProperty(0);
        final IntegerProperty frameCount = new SimpleIntegerProperty(0);
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.075), event -> {
            frameCount.set(frameCount.get() + 1);
            if (frameCount.get() % 3 == 0) {
                substr.set(Math.min(substr.get() + 1, title.length()));
            }
            if (frameCount.get() % 11 <= 3) {
                label.setText(title.substring(0, substr.get()));
            } else {
                label.setText(title.substring(0, substr.get()).concat("_"));
            }
            if (frameCount.get() > 10000) {
                frameCount.add(-2);
            }
        });

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(keyFrame);
        vbox.getChildren().add(label);

        Region r = new Region();
        vbox.getChildren().add(r);
        VBox.setVgrow(r, Priority.ALWAYS);

        // INFO page setup/layout
        VBox v = new VBox();
        Label hehe = new Label("Welcome to CritterWorld!");
        hehe.setFont(Font.font("System", 36));
        v.getChildren().add(hehe);
        v.setAlignment(Pos.CENTER);

        String information = "\nThis is a computer science simulation implemented by freshman at Conrell University.\n\n\n"
                + "To play, first import a valid CritterWorld file and then import as many critters \nas you wish.\n"
                + "\nAuthors: Andrey Yao, Benjamin Grass, Cameron Russell" + "\n\n\n\n";
        Label text = new Label(information);
        text.setFont(Font.font("System", 20));
        v.getChildren().add(text);

        Button back = new Button("BACK");
        AnchorPane anchor = new AnchorPane(back);
        AnchorPane.setTopAnchor(back, (double) 30);
        AnchorPane.setLeftAnchor(back, (double) 30);
        anchor.setMouseTransparent(true);

        back.setOnAction(e -> {
            v.setVisible(false);
            anchor.setVisible(false);
            vbox.setVisible(true);
        });

        back.setFont(Font.font("System", 14));

        v.setPadding(new Insets(5, 5, 5, 5));
        v.getChildren().add(back);
        v.setVisible(false);

        Button play = new Button("PLAY");
        play.setFont(Font.font("Impact", 20));
        play.setOnAction(e -> {
            play.getScene().setRoot(playPage());
        });
        vbox.getChildren().add(play);

        // INFO controls
        Button info = new Button("INFO");
        info.setFont(Font.font("Impact", 20));
        vbox.getChildren().add(info);

        info.setOnAction(e -> {
            v.setVisible(true);
            vbox.setVisible(false);
            anchor.setVisible(true);
        });
        timeline.play();

        StackPane stackpane = new StackPane();

        BackgroundImage img = new BackgroundImage(
                new Image("background.png", Screen.getPrimary().getVisualBounds().getWidth(),
                        Screen.getPrimary().getVisualBounds().getHeight(), false, false),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));

        stackpane.setBackground(new Background(img));
        stackpane.getChildren().addAll(vbox, v, anchor);

        return stackpane;

    }

    /**
     * The layout, controls, and action handling for the primary game page
     */
	public static VBox playPage() {

        HBox hbox = new HBox();
        LeftPane playPageLeft = LeftPane.get();
        CenterPane playPageCenter = CenterPane.get();
        RightPane playPageRight = RightPane.get();
        hbox.getChildren().addAll(playPageLeft, playPageCenter, playPageRight);
        hbox.setFillHeight(true);
        HBox.setHgrow(playPageCenter, Priority.ALWAYS);

        VBox vbox = new VBox();
        VBox.setVgrow(hbox, Priority.ALWAYS);
        vbox.getChildren().addAll(hbox, BottomPane.get());
        playPageLeft.setMinWidth(GUI.MIN_WIDTH * 0.18);
        playPageLeft.setMaxWidth(GUI.MIN_WIDTH * 0.18);
        playPageLeft.setPrefWidth(GUI.MIN_WIDTH * 0.18);

        playPageRight.setMinWidth(GUI.MIN_WIDTH * 0.20);
        playPageRight.setMaxWidth(GUI.MIN_WIDTH * 0.20);
        playPageRight.setPrefWidth(GUI.MIN_WIDTH * 0.20);

        // world and critter pop-up menus
        playPageLeft.selectWorld.setOnAction(e -> worldPage());
        playPageLeft.addCritter.setOnAction(e -> critterPage());

        // bottom game controls
        Button play = BottomPane.get().play;
        Button pause = BottomPane.get().pause;
        TextField stepsPerSec = BottomPane.get().stepsPerSec;
        Button advance = BottomPane.get().advance;
        Button addWorld = playPageLeft.selectWorld;
        Button addCritter = playPageLeft.addCritter;

        // updates after play button is pressed
        play.setOnAction(e -> {
            pause.setDisable(false);
            play.setDisable(true);
            stepsPerSec.setDisable(true);
            advance.setDisable(true);
            addWorld.setDisable(true);
            addCritter.setDisable(true);
            running.set(true);
        });

        // updates after pause button is pressed
        pause.setOnAction(e -> {
            pause.setDisable(true);
            play.setDisable(false);
            stepsPerSec.setDisable(false);
            advance.setDisable(false);
            addWorld.setDisable(false);
            addCritter.setDisable(false);
            running.set(false);
        });

        running.addListener((Observable, oldval, newval) -> {
            if (newval) {
                count = 0;
                if (framerate <= 30) {
                    worker = new BackgroundTask() {
                        @Override
                        public void run() {
                            super.run();
                            Platform.runLater(() -> GUIController.updateAfterStep());
                        }
                    };
                } else {
                    worker = new BackgroundTask() {
                        @Override
                        public void run() {
                            super.run();
                            count += 1000 / framerate;
                            Platform.runLater(() -> {
                                if (count > 33) {
                                    count -= 33;
                                    GUIController.updateAfterStep();
                                }
                            });
                        }
                    };
                }
                scheduler.scheduleAtFixedRate(worker, 0, (long) (1000 / (framerate + 0.001)));

            } else {
                worker.cancel();
                scheduler.purge();
            }
        });

        return vbox;

    }

    /**
     * Updates canvas after new world is loaded
     */
    public static void updateAfterNewWorld() {
        CenterPane.get().canvas.resetConstraints();
        CenterPane.get().canvas.redraw();
        CenterPane.get().canvas.setHighlighted(null);
		LeftPane.get().worldInfo.setText("    " + ControllerImpl.worldName);
		updateAfterStep();
    }

    /**
     * Updates the canvas after a time step
     */
    public static void updateAfterStep() {
        CenterPane.get().timeStep.setText(World.world().getSteps() + "");
        CenterPane.get().aliveCritters.setText(World.world().getNumberOfAliveCritters() + "");
        SmartCanvas scanvas = CenterPane.get().canvas;
        for (Tile t : World.updatedTiles) {
            scanvas.drawTile(t);
        }
        World.updatedTiles.clear();
		StringBuilder sb = new StringBuilder("");
		for (Critter c : World.world().critters()) {
			sb.append(c.getSpecies());
		}
		LeftPane.get().crittersList.setText(sb.toString());
		CenterPane.get().aliveCritters.setText(World.world().getNumberOfAliveCritters()+"");
    }

    public static void worldPage() {
		try {

			URL url = new File("src/main/resources/loadWorld.fxml").toURI().toURL();
			Stage stage = new Stage();
			stage = FXMLLoader.load(url);
			List<Window> open = Stage.getWindows().filtered(window -> window.isShowing());
			stage.initOwner(open.get(0).getScene().getWindow());
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();

		} catch (Exception e) {
			e.printStackTrace();
		}

    }

    /**
     * Generates a pop-up window that allows user to import critter to world.
     */
    public static void critterPage() {

		try {
			URL url = new File("src/main/resources/loadCritters.fxml").toURI().toURL();
			Stage stage = new Stage();
			stage = FXMLLoader.load(url);
			List<Window> open = Stage.getWindows().filtered(window -> window.isShowing());
			stage.initOwner(open.get(0).getScene().getWindow());
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();
		} catch (Exception e) {
			e.printStackTrace();
		}

    }

}
