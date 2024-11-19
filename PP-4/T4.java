import java.util.Arrays;

public class T4 extends Thread{
    private final String name;
    private final Resources resources;
    private final Monitor monitor;
    private final int startPosition;
    private final int endPosition;

    public T4(String name, Resources resources, Monitor monitor, int h) {
        this.name = name;
        this.resources = resources;
        this.monitor = monitor;
        this.startPosition = 3;
        this.endPosition = startPosition + h;
    }

    public void run() {
        System.out.printf("\nThread %s started\n", name);

        try {
            resources.vectorZ = Data.fillVector(resources.getN(), 1);
            resources.matrixD = Data.fillMatrix(resources.getN(), 1);
            resources.p = 1;

            monitor.signalInput();
            monitor.waitInput();

            int m4 = Data.getMaxVectorValue(Data.getPartOfVector(startPosition, endPosition, resources.vectorZ));

            monitor.calcMinM(m4);

            monitor.signalCalcMinM();

            monitor.waitCalcMinM();

            int m4Copy = monitor.copyScalarM();
            int d4Copy = monitor.copyScalarD();
            int p4Copy = monitor.copyScalarP();

            int[][] matrixCH = Data.getPartOfMatrix(resources.matrixC, startPosition, endPosition);
            int[][] matrixXH = Data.getPartOfMatrix(resources.matrixX, startPosition, endPosition);
            int[][] matrixMH = Data.getPartOfMatrix(resources.matrixM, startPosition, endPosition);

            resources.matrixAH4 = Data.calcMatrixAH(matrixCH, d4Copy, m4Copy, p4Copy, matrixXH, matrixMH, resources.matrixD);

            monitor.signalCalcThree();

            System.out.printf("\nThread %s finished\n", name);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
