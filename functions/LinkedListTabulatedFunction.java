package functions;

import java.io.*;

public class LinkedListTabulatedFunction implements TabulatedFunction, Serializable {
    private FunctionNode head;
    private int pointcount;
    private static final double EPSILON = 1e-10;

    private static class FunctionNode implements Serializable {
        private FunctionPoint point;
        private FunctionNode prev;
        private FunctionNode next;

        public FunctionNode(FunctionPoint point) {
            this.point = point; // устанавливаем точку
            this.prev = null; // предыдущий узел пока null
            this.next = null; // следующий узел пока null
        }

        public FunctionPoint getPoint() {
            return point; // возвращаем точку
        }

        public void setPoint(FunctionPoint point) {
            this.point = point; // устанавливаем новую точку
        }

        public FunctionNode getPrev() {
            return prev; // возвращаем предыдущий узел
        }

        public void setPrev(FunctionNode prev) {
            this.prev = prev; // устанавливаем предыдущий узел
        }

        public FunctionNode getNext() {
            return next; // возвращаем следующий узел
        }

        public void setNext(FunctionNode next) {
            this.next = next; // устанавливаем следующий узел
        }
    }

    public LinkedListTabulatedFunction() {
        this.head = new FunctionNode(null); // создаем голову
        head.setPrev(head);
        head.setNext(head);
        this.pointcount = 0; // начинаем с нуля точек
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        this(); // вызов конструктора по умолчанию

        if (leftX >= rightX - EPSILON) { // используем EPSILON вместо числового значения
            throw new IllegalArgumentException("некорректные границы области определения");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("недостаточное количество точек");
        }

        double interval = rightX - leftX;
        double step = interval / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++) {
            double currentX = leftX + i * step;
            addNodeToTail(new FunctionPoint(currentX, 0));
        }
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        this(); // вызов конструктора по умолчанию

        if (leftX >= rightX - EPSILON) { // используем EPSILON вместо числового значения
            throw new IllegalArgumentException("некорректные границы области определения");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("недостаточное количество точек");
        }

        double interval = rightX - leftX;
        double step = interval / (values.length - 1);

        for (int i = 0; i < values.length; i++) {
            double currentX = leftX + i * step;
            addNodeToTail(new FunctionPoint(currentX, values[i]));
        }
    }

    // конструктор, получающий массив точек
    public LinkedListTabulatedFunction(FunctionPoint[] points) {
        this(); // вызов конструктора по умолчанию для инициализации головы

        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        // проверка упорядоченности точек по X
        for (int i = 0; i < points.length - 1; i++) {
            if (points[i].getX() >= points[i + 1].getX() - EPSILON) { // используем EPSILON
                throw new IllegalArgumentException("Точки должны быть упорядочены по возрастанию X");
            }
        }

        // добавляем точки в список, создавая копии для инкапсуляции
        for (FunctionPoint point : points) {
            addNodeToTail(new FunctionPoint(point));
        }
    }

    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= pointcount) {
            throw new FunctionPointIndexOutOfBoundsException("индекс выходит за границы");
        }

        FunctionNode current;
        if (index < pointcount / 2) {
            current = head.getNext();
            for (int i = 0; i < index; i++) {
                current = current.getNext();
            }
        } else {
            current = head.getPrev();
            for (int i = pointcount - 1; i > index; i--) {
                current = current.getPrev();
            }
        }
        return current;
    }

    private FunctionNode addNodeToTail(FunctionPoint point) {
        FunctionNode newNode = new FunctionNode(point); // создаем новый узел
        FunctionNode tail = head.getPrev(); // получаем хвост списка

        newNode.setPrev(tail);
        newNode.setNext(head);
        tail.setNext(newNode);
        head.setPrev(newNode);

        pointcount++;
        return newNode; // возвращаем новый узел
    }

    private FunctionNode addNodeByIndex(int index, FunctionPoint point) {
        if (index < 0 || index > pointcount) {
            throw new FunctionPointIndexOutOfBoundsException("индекс выходит за границы");
        }

        if (index == pointcount) {
            return addNodeToTail(point); // добавляем в конец
        }

        FunctionNode currentNode = getNodeByIndex(index); // получаем текущий узел
        FunctionNode newNode = new FunctionNode(point); // создаем новый узел
        FunctionNode prevNode = currentNode.getPrev(); // получаем предыдущий узел

        newNode.setPrev(prevNode);
        newNode.setNext(currentNode);
        prevNode.setNext(newNode);
        currentNode.setPrev(newNode);

        pointcount++;
        return newNode; // возвращаем новый узел
    }

    private FunctionNode deleteNodeByIndex(int index) {
        if (index < 0 || index >= pointcount) {
            throw new FunctionPointIndexOutOfBoundsException("индекс выходит за границы");
        }

        FunctionNode nodeToDelete = getNodeByIndex(index); // узел для удаления
        FunctionNode prevNode = nodeToDelete.getPrev(); // предыдущий узел
        FunctionNode nextNode = nodeToDelete.getNext(); // следующий узел

        prevNode.setNext(nextNode);
        nextNode.setPrev(prevNode);

        pointcount--;
        return nodeToDelete; // возвращаем удаленный узел
    }

    public double getLeftDomainBorder() {
        if (pointcount == 0) return Double.NaN;
        return head.getNext().getPoint().getX(); // x первой точки
    }

    public double getRightDomainBorder() {
        if (pointcount == 0) return Double.NaN;
        return head.getPrev().getPoint().getX(); // x последней точки
    }

    public double getFunctionValue(double x) {
        if (pointcount == 0) {
            return Double.NaN;
        }

        double leftBorder = getLeftDomainBorder();
        double rightBorder = getRightDomainBorder();

        if (x < leftBorder - EPSILON || x > rightBorder + EPSILON) { // используем EPSILON
            return Double.NaN;
        }

        // ищем точку с точно таким же x
        FunctionNode current = head.getNext();
        while (current != head) {
            if (Math.abs(current.getPoint().getX() - x) < EPSILON) { // используем EPSILON вместо 1e-10
                return current.getPoint().getY();
            }
            current = current.getNext();
        }

        // совпадения нет - ищем интервал для интерполяции
        current = head.getNext();
        while (current != head && current.getNext() != head) {
            double x1 = current.getPoint().getX();
            double x2 = current.getNext().getPoint().getX();

            if (x >= x1 - EPSILON && x <= x2 + EPSILON) { // используем EPSILON
                double y1 = current.getPoint().getY();
                double y2 = current.getNext().getPoint().getY();
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
            current = current.getNext();
        }

        return Double.NaN;
    }

    public int getPointsCount() {
        return pointcount; // возвращаем количество точек
    }

    public FunctionPoint getPoint(int index) {
        if (index < 0 || index >= pointcount) {
            throw new FunctionPointIndexOutOfBoundsException("индекс выходит за границы");
        }
        // возвращаем копию чтобы защитить исходные данные
        FunctionPoint original = getNodeByIndex(index).getPoint();
        return new FunctionPoint(original.getX(), original.getY());
    }

    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        if (index < 0 || index >= pointcount) {
            throw new FunctionPointIndexOutOfBoundsException("индекс выходит за границы");
        }

        double newX = point.getX();
        if (index > 0 && newX <= getNodeByIndex(index - 1).getPoint().getX() + EPSILON) { // используем EPSILON
            throw new InappropriateFunctionPointException("нарушение порядка точек");
        }
        if (index < pointcount - 1 && newX >= getNodeByIndex(index + 1).getPoint().getX() - EPSILON) { // используем EPSILON
            throw new InappropriateFunctionPointException("нарушение порядка точек");
        }

        getNodeByIndex(index).setPoint(new FunctionPoint(point));
    }

    public double getPointX(int index) {
        if (index < 0 || index >= pointcount) {
            throw new FunctionPointIndexOutOfBoundsException("индекс выходит за границы");
        }
        return getNodeByIndex(index).getPoint().getX(); // возвращаем x точки
    }

    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        FunctionPoint currentPoint = getNodeByIndex(index).getPoint();
        FunctionPoint newPoint = new FunctionPoint(x, currentPoint.getY());
        setPoint(index, newPoint);
    }

    public double getPointY(int index) {
        if (index < 0 || index >= pointcount) {
            throw new FunctionPointIndexOutOfBoundsException("индекс выходит за границы");
        }
        return getNodeByIndex(index).getPoint().getY(); // возвращаем y точки
    }

    public void setPointY(int index, double y) {
        if (index < 0 || index >= pointcount) {
            throw new FunctionPointIndexOutOfBoundsException("индекс выходит за границы");
        }
        FunctionPoint currentPoint = getNodeByIndex(index).getPoint();
        FunctionPoint newPoint = new FunctionPoint(currentPoint.getX(), y);
        getNodeByIndex(index).setPoint(newPoint);
    }

    public void deletePoint(int index) {
        if (index < 0 || index >= pointcount) {
            throw new FunctionPointIndexOutOfBoundsException("индекс выходит за границы");
        }
        if (pointcount < 3) {
            throw new IllegalStateException("невозможно удалить точку");
        }
        deleteNodeByIndex(index);
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        FunctionNode current = head.getNext();
        int insertIndex = 0;

        while (current != head && current.getPoint().getX() < point.getX() - EPSILON) { // используем EPSILON
            insertIndex++;
            current = current.getNext();
        }

        if (current != head && Math.abs(current.getPoint().getX() - point.getX()) < EPSILON) { // используем EPSILON вместо 1e-10
            throw new InappropriateFunctionPointException("точка с таким x уже существует");
        }

        addNodeByIndex(insertIndex, new FunctionPoint(point));
    }
}