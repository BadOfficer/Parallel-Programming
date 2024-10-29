import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;

public class T4 extends Thread {
    private final String name;
    private final CommonResources commonResources;
    private final int startPosition;
    private final int endPosition;
    private final Scanner scanner;

    public T4(String name, CommonResources commonResources, int priority, int h, Scanner scanner) {
        this.name = name;
        this.commonResources = commonResources;
        this.setPriority(priority);
        this.startPosition = 3 * h;
        this.endPosition = startPosition + h;
        this.scanner = scanner;
    }

    @Override
    public void run() {
        System.out.printf("\nThread %s started\n", name);

        try {
            Thread.sleep(1000);

            // Введення: Z, MM, d
            Main.S01.acquire();
            System.out.print("\nEnter value for VectorZ and Matrix M and Scalar d: ");
            int n = scanner.nextInt();

            commonResources.vectorZ = Data.fillVector(commonResources.getN(), n);
            commonResources.matrixM = Data.fillMatrix(commonResources.getN(), n);
            commonResources.scalarD = n;
            Main.S01.release();

            // Очікувати на закінчення введення даних в інших задачах
            Main.barrier1.await();

            // Обчислення 1: mi = min(BH)
            int scalarMi = Data.getMinVectorValue(Data.getPartOfVector(startPosition, endPosition, commonResources.vectorB));

            // Обчислення 2: m = min(m, mi)
            if (scalarMi < commonResources.getM().get()) {
                commonResources.getM().set(scalarMi);
            }

            // Сигнал задачам T1, T2, T3 про обчислення 2
            Main.S4.release(3);

            // Чекати сигнал про обчислення 2 у задачах T1, T2, T3
            Main.S1.acquire();
            Main.S2.acquire();
            Main.S3.acquire();

            // Копіювання: d1 = d
            Main.CS1.lock();
            int scalarD1 = commonResources.scalarD;
            Main.CS1.unlock();

            // Копіювання: Z1 = Z
            Main.CS2.lock();
            int[] vectorZ1 = commonResources.vectorZ;
            Main.CS2.unlock();

            // Копіювання: MM1 = MM
            Main.S02.acquire();
            int[][] matrixM1 = commonResources.matrixM;
            Main.S02.release();

            // Обчислення 3: RH = d1 * BH + Z1 * (MM1 * MXH)
            int[] vectorRH = Data.vectorSum(Data.scalarVectorMultiply(scalarD1, Data.getPartOfVector(startPosition, endPosition, commonResources.vectorB)), Data.vectorMatrixMultiply(vectorZ1, Data.transposeMatrix(Data.matrixMultiply(Data.getPartOfMatrix(commonResources.matrixX, startPosition, endPosition), matrixM1))));

            // Обчислення 4: KH = sort(RH)
            commonResources.vectorKH1_2 = Data.sortVector(vectorRH);

            // Сигнал задачі T3 про обчислення 4
            Main.S6.release();

            // Чекати сигнал про обчислення 6
            Main.S8.acquire();

            // Обчислення 7: XH = KH * m
            int[] vectorXH = Data.scalarVectorMultiply(commonResources.getM().get(), Data.getPartOfVector(startPosition, endPosition, commonResources.vectorK));

            // Чекати сигнал про обчислення 7 у задачах T1, T2, T3
            Main.barrier2.await();

            // Виведення результату X
            System.out.printf("\n%s result X: " + Arrays.toString(Data.showFinalResult(commonResources.vectorXH_1, commonResources.vectorXH_2, commonResources.vectorXH_3, vectorXH)) + "\n", name);

            System.out.printf("\n%s is finished \n", name);
        } catch (InterruptedException | BrokenBarrierException e) {
            System.out.println("Error in Thread 2 - " + e.getMessage());
        }
    }
}
