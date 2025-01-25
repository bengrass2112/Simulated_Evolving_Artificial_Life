package gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.google.gson.JsonObject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Constants;

/**
 * Handles actions for the world configurations pop-up window
 */
public class WorldController {

	@FXML
	AnchorPane anchorPane;

	@FXML
	Button worldSelector;

	@FXML
	RadioButton importWorld;

	@FXML
	RadioButton randomWorld;

	@FXML
	RadioButton customWorld;

	@FXML
	Button finishWorld;

	@FXML
	Button closeWorld;

	@FXML
	TextField worldHeight;

	@FXML
	TextField worldWidth;

	@FXML
	ListView<String> listview;

	private String filename = null;

	public StringProperty errorMSG = new SimpleStringProperty();

	@FXML
	public void initialize() {
		ToggleGroup tg = new ToggleGroup();
		importWorld.setToggleGroup(tg);
		customWorld.setToggleGroup(tg);
		randomWorld.setToggleGroup(tg);
	}

	@FXML
	/**
	 * Opens up the file chooser to allow world file selection
	 */
	void handleButtonAction1(ActionEvent event) {

		FileChooser fc = new FileChooser();
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
		fc.getExtensionFilters().add(filter);

		File file = fc.showOpenDialog(null);

		if (file != null) {
			listview.getItems().add(file.getName());
			filename = file.getAbsolutePath();
		} else {
			System.out.println("invalid file");
		}

	}

	@FXML
	/**
	 * Loads in a valid world file. Handles all user input errors with error
	 * messages.
	 */
	void handleButtonAction2(ActionEvent event) {

		String world = "";

		if (importWorld.isSelected()) {
			try {
				world = new String(Files.readAllBytes(Paths.get(filename)));
			} catch (IllegalArgumentException | IOException e) {
				e.printStackTrace();
				Alert errorAlert = new Alert(AlertType.ERROR);
				errorAlert.setContentText("Please make sure world is valid.");
				List<Window> open = Stage.getWindows().filtered(window -> window.isShowing());
				errorAlert.initOwner(open.get(0).getScene().getWindow());
				errorAlert.setHeaderText("File not valid");
				return;
			}
		} else if (customWorld.isSelected()) {
			int height = 0, width = 0;
			try {
				height = Integer.parseInt(worldHeight.getText());
				width = Integer.parseInt(worldWidth.getText());
				if (height > 0 && width > 0 && height <= 500 && width <= 500) {
					world = "name Custom World \nsize " + height + " " + width + "\n";
				} else {
					Alert errorAlert = new Alert(AlertType.ERROR);
					errorAlert.setContentText("Please make sure width and height are within the range [1,500]");
					List<Window> open = Stage.getWindows().filtered(window -> window.isShowing());
					errorAlert.initOwner(open.get(0).getScene().getWindow());
					errorAlert.showAndWait();
					return;
				}
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
				Alert errorAlert = new Alert(AlertType.ERROR);
				errorAlert.setContentText("Please make sure input is numerical.");
				List<Window> open = Stage.getWindows().filtered(window -> window.isShowing());
				errorAlert.initOwner(open.get(0).getScene().getWindow());
				errorAlert.showAndWait();
				return;
			}
		} else if (randomWorld.isSelected()) {
			world = "name Default World \nsize " + Constants.HEIGHT + " " + Constants.WIDTH + "\n";
		}

		JsonObject json = new JsonObject();
		json.addProperty("description", world);
		Client.post(json, "world");

		if (randomWorld.isSelected() || customWorld.isSelected() || importWorld.isSelected()) {
			Stage stage = (Stage) finishWorld.getScene().getWindow();
			stage.close();
		}

	}

	@FXML
	/**
	 * Closes the pop-up window
	 */
	void handleButtonAction3(ActionEvent event) {
		Stage stage = (Stage) closeWorld.getScene().getWindow();
		stage.close();
	}

}
