using System;
using System.Diagnostics;

/* ---------------------------------------------------
-- Паралельне програмування                         --
--                                                  --
-- Лабораторна робота №3                            --
--                                                  --
-- Функція:                                         --
-- MO = MB * (MC * MM) * d + min(Z) * MC            --
--                                                  --
-- Виконав: Бондаренко Тарас Андрійович             --
-- Група: ІО-24                                     --
-- Дата: 11.11.2024                                 --
--------------------------------------------------- */
class Program {
    public static int p = 4;

    // Події для синхронізації введення даних;
    public static EventWaitHandle Evn1 = new EventWaitHandle(false, EventResetMode.ManualReset);
    public static EventWaitHandle Evn2 = new EventWaitHandle(false, EventResetMode.ManualReset);

    // Семафори для синхронізації обчислення 2
    public static Semaphore S1 = new Semaphore(0, 3);
    public static Semaphore S2 = new Semaphore(0, 3);
    public static Semaphore S3 = new Semaphore(0, 3);
    public static Semaphore S4 = new Semaphore(0, 3);

    // Критична секція для доступу до спільного ресурсу m;
    public static object CS1 = new Object();

    // Мютекс для керування доступом до спільного ресурсу;
    public static Mutex Mtx1 = new Mutex();

    // Бар'єр для визначення закінчення обчислення 3
    public static Barrier B1 = new Barrier(p);

    public static void Main(String[] args) {
        Stopwatch timer = new Stopwatch();
        Console.Write("Enter size for matrices and vectors: ");
        if (int.TryParse(Console.ReadLine(), out int value)) {
            Resources.InitializeResources(value);

            Thread T_1 = new Thread(T1.Start);
            Thread T_2 = new Thread(T2.Start);
            Thread T_3 = new Thread(T3.Start);
            Thread T_4 = new Thread(T4.Start);

            timer.Start();

            // Запуск потоків
            T_1.Start();
            T_2.Start();
            T_3.Start();
            T_4.Start();

            T_1.Join();
            T_2.Join();
            T_3.Join();
            T_4.Join();

            timer.Stop();

            Console.WriteLine("All threads are finished in " + timer.ElapsedMilliseconds + " ms");
        }
    }
}