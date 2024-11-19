import java.util.Arrays;

public class T3 extends Thread {
    private final String name;
    private final Resources resources;
    private final Monitor monitor;
    private final int startPosition;
    private final int endPosition;

    public T3(String name, Resources resources, Monitor monitor, int h) {
        this.name = name;
        this.resources = resources;
        this.monitor = monitor;
        this.startPosition = 2;
        this.endPosition = startPosition + h;
    }

    public void run() {
        System.out.printf("\nThread %s started\n", name);

        try {
            monitor.waitInput();

            int m3 = Data.getMaxVectorValue(Data.getPartOfVector(startPosition, endPosition, resources.vectorZ));

            monitor.calcMinM(m3);

            monitor.signalCalcMinM();

            monitor.waitCalcMinM();

            int m3Copy = monitor.copyScalarM();
            int d3Copy = monitor.copyScalarD();
            int p3Copy = monitor.copyScalarP();

            int[][] matrixCH = Data.getPartOfMatrix(resources.matrixC, startPosition, endPosition);
            int[][] matrixXH = Data.getPartOfMatrix(resources.matrixX, startPosition, endPosition);
            int[][] matrixMH = Data.getPartOfMatrix(resources.matrixM, startPosition, endPosition);

            resources.matrixAH3 = Data.calcMatrixAH(matrixCH, d3Copy, m3Copy, p3Copy, matrixXH, matrixMH, resources.matrixD);

            monitor.signalCalcThree();

            System.out.printf("\nThread %s finished\n", name);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
