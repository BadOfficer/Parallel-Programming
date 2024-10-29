import java.util.concurrent.atomic.AtomicInteger;

public class CommonResources {
    private int n;

    private int[][] matrixX;
    private int[] vectorB;
    private int[] vectorX;
    public int[] vectorKH1_1;
    public int[] vectorKH1_2;
    public int[] vectorKH2_1;
    public int[] vectorK;
    public int[] vectorXH_1;
    public int[] vectorXH_2;
    public int[] vectorXH_3;
    public int[] vectorXH_4;

    // Спільні ресурси
    private int scalarD;
    private int[] vectorZ;
    private int[][] matrixM;
    private AtomicInteger m;

    public CommonResources(int n) {
        this.n = n;
        m = new AtomicInteger(Integer.MAX_VALUE);
    }

    public void setM(AtomicInteger m) {
        this.m = m;
    }

    public void setMatrixM(int[][] matrixM) {
        this.matrixM = matrixM;
    }

    public void setVectorZ(int[] vectorZ) {
        this.vectorZ = vectorZ;
    }

    public void setVectorX(int[] vectorX) {
        this.vectorX = vectorX;
    }

    public void setVectorB(int[] vectorB) {
        this.vectorB = vectorB;
    }

    public void setMatrixX(int[][] matrixX) {
        this.matrixX = matrixX;
    }

    public int getN() {
        return n;
    }

    public int[][] getMatrixX() {
        return matrixX;
    }

    public int[] getVectorB() {
        return vectorB;
    }

    public int[] getVectorX() {
        return vectorX;
    }

    public int getScalarD() {
        return scalarD;
    }

    public int[] getVectorZ() {
        return vectorZ;
    }

    public int[][] getMatrixM() {
        return matrixM;
    }

    public AtomicInteger getM() {
        return m;
    }

    public void setScalarD(int scalarD) {
        this.scalarD = scalarD;
    }
}
