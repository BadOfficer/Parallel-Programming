import mpi.MPI;

/* ---------------------------------------------------
-- Паралельне програмування                         --
--                                                  --
-- РГР                                              --
--                                                  --
-- Функція:                                         --
-- A = min(C) * Z + D * (MX * MR)                   --
--                                                  --
-- ПВВ1(Потік 1): C, Z                              --
-- ПВВ2(Потік 2): MX                                --
-- ПВВ3(Потік P): MR, D                             --
-- Виконав: Бондаренко Тарас Андрійович             --
-- Група: ІО-24                                     --
-- Дата: 21.12.2024                                 --
--------------------------------------------------- */
public class Main {
    public static void main(String[] args) {
        MPI.Init(args);
        long startTime = System.currentTimeMillis();
        int rank = MPI.COMM_WORLD.Rank();

        int[] N = new int[1];

        if (rank == 0) {
            N[0] = 8;
        }
        MPI.COMM_WORLD.Bcast(N, 0, 1, MPI.INT, 0);

        int P = 8;
        int H = N[0] / P;

        int[] C = new int[N[0]];
        int[] Z = new int[N[0]];
        int[] D = new int[N[0]];

        int[][] MX = new int[N[0]][N[0]];
        int[][] MR = new int[N[0]][N[0]];

        System.out.println("Thread " + (rank + 1) + " is started!");

        switch (rank) {
            case 0: {
                // Ввід C, Z
                Data.fillVector(C, 1);
                Data.fillVector(Z, 1);

                int[] receivedMX = new int[N[0] * N[0]];
                int[] receivedMR = new int[3 * H * N[0]];
                int[] receivedD = new int[N[0]];

                // Передати C[H..2H], C[2H..3H], C[3H..4H], C[4H..5H] в задачу T2
                MPI.COMM_WORLD.Send(C, H, 4 * H, MPI.INT, 1, 0);

                // Передати C[5H..6H], C[6H..7H], C[7H..8H] в задачу T8
                MPI.COMM_WORLD.Send(C, 5 * H, 3 * H, MPI.INT, 7, 0);

                // Передати Z[H..2H], Z[2H..3H], Z[3H..4H], Z[4H..5H] в задачу T2
                MPI.COMM_WORLD.Send(Z, H, 4 * H, MPI.INT, 1, 0);

                // Передати Z[5H..6H], Z[6H..7H], Z[7H..8H] в задачу T8
                MPI.COMM_WORLD.Send(Z, 5 * H, 3 * H, MPI.INT, 7, 0);

                // Прийняти MX від задачі T2
                MPI.COMM_WORLD.Recv(receivedMX, 0, N[0] * N[0], MPI.INT, 1, 0);

                // Передати MX задачі T8
                MPI.COMM_WORLD.Send(receivedMX, 0, N[0] * N[0], MPI.INT, 7, 0);

                // Прийняти MR[0..H], MR[H..2H], MR[2H..3H] від задачі T8
                MPI.COMM_WORLD.Recv(receivedMR, 0, 3 * H * N[0], MPI.INT, 7, 0);

                // Передати MR[H..2H], MR[2H..3H] в задачу Т2
                MPI.COMM_WORLD.Send(receivedMR, H * N[0], 2 * H * N[0], MPI.INT, 1, 0);

                // Прийняти D від задачі T8
                MPI.COMM_WORLD.Recv(receivedD, 0, N[0], MPI.INT, 7, 0);

                // Передати D в задачу T2
                MPI.COMM_WORLD.Send(receivedD, 0, N[0], MPI.INT, 1, 0);

                // Обчислення 1: с1 = min(CH)
                int c1 = Data.getMinElement(C, 0, H);

                int[] c7_8_copy = new int[1];
                int[] c2_3_copy = new int[1];

                // Прийняти c7_8 від задачі T8
                MPI.COMM_WORLD.Recv(c7_8_copy, 0, 1, MPI.INT, 7, 0);

                // Прийняти c2_3 від задачі T2
                MPI.COMM_WORLD.Recv(c2_3_copy, 0, 1, MPI.INT, 1, 0);

                // Обчислення 2: с1_8 = min(c7_8, c1)
                int c1_8 = Math.min(c1, c7_8_copy[0]);

                // Обчислення 3: с = min(c1_8, c2_3)
                int c = Math.min(c1_8, c2_3_copy[0]);

                // Передати с в задачу T8
                MPI.COMM_WORLD.Send(new int[]{c}, 0, 1, MPI.INT, 7, 0);

                // Передати с в задачу T2
                MPI.COMM_WORLD.Send(new int[]{c}, 0, 1, MPI.INT, 1, 0);

                int[] Zh = Data.getPartOfVector(0, H, Z);
                int[][] MRh = Data.transposeMatrix(Data.vectorToMatrix(receivedMR, 0, H, N[0]));
                int[][] MX_1 = Data.vectorToMatrix(receivedMX, 0, N[0], N[0]);

                // Обчислення 4: AH = c * ZH + D * (MX * MRH)
                int[] resultAh = Data.vectorSum(Data.scalarVectorMultiply(c, Zh), Data.vectorMatrixMultiply(receivedD, Data.matrixMultiply(MX_1, MRh)));

                int[] Ah7_h8 = new int[2 * H];

                // Прийняти AH7, AH8 від T8
                MPI.COMM_WORLD.Recv(Ah7_h8, 0, 2 * H, MPI.INT, 7, 0);

                int[] Ah_h8 = new int[resultAh.length + Ah7_h8.length];
                System.arraycopy(resultAh, 0, Ah_h8, 0, resultAh.length);
                System.arraycopy(Ah7_h8, 0, Ah_h8, resultAh.length, Ah7_h8.length);

                // Передати AH, AH7, AH8 задачі T2
                MPI.COMM_WORLD.Send(Ah_h8, 0, 3 * H, MPI.INT, 1, 0);

                break;
            }
            case 1: {
                Data.fillMatrix(MX, 1);

                int[] sendedMX = Data.matrixToVector(MX, new int[N[0] * N[0]], N[0]);
                int[] receivedCh_5h = new int[4 * H];
                int[] receivedZh_5h = new int[4 * H];
                int[] receivedMRh_3h = new int[2 * H * N[0]];
                int[] receivedD = new int[N[0]];

                // Прийняти C[H..2H], C[2H..3H], C[3H..4H], C[4H..5H] від задачі T1
                MPI.COMM_WORLD.Recv(receivedCh_5h, 0, 4 * H, MPI.INT, 0, 0);

                // Передати C[2H..3H], C[3H..4H], C[4H..5H] в задачу T3
                MPI.COMM_WORLD.Send(receivedCh_5h, H, 3 * H, MPI.INT, 2, 0);

                // Прийняти Z[H..2H], Z[2H..3H], Z[3H..4H], Z[4H..5H] з задачі Т1
                MPI.COMM_WORLD.Recv(receivedZh_5h, 0, 4 * H, MPI.INT, 0, 0);

                // Передати Z[2H..3H], Z[3H..4H], Z[4H..5H] задачі T3
                MPI.COMM_WORLD.Send(receivedZh_5h, H, 3 * H, MPI.INT, 2, 0);

                // Передати MX задачі T1
                MPI.COMM_WORLD.Send(sendedMX, 0, N[0] * N[0], MPI.INT, 0, 0);

                // Передати MX задачі T3
                MPI.COMM_WORLD.Send(sendedMX, 0, N[0] * N[0], MPI.INT, 2, 0);

                // Прийняти MR[H..2H], MR[2H..3H] з задачі Т1
                MPI.COMM_WORLD.Recv(receivedMRh_3h, 0, 2 * H * N[0], MPI.INT, 0, 0);

                // Передати MR[2H..3H] задачі T3
                MPI.COMM_WORLD.Send(receivedMRh_3h, H * N[0], H * N[0], MPI.INT, 2, 0);

                // Прийняти D з задачі Т1
                MPI.COMM_WORLD.Recv(receivedD, 0, N[0], MPI.INT, 0, 0);

                // Передати D задачі T3
                MPI.COMM_WORLD.Send(receivedD, 0, N[0], MPI.INT, 2, 0);

                // Обчислення 1: с2 = min(CH)
                int c2 = Data.getMinElement(receivedCh_5h, 0, H);

                int[] c3_4 = new int[1];

                // Прийняти с3_4 від задачі T3
                MPI.COMM_WORLD.Recv(c3_4, 0, 1, MPI.INT, 2, 0);

                // Обчислення 2: с2_3 = min(c3_4, c2)
                int c2_3 = Math.min(c3_4[0], c2);

                // Передати с2_3 в задачу T1
                MPI.COMM_WORLD.Send(new int[]{c2_3}, 0, 1, MPI.INT, 0, 0);

                int[] c = new int[1];

                // Прийняти с від задачі T1
                MPI.COMM_WORLD.Recv(c, 0, 1, MPI.INT, 0, 0);

                // Передати с в задачу T3
                MPI.COMM_WORLD.Send(c, 0, 1, MPI.INT, 2, 0);

                int[] Zh = Data.getPartOfVector(0, H, receivedZh_5h);
                int[][] MRh = Data.transposeMatrix(Data.vectorToMatrix(receivedMRh_3h, H, H, N[0]));

                // Обчислення 3: AH = c * ZH + D * (MX * MRH)
                int[] resultAh = Data.vectorSum(Data.scalarVectorMultiply(c[0], Zh), Data.vectorMatrixMultiply(receivedD, Data.matrixMultiply(MX, MRh)));

                int[] receivedAh_h8 = new int[3 * H];
                int[] receivedAh3_h6 = new int[4 * H];

                // Прийняти AH, AH7, AH8 від T1
                MPI.COMM_WORLD.Recv(receivedAh_h8, 0, 3 * H, MPI.INT, 0, 0);

                // Прийняти AH3, AH4, AH5, AH6 від T3
                MPI.COMM_WORLD.Recv(receivedAh3_h6, 0, 4 * H, MPI.INT, 2, 0);

                int[] A = new int[resultAh.length + receivedAh_h8.length + receivedAh3_h6.length];
                System.arraycopy(resultAh, 0, A, 0, resultAh.length);
                System.arraycopy(receivedAh_h8, 0, A, resultAh.length, receivedAh_h8.length);
                System.arraycopy(receivedAh3_h6, 0, A, resultAh.length + receivedAh_h8.length, receivedAh3_h6.length);

                System.out.println("Result of min(C) = " + c[0]);

                // Вивід результату A
                if (N[0] <= 16) {
                    System.out.println("\nResult A: ");
                    for (int i : A) {
                        System.out.print(i + " ");
                    }
                    System.out.println("\n");
                }

                break;
            }

            case 2: {
                int[] receivedC2h_5h = new int[3 * H];
                int[] receivedZ2h_5h = new int[3 * H];
                int[] receivedMX = new int[N[0] * N[0]];
                int[] receivedMR2h_3h = new int[H * N[0]];
                int[] receivedD = new int[N[0]];

                // Прийняти C[2H..3H], C[3H..4H], C[4H..5H] від задачі T2
                MPI.COMM_WORLD.Recv(receivedC2h_5h, 0, 3 * H, MPI.INT, 1, 0);

                // Передати C[3H..4H], C[4H..5H] в задачу T4
                MPI.COMM_WORLD.Send(receivedC2h_5h, H, 2 * H, MPI.INT, 3, 0);

                // Прийняти Z[2H..3H], Z[3H..4H], Z[4H..5H] з задачі Т2
                MPI.COMM_WORLD.Recv(receivedZ2h_5h, 0, 3 * H, MPI.INT, 1, 0);

                // Передати Z[3H..4H], Z[4H..5H] задачі T4
                MPI.COMM_WORLD.Send(receivedZ2h_5h, H, 2 * H, MPI.INT, 3, 0);

                // Прийняти MX з задачі Т2
                MPI.COMM_WORLD.Recv(receivedMX, 0, N[0] * N[0], MPI.INT, 1, 0);

                // Передати MX в задачу T4
                MPI.COMM_WORLD.Send(receivedMX, 0, N[0] * N[0], MPI.INT, 3, 0);

                // Прийняти MR[2H..3H] з задачі Т2
                MPI.COMM_WORLD.Recv(receivedMR2h_3h, 0, H * N[0], MPI.INT, 1, 0);

                // Прийняти D з задачі Т2
                MPI.COMM_WORLD.Recv(receivedD, 0, N[0], MPI.INT, 1, 0);

                // Обчислення 1: с3 = min(CH)
                int c3 = Data.getMinElement(receivedC2h_5h, 0, H);

                int[] c4_5 = new int[1];

                // Прийняти с4_5 від задачі T4
                MPI.COMM_WORLD.Recv(c4_5, 0, 1, MPI.INT, 3, 0);

                // Обчислення 2: с3_4 = min(c4_5, c3)
                int c3_4 = Math.min(c3, c4_5[0]);

                // Передати с3_4 в задачу T2
                MPI.COMM_WORLD.Send(new int[]{c3_4}, 0, 1, MPI.INT, 1, 0);

                int[] c = new int[1];

                // Прийняти с від задачі T2
                MPI.COMM_WORLD.Recv(c, 0, 1, MPI.INT, 1, 0);

                // Передати с в задачу T4
                MPI.COMM_WORLD.Send(c, 0, 1, MPI.INT, 3, 0);

                int[] Zh = Data.getPartOfVector(0, H, receivedZ2h_5h);
                int[][] MRh = Data.transposeMatrix(Data.vectorToMatrix(receivedMR2h_3h, 0, H, N[0]));
                int[][] MX_3 = Data.vectorToMatrix(receivedMX, 0, N[0], N[0]);

                // Обчислення 3: AH = c * ZH + D * (MX * MRH)
                int[] resultAh = Data.vectorSum(Data.scalarVectorMultiply(c[0], Zh), Data.vectorMatrixMultiply(receivedD, Data.matrixMultiply(MX_3, MRh)));

                int[] Ah4_h6 = new int[3 * H];

                // Прийняти AH4, AH5, AH6 від T4
                MPI.COMM_WORLD.Recv(Ah4_h6, 0, 3 * H, MPI.INT, 3, 0);

                int[] Ah3_h6 = new int[resultAh.length + Ah4_h6.length];
                System.arraycopy(resultAh, 0, Ah3_h6, 0, resultAh.length);
                System.arraycopy(Ah4_h6, 0, Ah3_h6, resultAh.length, Ah4_h6.length);

                // Передати AH3, AH4, AH5, AH6 в задачу Т2
                MPI.COMM_WORLD.Send(Ah3_h6, 0, 4 * H, MPI.INT, 1, 0);

                break;
            }

            case 3: {
                int[] receivedC3h_5h = new int[2 * H];
                int[] receivedZ3h_5h = new int[2 * H];
                int[] receivedMX = new int[N[0] * N[0]];
                int[] receivedMR3h_4h = new int[H * N[0]];
                int[] receivedD = new int[N[0]];

                // Прийняти C[3H..4H], C[4H..5H] від задачі T3
                MPI.COMM_WORLD.Recv(receivedC3h_5h, 0, 2 * H, MPI.INT, 2, 0);

                // Передати C[4H..5H] в задачу T5
                MPI.COMM_WORLD.Send(receivedC3h_5h, H, H, MPI.INT, 4, 0);

                // Прийняти Z[3H..4H], Z[4H..5H] з задачі Т3
                MPI.COMM_WORLD.Recv(receivedZ3h_5h, 0, 2 * H, MPI.INT, 2, 0);

                // Передати Z[4H..5H] задачі T5
                MPI.COMM_WORLD.Send(receivedZ3h_5h, H, H, MPI.INT, 4, 0);

                // Прийняти MX з задачі Т3
                MPI.COMM_WORLD.Recv(receivedMX, 0, N[0] * N[0], MPI.INT, 2, 0);

                // Передати MX в задачу T5
                MPI.COMM_WORLD.Send(receivedMX, 0, N[0] * N[0], MPI.INT, 4, 0);

                // Прийняти MR[3H..4H] з задачі Т5
                MPI.COMM_WORLD.Recv(receivedMR3h_4h, 0, H * N[0], MPI.INT, 4, 0);

                // Прийняти D з задачі Т5
                MPI.COMM_WORLD.Recv(receivedD, 0, N[0], MPI.INT, 4, 0);

                // Обчислення 1: с4 = min(CH)
                int c4 = Data.getMinElement(receivedC3h_5h, 0, H);

                int[] c5 = new int[1];

                // Прийняти с5 від задачі T5
                MPI.COMM_WORLD.Recv(c5, 0, 1, MPI.INT, 4, 0);

                // Обчислення 2: с4_5 = min(c4, c5)
                int c4_5 = Math.min(c4, c5[0]);

                // Передати c4_5 в задачу T3
                MPI.COMM_WORLD.Send(new int[]{c4_5}, 0, 1, MPI.INT, 2, 0);

                int[] c = new int[1];

                // Прийняти с від задачі T3
                MPI.COMM_WORLD.Recv(c, 0, 1, MPI.INT, 2, 0);

                // Передати с в задачу T5
                MPI.COMM_WORLD.Send(c, 0, 1, MPI.INT, 4, 0);

                int[] Zh = Data.getPartOfVector(0, H, receivedZ3h_5h);
                int[][] MRh = Data.transposeMatrix(Data.vectorToMatrix(receivedMR3h_4h, 0, H, N[0]));
                int[][] MX_4 = Data.vectorToMatrix(receivedMX, 0, N[0], N[0]);

                // Обчислення 3: AH = c * ZH + D * (MX * MRH)
                int[] resultAh = Data.vectorSum(Data.scalarVectorMultiply(c[0], Zh), Data.vectorMatrixMultiply(receivedD, Data.matrixMultiply(MX_4, MRh)));

                int[] Ah5_h6 = new int[2 * H];

                // Прийняти AH5, AH6 від T5
                MPI.COMM_WORLD.Recv(Ah5_h6, 0, 2 * H, MPI.INT, 4, 0);

                int[] Ah4_h6 = new int[resultAh.length + Ah5_h6.length];

                System.arraycopy(resultAh, 0, Ah4_h6, 0, resultAh.length);
                System.arraycopy(Ah5_h6, 0, Ah4_h6, resultAh.length, Ah5_h6.length);

                // Передати AH4, AH5, AH6 в задачу Т3
                MPI.COMM_WORLD.Send(Ah4_h6, 0, 3 * H, MPI.INT, 2, 0);

                break;
            }

            case 4: {
                int[] receivedC4h_5h = new int[H];
                int[] receivedZ4h_5h = new int[H];
                int[] receivedMX = new int[N[0] * N[0]];
                int[] receivedMR3h_5h = new int[2 * H * N[0]];
                int[] receivedD = new int[N[0]];

                // Прийняти C[4H..5H] від задачі T4
                MPI.COMM_WORLD.Recv(receivedC4h_5h, 0, H, MPI.INT, 3, 0);

                // Прийняти Z[4H..5H] з задачі Т4
                MPI.COMM_WORLD.Recv(receivedZ4h_5h, 0, H, MPI.INT, 3, 0);

                // Прийняти MX з задачі Т4
                MPI.COMM_WORLD.Recv(receivedMX, 0, N[0] * N[0], MPI.INT, 3, 0);

                // Передати MX в задачу T6
                MPI.COMM_WORLD.Send(receivedMX, 0, N[0] * N[0], MPI.INT, 5, 0);

                // Прийняти MR[3H..4H], MR[4H..5H] з задачі Т6
                MPI.COMM_WORLD.Recv(receivedMR3h_5h, 0, 2 * H * N[0], MPI.INT, 5, 0);

                // Передати MR[3H..4H] в задачу T4
                MPI.COMM_WORLD.Send(receivedMR3h_5h, 0, H * N[0], MPI.INT, 3, 0);

                // Прийняти D з задачі Т6
                MPI.COMM_WORLD.Recv(receivedD, 0, N[0], MPI.INT, 5, 0);

                // Передати D в задачу T4
                MPI.COMM_WORLD.Send(receivedD, 0, N[0], MPI.INT, 3, 0);

                // Обчислення 1: с5 = min(CH)
                int c5 = Data.getMinElement(receivedC4h_5h, 0, receivedC4h_5h.length);

                // Передати c5 в задачу T4
                MPI.COMM_WORLD.Send(new int[]{c5}, 0, 1, MPI.INT, 3, 0);

                // Передати c5 в задачу T6
                MPI.COMM_WORLD.Send(new int[]{c5}, 0, 1, MPI.INT, 5, 0);

                int[] c = new int[1];

                // Прийняти с від задачі T4
                MPI.COMM_WORLD.Recv(c, 0, 1, MPI.INT, 3, 0);

                int[] Zh = Data.getPartOfVector(0, H, receivedZ4h_5h);
                int[][] MRh = Data.transposeMatrix(Data.vectorToMatrix(receivedMR3h_5h, H * N[0], H, N[0]));
                int[][] MX_5 = Data.vectorToMatrix(receivedMX, 0, N[0], N[0]);

                // Обчислення 2: AH = c * ZH + D * (MX * MRH)
                int[] resultAh = Data.vectorSum(Data.scalarVectorMultiply(c[0], Zh), Data.vectorMatrixMultiply(receivedD, Data.matrixMultiply(MX_5, MRh)));

                int[] Ah6 = new int[H];

                // Прийняти AH6 від T6
                MPI.COMM_WORLD.Recv(Ah6, 0, H, MPI.INT, 5, 0);

                int[] Ah5_h6 = new int[resultAh.length + Ah6.length];
                System.arraycopy(resultAh, 0, Ah5_h6, 0, resultAh.length);
                System.arraycopy(Ah6, 0, Ah5_h6, resultAh.length, Ah6.length);

                // Передати AH5, AH6 в задачу Т4
                MPI.COMM_WORLD.Send(Ah5_h6, 0, 2 * H, MPI.INT, 3, 0);

                break;
            }

            case 5: {
                int[] receivedC5h_6h = new int[H];
                int[] receivedZ5h_6h = new int[H];
                int[] receivedMX = new int[N[0] * N[0]];
                int[] receivedMR3h_6h = new int[3 * H * N[0]];
                int[] receivedD = new int[N[0]];

                // Прийняти C[5H..6H] від задачі T7
                MPI.COMM_WORLD.Recv(receivedC5h_6h, 0, H, MPI.INT, 6, 0);

                // Прийняти Z[5H..6H] з задачі Т7
                MPI.COMM_WORLD.Recv(receivedZ5h_6h, 0, H, MPI.INT, 6, 0);

                // Прийняти MX з задачі Т5
                MPI.COMM_WORLD.Recv(receivedMX, 0, N[0] * N[0], MPI.INT, 4, 0);

                // Прийняти MR[3H..4H], MR[4H..5H], MR[5H..6H] з задачі Т7
                MPI.COMM_WORLD.Recv(receivedMR3h_6h, 0, 3 * H * N[0], MPI.INT, 6, 0);

                // Передати MR[3H..4H], MR[4H..5H] задачі T5
                MPI.COMM_WORLD.Send(receivedMR3h_6h, 0, 2 * H * N[0], MPI.INT, 4, 0);

                // Прийняти D з задачі Т7
                MPI.COMM_WORLD.Recv(receivedD, 0, N[0], MPI.INT, 6, 0);

                // Передати D в задачу T5
                MPI.COMM_WORLD.Send(receivedD, 0, N[0], MPI.INT, 4, 0);

                // Обчислення 1: с6 = min(CH)
                int c6 = Data.getMinElement(receivedC5h_6h, 0, H);

                int[] c5 = new int[1];

                // Прийняти с5 від задачі T5
                MPI.COMM_WORLD.Recv(c5, 0, 1, MPI.INT, 4, 0);

                // Обчислення 2: с5_6 = min(c5, c6)
                int c5_6 = Math.min(c5[0], c6);

                // Передати c5_6 в задачу T7
                MPI.COMM_WORLD.Send(new int[]{c5_6}, 0, 1, MPI.INT, 6, 0);

                int[] c = new int[1];

                // Прийняти с від задачі T7
                MPI.COMM_WORLD.Recv(c, 0, 1, MPI.INT, 6, 0);

                int[] Zh = Data.getPartOfVector(0, H, receivedZ5h_6h);
                int[][] MRh = Data.transposeMatrix(Data.vectorToMatrix(receivedMR3h_6h, 2 * H * N[0], H, N[0]));
                int[][] MX_6 = Data.vectorToMatrix(receivedMX, 0, N[0], N[0]);

                // Обчислення 3: AH = c * ZH + D * (MX * MRH)
                int[] resultAh = Data.vectorSum(Data.scalarVectorMultiply(c[0], Zh), Data.vectorMatrixMultiply(receivedD, Data.matrixMultiply(MX_6, MRh)));

                // Передати AH6 в задачу Т5
                MPI.COMM_WORLD.Send(resultAh, 0, H, MPI.INT, 4, 0);

                break;
            }

            case 6: {
                int[] receivedC5h_7h = new int[2 * H];
                int[] receivedZ5h_7h = new int[2 * H];
                int[] receivedMX = new int[N[0] * N[0]];
                int[] receivedMR3h_7h = new int[4 * H * N[0]];
                int[] receivedD = new int[N[0]];

                // Прийняти C[5H..6H], C[6H..7H] від задачі T8
                MPI.COMM_WORLD.Recv(receivedC5h_7h, 0, 2 * H, MPI.INT, 7, 0);

                // Передати C[5H..6H] в задачу T6
                MPI.COMM_WORLD.Send(receivedC5h_7h, 0, H, MPI.INT, 5, 0);

                // Прийняти Z[5H..6H], Z[6H..7H] з задачі Т8
                MPI.COMM_WORLD.Recv(receivedZ5h_7h, 0, 2 * H, MPI.INT, 7, 0);

                // Передати Z[5H..6H] задачі T6
                MPI.COMM_WORLD.Send(receivedZ5h_7h, 0, H, MPI.INT, 5, 0);

                // Прийняти MX з задачі Т8
                MPI.COMM_WORLD.Recv(receivedMX, 0, N[0] * N[0], MPI.INT, 7, 0);

                // Прийняти MR[3H..4H], MR[4H..5H], MR[5H..6H], MR[6H..7H] з задачі Т8
                MPI.COMM_WORLD.Recv(receivedMR3h_7h, 0, 4 * H * N[0], MPI.INT, 7, 0);

                // Передати MR[3H..4H], MR[4H..5H], MR[5H..6H], задачі T6
                MPI.COMM_WORLD.Send(receivedMR3h_7h, 0, 3 * H * N[0], MPI.INT, 5, 0);

                // Прийняти D з задачі Т8
                MPI.COMM_WORLD.Recv(receivedD, 0, N[0], MPI.INT, 7, 0);

                // Передати D в задачу T6
                MPI.COMM_WORLD.Send(receivedD, 0, N[0], MPI.INT, 5, 0);

                // Обчислення 1: с7 = min(CH)
                int c7 = Data.getMinElement(receivedC5h_7h, H, 2 * H);

                int[] c5_6 = new int[1];

                // Прийняти с5_6 від задачі T6
                MPI.COMM_WORLD.Recv(c5_6, 0, 1, MPI.INT, 5, 0);

                // Обчислення 2: с6_7 = min(c5_6, c7)
                int c6_7 = Math.min(c5_6[0], c7);

                // Передати c6_7 в задачу T8
                MPI.COMM_WORLD.Send(new int[]{c6_7}, 0, 1, MPI.INT, 7, 0);

                int[] c = new int[1];

                // Прийняти с від задачі T8
                MPI.COMM_WORLD.Recv(c, 0, 1, MPI.INT, 7, 0);

                // Передати c в задачу T6
                MPI.COMM_WORLD.Send(c, 0, 1, MPI.INT, 5, 0);

                int[] Zh = Data.getPartOfVector(H, 2 * H, receivedZ5h_7h);
                int[][] MRh = Data.transposeMatrix(Data.vectorToMatrix(receivedMR3h_7h, 3 * H * N[0], H, N[0]));
                int[][] MX_7 = Data.vectorToMatrix(receivedMX, 0, N[0], N[0]);

                // Обчислення 3: AH = c * ZH + D * (MX * MRH)
                int[] resultAh = Data.vectorSum(Data.scalarVectorMultiply(c[0], Zh), Data.vectorMatrixMultiply(receivedD, Data.matrixMultiply(MX_7, MRh)));

                // Передати AH7 в задачу Т8
                MPI.COMM_WORLD.Send(resultAh, 0, H, MPI.INT, 7, 0);

                break;
            }

            case 7: {
                Data.fillMatrix(MR, 1);
                Data.fillVector(D, 1);
                int[] receivedC5h_8h = new int[3 * H];
                int[] receivedZ5h_8h = new int[3 * H];
                int[] receivedMX = new int[N[0] * N[0]];
                int[] sendedMR = Data.matrixToVector(MR, new int[N[0] * N[0]], N[0]);

                // Прийняти C[5H..6H], C[6H..7H], C[7H..8H] від задачі T1
                MPI.COMM_WORLD.Recv(receivedC5h_8h, 0, 3 * H, MPI.INT, 0, 0);

                // Передати C[5H..6H], C[6H..7H] в задачу T7
                MPI.COMM_WORLD.Send(receivedC5h_8h, 0, 2 * H, MPI.INT, 6, 0);

                // Прийняти Z[5H..6H], Z[6H..7H], Z[7H..8H] з задачі Т1
                MPI.COMM_WORLD.Recv(receivedZ5h_8h, 0, 3 * H, MPI.INT, 0, 0);

                // Передати Z[5H..6H], Z[6H..7H] задачі T7
                MPI.COMM_WORLD.Send(receivedZ5h_8h, 0, 2 * H, MPI.INT, 6, 0);

                // Прийняти MX з задачі Т1
                MPI.COMM_WORLD.Recv(receivedMX, 0, N[0] * N[0], MPI.INT, 0, 0);

                // Передати MX в задачу T7
                MPI.COMM_WORLD.Send(receivedMX, 0, N[0] * N[0], MPI.INT, 6, 0);

                // Передати MR[3H..4H], MR[4H..5H], MR[5H..6H], MR[6H..7H] задачі T7
                MPI.COMM_WORLD.Send(sendedMR, 3 * H * N[0], 4 * H * N[0], MPI.INT, 6, 0);

                // Передати MR[0..H], MR[H..2H], MR[2H..3H] задачі T1
                MPI.COMM_WORLD.Send(sendedMR, 0, 3 * H * N[0], MPI.INT, 0, 0);

                // Передати D в задачу T7
                MPI.COMM_WORLD.Send(D, 0, N[0], MPI.INT, 6, 0);

                // Передати D в задачу T1
                MPI.COMM_WORLD.Send(D, 0, N[0], MPI.INT, 0, 0);

                // Обчислення 1: с8 = min(CH)
                int c8 = Data.getMinElement(receivedC5h_8h, 2 * H, 3 * H);

                int[] c6_7 = new int[1];

                // Прийняти с6_7 від задачі T7
                MPI.COMM_WORLD.Recv(c6_7, 0, 1, MPI.INT, 6, 0);

                // Обчислення 2: с7_8 = min(c6_7, c8)
                int c7_8 = Math.min(c6_7[0], c8);

                // Передати c7_8 в задачу T1
                MPI.COMM_WORLD.Send(new int[]{c7_8}, 0, 1, MPI.INT, 0, 0);

                int[] c = new int[1];

                // Прийняти с від задачі T1
                MPI.COMM_WORLD.Recv(c, 0, 1, MPI.INT, 0, 0);

                // Передати c в задачу T7
                MPI.COMM_WORLD.Send(c, 0, 1, MPI.INT, 6, 0);

                int[] Zh = Data.getPartOfVector(2 * H, 3 * H, receivedZ5h_8h);
                int[][] MRh = Data.transposeMatrix(Data.getPartOfMatrix(MR, 7 * H, 8 * H));
                int[][] MX_8 = Data.vectorToMatrix(receivedMX, 0, N[0], N[0]);

                // Обчислення 3: AH = c * ZH + D * (MX * MRH)
                int[] resultAh = Data.vectorSum(Data.scalarVectorMultiply(c[0], Zh), Data.vectorMatrixMultiply(D, Data.matrixMultiply(MX_8, MRh)));

                int[] resultAh7 = new int[H];
                // Прийняти AH7 від задачі T7
                MPI.COMM_WORLD.Recv(resultAh7, 0, H, MPI.INT, 6, 0);

                int[] Ah7_h8 = new int[resultAh.length + resultAh7.length];
                System.arraycopy(resultAh, 0, Ah7_h8, 0, resultAh.length);
                System.arraycopy(resultAh7, 0, Ah7_h8, resultAh.length, resultAh7.length);

                // Передати AH7, AH8 в задачу Т1
                MPI.COMM_WORLD.Send(Ah7_h8, 0, 2 * H, MPI.INT, 0, 0);

                break;
            }
        }
        MPI.Finalize();

        long endTime = System.currentTimeMillis();

        long calculatingTime = endTime - startTime;

        System.out.println("Thread" + (rank + 1) + " is finished! Time executing - " + calculatingTime + " ms");

    }
}