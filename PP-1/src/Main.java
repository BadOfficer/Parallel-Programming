import java.util.Scanner;

/* ---------------------------------------------------
-- Паралельне програмування                         --
--                                                  --
-- Лабораторна робота №1                            --
--                                                  --
-- Функції:                                         --
-- F1 (1.3)  =>  C = A - B * (MA * MC) * e          --
-- F2 (2.5) ->  MG = SORT(MF) * MK + ML             --
-- F3 (3.7)  ->  O = (P + R) * (MS * MT)            --
--                                                  --
-- Виконав: Бондаренко Тарас Андрійович             --
-- Група: ІО-24                                     --
--------------------------------------------------- */

public class Main {
    public static void main(String[] args) throws InterruptedException {
        //Введення даних
        System.out.print("Enter size for matrices and vectors: ");
        Scanner sc = new Scanner(System.in);
        int N = sc.nextInt();

        Thread T1 = new T1("T1", 1, N);
        Thread T2 = new T2("T2", 5, N);
        Thread T3 = new T3("T3", 10, N);

        //Запускаємо потоки на виконання
        T1.start();
        T2.start();
        T3.start();

        // Визначаємо час початку виконання програми
        long startTime = System.nanoTime();

        //Очікуємо на завершшення всіма потоками обчслення функцій
        T1.join();
        T2.join();
        T3.join();

        // Визначаємо час закінчення програми
        long endTime = System.nanoTime();

        System.out.println("\nAll threads are finished in " + (endTime - startTime) / 1000000 + "ms"); // Час виконання програми
    }
}
