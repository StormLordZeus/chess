package chess;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final PieceType m_type;
    private final chess.ChessGame.TeamColor m_pieceColor;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type)
    {
        m_type = type;
        m_pieceColor = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }


    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor()
    {
        return m_pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType()
    {
        return m_type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition)
    {
        List<ChessMove> myMoves = new java.util.ArrayList<>(List.of());
        ChessPiece piece1 = board.getPiece(myPosition);
        ChessGame.TeamColor color = piece1.getTeamColor();
        if (piece1.getPieceType() == PieceType.BISHOP)
        {
            int y = myPosition.getRow();
            int x = myPosition.getColumn();
            while(x > 1 && x < 8 && y > 1 && y < 8)
            {
                x++;
                y++;
                ChessPiece piece2 = board.getPiece(new ChessPosition(y,x));
                if(piece2 != null)
                {
                    if (color != piece2.getTeamColor())
                    {
                        myMoves.add(new ChessMove(myPosition, new ChessPosition(y, x), null));
                    }
                    break;
                }
                myMoves.add(new ChessMove(myPosition, new ChessPosition(y, x), null));
            }

            y = myPosition.getRow();
            x = myPosition.getColumn();
            while(x > 1 && x < 8 && y > 1 && y < 8)
            {
                x--;
                y--;
                ChessPiece piece2 = board.getPiece(new ChessPosition(y,x));
                if(piece2 != null)
                {
                    if (color != piece2.getTeamColor())
                    {
                        myMoves.add(new ChessMove(myPosition, new ChessPosition(y, x), null));
                    }
                    break;
                }
                myMoves.add(new ChessMove(myPosition, new ChessPosition(y, x), null));
            }

            y = myPosition.getRow();
            x = myPosition.getColumn();
            while(x > 1 && x < 8 && y > 1 && y < 8)
            {
                x++;
                y--;
                ChessPiece piece2 = board.getPiece(new ChessPosition(y,x));
                if(piece2 != null)
                {
                    if (color != piece2.getTeamColor())
                    {
                        myMoves.add(new ChessMove(myPosition, new ChessPosition(y, x), null));
                    }
                    break;
                }
                myMoves.add(new ChessMove(myPosition, new ChessPosition(y, x), null));
            }

            y = myPosition.getRow();
            x = myPosition.getColumn();
            while(x > 1 && x < 8 && y > 1 && y < 8)
            {
                x--;
                y++;
                ChessPiece piece2 = board.getPiece(new ChessPosition(y,x));
                if(piece2 != null)
                {
                    if (color != piece2.getTeamColor())
                    {
                        myMoves.add(new ChessMove(myPosition, new ChessPosition(y, x), null));
                    }
                    break;
                }
                myMoves.add(new ChessMove(myPosition, new ChessPosition(y, x), null));
            }

            return myMoves;
        }
        return myMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return m_type == that.m_type && m_pieceColor == that.m_pieceColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_type, m_pieceColor);
    }
}
