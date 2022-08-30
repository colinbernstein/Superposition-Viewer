import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

class GraphicsFrame {
    // for synchronization
    final Object mouseLock = new Object();
    //static Object keyLock = new Object();
    
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
        widthPixelCount = width / 3;
        heightPixelCount = height / 3;
        offscreenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        onscreenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        offscreen = offscreenImage.createGraphics();
        onscreen = onscreenImage.createGraphics();
        //setXscale();
        //setYscale();
        //offscreen.setColor(Color.RED);
        //offscreen.fillRect(0, 0, width, height);
        //clear();
        
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
        //draw();
    }
    
    void show(JFrame frame, int t) {
        defer = false;
        draw(frame);
        try { Thread.sleep(t); } catch (InterruptedException e) { System.out.println("Error sleeping"); }
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
     * @param min the minimum value of the x-scale
     * @param max the maximum value of the x-scale
     */
    void setXscale(double min, double max) {
        double size = max - min;
        synchronized (mouseLock) {
            xMin = min - BORDER * size;
            xMax = max + BORDER * size;
        }
    }
    
    /**
     * Set the y-scale
     *
     * @param min the minimum value of the y-scale
     * @param max the maximum value of the y-scale
     */
    void setYscale(double min, double max) {
        double size = max - min;
        synchronized (mouseLock) {
            yMin = min - BORDER * size;
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
