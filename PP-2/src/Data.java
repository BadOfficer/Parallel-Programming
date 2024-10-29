import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

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

    public static int[] fillVectorByReverseInc(int n) {
        int[] vector = new int[n];

        for (int i = 0; i < n; i++) {
            vector[i] = (n + 1) - i;
        }

        return vector;
    }

    public static int[] getPartOfVector(int startPosition, int endPosition, int[] vector) {
        int[] newVector = new int[endPosition - startPosition];

        for (int i = 0; i < endPosition - startPosition; i++) {
            newVector[i] = vector[i + startPosition];
        }

        return newVector;
    }

    public static int getMinVectorValue(int[] vector) {
        int minValue = vector[0];
        for (int i = 1; i < vector.length; i++) {
            if (vector[i] < minValue) {
                minValue = vector[i];
            }
        }

        return minValue;
    }

    public static int[] scalarVectorMultiply(int scalar, int[] vector) {
        int[] newVector = new int[vector.length];
        for (int i = 0; i < vector.length; i++) {
            newVector[i] = vector[i] * scalar;
        }

        return newVector;
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

        // Perform element-wise addition
        for (int i = 0; i < vector1.length; i++) {
            result[i] = vector1[i] + vector2[i];
        }

        return result;
    }

    public static int[] sortVector(int[] vector) {
        int[] sortedVector = vector.clone();

        Arrays.sort(sortedVector);

        return sortedVector;
    }

    public static int[] concatAndSort(int[] vector1, int[] vector2) {
        int[] result = new int[vector1.length + vector2.length];

        System.arraycopy(vector1, 0, result, 0, vector2.length);
        System.arraycopy(vector2, 0, result, vector1.length, vector2.length);

        Arrays.sort(result);

        return result;
    }


}
