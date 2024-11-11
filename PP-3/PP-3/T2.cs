class T2 {
    public static int startPosition = Resources.n / Program.p;
    public static int endPosition = startPosition + Resources.n / Program.p;
    public static void Start() {
        int m2_copy;
        
        Console.WriteLine("T2 is started");

        // Очікувати на закінчення введення даних в T1, T3, T4
        Program.Evn1.WaitOne();
        Program.Evn2.WaitOne();

        // Обчислення 1: m2 = min(ZH)
        int m2 = Data.GetMinValue(Data.GetPartOfVector(Resources.Z, startPosition, endPosition));

        // Обчислення 2: m = min(m, m2), КД1
        Interlocked.Exchange(ref Resources.m, Math.Min(m2, Resources.m));
        
        // Сигнал задачам T1, T3, T4 про обчислення 2
        Program.S2.Release(3);

        // Чекати сигнал про обчислення 2 у задачах T1, T3, T4
        Program.S1.WaitOne();
        Program.S3.WaitOne();
        Program.S4.WaitOne();

        // Копіювання: m2 = m, КД2
        lock (Program.CS1) {
            m2_copy = Resources.m;
        }

        // Копіювання: d2 = d, КД3
        Program.Mtx1.WaitOne();
        int d2 = Resources.d;
        Program.Mtx1.ReleaseMutex();

        int[][] matrixCH = Data.GetPartOfMatrix(Resources.MC, startPosition, endPosition);
        
        // Обчислення 3: MOH = MB * (MCH * MM) * d + m * MCH
        int[][] MOH = Data.MatrixSum(Data.ScalarMatrixMultiply(Data.CalculateFirstPart(matrixCH), d2), Data.ScalarMatrixMultiply(matrixCH, m2_copy));

        Resources.MOH_2 = MOH;

        // Очікувати на закінчення обчислення 3 в T2, T3, T4
        Program.B1.SignalAndWait();
        
        // Виведення результату MO
        Data.ShowMatrix(Resources.n);

        Console.WriteLine("T2 is finished");
    }
}