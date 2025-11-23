package functions;

public interface Function {
    // возвращает значение левой границы обл. опр. ф-ции
    double getLeftDomainBorder();

    // возвращает значение правой границы обл. опр. ф-ции
    double getRightDomainBorder();

    // возвращает значение ф-ции в заданной точке
    double getFunctionValue(double x);
}
