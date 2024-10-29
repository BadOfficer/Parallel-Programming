import java.util.Scanner;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private static final int p = 4;

    public static final CyclicBarrier B = new CyclicBarrier(p);
    public static final Semaphore S01 = new Semaphore(1);

    public static final Lock CS1 = new ReentrantLock();
    public static final Lock CS2 = new ReentrantLock();
    public static final Semaphore S02 = new Semaphore(1);

    public static final Semaphore S1 = new Semaphore(0);
    public static final Semaphore S2 = new Semaphore(0);
    public static final Semaphore S3 = new Semaphore(0);
    public static final Semaphore S4 = new Semaphore(0);
    public static final Semaphore S5 = new Semaphore(1);
    public static final Semaphore S6 = new Semaphore(1);
    public static final Semaphore S7 = new Semaphore(0);
    public static final Semaphore S8 = new Semaphore(0, true);
    public static final Semaphore S9 = new Semaphore(0);

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in); scanner) {
            System.out.print("Enter size for matrices and vectors: ");
            int n = scanner.nextInt();
            if (n < 0) {
                throw new IllegalArgumentException("Size must be greater than 0");
            }

            if (n % p != 0) {
                throw new IllegalArgumentException("Size must be a multiple of 4");
            }

            int h = n / p;

            CommonResources commonResources = new CommonResources(n);

            Thread T1 = new T1("T1", commonResources, Thread.NORM_PRIORITY, h);
            Thread T2 = new T2("T2", commonResources, Thread.NORM_PRIORITY, h, scanner);
            Thread T3 = new T3("T3", commonResources, Thread.NORM_PRIORITY, h);
            Thread T4 = new T4("T4", commonResources, Thread.NORM_PRIORITY, h, scanner);

            long startTime = System.nanoTime();

            T1.start();
            T2.start();
            T3.start();
            T4.start();

            T1.join();
            T2.join();
            T3.join();
            T4.join();

            long endTime = System.nanoTime();

            System.out.println("\nAll threads are finished in " + (endTime - startTime) / 1000000 + "ms");

        } catch (Exception e) {
            exceptionHandler(e);
        }
    }

    public static void exceptionHandler(Exception e) {
        System.out.println(e.getMessage());
    }

}