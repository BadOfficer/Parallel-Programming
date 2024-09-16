public class T1 extends Thread {
    private final String name;
    private final int N;

    public T1(String name, int priority, int N) {
        this.name = name;
        this.setPriority(priority); // Визначаємо пріорітетність
        this.N = N;
    }

    public void run() {
        // Початок виконання потоку 1
        System.out.println("T1 is started!");

        double[][] matrixMA, matrixMC;
        double[] vectorA, vectorB;
        double scalarE;

        //Даємо можливість введення даних для кожного потоку
        // Введення даних для 1 потоку
        synchronized (System.in) {
            try {
                Thread.sleep(100); //Даємо можливість вивести повідомлення про початок іншим потокам
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (N <= 3) {
                System.out.println("\nThread T1. Fill MA");
                matrixMA = Data.fillMatrix(N);

                System.out.println("Thread T1. Fill MC");
                matrixMC = Data.fillMatrix(N);

                System.out.println("Thread T1. Fill vector A");
                vectorA = Data.fillVector(N);

                System.out.println("Thread T1. Fill vector B");
                vectorB = Data.fillVector(N);

                System.out.print("Thread T1. Write scalar e: ");
                scalarE = Data.writeScalar();

            } else {
                System.out.println("\nThread T1. Filling matrices and vectors by value = 1");
                matrixMA = Data.fillMatrixAuto(N, 1);
                matrixMC = Data.fillMatrixAuto(N, 1);
                vectorA = Data.fillVectorAuto(N, 1);
                vectorB = Data.fillVectorAuto(N, 1);
                scalarE = 1;
            }
        }

        // Обчислюємо F1
        double[] vectorC = Data.vectorsSubtract(vectorA, Data.scalarVectorMultiply(Data.vectorMatrixMultiply(vectorB, Data.multiplyMatrix(matrixMA, matrixMC)), scalarE));

        // Виведення результату функції потоку 1
        synchronized (System.in) {
            System.out.printf("\n%s. Vector C (F1): \n", name);
            Data.showVector(vectorC);
            System.out.printf("\n%s is finished.", name);
        }

    }
}
