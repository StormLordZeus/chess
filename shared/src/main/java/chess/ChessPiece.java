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

    private final ChessGame.TeamColor mColor;
    private final ChessPiece.PieceType mType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type)
    {
        mType = type;
        mColor = pieceColor;
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
        return mColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType()
    {
        return mType;
    }

    public void addCastleMove(ChessBoard board, Collection<ChessMove> moves, ChessGame.TeamColor color,
                              ChessPosition position)
    {
        if (color == ChessGame.TeamColor.WHITE &&
                (position.getRow() == 1 && position.getColumn() == 5))
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
                    moves.add(new ChessMove(position, new ChessPosition(1,3),null));
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
                    moves.add(new ChessMove(position, new ChessPosition(1,7),null));
                }
            }
        }
        else if (color == ChessGame.TeamColor.BLACK &&
                (position.getRow() == 8 && position.getColumn() == 5))
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
                    moves.add(new ChessMove(position, new ChessPosition(8,3),null));
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
                    moves.add(new ChessMove(position, new ChessPosition(8,7),null));
                }
            }

        }
    }

    public void addPawnMoves(ChessBoard board, Collection<ChessMove> moves,
                             ChessGame.TeamColor color,ChessPosition position)
    {
        int[][] pawnDirections;
        if (color == ChessGame.TeamColor.WHITE)
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
                    (color == ChessGame.TeamColor.WHITE && position.getRow() != 2) ||
                    (color == ChessGame.TeamColor.BLACK && position.getRow() != 7)))
            {
                continue;
            }

            int y = position.getRow() + pawnDirections[i][0];
            int x = position.getColumn() + pawnDirections[i][1];
            if (x <= 8 && x >= 1 && y <= 8 && y >= 1)
            {
                ChessPiece piece2 = board.getPiece(new ChessPosition(y,x));
                if (i < 2)
                {
                    if (piece2 != null && i == 0)
                    {
                        moveTwoBlocked = true;
                        continue;
                    }
                    else if (piece2 != null)
                    {
                        continue;
                    }
                }
                else if (piece2 != null && piece2.getTeamColor() == color)
                {
                    continue;
                }
                else if (piece2 == null && color == ChessGame.TeamColor.WHITE && position.getRow() == 5)
                {
                    ChessPiece enemyPiece = board.getPiece(new ChessPosition(y - 1, x));
                    if (enemyPiece == null)
                    {
                        continue;
                    }
                    if (enemyPiece.getPieceType() != PieceType.PAWN)
                    {
                        continue;
                    }
                }
                else if (piece2 == null && color == ChessGame.TeamColor.BLACK && position.getRow() == 4)
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
                else if (piece2 == null)
                {
                    continue;
                }


                if ((color == ChessGame.TeamColor.WHITE && y == 8) ||
                        (color == ChessGame.TeamColor.BLACK && y == 1))
                {
                    moves.add(new ChessMove(position, new ChessPosition(y,x),PieceType.BISHOP));
                    moves.add(new ChessMove(position, new ChessPosition(y,x),PieceType.KNIGHT));
                    moves.add(new ChessMove(position, new ChessPosition(y,x),PieceType.QUEEN));
                    moves.add(new ChessMove(position, new ChessPosition(y,x),PieceType.ROOK));
                }
                else
                {
                    moves.add(new ChessMove(position, new ChessPosition(y,x),null));
                }
            }
        }
    }

    public void addMove(int[][] directions, ChessPosition position, ChessBoard board,
                                ChessGame.TeamColor color, Collection<ChessMove> moves, boolean slide)
    {
        for (int[] direction : directions) {
            int y = position.getRow() + direction[0];
            int x = position.getColumn() + direction[1];
            while (x <= 8 && x >= 1 && y <= 8 && y >= 1) {
                ChessPiece piece2 = board.getPiece(new ChessPosition(y, x));

                if (piece2 != null) {
                    if (piece2.getTeamColor() != color) {
                        moves.add(new ChessMove(position, new ChessPosition(y, x), null));
                    }
                    break;
                } else {
                    moves.add(new ChessMove(position, new ChessPosition(y, x), null));
                }
                if (!slide)
                {
                    break;
                }
                y += direction[0];
                x += direction[1];
            }
        }
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
        // Local variables to be used throughout
        List<ChessMove> myMoves = new ArrayList<>();
        ChessPiece piece1 = board.getPiece(myPosition);
        ChessGame.TeamColor myColor = piece1.getTeamColor();
        PieceType myType = piece1.getPieceType();

        // Bishop piece moves
        if (myType == PieceType.BISHOP)
        {
            int[][] bishopDirections = { {1,1},{1,-1},{-1,1},{-1,-1} };
            addMove(bishopDirections, myPosition, board, myColor, myMoves, true);
        }

        if (myType == PieceType.KING)
        {
            int[][] kingDirections = { {1,1},{1,-1},{-1,1},{-1,-1},{1,0},{-1,0},{0,1},{0,-1} };
            addMove(kingDirections, myPosition, board, myColor, myMoves, false);
            addCastleMove(board, myMoves, myColor, myPosition);
        }

        // Knight piece moves
        if (myType == PieceType.KNIGHT)
        {
            int[][] knightDirections = { {2,1},{2,-1},{-1,2},{1,2},{-2,1},{-2,-1},{-1,-2},{1,-2} };
            addMove(knightDirections, myPosition, board, myColor, myMoves, false);
        }

        // Pawn piece moves
        if (myType == PieceType.PAWN)
        {
            addPawnMoves(board, myMoves, myColor, myPosition);
        }

        if (myType == PieceType.QUEEN) {
            int[][] queenDirections = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            addMove(queenDirections, myPosition, board, myColor, myMoves, true);
        }

        if (myType == PieceType.ROOK) {
            int[][] rookDirections = { {1,0},{-1,0},{0,1},{0,-1} };
            addMove(rookDirections, myPosition, board, myColor, myMoves, true);
        }

        return myMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) { return false; }
        ChessPiece that = (ChessPiece) o;
        return mColor == that.mColor && mType == that.mType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mColor, mType);
    }

    @Override
    public String toString()
    {
        String pieceString = "";
        if (mColor == ChessGame.TeamColor.WHITE) {
            switch (mType) {
                case KNIGHT -> {
                    pieceString = " ♘ ";
                }
                case BISHOP -> {
                    pieceString = " ♗ ";
                }
                case QUEEN -> {
                    pieceString = " ♕ ";
                }
                case ROOK -> {
                    pieceString = " ♖ ";
                }
                case KING -> {
                    pieceString = " ♔ ";
                }
                case PAWN -> {
                    pieceString = " ♙ ";
                }
            }
        }
        else if (mColor == ChessGame.TeamColor.BLACK) {
            switch (mType) {
                case KNIGHT -> {
                    pieceString = " ♞ ";
                }
                case BISHOP -> {
                    pieceString = " ♝ ";
                }
                case QUEEN -> {
                    pieceString = " ♛ ";
                }
                case ROOK -> {
                    pieceString = " ♜ ";
                }
                case KING -> {
                    pieceString = " ♚ ";
                }
                case PAWN -> {
                    pieceString = " ♟ ";
                }
            }
        }
        return pieceString;
    }

}

