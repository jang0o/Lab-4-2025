package functions;

import java.io.*;

public class ArrayTabulatedFunction implements TabulatedFunction, Externalizable {
    private FunctionPoint[] points;
    private int pointsCount;
    private static final double EPSILON = 1e-10;

    // создает табулированную ф-цию с нулевыми значениями
    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX - EPSILON) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        this.pointsCount = pointsCount;
        points = new FunctionPoint[pointsCount + 3];

        double step = (rightX - leftX) / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++) {
            points[i] = new FunctionPoint(leftX + step * i, 0);
        }
    }

    // создает табулированную ф-цию с заданными значениями в точках
    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX - EPSILON) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        pointsCount = values.length;
        points = new FunctionPoint[pointsCount + 3];

        double step = (rightX - leftX) / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++) {
            points[i] = new FunctionPoint(leftX + step * i, values[i]);
        }
    }

    // Конструктор без параметров для Externalizable
    public ArrayTabulatedFunction() {
    }

    // конструктор, получающий массив точек
    public ArrayTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        // проверка упорядоченности точек по X
        for (int i = 0; i < points.length - 1; i++) {
            if (points[i].getX() >= points[i + 1].getX() - EPSILON) {
                throw new IllegalArgumentException("Точки должны быть упорядочены по возрастанию X");
            }
        }

        this.pointsCount = points.length;
        this.points = new FunctionPoint[pointsCount + 3];

        // создаем копии точек для обеспечения инкапсуляции
        for (int i = 0; i < pointsCount; i++) {
            this.points[i] = new FunctionPoint(points[i]);
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(pointsCount);
        for (int i = 0; i < pointsCount; i++) {
            out.writeDouble(points[i].getX());
            out.writeDouble(points[i].getY());
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int count = in.readInt();
        points = new FunctionPoint[count + 3];
        pointsCount = count;
        for (int i = 0; i < count; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            points[i] = new FunctionPoint(x, y);
        }
    }

    // возвращает левую границу обл. опр. ф-ции
    public double getLeftDomainBorder() {
        return points[0].getX();
    }

    // возвращает правую границу обл. опр. ф-ции
    public double getRightDomainBorder() {
        return points[pointsCount - 1].getX();
    }

    // вычисляет значение ф-ции в заданной точке
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() - EPSILON || x > getRightDomainBorder() + EPSILON) {
            return Double.NaN;
        }

        // ищем точку с таким же X
        for (int i = 0; i < pointsCount; i++) {
            if (Math.abs(points[i].getX() - x) < EPSILON) {
                return points[i].getY();
            }
        }

        // если совпадения нет - ищем интервал для интерполяции
        for (int i = 0; i < pointsCount - 1; i++) {
            double x1 = points[i].getX();
            double x2 = points[i + 1].getX();

            if (x >= x1 - EPSILON && x <= x2 + EPSILON) {
                double y1 = points[i].getY();
                double y2 = points[i + 1].getY();
                return ((x - x1) * (y2 - y1)) / (x2 - x1) + y1;
            }
        }

        return Double.NaN;
    }

    // возвращает количество точек в ф-ции
    public int getPointsCount() {
        return pointsCount;
    }

    // возвращает копию точки по указанному индексу
    public FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы [0, " + (pointsCount - 1) + "]");
        }
        return new FunctionPoint(points[index]);
    }

    // заменяет точку по указанному индексу
    public void setPoint(int index, FunctionPoint point) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы [0, " + (pointsCount - 1) + "]");
        }

        double newX = point.getX();

        // Проверка корректности новой координаты X
        if (index > 0 && newX <= points[index - 1].getX() + EPSILON) {
            throw new InappropriateFunctionPointException("Новая координата X должна быть больше X предыдущей точки");
        }
        if (index < pointsCount - 1 && newX >= points[index + 1].getX() - EPSILON) {
            throw new InappropriateFunctionPointException("Новая координата X должна быть меньше X следующей точки");
        }

        points[index] = new FunctionPoint(point);
    }

    // возвращает координату x точки по индексу
    public double getPointX(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы [0, " + (pointsCount - 1) + "]");
        }
        return points[index].getX();
    }

    // возвращает координату y точки по индексу
    public double getPointY(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы [0, " + (pointsCount - 1) + "]");
        }
        return points[index].getY();
    }

    // изменяет координату x точки по индексу
    public void setPointX(int index, double x) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        setPoint(index, new FunctionPoint(x, points[index].getY()));
    }

    // изменяет координату y точки по индексу
    public void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы [0, " + (pointsCount - 1) + "]");
        }
        points[index].setY(y);
    }

    // удаляет точку по указанному индексу
    public void deletePoint(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы [0, " + (pointsCount - 1) + "]");
        }
        if (pointsCount <= 2) {
            throw new IllegalStateException("Невозможно удалить точку - функция должна иметь как минимум 2 точки");
        }

        System.arraycopy(points, index + 1, points, index, pointsCount - index - 1);
        pointsCount--;
    }

    // добавляет новую точку в ф-цию
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        double newX = point.getX();

        // проверка на существование точки с такой же координатой
        for (int i = 0; i < pointsCount; i++) {
            if (Math.abs(points[i].getX() - newX) < EPSILON) {
                throw new InappropriateFunctionPointException("Точка с X = " + newX + " уже существует");
            }
        }

        // увеличение массива
        if (pointsCount == points.length) {
            FunctionPoint[] newPoints = new FunctionPoint[points.length * 2];
            System.arraycopy(points, 0, newPoints, 0, pointsCount);
            points = newPoints;
        }

        // поиск места для вставки
        int insertIndex = 0;
        while (insertIndex < pointsCount && points[insertIndex].getX() < newX - EPSILON) {
            insertIndex++;
        }

        // вставка точки
        System.arraycopy(points, insertIndex, points, insertIndex + 1, pointsCount - insertIndex);
        points[insertIndex] = new FunctionPoint(point);
        pointsCount++;
    }
}