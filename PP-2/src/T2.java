import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;

public class T2 extends Thread {
    private final String name;
    private final CommonResources commonResources;
    private final int startPosition;
    private final int endPosition;
    private final Scanner scanner;


    public T2(String name, CommonResources commonResources, int priority, int h, Scanner scanner) {
        this.name = name;
        this.commonResources = commonResources;
        this.setPriority(priority);
        this.startPosition = h;
        this.endPosition = this.startPosition + h;
        this.scanner = scanner;
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
            Thread.sleep(1000);
            Main.S01.acquire();
            System.out.print("\nEnter value for Matrix X: ");
            int n = scanner.nextInt();

            commonResources.setVectorB(Data.fillVectorByReverseInc(commonResources.getN()));
            commonResources.setMatrixX(Data.fillMatrix(commonResources.getN(), n));

            Main.S01.release();

            Main.B.await();

            scalarMi = Data.getMinVectorValue(Data.getPartOfVector(startPosition, endPosition, commonResources.getVectorB()));

            if (scalarMi < commonResources.getM().get()) {
                commonResources.getM().set(scalarMi);
            }

            Main.S2.release(3);

            Main.S1.acquire();
            Main.S3.acquire();
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

            commonResources.vectorKH1_1 = Data.sortVector(vectorRH);

            Main.S5.release();

            Main.S8.acquire();

            commonResources.vectorXH_2 = Data.scalarVectorMultiply(commonResources.getM().get(), Data.getPartOfVector(startPosition, endPosition, commonResources.vectorK));

            Main.S9.release();

            System.out.println("Thread 2 Result: " + Arrays.toString(commonResources.vectorXH_2));

            System.out.println("Thread 2 is finished");

        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }
}
