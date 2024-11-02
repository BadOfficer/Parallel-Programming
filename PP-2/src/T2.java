import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;

public class T2 extends Thread {
    private final String name;
    private final CommonResources commonResources;
    private final int startPosition;
    private final int endPosition;

    public T2(String name, CommonResources commonResources, int priority, int h) {
        this.name = name;
        this.commonResources = commonResources;
        this.setPriority(priority);
        this.startPosition = h;
        this.endPosition = startPosition + h;
    }

    @Override
    public void run() {
        System.out.printf("\nThread %s started\n", name);

        try {
            Thread.sleep(1000);

            // Введення: B, MX
            commonResources.vectorB = Data.fillVector(commonResources.getN(), 1);
            commonResources.matrixX = Data.fillMatrix(commonResources.getN(), 1);

            // Очікувати на закінчення введення даних в інших задачах
            Main.barrier1.await();

            // Обчислення 1: m2 = min(BH)
            int scalarM2 = Data.getMinVectorValue(Data.getPartOfVector(startPosition, endPosition, commonResources.vectorB));

            // Обчислення 2: m = min(m, m2), КД 1
            if (scalarM2 < commonResources.getM().get()) {
                commonResources.getM().set(scalarM2);
            }

            // Сигнал задачам T1, T3, T4 про обчислення 2
            Main.S2.release(3);

            // Чекати сигнал про обчислення m у задачах T1, T3, T4
            Main.S1.acquire();
            Main.S3.acquire();
            Main.S4.acquire();

            // Копіювання: d2 = d, КД 2
            Main.CS1.lock();
            int scalarD2 = commonResources.scalarD;
            Main.CS1.unlock();

            // Копіювання: Z2 = Z, КД 3
            Main.CS2.lock();
            int[] vectorZ2 = commonResources.vectorZ;
            Main.CS2.unlock();

            // Копіювання: MM2 = MM, КД 4
            Main.S02.acquire();
            int[][] matrixM2 = commonResources.matrixM;
            Main.S02.release();

            // Обчислення 3: RH = d2 * BH + Z2 * (MM2 * MXH)
            int[] vectorRH = Data.vectorSum(Data.scalarVectorMultiply(scalarD2, Data.getPartOfVector(startPosition, endPosition, commonResources.vectorB)), Data.vectorMatrixMultiply(vectorZ2, Data.transposeMatrix(Data.matrixMultiply(Data.getPartOfMatrix(commonResources.matrixX, startPosition, endPosition), matrixM2))));

            // Обчислення 4: KH = sort(RH)
            commonResources.vectorKH1_1 = Data.sortVector(vectorRH);

            // Сигнал задачі T1 про завершення обчислення 4
            Main.S5.release();

            // Чекати сигнал про обчислення 6 в Т1
            Main.S8.acquire();

            // Обчислення 7: XH = KH * m
            commonResources.vectorXH_2 = Data.scalarVectorMultiply(commonResources.getM().get(), Data.getPartOfVector(startPosition, endPosition, commonResources.vectorK));

            // Очікувати на закінчення обчислення 7 в T1, T3, T4
            Main.barrier2.await();

            System.out.printf("\n%s is finished\n", name);
        } catch (InterruptedException | BrokenBarrierException e) {
            System.out.println("Error in Thread 2 - " + e.getMessage());
        }
    }
}
