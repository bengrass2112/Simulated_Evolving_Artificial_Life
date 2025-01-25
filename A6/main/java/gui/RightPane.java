package gui;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import model.Critter;
import model.Tile;

public class RightPane extends VBox {

	private static RightPane singleton;

	public final Label programLabel;

	public final Label attackDisplay;
	public final Label defenseDisplay;
	public final Label memSizeDisplay;
	public final Label energyDisplay;
	public final Label postureDisplay;
	public final Label passNumberDisplay;
	public final Label sizeDisplay;
	public final Label directionDisplay;
	public final Label speciesDisplay;
	public final TitledPane critterInfoTab;
	public final TitledPane programTab;
	public final VBox tileBox;

	private final Canvas canvas;
	private final Label tileInfo;

	public static RightPane get() {
		if (singleton == null) {
			singleton = new RightPane();
		}
		return singleton;
	}

	private RightPane() {
		this.setFillWidth(true);

		tileBox = new VBox();
		tileBox.setAlignment(Pos.CENTER);
		TitledPane tile = new TitledPane("Tile info", tileBox);
		tile.setStyle("-fx-box-border: transparent;");
		canvas = new Canvas();
		canvas.setWidth(GUI.MIN_WIDTH / 5 - 20);
		canvas.setHeight(GUI.MIN_WIDTH / 5 - 20);
		tileInfo = new Label("");
		tileInfo.setTextAlignment(TextAlignment.CENTER);
		tileBox.getChildren().addAll(canvas, tileInfo);

		Label attackIcon = new Label(); //
		attackIcon.setGraphic(loadIcon("attackIcon.png"));
		Label defenseIcon = new Label();
		defenseIcon.setGraphic(loadIcon("defenseIcon.png"));
		Label sizeIcon = new Label("Size: ");
		Label energyIcon = new Label();
		energyIcon.setGraphic(loadIcon("energyIcon.png"));
		Label memSizeIcon = new Label("MemSize: ");
		Label postureIcon = new Label("Posture: ");
		Label passNumberIcon = new Label("Pass #: ");
		Label directionIcon = new Label("Direction: ");
		Label speciesIcon = new Label("Species: ");
		
		attackDisplay = new Label();
		defenseDisplay = new Label();
		energyDisplay = new Label();
		passNumberDisplay = new Label();
		postureDisplay = new Label();
		sizeDisplay = new Label();
		memSizeDisplay = new Label();
		directionDisplay = new Label();
		speciesDisplay = new Label();

		GridPane critterGrid = new GridPane();
		critterGrid.add(attackIcon, 0, 0);
		critterGrid.add(defenseIcon, 0, 1);
		critterGrid.add(energyIcon, 0, 2);
		critterGrid.add(sizeIcon, 0, 3);
		critterGrid.add(memSizeIcon, 0, 4);
		critterGrid.add(postureIcon, 0, 5);
		critterGrid.add(passNumberIcon, 0, 6);
		critterGrid.add(directionIcon, 0, 7);
		critterGrid.add(speciesIcon, 0, 8);

		critterGrid.add(attackDisplay, 1, 0);
		critterGrid.add(defenseDisplay, 1, 1);
		critterGrid.add(energyDisplay, 1, 2);
		critterGrid.add(sizeDisplay, 1, 3);
		critterGrid.add(memSizeDisplay, 1, 4);
		critterGrid.add(postureDisplay, 1, 5);
		critterGrid.add(passNumberDisplay, 1, 6);
		critterGrid.add(directionDisplay, 1, 7);
		critterGrid.add(speciesDisplay, 1, 8);


		GridPane.setHalignment(sizeIcon, HPos.RIGHT);
		GridPane.setHalignment(attackIcon, HPos.RIGHT);
		GridPane.setHalignment(defenseIcon, HPos.RIGHT);
		GridPane.setHalignment(energyIcon, HPos.RIGHT);
		GridPane.setHalignment(memSizeIcon, HPos.RIGHT);
		GridPane.setHalignment(postureIcon, HPos.RIGHT);
		GridPane.setHalignment(passNumberIcon, HPos.RIGHT);
		GridPane.setHalignment(directionIcon, HPos.RIGHT);
		GridPane.setHalignment(speciesIcon, HPos.RIGHT);

		GridPane.setHalignment(sizeDisplay, HPos.LEFT);
		GridPane.setHalignment(attackDisplay, HPos.LEFT);
		GridPane.setHalignment(defenseDisplay, HPos.LEFT);
		GridPane.setHalignment(energyDisplay, HPos.LEFT);
		GridPane.setHalignment(memSizeDisplay, HPos.LEFT);
		GridPane.setHalignment(postureDisplay, HPos.LEFT);
		GridPane.setHalignment(passNumberDisplay, HPos.LEFT);
		GridPane.setHalignment(directionDisplay, HPos.LEFT);
		GridPane.setHalignment(speciesDisplay, HPos.LEFT);

		critterGrid.setHgap(5);
		critterGrid.setVgap(5);

		critterGrid.setPadding(new Insets(10, 10, 10, 10));
		critterInfoTab = new TitledPane("Critter info", critterGrid);
		critterInfoTab.setStyle("-fx-box-border: transparent;");

		programLabel = new Label();
		ScrollPane programScroll = new ScrollPane(programLabel);
		programTab = new TitledPane("Critter program", programScroll);
		programTab.setStyle("-fx-box-border: transparent;");
		this.getChildren().addAll(tile, critterInfoTab, programTab);
		this.setStyle("-fx-border-color: grey; -fx-border-width: 0 0 0 1;");
	}

	private ImageView loadIcon(String fileName) {
		Image image = new Image(fileName);
		return new ImageView(image);
	}
	
	public void updateCritterInfo(Critter c) {
		if (c != null) {
			attackDisplay.setText(c.getMemory()[2] + "");
			defenseDisplay.setText(c.getMemory()[1] + "");
			energyDisplay.setText(c.getMemory()[4] + "");
			memSizeDisplay.setText(c.getMemory()[0] + "");
			sizeDisplay.setText(c.getMemory()[3] + "");
			passNumberDisplay.setText(c.getMemory()[5] + "");
			postureDisplay.setText(c.getMemory()[6] + "");
			directionDisplay.setText(c.dir() * 60 + "  degree(s)");
			speciesDisplay.setText(c.getSpecies());
		}
	}
	public void updateTileInfo(Tile highlighted) {
		double x = canvas.getWidth() / 2;
		double y = canvas.getHeight() / 2;
		double c = 0.5 * SmartCanvas.ROOT3;
		double a = canvas.getWidth() / 2.5;
		canvas.getGraphicsContext2D().setFill(Color.LIGHTSKYBLUE);
		canvas.getGraphicsContext2D().fillPolygon(
				new double[] { x - a, x - a * 0.5, x + a * 0.5, x + a, x + a * 0.5, x - a * 0.5 },
				new double[] { y, y + a * c, y + a * c, y, y - a * c, y - a * c }, 6);
		if (highlighted != null) {
			if (highlighted.contents() != -1) {
				double height = a * 3, width = a * 2;
				canvas.getGraphicsContext2D().drawImage(SmartCanvas.lilypad1, (x - width / 2), (y - height / 2), width,
						height);
			}

			if (highlighted.contents() < -1) {
				drawFood(highlighted.contents(), x, y);
				tileInfo.setText("( r=" + highlighted.row() + " , c=" + highlighted.col() + " )\nFood: "
						+ (highlighted.contents() * (-1) - 1));
			} else if(highlighted.contents() == -1){
				tileInfo.setText("( r=" + highlighted.row() + " , c=" + highlighted.col() + " )\nBarrier");
			} else if (highlighted.contents() > 0) {
				drawCritter(highlighted.critter(), x, y);
				tileInfo.setText("( r=" + highlighted.row() + " , c=" + highlighted.col() + " )\nCritter");
				updateCritterInfo(highlighted.critter());
			} else if (highlighted.contents() == 0) {
				tileInfo.setText("( r=" + highlighted.row() + " , c=" + highlighted.col() + " )\nEmpty");
			}
		} else {
			tileInfo.setText("( N/A , N/A )\n No tile selected");
			}

		drawHex(Color.DARKGOLDENROD, x, y);
		////////////////////////////
		if (highlighted == null || highlighted.contents() <= 0) {
			critterInfoTab.setDisable(true);
			programTab.setDisable(true);
		} else {
			critterInfoTab.setDisable(false);
			programTab.setDisable(false);
		}
	}

	private void drawHex(Color color, double x, double y) {
		double c = 0.5 * SmartCanvas.ROOT3;
		double a = canvas.getWidth() / 2.5;
		canvas.getGraphicsContext2D().setStroke(color);
		canvas.getGraphicsContext2D().strokePolygon(
				new double[] { x - a, x - a * 0.5, x + a * 0.5, x + a, x + a * 0.5, x - a * 0.5 },
				new double[] { y, y + a * c, y + a * c, y, y - a * c, y - a * c }, 6);
	}

	private void drawFood(int amt, double x, double y) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		double height = 75, width = 75;
		if (amt < 50)
			gc.drawImage(SmartCanvas.berry1, x - width / 2, (y - height / 2) - (8), width, height);
		else if (amt >= 50 && amt < 200)
			gc.drawImage(SmartCanvas.berry2, (x - width / 2) - (8), (y - height / 2) - (10), width,
					height);
		else if (amt >= 200 && amt < 600)
			gc.drawImage(SmartCanvas.berry3, x - width / 2, (y - height / 2) - (8), width, height);
		else if (amt >= 600 && amt < 1000)
			gc.drawImage(SmartCanvas.berry4, x - width / 2, y - height / 2, width, height);
		else if (amt >= 1000)
			gc.drawImage(SmartCanvas.berry5, x - width / 2, y - height / 2, width, height);
	}

	private void drawCritter(Critter critter, double x, double y) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		Rotate r = new Rotate(critter.dir() * (60), x, y);
		gc.save();
		gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
		double height = 240, width = 190;
		gc.drawImage(SmartCanvas.critter, x - width / 2, (y - height / 2) + (20), width, height);
		gc.restore();

		gc.setFill(critter.dotColor());
		gc.fillOval(x, y, 10, 10);
		gc.strokeOval(x, y, 11, 11);
	}
}
