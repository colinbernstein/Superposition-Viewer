public enum Variable {
    AMPLITUDE("A", 0.0, 1.0),
    K_X("Kx", Double.MIN_VALUE, Double.MAX_VALUE),
    K_Y("Ky", Double.MIN_VALUE, Double.MAX_VALUE),
    RHO("ρ", 0.0, 1.0),
    OMEGA("ω", 0.0, 1.0),
    PHI("φ", 0.0, 2 * Math.PI),
    TYPE("Type", 0.0, 0.2);
    
    private final String label;
    private final double min, max;
    
    Variable(String label, double min, double max) {
        this.label = label;
        this.min = min;
        this.max = max;
    }
    
    Variable next() {
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
}