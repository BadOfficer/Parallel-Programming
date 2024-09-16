public class T3 extends Thread {
    private final String name;
    private final int N;

    public T3(String name, int priority, int N) {
        this.name = name;
        this.setPriority(priority); // Визначаємо пріорітетність
        this.N = N;
    }

    @Override
    public void run() {
        // Початок виконання потоку 3
        System.out.println("T3 is started!");

        double[][] matrixMS, matrixMT;
        double[] vectorP, vectorR;
        synchronized (System.in) {
            try {
                Thread.sleep(100); //Даємо можливість вивести повідомлення про початок іншим потокам
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            //Даємо можливість введення даних для кожного потоку
            // Введення даних для 3 потоку
            if (N <= 3) {
                System.out.println("\nThread T3. Fill MS");
                matrixMS = Data.fillMatrix(N);

                System.out.println("Thread T3. Fill MT");
                matrixMT = Data.fillMatrix(N);

                System.out.println("Thread T3. Fill vector P");
                vectorP = Data.fillVector(N);

                System.out.println("Thread T3. Fill vector R");
                vectorR = Data.fillVector(N);
            } else {
                System.out.println("\nThread T3. Filling matrices and vectors by value = 3");
                matrixMS = Data.fillMatrixAuto(N, 3);
                matrixMT = Data.fillMatrixAuto(N, 3);
                vectorP = Data.fillVectorAuto(N, 3);
                vectorR = Data.fillVectorAuto(N, 3);
            }
        }
        // Обчислюємо F3
        double[] vectorO = Data.vectorMatrixMultiply(Data.vectorsSum(vectorP, vectorR), Data.multiplyMatrix(matrixMS, matrixMT));

        // Виведення результату функції потоку 3
        synchronized (System.in) {
            System.out.printf("\n%s. Vector O (F3): \n", name);
            Data.showVector(vectorO);
            System.out.printf("\n%s is finished.", name);
        }
    }
}
