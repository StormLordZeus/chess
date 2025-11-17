package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int mRow;
    private final int mCol;

    public ChessPosition(int row, int col)
    {
        mRow = row;
        mCol = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow()
    {
        return mRow;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn()
    {
        return mCol;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) { return false; }
        ChessPosition that = (ChessPosition) o;
        return mRow == that.mRow && mCol == that.mCol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mRow, mCol);
    }

    @Override
    public String toString() {
        return "{" + mRow +
                ", " + mCol +
                '}';
    }
}
