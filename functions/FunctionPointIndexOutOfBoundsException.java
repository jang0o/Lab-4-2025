package functions;

public class FunctionPointIndexOutOfBoundsException extends IndexOutOfBoundsException {

    public FunctionPointIndexOutOfBoundsException(String errorMessage) {// конструктор класса принимающий строку
        super(errorMessage);
    }

    public FunctionPointIndexOutOfBoundsException() { // конструктор без параметров
    }
}
