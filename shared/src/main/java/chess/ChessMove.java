package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove
{

    private final ChessPosition mStartPos;
    private final ChessPosition mEndPos;
    private final ChessPiece.PieceType mPromotionType;



    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece)
    {
        mStartPos = startPosition;
        mEndPos = endPosition;
        mPromotionType = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition()
    {
        return mStartPos;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition()
    {
        return mEndPos;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece()
    {
        return mPromotionType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) { return false; }
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(mStartPos, chessMove.mStartPos) &&
                Objects.equals(mEndPos, chessMove.mEndPos) &&
                mPromotionType == chessMove.mPromotionType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mStartPos, mEndPos, mPromotionType);
    }

    @Override
    public String toString() {
        return "[" + mStartPos.getRow() + "," + mStartPos.getColumn() + "]"
                + "[" + mEndPos.getRow() + "," + mEndPos.getColumn() + "]";
    }
}
