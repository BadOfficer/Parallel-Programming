import java.util.concurrent.BrokenBarrierException;

public class T3 extends Thread {
    private final String name;
    private final CommonResources commonResources;
    private final int startPosition;
    private final int endPosition;

    public T3(String name, CommonResources commonResources, int priority, int h) {
        this.name = name;
        this.commonResources = commonResources;
        this.setPriority(priority);
        this.startPosition = 2 * h;
        this.endPosition = startPosition + h;
    }

    @Override
    public void run() {
        System.out.printf("\nThread %s started\n", name);

        try {
            // Очікувати на закінчення введення даних в інших задачах
            Main.barrier1.await();

            // Обчислення 1: m3 = min(BH)
            int scalarM3 = Data.getMinVectorValue(Data.getPartOfVector(startPosition, endPosition, commonResources.vectorB));

            // Обчислення 2: m = min(m, m3), КД 1
            if (scalarM3 < commonResources.getM().get()) {
                commonResources.getM().set(scalarM3);
            }

            // Сигнал задачам T1, T2, T4 про обчислення 2
            Main.S3.release(3);

            // Чекати сигнал про обчислення 2 у задачах T1, T2, T4
            Main.S1.acquire();
            Main.S2.acquire();
            Main.S4.acquire();

            // Копіювання: d3 = d, КД 2
            Main.CS1.lock();
            int scalarD3 = commonResources.scalarD;
            Main.CS1.unlock();

            // Копіювання: Z3 = Z, КД 3
            Main.CS2.lock();
            int[] vectorZ3 = commonResources.vectorZ;
            Main.CS2.unlock();

            // Копіювання: MM3 = MM, КД 4
            Main.S02.acquire();
            int[][] matrixM3 = commonResources.matrixM;
            Main.S02.release();

            // Обчислення 3: RH = d3 * BH + Z3 * (MM3 * MXH)
            int[] vectorRH = Data.vectorSum(Data.scalarVectorMultiply(scalarD3, Data.getPartOfVector(startPosition, endPosition, commonResources.vectorB)), Data.vectorMatrixMultiply(vectorZ3, Data.transposeMatrix(Data.matrixMultiply(Data.getPartOfMatrix(commonResources.matrixX, startPosition, endPosition), matrixM3))));

            // Обчислення 4: KH = sort(RH)
            int[] vectorKH = Data.sortVector(vectorRH);

            // Очікувати на закінчення обчислення 4 в задачі Т4
            Main.S6.acquire();

            // Обчислення 5: K2H = msort(KH, KH)
            commonResources.vectorKH2_1 = Data.concatAndSort(vectorKH, commonResources.vectorKH1_2);

            // Сигнал задачі T1 про обчислення 5
            Main.S7.release();

            // Чекати сигнал про обчислення 6 в T1
            Main.S8.acquire();

            // Обчислення 7: XH = KH * m
            commonResources.vectorXH_3 = Data.scalarVectorMultiply(commonResources.getM().get(), Data.getPartOfVector(startPosition, endPosition, commonResources.vectorK));

            // Очікувати на закінчення обчислення 7 в T1, T2, T4
            Main.barrier2.await();

            System.out.printf("\n%s is finished\n", name);
        } catch (InterruptedException | BrokenBarrierException e) {
            System.out.println("Error in Thread 2 - " + e.getMessage());
        }
    }
}
