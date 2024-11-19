import java.util.Arrays;

public class T1 extends Thread {
    private final String name;
    private final Resources resources;
    private final Monitor monitor;
    private final int startPosition;
    private final int endPosition;

    public T1(String name, Resources resources, Monitor monitor, int h) {
        this.name = name;
        this.resources = resources;
        this.monitor = monitor;
        this.startPosition = 0;
        this.endPosition = startPosition + h;
    }

    public void run() {
        System.out.printf("\nThread %s started\n", name);

        try {
            resources.d = 1;
            resources.matrixM = Data.fillMatrix(resources.getN(), 1);

            monitor.signalInput();
            monitor.waitInput();

            int m1 = Data.getMaxVectorValue(Data.getPartOfVector(startPosition, endPosition, resources.vectorZ));

            monitor.calcMinM(m1);

            monitor.signalCalcMinM();

            monitor.waitCalcMinM();

            int m1Copy = monitor.copyScalarM();
            int d1Copy = monitor.copyScalarD();
            int p1Copy = monitor.copyScalarP();

            int[][] matrixCH = Data.getPartOfMatrix(resources.matrixC, startPosition, endPosition);
            int[][] matrixXH = Data.getPartOfMatrix(resources.matrixX, startPosition, endPosition);
            int[][] matrixMH = Data.getPartOfMatrix(resources.matrixM, startPosition, endPosition);

            int[][] MAH = Data.calcMatrixAH(matrixCH, d1Copy, m1Copy, p1Copy, matrixXH, matrixMH, resources.matrixD);

            monitor.waitCalcThree();

            Data.showResult(resources.getN(), MAH, resources.matrixAH2, resources.matrixAH3, resources.matrixAH4);

            System.out.printf("\nThread %s finished\n", name);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
