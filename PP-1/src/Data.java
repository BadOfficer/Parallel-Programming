import java.util.Arrays;
import java.util.Scanner;

public class Data {
    // Метод для заповнення матриць через клавіатуру
    public static double[][] fillMatrix(int matrixSize) {
        double[][] matrix = new double[matrixSize][matrixSize];
        Scanner scanner = new Scanner(System.in);

        System.out.print("Select fill method (\n\t1 - values from keyboard; \n\t2 - random values; \n\t3 - fill all elements only by single value \n): ");

        switch (scanner.nextInt()) {
            case 1:
                for (int i = 0; i < matrixSize; i++) {
                    for (int j = 0; j < matrixSize; j++) {
                        System.out.printf("Enter [%d][%d] element", i, j);
                        matrix[i][j] = scanner.nextDouble(); // Заповнюємо значенням з клавіатури
                    }
                }
                break;
            case 2:
                for (int i = 0; i < matrixSize; i++) {
                    for (int j = 0; j < matrixSize; j++) {
                        matrix[i][j] = Math.floor(Math.random() * 11); // Заповнюємо рандомним значенням
                    }
                }
                break;
            case 3:
                System.out.print("Enter value for matrix: ");
                int value = scanner.nextInt();

                for (int i = 0; i < matrixSize; i++) {
                    for (int j = 0; j < matrixSize; j++) {
                        matrix[i][j] = value; //Заповнюємо введеним значенням
                    }
                }
                break;
            default:
                System.out.println("Некоректне значення для типу вводу. Будь ласка, введіть 1, 2 або 3.");
                break;
        }

        return matrix;
    }

    //Метод для автоматичного заповнення матриць
    public static double[][] fillMatrixAuto(int matrixSize, double value) {
        double[][] matrix = new double[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                matrix[i][j] = value;
            }
        }

        return matrix;
    }

    // Метод для множення матриць
    public static double[][] multiplyMatrix(double[][] matrix1, double[][] matrix2) {
        double[][] resultMatrix = new double[matrix1.length][matrix2[0].length];

        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix2[0].length; j++) {
                for (int k = 0; k < matrix1[0].length; k++) {
                    resultMatrix[i][j] += matrix1[i][k] * matrix2[k][j]; // Обчислюємо кожне значення
                }
            }
        }

        return resultMatrix;
    }

    //Метод для заповнення векторів
    public static double[] fillVector(int vectorSize) {
        double[] vector = new double[vectorSize];
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nSelect fill method (\n\t1 - values from keyboard; \n\t2 - random values; \n\t3 - fill all elements only by single value \n): ");

        switch (scanner.nextInt()) {
            case 1:
                for (int i = 0; i < vectorSize; i++) {
                    System.out.printf("Enter value %d for vector: ", i);
                    vector[i] = scanner.nextDouble();
                }
                break;

            case 2:
                Arrays.fill(vector, Math.random());
                break;

            case 3:
                System.out.print("Enter value for vector: ");
                int value = scanner.nextInt();

                Arrays.fill(vector, value);
                break;

            default:
                System.out.println("Некоректне значення для типу вводу. Будь ласка, введіть 1, 2 або 3.");
        }


        return vector;
    }


    //Метод для автоматичного заповнення векторів
    public static double[] fillVectorAuto(int vectorSize, double value) {
        double[] vector = new double[vectorSize];

        Arrays.fill(vector, value);

        return vector;
    }

    //Метод для множення вектора на матрицю
    public static double[] vectorMatrixMultiply(double[] vector, double[][] matrix) {
        double[] result = new double[vector.length];

        Arrays.fill(result, 0);

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                result[i] += matrix[i][j] * vector[j];
            }
        }

        return result;
    }

    //Метод для множення скаляра на вектор
    public static double[] scalarVectorMultiply(double[] vector, double scalar) {
        double[] result = new double[vector.length];

        for (int i = 0; i < vector.length; i++) {
            result[i] = vector[i] * scalar;
        }

        return result;
    }

    //Метод для віднімання векторів
    public static double[] vectorsSubtract(double[] vector1, double[] vector2) {
        double[] result = new double[vector1.length];

        for (int i = 0; i < vector1.length; i++) {
            result[i] = vector1[i] - vector2[i];
        }

        return result;
    }

    //Метод для сортування рядків матриць
    public static double[][] sortMatrixRows(double[][] matrix) {
        for (double[] doubles : matrix) {
            Arrays.sort(doubles);
        }

        return matrix;
    }

    //Метод для додавання матриць
    public static double[][] addMatrices(double[][] matrix1, double[][] matrix2) {
        double[][] result = new double[matrix1.length][matrix2[0].length];

        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix1[0].length; j++) {
                result[i][j] = matrix1[i][j] + matrix2[i][j];
            }
        }

        return result;
    }

    //Метод для додавання векторів
    public static double[] vectorsSum(double[] vector1, double[] vector2) {
        double[] result = new double[vector1.length];

        for (int i = 0; i < vector1.length; i++) {
            result[i] = vector1[i] + vector2[i];
        }

        return result;
    }

    //Метод для виведення матриці
    public static void showMatrix(double[][] matrix) {
        for (double[] doubles : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(doubles[j] + " ");
            }
            System.out.println();
        }
    }

    //Метод для виведення вектора
    public static void showVector(double[] vector) {
        for (double v : vector) {
            System.out.print(" " + v);
        }
    }

    //Метод для запису скаляра
    public static double writeScalar() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextDouble();
    }
}
