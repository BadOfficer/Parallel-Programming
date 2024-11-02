import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;

public class T4 extends Thread {
    private final String name;
    private final CommonResources commonResources;
    private final int startPosition;
    private final int endPosition;

    public T4(String name, CommonResources commonResources, int priority, int h) {
        this.name = name;
        this.commonResources = commonResources;
        this.setPriority(priority);
        this.startPosition = 3 * h;
        this.endPosition = startPosition + h;
    }

    @Override
    public void run() {
        System.out.printf("\nThread %s started\n", name);

        try {
            Thread.sleep(1000);

            // Введення: Z, MM, d
            commonResources.vectorZ = Data.fillVector(commonResources.getN(), 1);
            commonResources.matrixM = Data.fillMatrix(commonResources.getN(), 1);
            commonResources.scalarD = 1;

            // Очікувати на закінчення введення даних в інших задачах
            Main.barrier1.await();

            // Обчислення 1: m4 = min(BH)
            int scalarM4 = Data.getMinVectorValue(Data.getPartOfVector(startPosition, endPosition, commonResources.vectorB));

            // Обчислення 2: m = min(m, m4), КД 1
            if (scalarM4 < commonResources.getM().get()) {
                commonResources.getM().set(scalarM4);
            }

            // Сигнал задачам T1, T2, T3 про обчислення 2
            Main.S4.release(3);

            // Чекати сигнал про обчислення 2 у задачах T1, T2, T3
            Main.S1.acquire();
            Main.S2.acquire();
            Main.S3.acquire();

            // Копіювання: d4 = d, КД 2
            Main.CS1.lock();
            int scalarD4 = commonResources.scalarD;
            Main.CS1.unlock();

            // Копіювання: Z4 = Z, КД 3
            Main.CS2.lock();
            int[] vectorZ4 = commonResources.vectorZ;
            Main.CS2.unlock();

            // Копіювання: MM4 = MM, КД 4
            Main.S02.acquire();
            int[][] matrixM4 = commonResources.matrixM;
            Main.S02.release();

            // Обчислення 3: RH = d4 * BH + Z4 * (MM4 * MXH)
            int[] vectorRH = Data.vectorSum(Data.scalarVectorMultiply(scalarD4, Data.getPartOfVector(startPosition, endPosition, commonResources.vectorB)), Data.vectorMatrixMultiply(vectorZ4, Data.transposeMatrix(Data.matrixMultiply(Data.getPartOfMatrix(commonResources.matrixX, startPosition, endPosition), matrixM4))));

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
