import java.util.Scanner;

public class Main {
    private static final int p = 4;

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter size for matrices and vectors: ");
        int n = scanner.nextInt();
        scanner.close();

        Resources resources = new Resources(n);
        Monitor monitor = new Monitor(p, resources);

        int h = n / p;

        Thread T1 = new T1("T1", resources, monitor, h);
        Thread T2 = new T2("T2", resources, monitor, h);
        Thread T3 = new T3("T3", resources, monitor, h);
        Thread T4 = new T4("T4", resources, monitor, h);

        long startTime = System.nanoTime();

        //Запуск потоків
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
    }
}
