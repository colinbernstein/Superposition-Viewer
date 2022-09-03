import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

class GraphicsFrame {
    final Object mouseLock = new Object();  // for synchronization
    
    private BufferedImage offscreenImage, onscreenImage;    //Double buffered graphics
    private Graphics2D offscreen, onscreen;
    private boolean defer = false;
    private final double BORDER = 0.00;
    private double xMin, yMin, xMax, yMax;
    int width, height;
    int widthPixelCount, heightPixelCount;
    
    GraphicsFrame(int width, int height) {
        this.width = width;
        this.height = height;
        widthPixelCount = width / 5;
        heightPixelCount = height / 5;
        setXscale(widthPixelCount - 1);
        setYscale(heightPixelCount);
        offscreenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        onscreenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        offscreen = offscreenImage.createGraphics();
        onscreen = onscreenImage.createGraphics();
        
        // add antialiasing
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        offscreen.addRenderingHints(hints);
    }
    
    void filledSquare(double x, double y, double r, Color color) {
        if (r < 0) throw new IllegalArgumentException("square side length must be nonnegative");
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2 * r);
        double hs = factorY(2 * r);
        offscreen.setColor(color);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else offscreen.fill(new Rectangle2D.Double(xs - ws / 2, ys - hs / 2, ws, hs));
    }
    
    void show(JFrame frame) {
        defer = false;
        draw(frame);
        defer = true;
    }
    
    // draw onscreen if defer is false
    private void draw(JFrame frame) {
        if (defer) return;
        onscreen.drawImage(offscreenImage, 0, 0, null);
        frame.repaint();
    }
    
    // helper functions that scale from user coordinates to screen coordinates and back
    private double scaleX(double x) {
        return width * (x - xMin) / (xMax - xMin);
    }
    
    private double scaleY(double y) {
        return height * (yMax - y) / (yMax - yMin);
    }
    
    private double factorX(double w) {
        return w * width / Math.abs(xMax - xMin);
    }
    
    private double factorY(double h) {
        return h * height / Math.abs(yMax - yMin);
    }
    
    /**
     * Set the x-scale
     *
     * @param max the maximum value of the x-scale
     */
    private void setXscale(double max) {
        synchronized (mouseLock) {
            xMin = -BORDER * max;
            xMax = max + BORDER * max;
        }
    }
    
    /**
     * Set the y-scale
     *
     * @param max the maximum value of the y-scale
     */
    private void setYscale(double max) {
        double size = max - 1.0;
        synchronized (mouseLock) {
            yMin = 1.0 - BORDER * size;
            yMax = max + BORDER * size;
        }
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
    
    BufferedImage getOnscreenImage() {
        return onscreenImage;
    }
}
