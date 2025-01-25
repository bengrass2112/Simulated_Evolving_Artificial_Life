package gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

public class SmartCanvas extends Canvas {

	static final Image berry1 = new Image("berry1.png");
	static final Image berry2 = new Image("berry2.png");
	static final Image berry3 = new Image("berry3.png");
	static final Image berry4 = new Image("berry4.png");
	static final Image berry5 = new Image("berry5.png");

	static final Image lilypad1 = new Image("lilypad1.png");

	static final Image critter = new Image("critter.png");

	final double MAX_RATIO = 1.5;
	final double MIN_RATIO = 0.5;
	final static double ROOT3 = Math.sqrt(3);
	final double HEX_LENGTH = 80;

	final ConstrainedDoubleProperty ratio = new ConstrainedDoubleProperty(1.0, MAX_RATIO, MIN_RATIO); // the ratio
	final ConstrainedDoubleProperty translateX; // left translate of world
	final ConstrainedDoubleProperty translateY; // right translate of world
	final GraphicsContext gc;

	public volatile int highlight_row = -1;
	public volatile int highlight_col = -1;

	private int worldWidth;
	private int worldHeight;

	double a;

	public SmartCanvas() {
		super();

		gc = this.getGraphicsContext2D();
		gc.setLineWidth(3);

		translateX = new ConstrainedDoubleProperty(0.0, 0.0, 0);
		translateY = new ConstrainedDoubleProperty(0.0, 0.0, 0);

		// @formatter:off
		/*
		this.widthProperty().addListener(observable -> {
			this.resetConstraints();
			new Thread(new UpdateTask()).start();
		});

		this.heightProperty().addListener(observable -> {
			this.resetConstraints();
			new Thread(new UpdateTask()).start();

		});
		*/
		// @formatter:on

		ratio.addListener(observable -> {
			this.a = this.HEX_LENGTH * this.ratio.get();
			this.resetConstraints();
			new Thread(new UpdateTask()).start();
		});

		translateX.addListener(observable -> {
			new Thread(new UpdateTask()).start();
		});

		translateY.addListener(observable -> {
			new Thread(new UpdateTask()).start();
		});

		setOnMouseClicked(e -> {
			double x = e.getX(), y = e.getY();
			int c = (int) Math.round(((x + this.translateX.get()) / a - 1) / 1.5);
			int r = (int) Math.round(
					((y - this.getHeight() + SmartCanvas.ROOT3 / 2 * a - this.translateY.get()) / (-SmartCanvas.ROOT3)
							+ a * (c * 1.5) / 3) / a);
			this.setHighlighted(c, r);
		});

		this.a = this.HEX_LENGTH * this.ratio.get();

	}

	// This method completely redraws the entire canvas, without resetting the
	// constraints
	public void redraw(JsonArray tiles) {
		gc.setFill(Color.LIGHTSKYBLUE);
		gc.fillRect(0, 0, this.getWidth(), this.getHeight());

		for (JsonElement tile : tiles) {
			drawTile(tile.getAsJsonObject());
		}
		highlight();
	}

	/**
	 * 
	 * @return query,for the url of post
	 */
	public synchronized String getSubWorld() {
		int width = (int) (this.getWidth() / (1.5 * a)) + 1;
		int height = (int) (this.getHeight() / (ROOT3 / 2 * a)) + 1;
		int c = (int) (translateX.get() / (1.5 * a) * 2);
		int r = (int) ((translateY.get() / (ROOT3 / 2 * a)) / 2 + c / 2);
		String query = "&from_row=" + r + "&from_col=" + c + "&height=" + height + "&width=" + width;
		return query;
	}

//	public void redrawUpdatedTiles() {
//		// TODO
//	}

	public void resetConstraints() {
		double widthDiff = contentWidth() - this.getWidth();
		translateX.max.set(Math.max(widthDiff, 0));
		translateX.min.set(Math.min(widthDiff, 0));
		double heightDiff = contentHeight() - this.getHeight();
		translateY.max.set(Math.max(heightDiff, 0));
		translateY.min.set(Math.min(heightDiff, 0));
	}

	private void drawTile(JsonObject json) {
		int r = json.get("row").getAsInt();
		int c = json.get("col").getAsInt();
		double x = a * (c * 1.5);
		double y = this.getHeight() - (ROOT3 * (-x / 3 + r * a)) - a * 0.5 * ROOT3 + translateY.get();
		x -= (translateX.get() - a);
		String type = json.get("type").getAsString();

		gc.setFill(Color.LIGHTSKYBLUE);
		double i = 0.5 * ROOT3;
		double a = this.a; // the actual length of the hex drawn
		gc.fillPolygon(new double[] { x - a, x - a * 0.5, x + a * 0.5, x + a, x + a * 0.5, x - a * 0.5 },
				new double[] { y, y + a * i, y + a * i, y, y - a * i, y - a * i }, 6);

		switch (type) {
		case "rock":
			break;
		case "nothing":
			drawLilypad(x, y);
			break;
		case "food":
			drawLilypad(x, y);
			drawFood(x, y, json.get("value").getAsInt());
			break;
		case "critter":
			drawLilypad(x, y);
			String species = json.get("species_id").getAsString();
			int dir = json.get("direction").getAsInt();
			int size = json.get("mem").getAsJsonArray().get(3).getAsInt();
			drawCritter(x, y, species, size, dir);
			break;
		}
		this.drawHexBorder(x, y, Color.FLORALWHITE);
	}

	private void drawLilypad(double x, double y) {
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

	private void drawFood(double x, double y, int amt) {
		double height = 60 * ratio.get(), width = 60 * ratio.get();
		if (amt < 50)
			gc.drawImage(berry1, x - width / 2, (y - height / 2) - (8 * ratio.get()), width, height);
		else if (amt >= 50 && amt < 200)
			gc.drawImage(berry2, (x - width / 2) - (8 * ratio.get()), (y - height / 2) - (10 * ratio.get()), width,
					height);
		else if (amt >= 200 && amt < 600)
			gc.drawImage(berry3, x - width / 2, (y - height / 2) - (8 * ratio.get()), width, height);
		else if (amt >= 600 && amt < 1000)
			gc.drawImage(berry4, x - width / 2, y - height / 2, width, height);
		else if (amt >= 1000)
			gc.drawImage(berry5, x - width / 2, y - height / 2, width, height);
	}

	private void drawCritter(double x, double y, String species_name, int critter_size, int dir) {

		double size = 0.66 + 0.033 * Math.min(10, critter_size);
		int species = species_name.hashCode();

		double height = a * 1.3 * size;
		double width = a * 1.7 * size;
		double dotSize = a * 0.1 * size;
		gc.save();

		/////////////////////////////////////////////
		Rotate r = new Rotate(dir * (60), x, y);
		gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
		gc.drawImage(critter, x - width / 2, y - height / 2, width, height);
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

	/**
	 * 
	 * @param x     the x coordinate of the center point
	 * @param y     the y coordinate of the center point
	 * @param color
	 */
	private void drawHexBorder(double x, double y, Color color) {
		gc.setStroke(color);
		double c = 0.5 * ROOT3;
		double a = this.a; // the actual length of the hex drawn
		gc.strokePolygon(new double[] { x - a, x - a * 0.5, x + a * 0.5, x + a, x + a * 0.5, x - a * 0.5 },
				new double[] { y, y + a * c, y + a * c, y, y - a * c, y - a * c }, 6);
	}

	public void setHighlighted(int c, int r) {
		if (highlight_row != r && highlight_col != c) {
			double x = a * (highlight_col * 1.5);
			double y = this.getHeight() - (ROOT3 * (-x / 3 + highlight_row * a)) - a * 0.5 * ROOT3 + translateY.get();
			x -= (translateX.get() - a);
			drawHexBorder(x, y, Color.LIGHTSKYBLUE);
			highlight_row = r;
			highlight_col = c;
		}
		highlight();
	}

	public void highlight() {
		if (highlight_row >= 0 && highlight_col >= 0) {
			int r = highlight_row;
			int c = highlight_col;

			double x = a * (c * 1.5);
			double y = this.getHeight() - (ROOT3 * (-x / 3 + r * a)) - a * 0.5 * ROOT3 + translateY.get();
			x -= (translateX.get() - a);

			drawHexBorder(x, y, Color.DARKGOLDENROD);
		}
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	private double contentWidth() {
		return (worldWidth * 1.5 + 0.5) * a;
	}

	private double contentHeight() {
		return (worldHeight + 1) / 2.0 * ROOT3 * a;
	}

	// the max is 32
	private int getBit(int n, int k) {
		return (n >> k) & 1;
	}

	// should all be ones or zeroes
	private void drawDot(double x, double y, double size, int a, int b, int c, int d) {
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

	public void setContentDimension(int w, int h) {
		worldWidth = w;
		worldHeight = h;
	}

}
