class Resources {
    public static int n;
    public static int[] Z;
    public static int m = int.MaxValue;
    public static int d;

    public static int[][] MO;

    public static int[][] MM;
    
    public static int[][] MB;
    public static int[][] MC;


    public static int[][] MOH_1;
    public static int[][] MOH_2;
    public static int[][] MOH_3;
    public static int[][] MOH_4;

    public static void InitializeResources(int size) {
        n = size;
        Z = new int[n];
        MM = new int[n][];
        MB = new int[n][];
        MC = new int[n][];
        MOH_1 = new int[n][];
        MOH_2 = new int[n][];
        MOH_3 = new int[n][];
        MOH_4 = new int[n][];

        for (int i = 0; i < n; i++) {
            MM[i] = new int[n];
            MB[i] = new int[n];
            MC[i] = new int[n];
            MOH_1[i] = new int[n];
            MOH_2[i] = new int[n];
            MOH_3[i] = new int[n];
            MOH_4[i] = new int[n];
        }
    }
}