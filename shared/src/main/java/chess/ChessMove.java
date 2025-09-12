package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition m_startPos;
    private final ChessPosition m_endPos;
    private final ChessPiece.PieceType m_promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece)
    {
        m_startPos = startPosition;
        m_endPos = endPosition;
        m_promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition()
    {
        return m_startPos;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition()
    {
        return m_endPos;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece()
    {
        return m_promotionPiece;
    }

    @Override
    public String toString()
    {
        return String.format("%s%s", m_startPos, m_endPos);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(m_startPos, chessMove.m_startPos) && Objects.equals(m_endPos, chessMove.m_endPos) && m_promotionPiece == chessMove.m_promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_startPos, m_endPos, m_promotionPiece);
    }
}
