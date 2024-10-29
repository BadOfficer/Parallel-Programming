import java.util.concurrent.atomic.AtomicInteger;

public class CommonResources {
    private final int n;

    public int[][] matrixX;
    public int[] vectorB;


    public int[] vectorX;


    public int[] vectorKH1_1;
    public int[] vectorKH1_2;
    public int[] vectorKH2_1;
    public int[] vectorK;


    public int[] vectorXH_1;
    public int[] vectorXH_2;
    public int[] vectorXH_3;

    // Спільні ресурси
    public int scalarD;
    public int[] vectorZ;
    public int[][] matrixM;
    public AtomicInteger m = new AtomicInteger(Integer.MAX_VALUE);

    public CommonResources(int n) {
        this.n = n;
    }

    public int getN() {
        return n;
    }

    public AtomicInteger getM() {
        return m;
    }
}
