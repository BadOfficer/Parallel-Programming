import java.util.Scanner;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* ---------------------------------------------------
-- Паралельне програмування                         --
--                                                  --
-- Лабораторна робота №2                            --
--                                                  --
-- Функція:                                         --
-- X = sort(d*B + Z*(MM*MX))* min(B)                --
--                                                  --
-- Виконав: Бондаренко Тарас Андрійович             --
-- Група: ІО-24                                     --
-- Дата: 29.10.2024                                 --
--------------------------------------------------- */

public class Main {
    private static final int p = 4;

    // Бар'єр для визначення закінчення введення даних
    public static final CyclicBarrier barrier1 = new CyclicBarrier(p);

    // Критична секція для керування доступом до спільного ресурсу d
    public static final Lock CS1 = new ReentrantLock();

    // Критична секція для керування доступом до спільного ресурсу Z
    public static final Lock CS2 = new ReentrantLock();

    // Семафора для керування доступом до спільного ресурсу MM
    public static final Semaphore S02 = new Semaphore(1);

    // Семафори для взаємодії потоків
    public static final Semaphore S1 = new Semaphore(0);
    public static final Semaphore S2 = new Semaphore(0);
    public static final Semaphore S3 = new Semaphore(0);
    public static final Semaphore S4 = new Semaphore(0);
    public static final Semaphore S5 = new Semaphore(0); // 1
    public static final Semaphore S6 = new Semaphore(0); // 1
    public static final Semaphore S7 = new Semaphore(0);
    public static final Semaphore S8 = new Semaphore(0);

    public static final CyclicBarrier barrier2 = new CyclicBarrier(p);

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
            Thread T2 = new T2("T2", commonResources, Thread.NORM_PRIORITY, h);
            Thread T3 = new T3("T3", commonResources, Thread.NORM_PRIORITY, h);
            Thread T4 = new T4("T4", commonResources, Thread.NORM_PRIORITY, h);

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

        } catch (Exception e) {
            exceptionHandler(e);
        }
    }

    public static void exceptionHandler(Exception e) {
        System.out.println(e.getMessage());
    }

}