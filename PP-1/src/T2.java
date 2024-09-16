public class T2 extends Thread {
    private final String name;
    private final int N;

    public T2(String name, int priority, int N) {
        this.name = name;
        this.setPriority(priority); // Визначаємо пріорітетність
        this.N = N;
    }

    @Override
    public void run() {
        // Початок виконання потоку 2
        System.out.println("T2 is started!");

        double[][] matrixMF, matrixMK, matrixML;

        //Даємо можливість введення даних для кожного потоку
        // Введення даних для 2 потоку
        synchronized (System.in) {
            try {
                Thread.sleep(100); //Даємо можливість вивести повідомлення про початок іншим потокам
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (N <= 3) {
                System.out.println("\nThread T2. Fill MF");
                matrixMF = Data.fillMatrix(N);

                System.out.println("Thread T2. Fill MK");
                matrixMK = Data.fillMatrix(N);

                System.out.println("Thread T2. Fill ML");
                matrixML = Data.fillMatrix(N);
            } else {
                System.out.println("\nThread T2. Filling matrices and vectors by value = 2");
                matrixMF = Data.fillMatrixAuto(N, 2);
                matrixMK = Data.fillMatrixAuto(N, 2);
                matrixML = Data.fillMatrixAuto(N, 2);
            }
        }

        // Обчислюємо F2
        double[][] matrixMG = Data.addMatrices(Data.multiplyMatrix(Data.sortMatrixRows(matrixMF), matrixMK), matrixML);

        // Виведення результату функції потоку 2
        synchronized (System.in) {
            System.out.printf("\n%s. Matrix MG (F2): \n", name);
            Data.showMatrix(matrixMG);
            System.out.printf("\n%s is finished.", name);
        }
    }
}
