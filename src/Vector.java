import java.util.ArrayList;

public class Vector {
    private ArrayList<Double> vector;
    
    Vector(double[] elements) {
        vector = new ArrayList<>();
        for (Double d : elements)
            vector.add(d);
    }
    
    double magnitude() {
        double sumOfSquares = 0.0;
        for (double d : vector)
            sumOfSquares += (d * d);
        return Math.sqrt(sumOfSquares);
    }
    
    double phase() {
        if (vector.size() != 2) throw new IllegalArgumentException();
        double x = vector.get(0);
        double y = vector.get(1);
        if (x == 0.0 && y == 0.0) throw new IllegalArgumentException();
        if (x == 0.0) return y > 0.0 ? Math.PI / 2 : -Math.PI / 2;
        double tan = Math.atan(y / x);
        if (x > 0.0) {
            if (y > 0.0)
                return tan;
            return tan + Math.PI;
        }
        return tan + Math.PI / 2;
    }
}
