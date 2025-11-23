package functions;

import java.io.Serializable;

public class FunctionPoint implements Serializable {
    private double x;
    private double y;

    // конструктор с заданными координатами
    public FunctionPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // конструктор копирования существующей точки
    public FunctionPoint(FunctionPoint point) {
        this.x = point.x;
        this.y = point.y;
    }

    // конструктор по умолчанию
    public FunctionPoint() { x = 0; y = 0; }

    // возвращает координату x
    public double getX() { return x; }

    // возвращает координату y
    public double getY() { return y; }

    // устанавливает новое значение x
    public void setX(double x) { this.x = x; }

    // устанавливает новое значение y
    public void setY(double y) { this.y = y; }
}