

abstract class WaveEquation {
    double[] coefficients;
    
    WaveEquation() {
        coefficients = new double[Variable.values().length];
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
    
    double evaluateEquation(double x, double y, double t) {
        double k_dot_r = (coefficients[Variable.K_X.ordinal()] * x) + (coefficients[Variable.K_Y.ordinal()] * y);
        return evaluateGeneralEquation(k_dot_r, t);
    }
}

class RadialWaveEquation extends WaveEquation {
    private double[] center;
    
    RadialWaveEquation() {
        super();
        center = new double[2];
    }
    
    double evaluateEquation(double x, double y, double t) {
        double r_x = x - center[0];
        double r_y = y - center[1];
        double r_mag = Math.sqrt((r_x * r_x) + (r_y * r_y));
        
        double k_dot_r = r_mag * coefficients[Variable.K_X.ordinal()];
        return evaluateGeneralEquation(k_dot_r, t);
    }
    
    void setCenter(double x, double y) {
        center[0] = x;
        center[1] = y;
    }
}

