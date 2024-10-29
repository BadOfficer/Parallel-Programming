import java.util.Arrays;
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
        this.endPosition = this.startPosition + h;
    }

    @Override
    public void run() {
        int scalarMi;
        int scalarD1;
        int[] vectorZ1;
        int[][] matrixM1;
        int[] vectorKH;

        System.out.printf("\nThread %s started\n", name);

        try {
            Main.B.await();

            scalarMi = Data.getMinVectorValue(Data.getPartOfVector(startPosition, endPosition, commonResources.getVectorB()));

            if (scalarMi < commonResources.getM().get()) {
                commonResources.getM().set(scalarMi);
            }

            Main.S3.release(3);

            Main.S1.acquire();
            Main.S2.acquire();
            Main.S4.acquire();

            Main.CS1.lock();
            scalarD1 = commonResources.getScalarD();
            Main.CS1.unlock();

            Main.CS2.lock();
            vectorZ1 = commonResources.getVectorZ();
            Main.CS2.unlock();

            Main.S02.acquire();
            matrixM1 = commonResources.getMatrixM();
            Main.S02.release();

            int[] vectorRH = Data.vectorSum(Data.scalarVectorMultiply(scalarD1, Data.getPartOfVector(startPosition, endPosition, commonResources.getVectorB())), Data.vectorMatrixMultiply(vectorZ1, Data.transposeMatrix(Data.matrixMultiply(Data.getPartOfMatrix(commonResources.getMatrixX(), startPosition, endPosition), matrixM1))));

            vectorKH = Data.sortVector(vectorRH);

            Main.S6.acquire();

            commonResources.vectorKH2_1 = Data.concatAndSort(vectorKH, commonResources.vectorKH1_2);

            Main.S7.release();

            Main.S8.acquire();

            commonResources.vectorXH_3 = Data.scalarVectorMultiply(commonResources.getM().get(), Data.getPartOfVector(startPosition, endPosition, commonResources.vectorK));

            Main.S9.release();

            System.out.println("Thread 3 Result: " + Arrays.toString(commonResources.vectorXH_3));

        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Thread 3 is finished");

    }
}
