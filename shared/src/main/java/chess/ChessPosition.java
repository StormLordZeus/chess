package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int m_row;
    private final int m_col;

    public ChessPosition(int row, int col) {
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
    public String toString()
    {
        return String.format("[%d,%d]", m_row, m_col);
    }
}
