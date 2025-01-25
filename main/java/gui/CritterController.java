package gui;

import java.io.File;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Critter;
import model.World;

/**
 * Handles actions for the add critters pop-up window
 */
public class CritterController {

	@FXML
	private AnchorPane anchorPane;

	@FXML
	private TextField quantity;

	@FXML
	private ListView<String> listview;

	@FXML
	private Button critterSelector;

	@FXML
	private TextField column;

	@FXML
	private TextField row;

	@FXML
	private Button closeCritter;

	@FXML
	private Button finishCritter;

	private String filename = null;

	@FXML
	/**
	 * Opens up the file chooser to allow for critter file selection
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
	 * Loads a valid critter file. Handles all user input errors accordingly.
	 */
	void handleButtonAction2(ActionEvent event) {
		Stage stage = (Stage) closeCritter.getScene().getWindow();
		stage.close();
	}

	@FXML
	void handleButtonAction3(ActionEvent event) {

		Boolean hasR = !(row.getText() == null || row.getText().trim().isEmpty());
		Boolean hasC = !(column.getText() == null || column.getText().trim().isEmpty());
		int r = 0, c = 0;

		try {
			if (filename == null) {
				throw new NullPointerException();
			}

			if (hasR && hasC) {
				r = Integer.parseInt(row.getText());
				c = Integer.parseInt(column.getText());
				if (World.world().tileAt(c, r) == null) {
					throw new ArrayIndexOutOfBoundsException();
				}
			}

			int q = Integer.parseInt(quantity.getText());

			JsonObject json = new JsonObject();

			try {

				Object[] properties = Critter.parseFile(filename);

				String species_id = (String) properties[0];
				String program = (String) properties[1];
				int[] mem = (int[]) properties[2];

				JsonArray memArray = new JsonArray();
				for (int m : mem) {
					memArray.add(m);
				}

				json.addProperty("program", program);
				json.add("mem", memArray);
				json.addProperty("species_id", species_id);

			} catch (Exception e) {
				e.printStackTrace();
			}

			// TODO:
			// @formatter:off
			/*
			if (q > 1) {
				JsonArray array = new JsonArray();
				JsonObject temp;
				for (int i = 0; i < q; i++) {
					temp = new JsonObject();
					temp.addProperty("row", r);
					temp.addProperty("col", c);
					temp.addProperty("direction", (int) (Math.random() * 6));
					array.add(temp);
				}
				json.add("positions", array);
			}
			*/
			// @formatter:on

			json.addProperty("num", q);
			System.out.println(json.toString());

			Client.post(json, "critters");

		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			Alert errorAlert = new Alert(AlertType.ERROR);
			errorAlert.setHeaderText("Input not valid");
			errorAlert.setContentText("Please make sure input is numerical.");
			List<Window> open = Stage.getWindows().filtered(window -> window.isShowing());
			errorAlert.initOwner(open.get(0).getScene().getWindow());
			errorAlert.showAndWait();
			return;
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			Alert errorAlert = new Alert(AlertType.ERROR);
			errorAlert.setHeaderText("Input not valid");
			errorAlert.setContentText("Please make sure file is selected.");
			List<Window> open = Stage.getWindows().filtered(window -> window.isShowing());
			errorAlert.initOwner(open.get(0).getScene().getWindow());
			errorAlert.showAndWait();
			return;
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			aioobe.printStackTrace();
			Alert errorAlert = new Alert(AlertType.ERROR);
			errorAlert.setHeaderText("Input not valid");
			errorAlert.setContentText("Please make sure critter placement is within world's dimensions");
			List<Window> open = Stage.getWindows().filtered(window -> window.isShowing());
			errorAlert.initOwner(open.get(0).getScene().getWindow());
			errorAlert.showAndWait();
			return;
		}

		Stage stage = (Stage) finishCritter.getScene().getWindow();
		stage.close();
	}

}
