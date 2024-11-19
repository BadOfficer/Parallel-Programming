import java.util.concurrent.atomic.AtomicInteger;

public class Monitor {
    private final int p;
    private final Resources resources;

    private int inputFlag = 0;
    private int calcMinMFlag = 0;
    private int calcThreeFlag = 0;

    public Monitor(int p, Resources resources) {
        this.p = p;
        this.resources = resources;
    }

    public synchronized void waitInput() throws InterruptedException {
        if(inputFlag != p - 1) {
            wait();
        }
    }

    public synchronized void signalInput() {
        inputFlag += 1;
        if(inputFlag == p - 1) {
            notifyAll();
        }
    }

    public synchronized void calcMinM(int valueM) {
        if(resources.m < valueM) {
            resources.m = valueM;
        }
    }

    public synchronized void signalCalcMinM() {
        calcMinMFlag += 1;
        if(calcMinMFlag == p) {
            notifyAll();
        }
    }

    public synchronized void waitCalcMinM() throws InterruptedException {
        if(calcMinMFlag != p) {
            wait();
        }
    }

    public synchronized int copyScalarM() {
        return resources.m;
    }

    public synchronized int copyScalarD() {
        return resources.d;
    }

    public synchronized int copyScalarP() {
        return resources.p;
    }

    public synchronized void signalCalcThree() {
        calcThreeFlag += 1;
        if(calcThreeFlag == p - 1) {
            notifyAll();
        }
    }

    public synchronized void waitCalcThree() throws InterruptedException {
        if(calcThreeFlag != p - 1) {
            wait();
        }
    }
}
