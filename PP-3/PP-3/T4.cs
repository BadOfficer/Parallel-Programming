class T4 {
    public static int startPosition = 3 * Resources.n / Program.p;
    public static int endPosition = startPosition + Resources.n / Program.p;
    public static void Start() {
        int m4_copy;

        Console.WriteLine("T4 is started");

        // Введення Z, d, MM
        Data.fillVector(Resources.Z, Program.p, 1);
        Resources.d = 1;
        Data.fillMatrix(Resources.MM, Program.p, 1);

        // Сигнал задачам T1, T2, T3
        Program.Evn2.Set();

        // Очікувати на закінчення введення даних в T1, T2, T3
        Program.Evn1.WaitOne();

        // Обчислення 1: m4 = min(ZH)
        int m4 = Data.GetMinValue(Data.GetPartOfVector(Resources.Z, startPosition, endPosition));
    
        // Обчислення 2: m = min(m, m4), КД1
        Interlocked.Exchange(ref Resources.m, Math.Min(m4, Resources.m));

        // Сигнал задачам T1, T2, T3 про обчислення 2
        Program.S4.Release(3);

        // Чекати сигнал про обчислення 2 у задачах T2, T3, T4
        Program.S1.WaitOne();
        Program.S2.WaitOne();
        Program.S3.WaitOne();

        // Копіювання: m4 = m, КД2
        lock (Program.CS1) {
            m4_copy = Resources.m;
        }

        // Копіювання: d4 = d, КД3
        Program.Mtx1.WaitOne();
        int d4 = Resources.d;
        Program.Mtx1.ReleaseMutex();

        int[][] matrixCH = Data.GetPartOfMatrix(Resources.MC, startPosition, endPosition);

        // Обчислення 3: MOH = MB * (MCH * MM) * d + m * MCH
        int[][] MOH = Data.MatrixSum(Data.ScalarMatrixMultiply(Data.CalculateFirstPart(matrixCH), d4), Data.ScalarMatrixMultiply(matrixCH, m4_copy));

        Resources.MOH_4 = MOH;

        // Очікувати на закінчення обчислення 3 в T1, T2, T3
        Program.B1.SignalAndWait();

        Console.WriteLine("T4 is finished");
    }
}