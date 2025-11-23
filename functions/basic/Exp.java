package functions.basic;

import functions.Function;

public class Exp implements Function {

    // возвращает значение экспоненты в точке x
    public double getFunctionValue(double x) {
        return Math.exp(x);
    }

    // возвращает левую границу
    public double getLeftDomainBorder() {
        return Double.NEGATIVE_INFINITY;
    }

    // возвращает правую границу
    public double getRightDomainBorder() {
        return Double.POSITIVE_INFINITY;
    }
}

