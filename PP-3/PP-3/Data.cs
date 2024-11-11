class Data {
    public static int[][] fillMatrix(int[][] matrix, int size, int value) {
        for (int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                matrix[i][j] = value;
            }
        }

        return matrix;
    }

    public static int[] fillVector(int[] vector, int size, int value) {
        for(int i = 0; i < size; i++) {
            vector[i] = value;
        }

        return vector;
    }

    public static int[][] GetPartOfMatrix(int[][] matrix, int startPosition, int endPosition) {
        int[][] result = new int[endPosition - startPosition][];

        for (int i = startPosition; i < endPosition; i++)
        {
            result[i - startPosition] = matrix[i];
        }

        return result;
    }

    public static int[] GetPartOfVector(int[] vector, int startPosition, int endPosition) {
        int[] result = new int[endPosition - startPosition];

        for(int i = 0; i < endPosition - startPosition; i++) {
            result[i] = vector[i + startPosition];
        }

        return result;
    }

    public static int GetMinValue(int[] vector) {
        int minValue = vector[0];

        for(int i = 0; i < vector.Length; i++) {
            minValue = Math.Min(minValue, vector[i]);
        }

        return minValue;
    }

    public static int[][] ScalarMatrixMultiply(int[][] matrix, int scalar) {
        int[][] resultMatrix = new int[matrix.Length][];
    
        for (int i = 0; i < matrix.Length; i++) {
            resultMatrix[i] = new int[matrix[i].Length];
            for (int j = 0; j < matrix[i].Length; j++) {
                resultMatrix[i][j] = matrix[i][j] * scalar;
            }
        }

        return resultMatrix;
    }

    public static int[][] MatrixMultiply(int[][] matrix1, int[][] matrix2) {
        int rowsA = matrix1.Length;
        int colsA = matrix1[0].Length;
        int rowsB = matrix2.Length;
        int colsB = matrix2[0].Length;

        if (colsA != rowsB)
        {
            throw new ArgumentException("Number of columns in the first matrix must be equal to the number of rows in the second matrix.");
        }

        int[][] resultMatrix = new int[rowsA][];
        for (int i = 0; i < rowsA; i++)
        {
            resultMatrix[i] = new int[colsB];
        }

        for (int i = 0; i < rowsA; i++)
        {
            for (int j = 0; j < colsB; j++)
            {
                resultMatrix[i][j] = 0;
                for (int k = 0; k < colsA; k++)
                {
                    resultMatrix[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }

        return resultMatrix;
    }

    public static int[][] MatrixSum(int[][] matrix1, int[][] matrix2) {
        int rows = matrix1.Length;
        int cols = matrix1[0].Length;

        int[][] resultMatrix = new int[rows][];
        for (int i = 0; i < rows; i++)
        {
            resultMatrix[i] = new int[cols];
        }

        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                resultMatrix[i][j] = matrix1[i][j] + matrix2[i][j];
            }
        }

        return resultMatrix;
    }

    public static void ShowMatrix(int size) {
        int[][] firstPart = Resources.MOH_1.Concat(Resources.MOH_2).ToArray();
        int[][] secondPart = Resources.MOH_3.Concat(Resources.MOH_4).ToArray();
        int[][] matrix = firstPart.Concat(secondPart).ToArray();

        if (size < 10) {
            Console.WriteLine("\nResult MO: ");
            for (int i = 0; i < matrix.Length; i++) {
                for(int j = 0; j < matrix[i].Length; j++) {
                    Console.Write(matrix[i][j] + " ");
                }
                Console.WriteLine();
            }
        }
    }

    public static int[][] CalculateFirstPart(int[][] matrixCH) {
        return MatrixMultiply(MatrixMultiply(matrixCH, Resources.MM), Resources.MB);
    }
}