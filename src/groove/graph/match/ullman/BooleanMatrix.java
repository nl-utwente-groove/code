package groove.graph.match.ullman;

import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.Edge;

/**
 * @author J. ter Hove
 */
public class BooleanMatrix {
    public boolean[][] matrix;

    public BooleanMatrix(int rows, int cols) {
        matrix = new boolean[rows][cols];
    }

    private BooleanMatrix(boolean[][] bm) {
        matrix = new boolean[rows(bm)][cols(bm)];

        int rows = rows(matrix);
        int cols = cols(matrix);

        for( int i = 0; i < rows; i++ ) {
            for( int j = 0; j < cols; j++ ) {
                matrix[i][j] = bm[i][j];
            }
        }

    }

    public boolean get(int row, int col) {
        return matrix[row][col];
    }

    public void set(int row, int col, boolean value) {
        matrix[row][col] = value;
    }

    /**
     * @return String the string rep. of the matrix
     */
    public String toString() {
        int rows = rows(matrix);
        int cols = cols(matrix);

        String result = "";
        for( int i = 0; i < rows; i++ ) {

            for( int j = 0; j < cols; j++ ) {
                if(matrix[i][j])
                    result = result.concat(" 1 ");
                else
                    result = result.concat(" 0 ");
            }
            result = result.concat("\n");            
        }

        return result;
    }

    private int rows(boolean[][] bm) {
        return bm.length;
    }

    private int cols(boolean[][] bm) {
        return bm[0].length;
    }

    public BooleanMatrix copy() {
        return new BooleanMatrix(matrix);
    }

    public BooleanMatrix mul(BooleanMatrix rhs) {
        if( cols(matrix) != rows(rhs.matrix) ) {
            System.err.println("Wrong matrices for multiplication, rows(lhs) != cols(rhs)");
            System.exit(-1);
        }

        boolean[][] bm = new boolean[rows(matrix)][cols(rhs.matrix)];

        for( int i = 0; i < rows(matrix); i++ ) {
            for( int j = 0; j < cols(rhs.matrix); j++ ) {
                int c = 0;
                for( int k = 0; k < cols(matrix); k++ ) {
                    if( matrix[i][k] && rhs.get(k,j) )
                        c++;
                }
                if( c > 0 )
                    bm[i][j] = true;
            }
        }

        return new BooleanMatrix(bm);
    }

    public BooleanMatrix transpose() {
        boolean[][] bm = new boolean[cols(matrix)][rows(matrix)];

        for( int i = 0; i < rows(matrix); i++ ) {
            for( int j = 0; j < cols(matrix); j++ ) {
                bm[j][i] = matrix[i][j];
            }
        }

        return new BooleanMatrix(bm);
    }

    public boolean implies(BooleanMatrix rhs) {
        boolean result = true;

        for( int i = 0; i < rows(matrix); i++ ) {
            for( int j = 0; j < rows(matrix); j++ ) {
                if( !( !matrix[i][j] || rhs.matrix[i][j] ) ) {
                    return false; // we know enough
                }
            }
        }

        return result;
    }

    public int rows() {
        return rows(matrix);
    }

    public int cols() {
        return cols(matrix);
    }

    public BooleanMatrix extract(int[] h) {
        boolean[][] bm = new boolean[rows(matrix)][cols(matrix)];

        for( int i = 0; i < h.length; i++ ) {
            if( !matrix[i][h[i]] )
                return null;
            bm[i][h[i]] = true;
        }

        return new BooleanMatrix(bm);
    }
}