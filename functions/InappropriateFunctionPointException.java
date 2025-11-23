package functions;

public class InappropriateFunctionPointException extends Exception {

    public InappropriateFunctionPointException(String errorMessage) {// конструктор принмающий строку
        super(errorMessage);
    }

    public InappropriateFunctionPointException() {//конструктор без параметров
    }
}

