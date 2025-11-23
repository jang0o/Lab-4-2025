package functions.basic;

public class Tan extends TrigonometricFunction {

    public double getFunctionValue(double x) {
        double cosX = Math.cos(x);
        if (Math.abs(cosX) < 1e-10) {
            return Double.NaN; // точка разрыва
        }
        return Math.tan(x);
    }
}