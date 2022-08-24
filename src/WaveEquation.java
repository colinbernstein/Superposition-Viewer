

public class WaveEquation {
    //z = Ae^(gamma*k.r)*cos(k.r - wt)
    
    
    private double[] coefficients;
    private double[] center;
    
    WaveEquation(double[] coefficients) {
        if (coefficients[Variable.RHO.ordinal()] < 0.0 || coefficients[Variable.RHO.ordinal()] > 1.0)
            throw new IllegalArgumentException();
        this.coefficients = coefficients;
        center = new double[]{Math.random(), Math.random()};
    }
    
    double eval(double[] r, double t) {
        //double rmag = -10 * Math.sqrt((r[0] * r[0]) + (r[1] * r[1]));
        double wt = coefficients[Variable.OMEGA.ordinal()] * t;
        //k = new double[]{r[0] / rmag, r[1] / rmag};
        //k = new double[]{r[1] / rmag, -r[0] / rmag / 2};
        double kr;
        if (coefficients[Variable.TYPE.ordinal()] == 0.0)
            kr = (coefficients[Variable.K_X.ordinal()] * r[0]) + (coefficients[Variable.K_Y.ordinal()] * r[1]);
        else {
            double shift_x = r[0] + center[0];
            double shift_y = r[1] + center[1];
            double rmag = Math.sqrt((shift_x * shift_x) + (shift_y * shift_y));
            double kmag = Math.sqrt((coefficients[Variable.K_X.ordinal()] * coefficients[Variable.K_X.ordinal()]) +
                    (coefficients[Variable.K_Y.ordinal()] * coefficients[Variable.K_Y.ordinal()]));
            
            kr = rmag * kmag;
        }
        double A = coefficients[Variable.AMPLITUDE.ordinal()];
        double rho = coefficients[Variable.RHO.ordinal()];
        double phi = coefficients[Variable.PHI.ordinal()];
        //return A * Math.exp(gamma * kr) * Math.cos(kr - (w * t));
        return A * (((1.0 - rho) * Math.cos(wt + kr + phi)) + (rho * Math.cos(kr + phi) * Math.cos(wt)));
    }
    
    void mutate(int idx, double val) {
        if (idx == Variable.RHO.ordinal() && (val < 0.0 || val > 1.0))
            throw new IllegalArgumentException();
        coefficients[idx] = val;
    }
    
    double getVal(int idx) {
        return coefficients[idx];
    }
}

