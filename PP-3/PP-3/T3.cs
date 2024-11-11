class T3 {
    public static int startPosition = 2 * Resources.n / Program.p;
    public static int endPosition = startPosition + Resources.n / Program.p;
    public static void Start() {
        int m3_copy;

        Console.WriteLine("T3 is started");

        // Очікувати на закінчення введення даних в T1, T2, T4
        Program.Evn1.WaitOne();
        Program.Evn2.WaitOne();

        // Обчислення 1: m3 = min(ZH)
        int m3 = Data.GetMinValue(Data.GetPartOfVector(Resources.Z, startPosition, endPosition));
    
        // Обчислення 2: m = min(m, m3), КД1
        Interlocked.Exchange(ref Resources.m, Math.Min(m3, Resources.m));

        // Сигнал задачам T1, T2, T4 про обчислення 2
        Program.S3.Release(3);

        // Чекати сигнал про обчислення 2 у задачах T1, T2, T4
        Program.S1.WaitOne();
        Program.S2.WaitOne();
        Program.S4.WaitOne();

        // Копіювання: m3 = m, КД3
        lock (Program.CS1) {
            m3_copy = Resources.m;
        }

        // Копіювання: d3 = d, КД3
        Program.Mtx1.WaitOne();
        int d3 = Resources.d;
        Program.Mtx1.ReleaseMutex();

        int[][] matrixCH = Data.GetPartOfMatrix(Resources.MC, startPosition, endPosition);
        
        // Обчислення 3: MOH = MB * (MCH * MM) * d + m * MCH
        int[][] MOH = Data.MatrixSum(Data.ScalarMatrixMultiply(Data.CalculateFirstPart(matrixCH), d3), Data.ScalarMatrixMultiply(matrixCH, m3_copy));
    
        Resources.MOH_3 = MOH;

        // Очікувати на закінчення обчислення 3 в T1, T2, T4
        Program.B1.SignalAndWait();

        Console.WriteLine("T3 is finished");
    }
}