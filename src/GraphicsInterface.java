import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

class GraphicsInterface {
    JFrame frame;
    private BufferedImage offscreenImage;
    BufferedImage onscreenImage;    // double buffered graphics
    private Graphics2D offscreen, onscreen;
    private static boolean defer = false;
    private static final double BORDER = 0.00;
    private Color penColor = Color.BLACK;
    private static double xmin, ymin, xmax, ymax;
    int width, height;
    
    GraphicsInterface() {
        frame = new JFrame();
        width = 1000;
        height = 1000;
        offscreenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        onscreenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        offscreen = offscreenImage.createGraphics();
        onscreen = onscreenImage.createGraphics();
        //setXscale();
        //setYscale();
        //offscreen.setColor(Color.RED);
        //offscreen.fillRect(0, 0, width, height);
        clear();
        
        // add antialiasing
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        offscreen.addRenderingHints(hints);
    }
    
    void filledSquare(double x, double y, double r) {
        if (r < 0) throw new IllegalArgumentException("square side length must be nonnegative");
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2 * r);
        double hs = factorY(2 * r);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else offscreen.fill(new Rectangle2D.Double(xs - ws / 2, ys - hs / 2, ws, hs));
        draw();
    }
    
    void show(int t) {
        defer = false;
        draw();
        try { Thread.sleep(t); } catch (InterruptedException e) { System.out.println("Error sleeping"); }
        defer = true;
    }
    
    // draw onscreen if defer is false
    private void draw() {
        if (defer) return;
        onscreen.drawImage(offscreenImage, 0, 0, null);
        frame.repaint();
    }
    
   /* public static void setScale(double min, double max) {
        double size = max - min;
        xmin = min - BORDER * size;
        xmax = max + BORDER * size;
        ymin = min - BORDER * size;
        ymax = max + BORDER * size;
    }*/
    
    // helper functions that scale from user coordinates to screen coordinates and back
    private double scaleX(double x) {
        return width * (x - xmin) / (xmax - xmin);
    }
    
    private double scaleY(double y) {
        return height * (ymax - y) / (ymax - ymin);
    }
    
    private double factorX(double w) {
        return w * width / Math.abs(xmax - xmin);
    }
    
    private double factorY(double h) {
        return h * height / Math.abs(ymax - ymin);
    }
    
    /**
     * Set the x-scale
     *
     * @param min the minimum value of the x-scale
     * @param max the maximum value of the x-scale
     */
    void setXscale(double min, double max) {
        double size = max - min;
        xmin = min - BORDER * size;
        xmax = max + BORDER * size;
    }
    
    /**
     * Set the y-scale
     *
     * @param min the minimum value of the y-scale
     * @param max the maximum value of the y-scale
     */
    void setYscale(double min, double max) {
        double size = max - min;
        ymin = min - BORDER * size;
        ymax = max + BORDER * size;
    }
    
    double userX(double x) {
        return xmin + x * (xmax - xmin) / width;
    }
    
    double userY(double y) {
        return ymax - y * (ymax - ymin) / height;
    }
    
    /**
     * Draw one pixel at (x, y).
     *
     * @param x the x-coordinate of the pixel
     * @param y the y-coordinate of the pixel
     */
    private void pixel(double x, double y) {
        offscreen.fillRect((int) Math.round(scaleX(x)), (int) Math.round(scaleY(y)), 1, 1);
    }
    
    void setPenColor(Color color) {
        penColor = color;
        offscreen.setColor(penColor);
    }
    
    /**
     * Draw a filled rectangle of given half width and half height, centered on (x, y).
     *
     * @param x          the x-coordinate of the center of the rectangle
     * @param y          the y-coordinate of the center of the rectangle
     * @param halfWidth  is half the width of the rectangle
     * @param halfHeight is half the height of the rectangle
     * @throws IllegalArgumentException if halfWidth or halfHeight is negative
     */
    void filledRectangle(double x, double y, double halfWidth, double halfHeight) {
        if (halfWidth < 0) throw new IllegalArgumentException("half width must be nonnegative");
        if (halfHeight < 0) throw new IllegalArgumentException("half height must be nonnegative");
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2 * halfWidth);
        double hs = factorY(2 * halfHeight);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else offscreen.fill(new Rectangle2D.Double(xs - ws / 2, ys - hs / 2, ws, hs));
        draw();
    }
    
    /**
     * Clear the screen to black.
     */
    void clear() {
        offscreen.setColor(Color.BLACK);
        offscreen.fillRect(0, 0, width, height);
        //offscreen.setColor(penColor);
        //draw();
    }
}
