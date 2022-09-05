import java.awt.*;

interface GeneralColorScheme {
    Color percentageToColor(double percentage);
}

abstract class ColorScheme implements GeneralColorScheme {
    
    private double saturation, lightness;
    
    ColorScheme(double saturation, double lightness) {
        this.saturation = saturation;
        this.lightness = lightness;
    }
    
    Color hslColorToRgb(double hue) {
        if (saturation == 0.0) {
            // The color is achromatic (has no color)
            // Thus use its lightness for a grey-scale color
            int grey = percentToColorValueRange(hue);
            return new Color(grey, grey, grey);
        }
        
        double q;
        if (lightness < 0.5)
            q = lightness * (1 + saturation);
        else
            q = lightness + saturation - lightness * saturation;
        double p = 2 * lightness - q;
        
        double oneThird = 1.0 / 3;
        double red = percentToColorValueRange(hueToRgb(p, q, hue + oneThird));
        double green = percentToColorValueRange(hueToRgb(p, q, hue));
        double blue = percentToColorValueRange(hueToRgb(p, q, hue - oneThird));
        
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
    
    private int percentToColorValueRange(double percentage) {
        return (int) Math.round(percentage * 255);
    }
}

class ClassicColorScheme extends ColorScheme {
    
    private int startHue, endHue;
    
    ClassicColorScheme(int startHue, int endHue, double saturation, double lightness) {
        super(saturation, lightness);
        this.startHue = startHue;
        this.endHue = endHue;
    }
    
    public Color percentageToColor(double percentage) {
        // From 'startHue' 'percentage'-many to 'endHue'
        // Finally map from [0°, 360°] -> [0, 1.0] by dividing
        double hue = ((percentage * (endHue - startHue)) + startHue) / 360;
        return hslColorToRgb(hue);
    }
}

class BlackToColorColorScheme extends ColorScheme {
    
    private Color baseColor;
    
    BlackToColorColorScheme(int baseHue, double saturation, double lightness) {
        super(saturation, lightness);
        baseColor = hslColorToRgb(baseHue / 360.0);
    }
    
    public Color percentageToColor(double percentage) {
        return new Color((int) (baseColor.getRed() * percentage), (int) (baseColor.getGreen() * percentage), (int) (baseColor.getBlue() * percentage));
    }
}