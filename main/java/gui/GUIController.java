package gui;

import java.io.File;
import java.net.URL;
import java.util.List;

import com.google.gson.JsonObject;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import model.Constants;

public class GUIController {

	public volatile static float framerate;

	private static PeriodicRefresh PeriodicUpdater = new PeriodicRefresh(1);

//	public WorldTask worldTask;
//	public HighlightTask highlightTask;

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

		BackgroundImage img = new BackgroundImage(
				new Image("background.png", Screen.getPrimary().getVisualBounds().getWidth(),
						Screen.getPrimary().getVisualBounds().getHeight(), false, false),
				BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
				new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));

		// INFO page setup/layout
		VBox vboxInfo = new VBox();
		Label infoHeader = new Label("Welcome to CritterWorld!");
		infoHeader.setFont(Font.font("System", 36));
		vboxInfo.getChildren().add(infoHeader);
		vboxInfo.setAlignment(Pos.CENTER);

		String information = "\nThis is a computer science simulation implemented by freshman at Conrell University.\n\n\n"
				+ "To play, first import a valid CritterWorld file and then import as many critters \nas you wish.\n"
				+ "\nAuthors: Andrey Yao, Benjamin Grass, Cameron Russell" + "\n\n\n\n";
		Label text = new Label(information);
		text.setFont(Font.font("System", 20));
		vboxInfo.getChildren().add(text);

		Button back = new Button("BACK");
		AnchorPane anchor = new AnchorPane(back);
		AnchorPane.setTopAnchor(back, (double) 30);
		AnchorPane.setLeftAnchor(back, (double) 30);
		anchor.setMouseTransparent(true);

		back.setOnAction(e -> {
			vboxInfo.setVisible(false);
			anchor.setVisible(false);
			vbox.setVisible(true);
		});

		back.setFont(Font.font("System", 14));

		vboxInfo.setPadding(new Insets(5, 5, 5, 5));
		vboxInfo.getChildren().add(back);
		vboxInfo.setVisible(false);

		Button play = new Button("PLAY");
		play.setFont(Font.font("Impact", 20));
		play.setOnAction(e -> {
			play.getScene().setRoot(loginPage(play.getScene(), img));
		});
		vbox.getChildren().add(play);

		// INFO controls
		Button info = new Button("INFO");
		info.setFont(Font.font("Impact", 20));
		vbox.getChildren().add(info);

		info.setOnAction(e -> {
			vboxInfo.setVisible(true);
			vbox.setVisible(false);
			anchor.setVisible(true);
		});
		timeline.play();

		StackPane stackpane = new StackPane();

		stackpane.setBackground(new Background(img));
		stackpane.getChildren().addAll(vbox, vboxInfo, anchor);

		return stackpane;

	}

	/**
	 * The login pane
	 */
	public static GridPane loginPage(Scene scene, BackgroundImage img) {

		GridPane login = new GridPane();
		login.setHgap(15);
		login.setVgap(15);
		login.setAlignment(Pos.CENTER);
		login.setBackground(new Background(img));

		Text header = new Text("Connect");
		header.setFont(Font.font("System", 36));
		login.add(header, 0, 0, 2, 1);

		Label port = new Label("Port:");
		port.setFont(Font.font("System", 20));
		login.add(port, 0, 1);
		TextField portField = new TextField();
		login.add(portField, 1, 1);

		Label host = new Label("Host:");
		host.setFont(Font.font("System", 20));
		login.add(host, 0, 2);
		TextField hostField = new TextField();
		login.add(hostField, 1, 2);

		Label level = new Label("Access Level:");
		level.setFont(Font.font("System", 20));
		login.add(level, 0, 3);
		TextField levelField = new TextField();
		login.add(levelField, 1, 3);

		Label password = new Label("Password:");
		password.setFont(Font.font("System", 20));
		login.add(password, 0, 4);
		PasswordField passwordField = new PasswordField();
		login.add(passwordField, 1, 4);

		Button cont = new Button();
		cont.setText("Connect to Server");
		HBox br = new HBox(10);
		br.setAlignment(Pos.BOTTOM_RIGHT);
		br.getChildren().add(cont);
		login.add(br, 1, 6);

		cont.setOnAction(e -> {
			try {
				JsonObject jsonPost = new JsonObject();
				Client.server_url = "http://" + hostField.getText() + ":" + portField.getText() + "/";
				jsonPost.addProperty("level", levelField.getText());
				jsonPost.addProperty("password", passwordField.getText());
				JsonObject json = Client.login(jsonPost);
				Client.session_id = json.get("session_id").getAsInt();
				scene.setRoot(playPage());
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("enter valid parameters please");
			}

		});

		return login;

	}

	/**
	 * The layout, controls, and action handling for the primary game page
	 */
	public static VBox playPage() {

		// TODO:
		// If the world on server is currently null, create default world
		try {
			JsonObject json = Client.get("world", "");
			json.toString();
		} catch (NullPointerException npe) {
			String world = "name Default World \nsize " + Constants.HEIGHT + " " + Constants.WIDTH + "\n";
			JsonObject json = new JsonObject();
			json.addProperty("description", world);
			Client.post(json, "world");
		}

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
		playPageLeft.setMinWidth(GUI.MIN_WIDTH * 0.1);
		playPageLeft.setMaxWidth(GUI.MIN_WIDTH * 0.1);
		playPageLeft.setPrefWidth(GUI.MIN_WIDTH * 0.1);

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

			changeFrame(framerate);

			pause.setDisable(false);
			play.setDisable(true);
			stepsPerSec.setDisable(true);
			advance.setDisable(true);
			addWorld.setDisable(true);
			addCritter.setDisable(true);
		});

		// updates after pause button is pressed
		pause.setOnAction(e -> {

			changeFrame(0);

			JsonObject json = new JsonObject();
			json.addProperty("rate", 0);
			PeriodicUpdater.setInterval(1);
			Client.post(json, "run");

			pause.setDisable(true);
			play.setDisable(false);
			stepsPerSec.setDisable(false);
			advance.setDisable(false);
			addWorld.setDisable(false);
			addCritter.setDisable(false);
		});
		PeriodicUpdater.play();
		return vbox;
	}

	/**
	 * Updates world
	 */
	public static void updateEverything(JsonObject json, JsonObject highlighted) {
		CenterPane.get().canvas.resetConstraints();
		CenterPane.get().canvas.redraw(json.get("state").getAsJsonArray());
		CenterPane.get().canvas.setContentDimension(json.get("width").getAsInt(), json.get("height").getAsInt());
		CenterPane.get().aliveCritters.setText(json.get("population").getAsString());
		CenterPane.get().timeStep.setText(json.get("current_timestep").getAsString());
		LeftPane.get().worldInfo.setText(json.get("name").getAsString());
		System.out.println(CenterPane.get().canvas.getHeight()); // TODO: remove
	}

	/**
	 * Updates the canvas after a time step
	 */
//	public static void updateAfterStep() {
//		CenterPane.get().timeStep.setText(World.world().getSteps() + "");
//		CenterPane.get().aliveCritters.setText(World.world().getNumberOfAliveCritters() + "");
//		SmartCanvas scanvas = CenterPane.get().canvas;
//		CenterPane.get().aliveCritters.setText(World.world().getNumberOfAliveCritters() + "");
//	}

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

	private static void changeFrame(float rate) {
		PeriodicUpdater.setInterval(rate);
		JsonObject j = new JsonObject();
		j.addProperty("rate", rate);
		Client.post(j, "run");
	}

}
