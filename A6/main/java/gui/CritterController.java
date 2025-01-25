package gui;

import java.io.File;
import java.util.List;

import controller.ControllerImpl;
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

        ControllerImpl controller = new ControllerImpl();

        Boolean hasR = !(row.getText() == null || row.getText().trim().isEmpty());
        Boolean hasC = !(column.getText() == null || column.getText().trim().isEmpty());

        try {

            if (filename == null) {
                throw new NullPointerException();
            }

            List<List<Integer>> empty = World.world().emptyTiles();
            int randomCoordinate = (int) (Math.random() * empty.size());
            List<Integer> coordinate = empty.get(randomCoordinate);
            int c = coordinate.get(0);
            int r = coordinate.get(1);

            if (hasR && hasC) {
                r = Integer.parseInt(row.getText());
            }
            if (hasC && hasR) {
                c = Integer.parseInt(column.getText());
            }

            int q = Integer.parseInt(quantity.getText());

            if (World.tileAt(c, r) == null) {
                throw new ArrayIndexOutOfBoundsException();
            }

            if (!hasR || !hasC) {
                controller.loadCritters(filename, q);
            } else if (hasR && hasC) {
                controller.load(c, r, (int) (Math.random() * 6), new File(filename));
            }

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
			errorAlert.setContentText("Please make sure critter placement within world's dimensions");
			List<Window> open = Stage.getWindows().filtered(window -> window.isShowing());
			errorAlert.initOwner(open.get(0).getScene().getWindow());
			errorAlert.showAndWait();
            return;
        }

        GUIController.updateAfterNewWorld();
        Stage stage = (Stage) finishCritter.getScene().getWindow();
        stage.close();

    }

}
