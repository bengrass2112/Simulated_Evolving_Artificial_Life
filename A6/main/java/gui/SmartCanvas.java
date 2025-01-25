package gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import model.Critter;
import model.Tile;
import model.World;

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

    final ConstrainedDoubleProperty ratio = new ConstrainedDoubleProperty(1.0, MAX_RATIO, MIN_RATIO); // the ratio at
                                                                                                      // which the
                                                                                                      // contents are
                                                                                                      // displayed
    final ConstrainedDoubleProperty translateX; // left translate of world
    final ConstrainedDoubleProperty translateY; // right translate of world

    public static Tile highlighted;

    public SmartCanvas() {
        super();

        translateX = new ConstrainedDoubleProperty(-30, 0.0, 0);
        translateY = new ConstrainedDoubleProperty(-30, 0.0, 0);

        this.widthProperty().addListener(observable -> {
            resetConstraints();
            redraw();
        });

        this.heightProperty().addListener(observable -> {
            resetConstraints();
            redraw();
        });

        ratio.addListener(observable -> {
            resetConstraints();
            redraw();
        });

        translateX.addListener(observable -> redraw());
        translateY.addListener(observable -> redraw());

        setOnMouseClicked(e -> {
            double x = e.getX(), y = e.getY();
            double a = this.actualHexLength();
            int c = (int) Math.round(((x + this.translateX.get()) / a - 1) / 1.5);
            int r = (int) Math.round(
                    ((y - this.getHeight() + SmartCanvas.ROOT3 / 2 * a - this.translateY.get()) / (-SmartCanvas.ROOT3)
                            + a * (c * 1.5) / 3) / a);
            this.setHighlighted(World.tileAt(c, r));
            RightPane.get().updateTileInfo(World.tileAt(c, r));
        });
    }

    // This method completely redraws the entire canvas, without resetting the
    // constraints
    public void redraw() {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.clearRect(0, 0, this.getWidth(), this.getHeight());
        gc.setLineWidth(2);
        Tile[][] tiles = World.tiles();
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                drawTile(tiles[i][j]);
            }
        }
        highlight(highlighted);
    }

    public void resetConstraints() {
        double widthDiff = contentWidth() - this.getWidth();
        translateX.max.set(Math.max(widthDiff, 0) + 30);
        translateX.min.set(Math.min(widthDiff, 0) - 30);
        double heightDiff = contentHeight() - this.getHeight();
        translateY.max.set(Math.max(heightDiff, 0) + 30);
        translateY.min.set(Math.min(heightDiff, 0) - 30);
    }

    /**
     * 
     * @param t the tile to be drawn
     */
    public void drawTile(Tile t) {
        int r = t.row();
        int c = t.col();
        double a = actualHexLength();

        double x = a * (c * 1.5);
        double y = this.getHeight() - (ROOT3 * (-x / 3 + r * a)) - a * 0.5 * ROOT3 + translateY.get();
        x -= (translateX.get() - a);

        if (x > -a * 1.5 && x < this.getWidth() + a * 2 && y > -a * 1.5 && y < this.getHeight() + a * 1.5) {
            int content = t.contents();

            if (content != -1) {
                drawLilypad(x, y);
                if (content < -1) {
                    int amt = (content * -1) - 1;
                    drawFood(x, y, amt);
                } else if (content > 0) {
                    drawCritter(x, y, t.critter());
                }
            }
            drawHex(x, y, Color.HONEYDEW);
        }
    }

    private void drawLilypad(double x, double y) {
        GraphicsContext gc = this.getGraphicsContext2D();

        double height = 320 * ratio.get(), width = 220 * ratio.get();
        gc.drawImage(lilypad1, (x - width / 2) + (8 * ratio.get()), (y - height / 2) + (8 * ratio.get()), width,
                height);

    }

    private void drawFood(double x, double y, int amt) {
        GraphicsContext gc = this.getGraphicsContext2D();
        double height = 75 * ratio.get(), width = 75 * ratio.get();
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

    private void drawCritter(double x, double y, Critter c) {
        GraphicsContext gc = this.getGraphicsContext2D();
        Rotate r = new Rotate(c.dir() * (60), x, y);
        gc.save();
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());

        int size = c.getMemory()[3];
        // MAX: 260 200
        // MIN: 130 100
        double height = 260 * ratio.get(), width = 200 * ratio.get(), dotSize = 25;
        switch (size) {
        case 1:
            height = 120;
            width = 92.31;
            dotSize = 11;
            break;
        case 2:
            height = 140;
            width = 107.69;
            dotSize = 13;
            break;
        case 3:
            height = 160;
            width = 123.09;
            dotSize = 15;
            break;
        case 4:
            height = 180;
            width = 138.46;
            dotSize = 17;
            break;
        case 5:
            height = 200;
            width = 153.85;
            dotSize = 19;
            break;
        case 6:
            height = 220;
            width = 169.23;
            dotSize = 21;
            break;
        case 7:
            height = 240;
            width = 184.62;
            dotSize = 23;
            break;
        case 8:
            height = 260;
            width = 200;
            dotSize = 25;
            break;
        }
        gc.drawImage(critter, (x - (width * ratio.get()) / 2), (y - (height * ratio.get()) / 2) + (20 * ratio.get()),
                width * ratio.get(), height * ratio.get());
        // gc.setGlobalBlendMode(BlendMode.ADD);

        gc.setStroke(Color.BLACK);
        gc.strokeOval((x - ((dotSize / 2)) * ratio.get()), (y - ((dotSize / 2)) * ratio.get() + (20 * ratio.get())),
                dotSize * ratio.get(), dotSize * ratio.get());
        gc.setFill(c.dotColor());
        gc.fillOval((x - ((dotSize / 2)) * ratio.get()), (y - ((dotSize / 2)) * ratio.get() + (20 * ratio.get())),
                dotSize * ratio.get(), dotSize * ratio.get());
        gc.restore();

    }

    /**
     * 
     * @param x     the x coordinate of the center point
     * @param y     the y coordinate of the center point
     * @param color
     */
    private void drawHex(double x, double y, Color color) {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.setStroke(color);
        gc.setLineWidth(3);
        double c = 0.5 * ROOT3;
        double a = this.actualHexLength(); // the actual length of the hex drawn
        gc.strokePolygon(new double[] { x - a, x - a * 0.5, x + a * 0.5, x + a, x + a * 0.5, x - a * 0.5 },
                new double[] { y, y + a * c, y + a * c, y, y - a * c, y - a * c }, 6);
    }

    public void setHighlighted(Tile target) {
        if (highlighted != null && highlighted != target) {
            this.getGraphicsContext2D().setLineWidth(3);
            drawTile(highlighted);
        }
        if (target.critter() != null) {
            RightPane.get().programLabel.setText(target.critter().getProgramString());
        }
        highlighted = target;
        highlight(target);
    }

    public void highlight(Tile t) {
        if (t != null) {
            drawTile(t);
            GraphicsContext gc = this.getGraphicsContext2D();
            gc.setLineWidth(3);
            int r = t.row();
            int c = t.col();
            double a = actualHexLength();

            double x = a * (c * 1.5);
            double y = this.getHeight() - (ROOT3 * (-x / 3 + r * a)) - a * 0.5 * ROOT3 + translateY.get();
            x -= (translateX.get() - a);

            drawHex(x, y, Color.DARKGOLDENROD);
        }
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    double actualHexLength() {
        return ratio.get() * HEX_LENGTH;
    }

    private double contentWidth() {
        return (World.world().WIDTH * 1.5 + 0.5) * this.actualHexLength();
    }

    private double contentHeight() {
        return (World.world().HEIGHT + 1) / 2.0 * ROOT3 * this.actualHexLength();
    }
}
