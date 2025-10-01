package chess;

import javax.swing.*;
import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor mTeamTurn;
    private ChessBoard mBoard;
    private boolean mBlackCastleLeft;
    private boolean mBlackCastleRight;
    private boolean mWhiteCastleLeft;
    private boolean mWhiteCastleRight;
    private ChessPosition mEnPassantPos;

    public ChessGame() {
        mTeamTurn = TeamColor.WHITE;
        mBoard = new ChessBoard();
        mBoard.resetBoard();
        mBlackCastleLeft = true;
        mBlackCastleRight = true;
        mWhiteCastleLeft = true;
        mWhiteCastleRight = true;
        mEnPassantPos = null;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn()
    {
        return mTeamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team)
    {
        mTeamTurn = team;
    }

    public boolean getBlackCastleLeft() {
        return mBlackCastleLeft;
    }

    public void setBlackCastleLeft(boolean blackCastleLeft) {
        mBlackCastleLeft = blackCastleLeft;
    }

    public boolean getBlackCastleRight() {
        return mBlackCastleRight;
    }

    public void setBlackCastleRight(boolean blackCastleRight) {
        mBlackCastleRight = blackCastleRight;
    }

    public boolean getWhiteCastleLeft() {
        return mWhiteCastleLeft;
    }

    public void setWhiteCastleLeft(boolean whiteCastleLeft) {
        this.mWhiteCastleLeft = whiteCastleLeft;
    }

    public boolean getWhiteCastleRight() {
        return mWhiteCastleRight;
    }

    public void setWhiteCastleRight(boolean whiteCastleRight) {
        this.mWhiteCastleRight = whiteCastleRight;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public void removeInvalidCastle(ChessPosition castlePos, ChessPosition startPosition, Collection<ChessMove> moves,
                                    ChessPiece piece, int x, int y, boolean castleVar, TeamColor color)
    {
        ChessMove castle = new ChessMove(startPosition, castlePos, null);
        if (moves.contains(castle))
        {
            if (!castleVar || isInCheck(color))
            {
                moves.remove(castle);
            }
            else
            {
                if(testMove(startPosition, new ChessPosition(y,x-1), piece, null))
                {
                    moves.remove(castle);
                }
            }
        }
    }

    public boolean testMove(ChessPosition startPos, ChessPosition endPos, ChessPiece piece1, ChessPiece piece2)
    {
        boolean inCheck = false;
        mBoard.addPiece(endPos, piece1);
        mBoard.addPiece(startPos, null);
        if(isInCheck(piece1.getTeamColor()))
        {
            inCheck = true;
        }
        mBoard.addPiece(endPos, piece2);
        mBoard.addPiece(startPos, piece1);
        return inCheck;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition)
    {
        ChessPiece myPiece = mBoard.getPiece(startPosition);
        if (myPiece == null) {
            return null;
        }
        Collection<ChessMove> myMoves = myPiece.pieceMoves(mBoard, startPosition);
        if (myPiece.getPieceType() == ChessPiece.PieceType.KING)
        {
            System.out.println(myMoves);
            int x = startPosition.getColumn();
            int y = startPosition.getRow();
            ChessPosition castleLeft = new ChessPosition(y, x-2);
            ChessPosition castleRight = new ChessPosition(y, x+2);
            if (myPiece.getTeamColor() == TeamColor.WHITE)
            {
                removeInvalidCastle(castleLeft, startPosition, myMoves, myPiece, x, y, mWhiteCastleLeft, TeamColor.WHITE);
                removeInvalidCastle(castleRight, startPosition, myMoves, myPiece, x, y, mWhiteCastleRight, TeamColor.WHITE);
            }
            else
            {
                removeInvalidCastle(castleLeft, startPosition, myMoves, myPiece, x, y, mBlackCastleLeft, TeamColor.BLACK);
                removeInvalidCastle(castleRight, startPosition, myMoves, myPiece, x, y, mBlackCastleRight, TeamColor.BLACK);
            }
            System.out.println(myMoves);
        }
        else if (myPiece.getPieceType() == ChessPiece.PieceType.PAWN)
        {
            int[][] pawnDirections;
            if (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE)
            {
                pawnDirections = new int[][] { {1,1},{1,-1} };
            }
            else
            {
                pawnDirections = new int[][] { {-1,1},{-1,-1} };
            }
            for (int[] pawnDirection : pawnDirections) {
                int y = startPosition.getRow() + pawnDirection[0];
                int x = startPosition.getColumn() + pawnDirection[1];
                if (x <= 8 && x >= 1 && y <= 8 && y >= 1) {

                    ChessPosition enPassantPos = new ChessPosition(y - pawnDirection[0], x);
                    ChessPiece enemyPiece = mBoard.getPiece(new ChessPosition(y, x));
                    ChessMove enPassantCapture = new ChessMove(startPosition, new ChessPosition(y, x), null);

                    if ((enemyPiece == null || enemyPiece.getTeamColor() == myPiece.getTeamColor()) &&
                            myMoves.contains(enPassantCapture) &&
                            !enPassantPos.equals(mEnPassantPos)) {
                        myMoves.remove(enPassantCapture);
                    }
                }
            }
        }

        Iterator<ChessMove> movesIterator = myMoves.iterator();
        while (movesIterator.hasNext())
        {
            ChessMove move = movesIterator.next();

            ChessPosition startPos = move.getStartPosition();
            ChessPosition endPos = move.getEndPosition();
            ChessPiece enemyPiece = mBoard.getPiece(endPos);
            if(testMove(startPos, endPos, myPiece, enemyPiece))
            {
                movesIterator.remove();
            }
        }

        return myMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        ChessPiece myPiece = mBoard.getPiece(startPos);
        if (myPiece == null) {
            throw new InvalidMoveException();
        }
        TeamColor myColor = myPiece.getTeamColor();
        if (!this.validMoves(startPos).contains(move) || !(myColor == mTeamTurn))
        {
            throw new InvalidMoveException();
        }
        if (myPiece.getPieceType() == ChessPiece.PieceType.KING) {
            mEnPassantPos = null;

            int startCol = startPos.getColumn();
            int endCol = endPos.getColumn();
            if (Math.abs(startCol - endCol) == 2) {
                if (endCol > startCol) {
                    if (mTeamTurn == TeamColor.WHITE) {
                        mBoard.addPiece(new ChessPosition(1,6), new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.ROOK));
                        mBoard.addPiece(new ChessPosition(1,8), null);
                    }
                    else {
                        mBoard.addPiece(new ChessPosition(8,6), new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.ROOK));
                        mBoard.addPiece(new ChessPosition(8,8), null);
                    }
                }
                else {
                    if (mTeamTurn == TeamColor.WHITE) {
                        mBoard.addPiece(new ChessPosition(1,4), new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.ROOK));
                        mBoard.addPiece(new ChessPosition(1,1), null);
                    }
                    else {
                        mBoard.addPiece(new ChessPosition(8,4), new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.ROOK));
                        mBoard.addPiece(new ChessPosition(8,1), null);
                    }
                }
            }
            if (mTeamTurn == TeamColor.WHITE) {
                mWhiteCastleLeft = false;
                mWhiteCastleRight = false;
            }
            else {
                mBlackCastleLeft = false;
                mBlackCastleRight = false;
            }
        }
        else if (myPiece.getPieceType() == ChessPiece.PieceType.ROOK) {
            mEnPassantPos = null;

            if (startPos.getRow() == 1 && startPos.getColumn() == 1) {
                mWhiteCastleLeft = false;
            }
            else if (startPos.getRow() == 1 && startPos.getColumn() == 8) {
                mWhiteCastleRight = false;
            }
            else if (startPos.getRow() == 8 && startPos.getColumn() == 1) {
                mBlackCastleLeft = false;
            }
            else if (startPos.getRow() == 8 && startPos.getColumn() == 8) {
                mBlackCastleRight = false;
            }
        }
        else if (myPiece.getPieceType() == ChessPiece.PieceType.PAWN && Math.abs(startPos.getRow() - endPos.getRow()) == 2)
        {
            mEnPassantPos = endPos;
        }
        else if (myPiece.getPieceType() == ChessPiece.PieceType.PAWN && mEnPassantPos != null) {
            ChessPosition enPassantCapture;
            if (myColor == TeamColor.WHITE) {
                enPassantCapture = new ChessPosition(mEnPassantPos.getRow()+1, mEnPassantPos.getColumn());
            }
            else {
                enPassantCapture = new ChessPosition(mEnPassantPos.getRow()-1, mEnPassantPos.getColumn());
            }
            if (enPassantCapture.equals(endPos)) {
                mBoard.addPiece(mEnPassantPos, null);
            }
            mEnPassantPos = null;
        }
        else { mEnPassantPos = null; }


        if (move.getPromotionPiece() != null) {
            mBoard.addPiece(endPos, new ChessPiece(myColor, move.getPromotionPiece()));
        }
        else { mBoard.addPiece(endPos, myPiece); }
        mBoard.addPiece(startPos, null);


        if (mTeamTurn == TeamColor.WHITE) { mTeamTurn = TeamColor.BLACK; }
        else { mTeamTurn = TeamColor.WHITE; }
        System.out.println("The final board is: \n" + mBoard);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor)
    {
        ChessPosition kingPos = null;
        for (int y = 1; y < 9; y ++) {
            for (int x = 1; x < 9; x++)
            {
                ChessPiece myPiece = mBoard.getPiece(new ChessPosition(y,x));
                if (myPiece == null)
                {
                    continue;
                }
                if (myPiece.getPieceType() == ChessPiece.PieceType.KING &&
                        myPiece.getTeamColor() == teamColor)
                {
                    kingPos = new ChessPosition(y,x);
                    break;
                }
            }
            if (kingPos != null)
            {
                break;
            }
        }

        for (int y = 1; y < 9; y ++) {
            for (int x = 1; x < 9; x++)
            {
                ChessPiece myPiece = mBoard.getPiece(new ChessPosition(y,x));
                if (myPiece == null || myPiece.getTeamColor() == teamColor)
                {
                    continue;
                }
                Collection<ChessMove> pieceMoves = myPiece.pieceMoves(mBoard, new ChessPosition(y, x));
                for (ChessMove move : pieceMoves) {
                    if (move.getEndPosition().equals(kingPos)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor)
    {
        if (teamColor != mTeamTurn || !isInCheck(teamColor))
        {
            return false;
        }
        for (int y = 1; y < 9; y ++) {
            for (int x = 1; x < 9; x++)
            {
                ChessPiece myPiece = mBoard.getPiece(new ChessPosition(y,x));
                if (myPiece != null && myPiece.getTeamColor() == teamColor) {
                    if (!this.validMoves(new ChessPosition(y,x)).isEmpty())
                    {
                        return false;
                    }
                }
            }
        }


        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor)
    {
        if (mTeamTurn != teamColor || isInCheck(teamColor))
        {
            return false;
        }
        else
        {
            for (int y = 1; y < 9; y ++) {
                for (int x = 1; x < 9; x++)
                {
                    ChessPiece myPiece = mBoard.getPiece(new ChessPosition(y,x));
                    if (myPiece == null)
                    {
                        continue;
                    }
                    if (myPiece.getTeamColor() == teamColor && !this.validMoves(new ChessPosition(y,x)).isEmpty())
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board)
    {
        mBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard()
    {
        return mBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return mTeamTurn == chessGame.mTeamTurn && Objects.equals(mBoard, chessGame.mBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mTeamTurn, mBoard);
    }
}
