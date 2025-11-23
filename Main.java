import functions.*;
import functions.basic.*;
import functions.meta.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Sin и Cos на отрезке от 0 до pi с шагом 0,1
            System.out.println("1. Sin и Cos на отрезке [0, pi] с шагом 0.1:");
            Function sinFunc = new Sin();
            Function cosFunc = new Cos();

            System.out.println("Sin(x):");
            for (double x = 0; x <= Math.PI; x += 0.1) {
                System.out.printf("sin(%.1f) = %.6f\n", x, sinFunc.getFunctionValue(x));
            }

            System.out.println("\nCos(x):");
            for (double x = 0; x <= Math.PI; x += 0.1) {
                System.out.printf("cos(%.1f) = %.6f\n", x, cosFunc.getFunctionValue(x));
            }

            // табулированные аналоги sin и cos
            System.out.println("\n2. Табулированные аналоги Sin и Cos (10 точек):");
            TabulatedFunction tabulatedSin = TabulatedFunctions.tabulate(sinFunc, 0, Math.PI, 10);
            TabulatedFunction tabulatedCos = TabulatedFunctions.tabulate(cosFunc, 0, Math.PI, 10);

            System.out.println("Сравнение точных и табулированных значений:");
            for (double x = 0; x <= Math.PI + 1e-10; x += 0.1) {
                double exactSin = sinFunc.getFunctionValue(x);
                double tabSin = tabulatedSin.getFunctionValue(x);
                double exactCos = cosFunc.getFunctionValue(x);
                double tabCos = tabulatedCos.getFunctionValue(x);

                double sinError = Math.abs(exactSin - tabSin);
                double cosError = Math.abs(exactCos - tabCos);

                System.out.printf("x=%.1f: sin=%.6f (tab=%.6f, err=%.6f) | cos=%.6f (tab=%.6f, err=%.6f)\n",
                        x, exactSin, tabSin, sinError, exactCos, tabCos, cosError);
            }

            System.out.println("\nТочки табулированного Sin:");
            for (int i = 0; i < tabulatedSin.getPointsCount(); i++) {
                System.out.printf("(%.4f, %.6f) ", tabulatedSin.getPointX(i), tabulatedSin.getPointY(i));
            }
            System.out.println();

            System.out.println("\nТочки табулированного Cos:");
            for (int i = 0; i < tabulatedCos.getPointsCount(); i++) {
                System.out.printf("(%.4f, %.6f) ", tabulatedCos.getPointX(i), tabulatedCos.getPointY(i));
            }
            System.out.println();

            // сумма квадратов табулированных синуса и косинуса
            System.out.println("\n3. Сумма квадратов табулированных Sin и Cos:");
            System.out.println("sin^2(x) + cos^2(x) (должно быть близко к 1):");
            for (double x = 0; x <= Math.PI; x += 0.1) {
                Function sin2 = Functions.power(tabulatedSin, 2);
                Function cos2 = Functions.power(tabulatedCos, 2);
                Function sum = Functions.sum(sin2, cos2);
                System.out.printf("x=%.1f: sin^2 + cos^2 = %.8f\n", x, sum.getFunctionValue(x));
            }

            // исследование влияния количества точек
            System.out.println("\nИсследование влияния количества точек на точность:");
            for (int points : new int[]{10, 20, 30}) {
                TabulatedFunction Tabsin = TabulatedFunctions.tabulate(sinFunc, 0, Math.PI, points);
                TabulatedFunction Tabcos = TabulatedFunctions.tabulate(cosFunc, 0, Math.PI, points);

                double maxError = 0;
                for (double x = 0; x <= Math.PI; x += 0.1) {
                    Function exact = Functions.sum(Functions.power(Tabsin, 2), Functions.power(Tabcos, 2));
                    maxError = Math.max(maxError, Math.abs(exact.getFunctionValue(x) - 1.0));
                }
                System.out.printf("Точек: %d, максимальное отклонение от 1: %.8f\n", points, maxError);
            }

            // экспонента и текстовый файл
            System.out.println("\n4. Экспонента и текстовый файл:");
            Function expFunc = new Exp();
            TabulatedFunction tabulatedExp = TabulatedFunctions.tabulate(expFunc, 0, 10, 11);

            // запись в текстовый файл
            FileWriter textWriter = new FileWriter("exp_function.txt");
            TabulatedFunctions.writeTabulatedFunction(tabulatedExp, textWriter);
            textWriter.close();

            // чтение из текстового файла
            FileReader textReader = new FileReader("exp_function.txt");
            TabulatedFunction readExp = TabulatedFunctions.readTabulatedFunction(textReader);
            textReader.close();

            System.out.println("Сравнение исходной и прочитанной экспоненты:");
            for (int i = 0; i < tabulatedExp.getPointsCount(); i++) {
                double x = tabulatedExp.getPointX(i);
                double original = tabulatedExp.getPointY(i);
                double fromFile = readExp.getPointY(i);
                System.out.printf("x=%.1f: исходная=%.6f, из файла=%.6f, совпадают=%b\n", x, original, fromFile, Math.abs(original - fromFile) < 1e-10);
            }

            // логарифм и бинарный файл
            System.out.println("\n5. Логарифм и текстовый файл:");
            Function logFunc = new Log(Math.E);
            TabulatedFunction tabulatedLog = TabulatedFunctions.tabulate(logFunc, 0.1, 10, 11);

            // запись в бинарный файл
            FileOutputStream out = new FileOutputStream("log_function.txt");
            TabulatedFunctions.outputTabulatedFunction(tabulatedLog, out);
            out.close();

            // чтение из бинарного файла
            FileInputStream in = new FileInputStream("log_function.txt");
            TabulatedFunction readLog = TabulatedFunctions.inputTabulatedFunction(in);
            in.close();

            System.out.println("Сравнение исходного и прочитанного логарифма:");
            for (int i = 0; i < tabulatedLog.getPointsCount(); i++) {
                double x = tabulatedLog.getPointX(i);
                double original = tabulatedLog.getPointY(i);
                double fromFile = readLog.getPointY(i);
                System.out.printf("x=%.1f: исходный=%.6f, из файла=%.6f, совпадают=%b\n", x, original, fromFile, Math.abs(original - fromFile) < 1e-10);
            }

            // сериализация
            System.out.println("\nСериализация:");

            // создаем композицию ln(exp(x)) = x
            Function composition = Functions.composition(new Log(Math.E), new Exp());

            System.out.println("\nСериализация");
            LinkedListTabulatedFunction linkedListFunction = new LinkedListTabulatedFunction(0, 10, 11);
            for (int i = 0; i < linkedListFunction.getPointsCount(); i++) {
                double x = linkedListFunction.getPointX(i);
                linkedListFunction.setPointY(i, composition.getFunctionValue(x));
            }

            System.out.println("Исходная функция LinkedList ln(exp(x)) = x:");
            for (double x = 0; x <= 10; x += 1) {
                System.out.printf("x=%.1f: %.6f\n", x, linkedListFunction.getFunctionValue(x));
            }

            try (FileOutputStream fos = new FileOutputStream("serializable.ser");
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(linkedListFunction);
                System.out.println("LinkedListTabulatedFunction сериализована в serializable.ser");
            }

            // десериализация Serializable
            LinkedListTabulatedFunction deserializedLinkedList;
            try (FileInputStream fis = new FileInputStream("serializable.ser");
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                deserializedLinkedList = (LinkedListTabulatedFunction) ois.readObject();
                System.out.println("Функция десериализована из serializable.ser");
            }

            System.out.println("Сравнение после Serializable:");
            for (double x = 0; x <= 10; x += 1) {
                double original = linkedListFunction.getFunctionValue(x);
                double deserialized = deserializedLinkedList.getFunctionValue(x);
                boolean matches = Math.abs(original - deserialized) < 1e-10;
                System.out.printf("x=%.1f: исходная=%.6f, восстановленная=%.6f, совпадают=%b\n",
                        x, original, deserialized, matches);
            }

            System.out.println("\nЭкстернализация");

            // создаем ArrayTabulatedFunction для Externalizable
            ArrayTabulatedFunction arrayFunction = new ArrayTabulatedFunction(0, 10, 11);
            for (int i = 0; i < arrayFunction.getPointsCount(); i++) {
                double x = arrayFunction.getPointX(i);
                arrayFunction.setPointY(i, composition.getFunctionValue(x));
            }

            System.out.println("Исходная функция Array ln(exp(x)) = x:");
            for (double x = 0; x <= 10; x += 1) {
                System.out.printf("x=%.1f: %.6f\n", x, arrayFunction.getFunctionValue(x));
            }

            try (FileOutputStream fos = new FileOutputStream("externalizable.ser");
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                arrayFunction.writeExternal(oos);
                System.out.println("ArrayTabulatedFunction сериализована в externalizable.ser");
            }

            // десериализация Externalizable
            ArrayTabulatedFunction deserializedExternalizable = new ArrayTabulatedFunction();
            try (FileInputStream fis = new FileInputStream("externalizable.ser");
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                deserializedExternalizable.readExternal(ois);
                System.out.println("Функция десериализована из externalizable.ser");
            }

            System.out.println("Сравнение после Externalizable:");
            for (double x = 0; x <= 10; x += 1) {
                double original = arrayFunction.getFunctionValue(x);
                double deserialized = deserializedExternalizable.getFunctionValue(x);
                boolean matches = Math.abs(original - deserialized) < 1e-10;
                System.out.printf("x=%.1f: исходная=%.6f, восстановленная=%.6f, совпадают=%b\n",
                        x, original, deserialized, matches);
            }

            // анализ размеров файлов
            System.out.println("\n--- Анализ размеров файлов ---");
            File serializableFile = new File("serializable.ser");
            File externalizableFile = new File("externalizable.ser");

            System.out.println("Размер файла Serializable (LinkedList): " + serializableFile.length() + " байт");
            System.out.println("Размер файла Externalizable (Array): " + externalizableFile.length() + " байт");

        } catch (Exception e) {
            System.out.println("Произошла ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}