import javax.swing.*;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

enum Variable {
    AMPLITUDE("A", 0.0, 1.0, new HashMap<>(Map.of(
            0, new JLabel("0"),
            1000, new JLabel("1")))),
    
    K_X("βx", 0.0, 100.0, new HashMap<>(Map.of(
            0, (new JLabel("0")),
            1000, (new JLabel("100"))
    ))),
    
    K_Y("βy", 0.0, 100.0, new HashMap<>(Map.of(
            0, new JLabel("0"),
            1000, new JLabel("100")
    ))),
    
    RHO("ρ", 0.0, 1.0, new HashMap<>(Map.of(
            0, new JLabel("0"),
            1000, new JLabel("1")
    ))),
    
    OMEGA("ω", -100 * Math.PI, 100 * Math.PI, new HashMap<>(Map.of(
            0, new JLabel("-100π"),
            500, new JLabel("0"),
            1000, new JLabel("100π")
    ))),
    
    PHI("φ", 0.0, 2 * Math.PI, new HashMap<>(Map.of(
            0, new JLabel("0"),
            500, new JLabel("π"),
            1000, new JLabel("2π")
    )));
    
    
    private final String label;
    private final double min, max;
    private final HashMap<Integer, JLabel> tickLabelMap;
    private double value;
    
    Variable(String label, double min, double max, HashMap<Integer, JLabel> tickLabelMap) {
        this.label = label;
        this.min = min;
        this.max = max;
        this.tickLabelMap = tickLabelMap;
        value = 0.0;
    }
    
    void setValue(double value) {
        this.setValue(value);
    }
    
    Variable nextLooping() {
        int ord = ordinal();
        return values()[ord >= Variable.values().length - 1 ? 0 : ord + 1];
    }
    
    String getLabel() {
        return this.label;
    }
    
    double getMin() {
        return this.min;
    }
    
    double getMax() {
        return this.max;
    }
    
    Dictionary<Integer, JLabel> getTickLabelMap() {
        return new Hashtable<>(this.tickLabelMap);
    }
}

enum WaveType {
    PLANE("Plane"), RADIAL("Radial");
    
    private final String label;
    
    WaveType(String label) {
        this.label = label;
    }
    
    WaveType nextLooping() {
        int ord = ordinal();
        return values()[ord >= WaveType.values().length - 1 ? 0 : ord + 1];
    }
    
    String getLabel() {
        return this.label;
    }
}