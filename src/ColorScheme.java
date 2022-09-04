import java.awt.*;

class ColorScheme {
    
    private int startHue, endHue;
    private double saturation, lightness;
    
    ColorScheme(int startHue, int endHue, double saturation, double lightness) {
        this.startHue = startHue;
        this.endHue = endHue;
        this.saturation = saturation;
        this.lightness = lightness;
    }
    
    Color transitionOfHueRange(double percentage) {
        // From 'startHue' 'percentage'-many to 'endHue'
        // Finally map from [0°, 360°] -> [0, 1.0] by dividing
        double hue = ((percentage * (endHue - startHue)) + startHue) / 360;
        
        // Get the color
        return hslColorToRgb(hue);
    }
    
    private Color hslColorToRgb(double hue) {
        if (saturation == 0.0) {
            // The color is achromatic (has no color)
            // Thus use its lightness for a grey-scale color
            int grey = percentToColor(hue);
            return new Color(grey, grey, grey);
        }
        
        double q;
        if (lightness < 0.5)
            q = lightness * (1 + saturation);
        else
            q = lightness + saturation - lightness * saturation;
        double p = 2 * lightness - q;
        
        double oneThird = 1.0 / 3;
        double red = percentToColor(hueToRgb(p, q, hue + oneThird));
        double green = percentToColor(hueToRgb(p, q, hue));
        double blue = percentToColor(hueToRgb(p, q, hue - oneThird));
        
        return new Color((int) red, (int) green, (int) blue);
    }
    
    private double hueToRgb(double p, double q, double t) {
        if (t < 0)
            t += 1;
        if (t > 1)
            t -= 1;
        if (t < 1.0 / 6)
            return p + (q - p) * 6 * t;
        if (t < 1.0 / 2)
            return q;
        if (t < 2.0 / 3)
            return p + (q - p) * (2.0 / 3 - t) * 6;
        return p;
    }
    
    private int percentToColor(double percentage) {
        return (int) Math.round(percentage * 255);
    }
}
