package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int m_row;
    private final int m_col;

    public ChessPosition(int row, int col)
    {
        m_row = row;
        m_col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow()
    {
        return m_row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn()
    {
        return m_col;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChessPosition that = (ChessPosition) o;
        return m_row == that.m_row && m_col == that.m_col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_row, m_col);
    }

    @Override
    public String toString() {
        return "ChessPosition{" + m_row +
                ", " + m_col +
                '}';
    }
}
