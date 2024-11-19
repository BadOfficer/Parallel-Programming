import java.util.Arrays;

public class Data {
    public static int[][] fillMatrix(int n, int value) {
        int[][] matrix = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = value;
            }
        }

        return matrix;
    }

    public static int[] fillVector(int n, int value) {
        int[] vector = new int[n];

        Arrays.fill(vector, value);

        return vector;
    }

    public static int[] getPartOfVector(int startPosition, int endPosition, int[] vector) {
        int[] newVector = new int[endPosition - startPosition];

        for (int i = 0; i < endPosition - startPosition; i++) {
            newVector[i] = vector[i + startPosition];
        }

        return newVector;
    }

    public static int getMaxVectorValue(int[] vector) {
        int maxValue = vector[0];
        for (int i = 1; i < vector.length; i++) {
            if (vector[i] > maxValue) {
                maxValue = vector[i];
            }
        }

        return maxValue;
    }

    public static int[][] matrixMultiply(int[][] matrix1, int[][] matrix2) {
        int[][] newMatrix = new int[matrix1.length][matrix2[0].length];
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix2[0].length; j++) {
                for (int k = 0; k < matrix1[0].length; k++) {
                    newMatrix[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }

        return newMatrix;
    }

    public static int[][] getPartOfMatrix(int[][] matrix, int startPosition, int endPosition) {
        int[][] result = new int[endPosition - startPosition][];

        for (int i = startPosition; i < endPosition; i++) {
            result[i - startPosition] = matrix[i];
        }

        return result;
    }

    public static int[][] scalarMatrixMultiply(int[][] matrix, int scalar) {
        int[][] newMatrix = new int[matrix[0].length][matrix.length];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                newMatrix[j][i] = matrix[i][j] * scalar;
            }
        }

        return newMatrix;
    }

    public static int[][] matricesSum(int[][] matrix1, int[][] matrix2) {
        if (matrix1.length != matrix2.length || matrix1[0].length != matrix2[0].length) {
            throw new IllegalArgumentException("Matrices must have the same dimensions");
        }

        int rows = matrix1.length;
        int cols = matrix1[0].length;
        int[][] sumMatrix = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sumMatrix[i][j] = matrix1[i][j] + matrix2[i][j];
            }
        }

        return sumMatrix;
    }

    public static int[][] transposeMatrix(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        int[][] result = new int[cols][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[j][i] = matrix[i][j];
            }
        }

        return result;
    }

    public static int[][] calcMatrixAH(int[][] matrixCH, int d, int m, int p, int[][] matrixXH, int[][] matrixMH, int[][] matrixD) {
        int[][] firstPart = Data.scalarMatrixMultiply(Data.matrixMultiply(matrixCH,matrixD), d);
        int[][] secondPart = Data.scalarMatrixMultiply(Data.scalarMatrixMultiply(Data.matricesSum(matrixXH, matrixMH), m), p);

        return Data.matricesSum(transposeMatrix(firstPart), secondPart);
    }

    public static void showResult(int n, int[][] matrix1, int[][] matrix2, int[][] matrix3, int[][] matrix4) {
        int cols = matrix1[0].length;
        int rows = matrix1.length + matrix2.length + matrix3.length + matrix4.length;

        int[][] matrix = new int[rows][cols];

        int rowIndex = 0;

        for (int i = 0; i < matrix1.length; i++) {
            System.arraycopy(matrix1[i], 0, matrix[rowIndex++], 0, cols);
        }

        // Copy matrix2
        for (int i = 0; i < matrix2.length; i++) {
            System.arraycopy(matrix2[i], 0, matrix[rowIndex++], 0, cols);
        }

        // Copy matrix3
        for (int i = 0; i < matrix3.length; i++) {
            System.arraycopy(matrix3[i], 0, matrix[rowIndex++], 0, cols);
        }

        // Copy matrix4
        for (int i = 0; i < matrix4.length; i++) {
            System.arraycopy(matrix4[i], 0, matrix[rowIndex++], 0, cols);
        }

        if (n < 10) {
            System.out.println("\nResult matrix A: ");
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[0].length; j++) {
                    System.out.print(matrix[i][j] + " ");
                }
                System.out.println();
            }
        }
    }
}
