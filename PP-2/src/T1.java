import java.util.concurrent.BrokenBarrierException;

public class T1 extends Thread {
    private final String name;
    private final CommonResources commonResources;
    private final int startPosition;
    private final int endPosition;


    public T1(String name, CommonResources commonResources, int priority, int h) {
        this.name = name;
        this.commonResources = commonResources;
        this.setPriority(priority);
        this.startPosition = 0;
        this.endPosition = startPosition + h;
    }

    @Override
    public void run() {

        System.out.printf("\nThread %s started\n", name);

        try {
            // Чекати на введення даних в інших потоках
            Main.barrier1.await();

            // Обчислення 1: m1 = min(BH)
            int scalarM1 = Data.getMinVectorValue(Data.getPartOfVector(startPosition, endPosition, commonResources.vectorB));

            // Обчислення 2: m = min(m, m1), КД 1
            if (scalarM1 < commonResources.getM().get()) {
                commonResources.getM().set(scalarM1);
            }

            // Сигнал задачам T2, T3, T4 про обчислення 2
            Main.S1.release(3);

            // Чекати сигнал про обчислення m у задачах T2, T3, T4
            Main.S2.acquire();
            Main.S3.acquire();
            Main.S4.acquire();

            // Копіювання: d1 = d, КД 2
            Main.CS1.lock();
            int scalarD1 = commonResources.scalarD;
            Main.CS1.unlock();

            // Копіювання: Z1 = Z, КД 3
            Main.CS2.lock();
            int[] vectorZ1 = commonResources.vectorZ;
            Main.CS2.unlock();

            // Копіювання: MM1 = MM, КД 4
            Main.S02.acquire();
            int[][] matrixM1 = commonResources.matrixM;
            Main.S02.release();

            // Обчислення 3: RH = d1 * BH + Z1 * (MM1 * MXH)
            int[] vectorRH = Data.vectorSum(Data.scalarVectorMultiply(scalarD1, Data.getPartOfVector(startPosition, endPosition, commonResources.vectorB)), Data.vectorMatrixMultiply(vectorZ1, Data.transposeMatrix(Data.matrixMultiply(Data.getPartOfMatrix(commonResources.matrixX, startPosition, endPosition), matrixM1))));

            // Обчислення 4: KH = sort(RH)
            int[] vectorKH = Data.sortVector(vectorRH);

            // Очікувати на закінчення обчислення 4 в задачі Т2
            Main.S5.acquire();

            // Обчислення 5: K2H = msort(KH, KH)
            int[] vectorK2H = Data.concatAndSort(vectorKH, commonResources.vectorKH1_1);

            // Очікувати на закінчення обчислення 5 в Т3
            Main.S7.acquire();

            // Обчислення 6: K = msort(K2H, K2H)
            commonResources.vectorK = Data.concatAndSort(vectorK2H, commonResources.vectorKH2_1);

            // Сигнал T2, T3, T4 задачам про обчислення 6
            Main.S8.release(3);

            // Обчислення 7: XH = KH * m
            commonResources.vectorXH_1 = Data.scalarVectorMultiply(commonResources.getM().get(), Data.getPartOfVector(startPosition, endPosition, commonResources.vectorK));

            // Очікувати на закінчення обчислення 7 в T2, T3, T4
            Main.barrier2.await();

            System.out.printf("\n%s is finished\n", name);
        } catch (InterruptedException | BrokenBarrierException e) {
            System.out.println("Error in Thread 2 - " + e.getMessage());
        }
    }
}
