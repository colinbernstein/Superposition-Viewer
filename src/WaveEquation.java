

abstract class WaveEquation {
    double[] coefficients;
    
    WaveEquation() {
        coefficients = new double[Variable.values().length];
    }
    
    WaveEquation(double[] coefficients) {
        this.coefficients = coefficients;
    }
    
    abstract double evaluateEquation(double x, double y, double t);
    
    double evaluateGeneralEquation(double k_dot_r, double t) {
        double A = coefficients[Variable.AMPLITUDE.ordinal()];
        double rho = coefficients[Variable.RHO.ordinal()];
        double phi = coefficients[Variable.PHI.ordinal()];
        double wt = coefficients[Variable.OMEGA.ordinal()] * t;
        return A * (((1.0 - rho) * Math.cos(k_dot_r - wt + phi)) + (rho * Math.cos(k_dot_r + phi) * Math.cos(-wt)));
    }
    
    void setCoefficient(Variable var, double val) {
        if (var == Variable.RHO && (val < 0.0 || val > 1.0))
            throw new IllegalArgumentException();
        if (var == Variable.AMPLITUDE && (val < -1.0 || val > 1.0))
            throw new IllegalArgumentException();
        coefficients[var.ordinal()] = val;
    }
    
    double getVal(Variable var) {
        return coefficients[var.ordinal()];
    }
}

class PlaneWaveEquation extends WaveEquation {
    PlaneWaveEquation() {
        super();
    }
    
    PlaneWaveEquation(double[] coefficients) {
        super(coefficients);
    }
    
    double evaluateEquation(double x, double y, double t) {
        double K = coefficients[Variable.K.ordinal()];
        double angle = -coefficients[Variable.ANGLE.ordinal()];
        double Kx = K * Math.cos(angle);
        double Ky = K * Math.sin(angle);
        double k_dot_r = (Kx * x) + (Ky * y);
        return evaluateGeneralEquation(k_dot_r, t);
    }
    
    RadialWaveEquation toRadialWave() {
        return new RadialWaveEquation(coefficients);
    }
}

class RadialWaveEquation extends WaveEquation {
    private double[] center;
    
    RadialWaveEquation() {
        super();
        center = new double[]{Math.random(), Math.random()};
    }
    
    RadialWaveEquation(double[] coefficients) {
        super(coefficients);
        center = new double[]{Math.random(), Math.random()};
    }
    
    double evaluateEquation(double x, double y, double t) {
        double r_x = x - center[0];
        double r_y = y - center[1];
        double r_mag = Math.sqrt((r_x * r_x) + (r_y * r_y));
        
        double k_dot_r = r_mag * coefficients[Variable.K.ordinal()];
        return evaluateGeneralEquation(k_dot_r, t);
    }
    
    void setCenter(double x, double y) {
        center[0] = x;
        center[1] = y;
    }
    
    PlaneWaveEquation toPlaneWave() {
        return new PlaneWaveEquation(coefficients);
    }
}