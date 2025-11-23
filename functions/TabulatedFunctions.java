package functions;

import java.io.*;

public class TabulatedFunctions {
    private TabulatedFunctions() {}

    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        // проверка границ
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Границы табулирования выходят за область определения функции");
        }

        // проверка минимального количества точек
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        // проверка интервала
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }

        // создаем массив значений ф-ции
        double[] values = new double[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);

        // вычисляем значения ф-ции
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            values[i] = function.getFunctionValue(x);
        }

        // возвращаем табулированную ф-цию
        return new ArrayTabulatedFunction(leftX, rightX, values);
    }

    // вывод табулированной ф-ции в байтовый поток
    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(out);

        // записываем кол-во точек
        dataOut.writeInt(function.getPointsCount());

        // записываем координаты всех точек
        for (int i = 0; i < function.getPointsCount(); i++) {
            dataOut.writeDouble(function.getPointX(i));
            dataOut.writeDouble(function.getPointY(i));
        }

        dataOut.flush();
    }

    // ввод табулированной функции из байтового потока
    public static TabulatedFunction inputTabulatedFunction(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(in);

        // читаем кол-во точек
        int pointsCount = dataIn.readInt();

        // читаем координаты точек
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            double x = dataIn.readDouble();
            double y = dataIn.readDouble();
            points[i] = new FunctionPoint(x, y);
        }

        // создаем табулированную ф-цию
        return new ArrayTabulatedFunction(points);
    }

    // запись ф-ции в символьный поток
    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) throws IOException {
        PrintWriter writer = new PrintWriter(out);

        // записываем кол-во точек
        writer.print(function.getPointsCount());
        writer.print(" ");

        for (int i = 0; i < function.getPointsCount(); i++) {
            writer.print(function.getPointX(i));
            writer.print(" ");
            writer.print(function.getPointY(i));
            if (i < function.getPointsCount() - 1) {
                writer.print(" ");
            }
        }

        writer.flush();
    }

    // считывание ф-ции из символьного потока
    public static TabulatedFunction readTabulatedFunction(Reader in) throws IOException {

        if (in == null) {
            throw new IllegalArgumentException("передан нулевой поток ввода");
        }

        StreamTokenizer tokenizer = new StreamTokenizer(in);
        tokenizer.parseNumbers();

        // считываем число точек
        int tokenType = tokenizer.nextToken();
        if (tokenType != StreamTokenizer.TT_NUMBER) {
            throw new IOException("не найдено количество точек");
        }
        int count = (int) tokenizer.nval;

        // создаем массив для хранения точек
        FunctionPoint[] functionPoints = new FunctionPoint[count];

        // считываем пары
        for (int i = 0; i < count; i++) {
            // считываем x координату
            tokenType = tokenizer.nextToken();
            if (tokenType != StreamTokenizer.TT_NUMBER) {
                throw new IOException("отсутствует x координата для точки " + i);
            }
            double xCoord = tokenizer.nval;

            // считываем y координату
            tokenType = tokenizer.nextToken();
            if (tokenType != StreamTokenizer.TT_NUMBER) {
                throw new IOException("отсутствует y координата для точки " + i);
            }
            double yCoord = tokenizer.nval;

            // создаем точку с полученными координатами
            functionPoints[i] = new FunctionPoint(xCoord, yCoord);
        }

        // возвращаем новую табулированную ф-цию
        return new ArrayTabulatedFunction(functionPoints);
    }
}