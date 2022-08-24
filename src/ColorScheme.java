import java.awt.*;

public class ColorScheme {
    //private int numColors;
    //private int stretch;
    //private int offset;
    
    /*ColorScheme(int stretch, int offset) {
        this.stretch = stretch;
        this.offset = offset;
    }*/
    
   /* Color valToColor(double val) {
        //int converted = Math.abs((int) (val * stretch) + offset);
        int converted = (int) val * stretch + offset;
        if (converted < 0 || converted > 1787) System.out.println(val);
        
        int r = 0, g = 0, b = 0;
        if (converted < 256) {  //Black to red:  0 0 0 -> 255 0 0
            r = converted;
            g = 0;
            b = 0;
        } else if (converted < 511) {   //red to yellow:  255 1 0 -> 255 255 0
            r = 255;
            g = converted - 255;
            b = 0;
        } else if (converted < 767) { //yellow to green:  254 255 0 -> 0 255 0
            r = 766 - converted;
            g = 255;
            b = 0;
        } else if (converted < 1022) { //green to cyan:  0 255 1 -> 0 255 255
            r = 0;
            g = 255;
            b = converted - 766;
        }
        else if (converted < 1277) { //cyan to blue:  0 254 255 -> 0 0 255
            r = 0;
            g = 1276 - converted;
            b = 255;
        }
        else if (converted < 1532) { //blue to purple:  1 0 255 -> 255 0 255
            r = converted - 1276;
            g = 0;
            b = 255;
        }
        else if (converted <= 1787) { //purple to white:  255 1 255 -> 255 255 255
            r = 255;
            g = converted - 1531;
            b = 255;
        }
        
        return new Color(r, g, b);
    }*/
    
    static Color transitionOfHueRange(double percentage, int startHue, int endHue, double saturation, double lightness) {
        // From 'startHue' 'percentage'-many to 'endHue'
        // Finally map from [0°, 360°] -> [0, 1.0] by dividing
        double hue = ((percentage * (endHue - startHue)) + startHue) / 360;
        
        // Get the color
        return hslColorToRgb(hue, saturation, lightness);
    }
    
    private static Color hslColorToRgb(double hue, double saturation, double lightness) {
        if (saturation == 0.0) {
            // The color is achromatic (has no color)
            // Thus use its lightness for a grey-scale color
            int grey = percToColor(lightness);
            return new Color(grey, grey, grey);
        }
        
        double q;
        if (lightness < 0.5) {
            q = lightness * (1 + saturation);
        } else {
            q = lightness + saturation - lightness * saturation;
        }
        double p = 2 * lightness - q;
        
        double oneThird = 1.0 / 3;
        double red = percToColor(hueToRgb(p, q, hue + oneThird));
        double green = percToColor(hueToRgb(p, q, hue));
        double blue = percToColor(hueToRgb(p, q, hue - oneThird));
        
        return new Color((int) red, (int) green, (int) blue);
    }
    
    private static double hueToRgb(double p, double q, double t) {
        if (t < 0) {
            t += 1;
        }
        if (t > 1) {
            t -= 1;
        }
        
        if (t < 1.0 / 6) {
            return p + (q - p) * 6 * t;
        }
        if (t < 1.0 / 2) {
            return q;
        }
        if (t < 2.0 / 3) {
            return p + (q - p) * (2.0 / 3 - t) * 6;
        }
        return p;
    }
    
    private static int percToColor(double percentage) {
        return (int) Math.round(percentage * 255);
    }
}
