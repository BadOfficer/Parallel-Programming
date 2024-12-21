import java.util.Arrays;

public class Data {
    public static void fillMatrix(int[][] matrix, int value) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = value;
            }
        }
    }

    public static void fillVector(int[] vector, int value) {
        Arrays.fill(vector, value);
    }

    public static int[] matrixToVector(int[][] matrix, int[] result, int n) {
        int index = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                result[index++] = matrix[i][j];
            }
        }

        return result;
    }

    public static int getMinElement(int[] vector, int start, int end) {
        int min = Integer.MAX_VALUE;

        for (int i = start; i < end; i++) {
            if (vector[i] < min) {
                min = vector[i];
            }
        }

        return min;
    }

    public static int[] getPartOfVector(int startPosition, int endPosition, int[] vector) {
        int[] newVector = new int[endPosition - startPosition];

        for (int i = 0; i < endPosition - startPosition; i++) {
            newVector[i] = vector[i + startPosition];
        }

        return newVector;
    }

    public static int[][] vectorToMatrix(int[] vector, int startIndex, int H, int N) {
        int[][] matrix = new int[H][N];
        for (int i = 0; i < H; i++) {
            System.arraycopy(vector, startIndex + i * N, matrix[i], 0, N);
        }

        return matrix;
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

    public static int[] scalarVectorMultiply(int scalar, int[] vector) {
        int[] newVector = new int[vector.length];
        for (int i = 0; i < vector.length; i++) {
            newVector[i] = vector[i] * scalar;
        }

        return newVector;
    }

    public static int[] vectorMatrixMultiply(int[] vector, int[][] matrix) {
        int cols = matrix[0].length;
        int[] result = new int[cols];

        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < vector.length; i++) {
                result[j] += vector[i] * matrix[i][j];
            }
        }

        return result;
    }

    public static int[] vectorSum(int[] vector1, int[] vector2) {
        int[] result = new int[vector1.length];

        for (int i = 0; i < vector1.length; i++) {
            result[i] = vector1[i] + vector2[i];
        }

        return result;
    }

    public static int[][] getPartOfMatrix(int[][] matrix, int startPosition, int endPosition) {
        int[][] result = new int[endPosition - startPosition][];

        for (int i = startPosition; i < endPosition; i++) {
            result[i - startPosition] = matrix[i];
        }

        return result;
    }
}
