import java.util.Arrays;

public class T2 extends Thread{
    private final String name;
    private final Resources resources;
    private final Monitor monitor;
    private final int startPosition;
    private final int endPosition;

    public T2(String name, Resources resources, Monitor monitor, int h) {
        this.name = name;
        this.resources = resources;
        this.monitor = monitor;
        this.startPosition = 1;
        this.endPosition = startPosition + h;
    }

    public void run() {
        System.out.printf("\nThread %s started\n", name);

        try {
            resources.matrixX = Data.fillMatrix(resources.getN(), 1);
            resources.matrixC = Data.fillMatrix(resources.getN(), 1);

            monitor.signalInput();
            monitor.waitInput();

            int m2 = Data.getMaxVectorValue(Data.getPartOfVector(startPosition, endPosition, resources.vectorZ));

            monitor.calcMinM(m2);

            monitor.signalCalcMinM();

            monitor.waitCalcMinM();

            int m2Copy = monitor.copyScalarM();
            int d2Copy = monitor.copyScalarD();
            int p2Copy = monitor.copyScalarP();

            int[][] matrixCH = Data.getPartOfMatrix(resources.matrixC, startPosition, endPosition);
            int[][] matrixXH = Data.getPartOfMatrix(resources.matrixX, startPosition, endPosition);
            int[][] matrixMH = Data.getPartOfMatrix(resources.matrixM, startPosition, endPosition);

            resources.matrixAH2 = Data.calcMatrixAH(matrixCH, d2Copy, m2Copy, p2Copy, matrixXH, matrixMH, resources.matrixD);

            monitor.signalCalcThree();

            System.out.printf("\nThread %s finished\n", name);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
