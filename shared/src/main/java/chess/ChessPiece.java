package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor m_color;
    private final ChessPiece.PieceType m_type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type)
    {
        m_type = type;
        m_color = pieceColor;
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
        return m_color;
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
        List<ChessMove> myMoves = new ArrayList<>();
        ChessPiece piece1 = board.getPiece(myPosition);
        ChessGame.TeamColor myColor = piece1.getTeamColor();
        PieceType myType = piece1.getPieceType();

        // Bishop piece moves
        if (myType == PieceType.BISHOP)
        {
            int[][] bishopDirections = { {1,1},{1,-1},{-1,1},{-1,-1} };
            for (int[] bishopDirection : bishopDirections) {
                int y = myPosition.getRow() + bishopDirection[0];
                int x = myPosition.getColumn() + bishopDirection[1];
                while (x <= 8 && x >= 1 && y <= 8 && y >= 1) {
                    ChessPiece piece2 = board.getPiece(new ChessPosition(y, x));

                    if (piece2 != null) {
                        if (piece2.getTeamColor() != myColor) {
                            myMoves.add(new ChessMove(myPosition, new ChessPosition(y, x), null));
                        }
                        break;
                    } else {
                        myMoves.add(new ChessMove(myPosition, new ChessPosition(y, x), null));
                    }
                    y += bishopDirection[0];
                    x += bishopDirection[1];
                }
            }
        }

        // King piece moves
        if (myType == PieceType.KING)
        {
            int[][] kingDirections = { {1,1},{1,-1},{-1,1},{-1,-1},{1,0},{-1,0},{0,1},{0,-1} };
            for (int[] kingDirection : kingDirections) {
                int y = myPosition.getRow() + kingDirection[0];
                int x = myPosition.getColumn() + kingDirection[1];
                if (x <= 8 && x >= 1 && y <= 8 && y >= 1) {
                    ChessPiece piece2 = board.getPiece(new ChessPosition(y, x));

                    if (piece2 != null) {
                        if (piece2.getTeamColor() != myColor) {
                            myMoves.add(new ChessMove(myPosition, new ChessPosition(y, x), null));
                        }
                    } else {
                        myMoves.add(new ChessMove(myPosition, new ChessPosition(y, x), null));
                    }
                }
            }
            if (myColor == ChessGame.TeamColor.WHITE &&
                    (myPosition.getRow() == 1 && myPosition.getColumn() == 5))
            {
                ChessPiece leftRook = board.getPiece(new ChessPosition(1,1));
                if (leftRook != null &&
                        leftRook.getPieceType() == PieceType.ROOK &&
                        leftRook.getTeamColor() == ChessGame.TeamColor.WHITE)
                {
                    if (board.getPiece(new ChessPosition(1,2)) == null &&
                            board.getPiece(new ChessPosition(1,3)) == null &&
                            board.getPiece(new ChessPosition(1,4)) == null)
                    {
                        myMoves.add(new ChessMove(myPosition, new ChessPosition(1,3),null));
                    }
                }
                ChessPiece rightRook = board.getPiece(new ChessPosition(1,8));
                if (rightRook != null &&
                        rightRook.getPieceType() == PieceType.ROOK &&
                        rightRook.getTeamColor() == ChessGame.TeamColor.WHITE)
                {
                    if (board.getPiece(new ChessPosition(1,6)) == null &&
                            board.getPiece(new ChessPosition(1,7)) == null)
                    {
                        myMoves.add(new ChessMove(myPosition, new ChessPosition(1,7),null));
                    }
                }
            }
            else if (myColor == ChessGame.TeamColor.BLACK &&
                    (myPosition.getRow() == 8 && myPosition.getColumn() == 5))
            {
                ChessPiece leftRook = board.getPiece(new ChessPosition(8,1));
                if (leftRook != null &&
                        leftRook.getPieceType() == PieceType.ROOK &&
                        leftRook.getTeamColor() == ChessGame.TeamColor.BLACK)
                {
                    if (board.getPiece(new ChessPosition(8,2)) == null &&
                            board.getPiece(new ChessPosition(8,3)) == null &&
                            board.getPiece(new ChessPosition(8,4)) == null)
                    {
                        myMoves.add(new ChessMove(myPosition, new ChessPosition(8,3),null));
                    }
                }
                ChessPiece rightRook = board.getPiece(new ChessPosition(8,8));
                if (rightRook != null &&
                        rightRook.getPieceType() == PieceType.ROOK &&
                        rightRook.getTeamColor() == ChessGame.TeamColor.BLACK)
                {
                    if (board.getPiece(new ChessPosition(8,6)) == null &&
                            board.getPiece(new ChessPosition(8,7)) == null)
                    {
                        myMoves.add(new ChessMove(myPosition, new ChessPosition(8,7),null));
                    }
                }

            }
        }

        // Knight piece moves
        if (myType == PieceType.KNIGHT)
        {
            int[][] knightDirections = { {2,1},{2,-1},{-1,2},{1,2},{-2,1},{-2,-1},{-1,-2},{1,-2} };
            for (int[] knightDirection : knightDirections) {
                int y = myPosition.getRow() + knightDirection[0];
                int x = myPosition.getColumn() + knightDirection[1];
                if (x <= 8 && x >= 1 && y <= 8 && y >= 1) {
                    ChessPiece piece2 = board.getPiece(new ChessPosition(y, x));

                    if (piece2 != null) {
                        if (piece2.getTeamColor() != myColor) {
                            myMoves.add(new ChessMove(myPosition, new ChessPosition(y, x), null));
                        }
                    } else {
                        myMoves.add(new ChessMove(myPosition, new ChessPosition(y, x), null));
                    }
                }
            }
        }

        if (myType == PieceType.PAWN)
        {
            int[][] pawnDirections;
            if (myColor == ChessGame.TeamColor.WHITE)
            {
                pawnDirections = new int[][] { {1,0},{2,0},{1,1},{1,-1} };
            }
            else
            {
                pawnDirections = new int[][] { {-1,0},{-2,0},{-1,1},{-1,-1} };
            }

            boolean moveTwoBlocked = false;
            for (int i = 0; i < pawnDirections.length; i++)
            {
                if (i == 1 && (moveTwoBlocked ||
                        (myColor == ChessGame.TeamColor.WHITE && myPosition.getRow() != 2) ||
                        (myColor == ChessGame.TeamColor.BLACK && myPosition.getRow() != 7)))
                {
                    continue;
                }

                int y = myPosition.getRow() + pawnDirections[i][0];
                int x = myPosition.getColumn() + pawnDirections[i][1];
                if (x <= 8 && x >= 1 && y <= 8 && y >= 1)
                {
                    ChessPiece piece2 = board.getPiece(new ChessPosition(y,x));
                    if (i < 2)
                    {
                        if (piece2 != null)
                        {
                            if (i == 0)
                            {
                                moveTwoBlocked = true;
                            }
                            continue;
                        }
                    }
                    else
                    {
                        if (piece2 != null)
                        {
                            if (piece2.getTeamColor() == myColor)
                            {
                                continue;
                            }
                        }
                        else
                        {
                            if (myColor == ChessGame.TeamColor.WHITE && myPosition.getRow() == 5)
                            {
                                ChessPiece enemyPiece = board.getPiece(new ChessPosition(y-1,x));
                                if (enemyPiece == null)
                                {
                                    continue;
                                }
                                if (enemyPiece.getPieceType() != PieceType.PAWN)
                                {
                                    continue;
                                }
                            }
                            else if (myColor == ChessGame.TeamColor.BLACK && myPosition.getRow() == 4)
                            {
                                ChessPiece enemyPiece = board.getPiece(new ChessPosition(y+1,x));
                                if (enemyPiece == null)
                                {
                                    continue;
                                }
                                if (enemyPiece.getPieceType() != PieceType.PAWN)
                                {
                                    continue;
                                }
                            }
                            else
                            {
                                continue;
                            }
                        }
                    }


                    if ((myColor == ChessGame.TeamColor.WHITE && y == 8) ||
                            (myColor == ChessGame.TeamColor.BLACK && y == 1))
                    {
                        myMoves.add(new ChessMove(myPosition, new ChessPosition(y,x),PieceType.BISHOP));
                        myMoves.add(new ChessMove(myPosition, new ChessPosition(y,x),PieceType.KNIGHT));
                        myMoves.add(new ChessMove(myPosition, new ChessPosition(y,x),PieceType.QUEEN));
                        myMoves.add(new ChessMove(myPosition, new ChessPosition(y,x),PieceType.ROOK));
                    }
                    else
                    {
                        myMoves.add(new ChessMove(myPosition, new ChessPosition(y,x),null));
                    }
                }
            }
        }

        if (myType == PieceType.QUEEN)
        {
            int[][] queenDirections = { {1,1},{1,-1},{-1,1},{-1,-1},{1,0},{-1,0},{0,1},{0,-1} };
            for (int[] queenDirection : queenDirections) {
                int y = myPosition.getRow() + queenDirection[0];
                int x = myPosition.getColumn() + queenDirection[1];
                while (x <= 8 && x >= 1 && y <= 8 && y >= 1) {
                    ChessPiece piece2 = board.getPiece(new ChessPosition(y, x));

                    if (piece2 != null) {
                        if (piece2.getTeamColor() != myColor) {
                            myMoves.add(new ChessMove(myPosition, new ChessPosition(y, x), null));
                        }
                        break;
                    } else {
                        myMoves.add(new ChessMove(myPosition, new ChessPosition(y, x), null));
                    }
                    y += queenDirection[0];
                    x += queenDirection[1];
                }
            }
        }

        if (myType == PieceType.ROOK)
        {
            int[][] rookDirections = { {1,0},{-1,0},{0,1},{0,-1} };
            for (int[] rookDirection : rookDirections) {
                int y = myPosition.getRow() + rookDirection[0];
                int x = myPosition.getColumn() + rookDirection[1];
                while (x <= 8 && x >= 1 && y <= 8 && y >= 1) {
                    ChessPiece piece2 = board.getPiece(new ChessPosition(y, x));

                    if (piece2 != null) {
                        if (piece2.getTeamColor() != myColor) {
                            myMoves.add(new ChessMove(myPosition, new ChessPosition(y, x), null));
                        }
                        break;
                    } else {
                        myMoves.add(new ChessMove(myPosition, new ChessPosition(y, x), null));
                    }
                    y += rookDirection[0];
                    x += rookDirection[1];
                }
            }
        }

        return myMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return m_color == that.m_color && m_type == that.m_type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_color, m_type);
    }
}
