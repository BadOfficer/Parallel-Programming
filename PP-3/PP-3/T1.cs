using System.Security.Cryptography.X509Certificates;

class T1 {
    public static int startPosition = 0;
    public static int endPosition = startPosition + Resources.n / Program.p;

    public static void Start() {
        int m1_copy;
        
        Console.WriteLine("T1 is started");

        // Введення MB, MC
        Data.fillMatrix(Resources.MB, Program.p, 1);
        Data.fillMatrix(Resources.MC, Program.p, 1);

        // Сигнал задачам T2, T3, T4
        Program.Evn1.Set();

        // Очікувати на закінчення введення даних в T2, T3, T4
        Program.Evn2.WaitOne();

        // Обчислення 1: m1 = min(ZH)
        int m1 = Data.GetMinValue(Data.GetPartOfVector(Resources.Z, startPosition, endPosition));

        // Обчислення 2: m = min(m, m1), КД1
        Interlocked.Exchange(ref Resources.m, Math.Min(m1, Resources.m));

        // Сигнал задачам T2, T3, T4 про обчислення 2
        Program.S1.Release(3);

        // Чекати сигнал про обчислення 2 у задачах T2, T3, T4
        Program.S2.WaitOne();
        Program.S3.WaitOne();
        Program.S4.WaitOne();

        // Копіювання: m1 = m, КД2
        lock (Program.CS1) {
            m1_copy = Resources.m;
        }

        // Копіювання: d1 = d, КД3
        Program.Mtx1.WaitOne();
        int d1 = Resources.d;
        Program.Mtx1.ReleaseMutex();

        int[][] matrixCH = Data.GetPartOfMatrix(Resources.MC, startPosition, endPosition);

        // Обчислення 3: MOH = MB * (MCH * MM) * d + m * MCH
        int[][] MOH = Data.MatrixSum(Data.ScalarMatrixMultiply(Data.CalculateFirstPart(matrixCH), d1), Data.ScalarMatrixMultiply(matrixCH, m1_copy));
        Resources.MOH_1 = MOH;

        // Очікувати на закінчення обчислення 3 в T2, T3, T4
        Program.B1.SignalAndWait();

        Console.WriteLine("T1 is finished");
    }
}