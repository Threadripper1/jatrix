package com.jatrix.core;

import com.jatrix.conversion.MatrixConversion;
import com.jatrix.exceptions.MatrixSingularException;
import com.jatrix.exceptions.MatrixSizeException;

import java.util.Random;

/**
 * Matrices class is intended for performing basic matrix operations such as
 * adding, subtraction, multiplication, searching,  etc.
 */

public class Matrices {

    private Matrices() {
    }


    private static boolean isNotEqualsDimensions(Matrix m1, Matrix m2) {
        return m1.getRowDimension() != m2.getRowDimension() || m1.getColumnDimension() != m2.getColumnDimension();
    }


    /**
     * Adds two matrices.
     * @param m1 the first Matrix operand.
     * @param m2 the second Matrix operand.
     * @return newMatrix Matrix object of summation of <code>m1</code> and <code>m2</code>
     */
    public static Matrix add(Matrix m1, Matrix m2) {
        if (isNotEqualsDimensions(m1, m2))
            throw new MatrixSizeException("Dimensions of matrices must be equaled");

        int rows = m1.getRowDimension();
        int columns = m2.getColumnDimension();
        Matrix newMatrix = new Matrix(rows, columns);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                newMatrix.set(i, j, m1.get(i, j) + m2.get(i, j));
            }
        }

        return newMatrix;
    }


    /**
     * Subtracts two matrices.
     * @param m1 the first Matrix operand.
     * @param m2 the second the second Matrix operand.
     * @return newMatrix difference of <code>m1</code> and <code>m2</code> {@link Matrix} objects.
     */
    public static Matrix sub(Matrix m1, Matrix m2) {
        if (isNotEqualsDimensions(m1, m2))
            throw new MatrixSizeException("Dimensions of matrices must be equaled");

        int rows = m1.getRowDimension();
        int columns = m2.getColumnDimension();
        Matrix newMatrix = new Matrix(rows, columns);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                newMatrix.set(i, j, m1.get(i, j) - m2.get(i, j));
            }
        }

        return newMatrix;
    }

    /**
     * Fills the matrix with random numbers.
     * @param m Matrix object to fill randomly.
     */
    public static void fillRandom(Matrix m) {
        Random random = new Random();
        for (int i = 0; i < m.getRowDimension(); i++) {
            for (int j = 0; j < m.getColumnDimension(); j++) {
                m.set(i, j, random.nextDouble());
            }
        }
    }

    /**
     * Multiplies two matrices. It can use a Strassen's method to multiply two matrices,
     * when matrices have dimensions, that greater than 32.
     * @param m1 the first Matrix operand.
     * @param m2 the second Matrix operand.
     * @return newMatrix result of <code>m1</code> and <code>m2</code> product.
     * @throws MatrixSizeException if number of columns of the first matrix is not equaled to number of rows of the
     * second matrix.
     * @see StrassenProduct
     */
    public static Matrix mul(Matrix m1, Matrix m2) {
        if (m1.getColumnDimension() != m2.getRowDimension())
            throw new MatrixSizeException("Invalid multiplication operation. Number of columns of the first matrix " +
                    "must be equaled to number of rows of the second one. Expected: " + m1.getColumnDimension() + ", but founded: "
                    + m2.getRowDimension());


        if (m1.isSquare() && m1.isPair() && m2.isSquare() && m2.isPair() && m1.getRowDimension() > 32) {
            return StrassenProduct.mul(m1, m2);
        }

        int rows = m1.getRowDimension();
        int cols = m2.getColumnDimension();
        int sum = m1.getColumnDimension();
        Matrix newMatrix = new Matrix(rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double s = 0;
                for (int k = 0; k < sum; k++) {
                    s += m1.get(i, k) * m2.get(k, j);
                }
                newMatrix.set(i, j, s);
            }
        }

        return newMatrix;
    }

    /**
     * Multiplies a Matrix object by a double constant.
     * @param c arbitrary number, constant.
     * @param matrix Matrix object to multiply by a constant.
     * @return newMatrix result of multiplication <code>matrix</code> by <code>c</code>.
     */
    public static Matrix mul(double c, Matrix matrix) {
        int rows = matrix.getRowDimension();
        int cols = matrix.getColumnDimension();
        Matrix newMatrix = new Matrix(rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                newMatrix.set(i, j, c * matrix.get(i, j));
            }
        }

        return newMatrix;
    }

    /**
     * Transpose a specified matrix. It's obtained by changing rows to columns and columns to rows.
     * @param matrix matrix to transpose.
     * @return transposedMatrix transposed matrix.
     */
    public static Matrix transpose(Matrix matrix) {
        int rows = matrix.getRowDimension();
        int cols = matrix.getColumnDimension();
        Matrix transposedMatrix = new Matrix(cols, rows);

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                transposedMatrix.set(i, j, matrix.get(j, i));
            }
        }

        return transposedMatrix;
    }


    /**
     * Inverse a matrix, using Gauss Elimination.
     * @param matrix Matrix object for which you want to get the inversion.
     * @return invertible matrix
     * @throws MatrixSizeException if a matrix is non-square.
     */
    public static Matrix inverse(Matrix matrix) {
        if (!matrix.isSquare())
            throw new MatrixSizeException("Matrix must be square. Founded: " +
                    matrix.getRowDimension() + " x " + matrix.getColumnDimension());

        int size = matrix.getRowDimension();
        Matrix A = matrix.clone();
        Matrix B = new Matrix(size).identity();

        for (int i = 0; i < size - 1; i++) {
            if (A.get(i, i) == 0) {
                for (int j = i+1; j < size; j++) {
                    if (A.get(j, i) == 0) {
                        if (j == size-1) {
                            throw new MatrixSingularException("Matrix is singular");
                        }
                    }
                    else {
                        MatrixConversion.swapRows(A, i, j);
                        MatrixConversion.swapRows(B, i, j);
                        break;
                    }
                }
            }

            for (int k = i+1; k < size; k++) {
                double div = A.get(k,i)/A.get(i, i);
                for (int j = 0; j < size; j++) {
                    A.set(k, j, A.get(k,j) - A.get(i,j)*div);
                    B.set(k, j, B.get(k,j) - B.get(i,j)*div);
                }
            }
        }

        for (int i = size - 1; i > 0; i--) {
            if (A.get(i, i) == 0) {
                for (int j = i+1; j < size; j++) {
                    if (A.get(j, i) == 0) {
                        if (j == size-1) {
                            throw new MatrixSingularException("Matrix is singular");
                        }
                    }
                    else {
                        MatrixConversion.swapRows(A, i, j);
                        MatrixConversion.swapRows(B, i, j);
                        break;
                    }
                }
            }

            for (int k = i-1; k >= 0; k--) {
                double div = A.get(k,i)/A.get(i, i);
                for (int j = size - 1; j >= 0; j--) {
                    A.set(k, j, A.get(k,j) - A.get(i,j)*div);
                    B.set(k, j, B.get(k,j) - B.get(i,j)*div);
                }
            }
        }

        for (int i = 0; i < size; i++) {
            double d = A.get(i, i);
            if (d == 0)
                throw new MatrixSingularException("Matrix is singular");
            if (d == 1) continue;
            for (int j = 0; j < size; j++) {
                B.set(i, j, B.get(i, j)/d);
            }
        }

        return B;
    }
}