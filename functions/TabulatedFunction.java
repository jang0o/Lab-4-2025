package functions;

public interface TabulatedFunction extends Function {
    // возвращает кол-во точек в табулированной ф-ции
    int getPointsCount();

    // возвращает точку по указанному индексу
    FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException;

    // заменяет точку по указанному индексу
    void setPoint(int index, FunctionPoint point) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException;

    // возвращает координату x точки по индексу
    double getPointX(int index) throws FunctionPointIndexOutOfBoundsException;

    // устанавливает координату x точки по индексу
    void setPointX(int index, double x) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException;

    // возвращает координату y точки по индексу
    double getPointY(int index) throws FunctionPointIndexOutOfBoundsException;

    // устанавливает координату y точки по индексу
    void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException;


    // удаляет точку по указанному индексу
    void deletePoint(int index) throws FunctionPointIndexOutOfBoundsException, IllegalStateException;

    // добавляет новую точку в ф-цию
    void addPoint(FunctionPoint point) throws InappropriateFunctionPointException;
}