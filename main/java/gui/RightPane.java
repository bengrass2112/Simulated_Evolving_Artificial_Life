package gui;

import com.google.gson.JsonObject;

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
	
	public void updateCritterInfo(int[] mem, int dir, String species) {
			attackDisplay.setText(mem[2] + "");
			defenseDisplay.setText(mem[1] + "");
			energyDisplay.setText(mem[4] + "");
			memSizeDisplay.setText(mem[0] + "");
			sizeDisplay.setText(mem[3] + "");
			passNumberDisplay.setText(mem[5] + "");
			postureDisplay.setText(mem[6] + "");
			directionDisplay.setText(dir * 60 + "  degree(s)");
			speciesDisplay.setText(species);
	}

	private void redraw(JsonObject json) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		int r = json.get("row").getAsInt();
		int c = json.get("col").getAsInt();
		double x = this.getWidth()/2;
		double y = this.getHeight()/2;
		String type = json.get("type").getAsString();

		gc.setFill(Color.LIGHTSKYBLUE);
		double i = 0.5 * SmartCanvas.ROOT3;
		double a = 40; // the actual length of the hex drawn
		gc.fillPolygon(new double[] { x - a, x - a * 0.5, x + a * 0.5, x + a, x + a * 0.5, x - a * 0.5 },
				new double[] { y, y + a * i, y + a * i, y, y - a * i, y - a * i }, 6);

		switch (type) {
		case "rock":
			break;
		case "nothing":
			drawLilypad(x, y, a);
			break;
		case "food":
			drawLilypad(x, y, a);
			drawFood(x, y, a, json.get("amount").getAsInt());
			break;
		case "critter":
			drawLilypad(x, y, a);
			String species = json.get("species_id").getAsString();
			int dir = json.get("dir").getAsInt();
			int size = json.get("mem").getAsJsonArray().get(3).getAsInt();
			drawCritter(x, y, a, species, size, dir);
			break;
		}
	}

	private void drawLilypad(double x, double y, double a) {
		GraphicsContext gc = canvas.getGraphicsContext2D();

		double width = a * 1.7;
		double height = a * 1.4;
		double yprime = y - a * 0.8;

		gc.setStroke(Color.LIGHTGREEN);
		gc.setFill(Color.DARKGREEN);
		gc.fillOval(x - width / 2, y - height / 2, width, height);
		gc.strokeOval(x - width / 2, y - height / 2, width, height);

		gc.setFill(Color.LIGHTSKYBLUE);
		gc.fillPolygon(new double[] { x, x - a / 2, x }, new double[] { y, yprime, yprime }, 3);
		gc.strokeLine(x, y, x, y - height * 0.5);
		gc.strokeLine(x, y, x - height * 0.27, y - height * 0.45);
		gc.strokeLine(x + height * 0.05, y + height * 0.15, x + height * 0.153, y + height * 0.46);
	}

	private void drawFood(double x, double y, double a, int amt) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		double height = 60, width = 60;
		if (amt < 50)
			gc.drawImage(SmartCanvas.berry1, x - width / 2, (y - height / 2), width, height);
		else if (amt >= 50 && amt < 200)
			gc.drawImage(SmartCanvas.berry2, (x - width / 2), (y - height / 2), width,
					height);
		else if (amt >= 200 && amt < 600)
			gc.drawImage(SmartCanvas.berry3, x - width / 2, (y - height / 2), width, height);
		else if (amt >= 600 && amt < 1000)
			gc.drawImage(SmartCanvas.berry4, x - width / 2, y - height / 2, width, height);
		else if (amt >= 1000)
			gc.drawImage(SmartCanvas.berry5, x - width / 2, y - height / 2, width, height);
	}

	private void drawCritter(double x, double y, double a, String species_name, int critter_size, int dir) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		double size = 0.66 + 0.033 * Math.min(10, critter_size);
		int species = species_name.hashCode();

		double height = a * 1.3 * size;
		double width = a * 1.7 * size;
		double dotSize = a * 0.1 * size;
		gc.save();

		/////////////////////////////////////////////
		Rotate r = new Rotate(dir * (60), x, y);
		gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
		gc.drawImage(SmartCanvas.critter, x - width / 2, y - height / 2, width, height);
		gc.setLineWidth(1);

		y += height * 0.1;
		double r1 = y - height * 0.2;
		double r2 = y - height * 0.1;
		double r3 = y;
		double r4 = y + height * 0.1;
		double r5 = y + height * 0.2;

		double c1 = x - width * 0.15;
		double c2 = x - width * 0.1;
		double c3 = x;
		double c4 = x + width * 0.1;
		double c5 = x + width * 0.15;

		drawDot(c2, r1, dotSize, getBit(species, 0), getBit(species, 1), getBit(species, 2), getBit(species, 3));
		drawDot(c4, r1, dotSize, getBit(species, 4), getBit(species, 5), getBit(species, 6), getBit(species, 7));
		drawDot(c3, r2, dotSize, getBit(species, 8), getBit(species, 9), getBit(species, 10), getBit(species, 11));
		drawDot(c1, r3, dotSize, getBit(species, 12), getBit(species, 13), getBit(species, 14), getBit(species, 15));
		drawDot(c5, r3, dotSize, getBit(species, 16), getBit(species, 17), getBit(species, 18), getBit(species, 9));
		drawDot(c3, r4, dotSize, getBit(species, 20), getBit(species, 21), getBit(species, 22), getBit(species, 23));
		drawDot(c2, r5, dotSize, getBit(species, 24), getBit(species, 25), getBit(species, 26), getBit(species, 27));
		drawDot(c4, r5, dotSize, getBit(species, 28), getBit(species, 29), getBit(species, 30), getBit(species, 31));

		//////////////////////////////////////////////
		gc.restore();
	}

	private void drawDot(double x, double y, double size, int a, int b, int c, int d) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		if (a == 0) {
			gc.setStroke(Color.DARKGRAY);
		} else {
			gc.setStroke(Color.YELLOW);
		}

		if (c == 0 && d == 0) {
			gc.setFill(Color.DARKGREEN);
		} else if (c == 0 && d == 1) {
			gc.setFill(Color.DEEPSKYBLUE);
		} else if (c == 1 && d == 0) {
			gc.setFill(Color.VIOLET);
		} else {
			gc.setFill(Color.SANDYBROWN);
		}

		if (b == 0) {
			double[] xs = new double[] { x, x + size / 2, x, x - size / 2 };
			double[] ys = new double[] { y - size, y, y + size, y };
			gc.fillPolygon(xs, ys, 4);
			gc.strokePolygon(xs, ys, 4);
		} else {
			gc.fillOval(x - size / 2, y - size, size, size * 2);
			gc.strokeOval(x - size / 2, y - size, size, size * 2);
		}
	}

	private int getBit(int n, int k) {
		return (n >> k) & 1;
	}

}
