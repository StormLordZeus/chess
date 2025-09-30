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

    private TeamColor m_teamTurn;
    private ChessBoard m_board;
    private boolean m_blackCastleLeft;
    private boolean m_blackCastleRight;
    private boolean m_whiteCastleLeft;
    private boolean m_whiteCastleRight;
    private ChessPosition m_enPassantPos;

    public ChessGame() {
        m_teamTurn = TeamColor.WHITE;
        m_board = new ChessBoard();
        m_board.resetBoard();
        m_blackCastleLeft = true;
        m_blackCastleRight = true;
        m_whiteCastleLeft = true;
        m_whiteCastleRight = true;
        m_enPassantPos = null;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn()
    {
        return m_teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team)
    {
        m_teamTurn = team;
    }

    public boolean get_blackCastleLeft() {
        return m_blackCastleLeft;
    }

    public void set_blackCastleLeft(boolean blackCastleLeft) {
        m_blackCastleLeft = blackCastleLeft;
    }

    public boolean get_blackCastleRight() {
        return m_blackCastleRight;
    }

    public void set_blackCastleRight(boolean blackCastleRight) {
        m_blackCastleRight = blackCastleRight;
    }

    public boolean get_whiteCastleLeft() {
        return m_whiteCastleLeft;
    }

    public void set_whiteCastleLeft(boolean whiteCastleLeft) {
        this.m_whiteCastleLeft = whiteCastleLeft;
    }

    public boolean get_whiteCastleRight() {
        return m_whiteCastleRight;
    }

    public void set_whiteCastleRight(boolean whiteCastleRight) {
        this.m_whiteCastleRight = whiteCastleRight;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
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
        ChessPiece myPiece = m_board.getPiece(startPosition);
        if (myPiece == null) {
            return null;
        }
        Collection<ChessMove> myMoves = myPiece.pieceMoves(m_board, startPosition);
        if (myPiece.getPieceType() == ChessPiece.PieceType.KING)
        {
            System.out.println(myMoves);
            int x = startPosition.getColumn();
            int y = startPosition.getRow();
            if (myPiece.getTeamColor() == TeamColor.WHITE)
            {
                ChessPosition castleLeft = new ChessPosition(y, x-2);
                ChessPosition castleRight = new ChessPosition(y, x+2);

                ChessMove castle = new ChessMove(startPosition, castleLeft, null);
                if (myMoves.contains(castle))
                {
                    if (!m_whiteCastleLeft || isInCheck(TeamColor.WHITE))
                    {
                        myMoves.remove(castle);
                    }
                    else
                    {
                        m_board.addPiece(new ChessPosition(y,x-1), myPiece);
                        m_board.addPiece(startPosition, null);
                        if(isInCheck(myPiece.getTeamColor()))
                        {
                            myMoves.remove(castle);
                        }
                        m_board.addPiece(new ChessPosition(y,x-1), null);
                        m_board.addPiece(startPosition, myPiece);
                    }
                }

                castle = new ChessMove(startPosition, castleRight, null);
                if (myMoves.contains(castle))
                {
                    if (!m_whiteCastleRight || isInCheck(TeamColor.WHITE))
                    {
                        myMoves.remove(castle);
                    }
                    else
                    {
                        m_board.addPiece(new ChessPosition(y,x+1), myPiece);
                        m_board.addPiece(startPosition, null);
                        if(isInCheck(myPiece.getTeamColor()))
                        {
                            myMoves.remove(castle);
                        }
                        m_board.addPiece(new ChessPosition(y,x+1), null);
                        m_board.addPiece(startPosition, myPiece);
                    }
                }
            }
            else
            {
                ChessPosition castleLeft = new ChessPosition(y, x-2);
                ChessPosition castleRight = new ChessPosition(y, x+2);

                ChessMove castle = new ChessMove(startPosition, castleLeft, null);
                if (myMoves.contains(castle))
                {
                    if (!m_blackCastleLeft || isInCheck(TeamColor.BLACK))
                    {
                        myMoves.remove(castle);
                    }
                    else
                    {
                        m_board.addPiece(new ChessPosition(y,x-1), myPiece);
                        m_board.addPiece(startPosition, null);
                        if(isInCheck(myPiece.getTeamColor()))
                        {
                            myMoves.remove(castle);
                        }
                        m_board.addPiece(new ChessPosition(y,x-1), null);
                        m_board.addPiece(startPosition, myPiece);
                    }
                }

                castle = new ChessMove(startPosition, castleRight, null);
                if (myMoves.contains(castle))
                {
                    if (!m_blackCastleRight || isInCheck(TeamColor.BLACK))
                    {
                        myMoves.remove(castle);
                    }
                    else
                    {
                        m_board.addPiece(new ChessPosition(y,x+1), myPiece);
                        m_board.addPiece(startPosition, null);
                        if(isInCheck(myPiece.getTeamColor()))
                        {
                            myMoves.remove(castle);
                        }
                        m_board.addPiece(new ChessPosition(y,x+1), null);
                        m_board.addPiece(startPosition, myPiece);
                    }
                }
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
                    ChessPiece enemyPiece = m_board.getPiece(new ChessPosition(y, x));
                    ChessMove enPassantCapture = new ChessMove(startPosition, new ChessPosition(y, x), null);

                    if ((enemyPiece == null || enemyPiece.getTeamColor() == myPiece.getTeamColor()) &&
                            myMoves.contains(enPassantCapture) &&
                            !enPassantPos.equals(m_enPassantPos)) {
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
            ChessPiece enemyPiece = m_board.getPiece(endPos);
            m_board.addPiece(endPos, myPiece);
            m_board.addPiece(startPos, null);
            if(isInCheck(myPiece.getTeamColor()))
            {
                movesIterator.remove();
            }
            m_board.addPiece(endPos, enemyPiece);
            m_board.addPiece(startPos, myPiece);
        }

        return myMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException
    {
        ChessPosition start_pos = move.getStartPosition();
        ChessPosition end_pos = move.getEndPosition();
        ChessPiece myPiece = m_board.getPiece(start_pos);
        if (myPiece != null)
        {
            TeamColor myColor = myPiece.getTeamColor();
            if (this.validMoves(start_pos).contains(move) && myColor == m_teamTurn) {
                if (myPiece.getPieceType() == ChessPiece.PieceType.KING)
                {
                    m_enPassantPos = null;

                    int start_col = start_pos.getColumn();
                    int end_col = end_pos.getColumn();
                    if (Math.abs(start_col - end_col) == 2)
                    {
                        if (end_col > start_col)
                        {
                            if (m_teamTurn == TeamColor.WHITE)
                            {
                                m_board.addPiece(new ChessPosition(1,6), new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.ROOK));
                                m_board.addPiece(new ChessPosition(1,8), null);
                            }
                            else
                            {
                                m_board.addPiece(new ChessPosition(8,6), new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.ROOK));
                                m_board.addPiece(new ChessPosition(8,8), null);
                            }
                        }
                        else
                        {
                            if (m_teamTurn == TeamColor.WHITE)
                            {
                                m_board.addPiece(new ChessPosition(1,4), new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.ROOK));
                                m_board.addPiece(new ChessPosition(1,1), null);
                            }
                            else
                            {
                                m_board.addPiece(new ChessPosition(8,4), new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.ROOK));
                                m_board.addPiece(new ChessPosition(8,1), null);
                            }
                        }
                    }
                    if (m_teamTurn == TeamColor.WHITE)
                    {
                        m_whiteCastleLeft = false;
                        m_whiteCastleRight = false;
                    }
                    else
                    {
                        m_blackCastleLeft = false;
                        m_blackCastleRight = false;
                    }
                }
                else if (myPiece.getPieceType() == ChessPiece.PieceType.ROOK)
                {
                    m_enPassantPos = null;

                    if (start_pos.getRow() == 1 && start_pos.getColumn() == 1)
                    {
                        m_whiteCastleLeft = false;
                    }
                    else if (start_pos.getRow() == 1 && start_pos.getColumn() == 8)
                    {
                        m_whiteCastleRight = false;
                    }
                    else if (start_pos.getRow() == 8 && start_pos.getColumn() == 1)
                    {
                        m_blackCastleLeft = false;
                    }
                    else if (start_pos.getRow() == 8 && start_pos.getColumn() == 8)
                    {
                        m_blackCastleRight = false;
                    }
                }
                else if (myPiece.getPieceType() == ChessPiece.PieceType.PAWN)
                {
                    if (Math.abs(start_pos.getRow() - end_pos.getRow()) == 2)
                    {
                        m_enPassantPos = end_pos;
                    }
                    else if (m_enPassantPos != null)
                    {
                        ChessPosition enPassantCapture;
                        if (myColor == TeamColor.WHITE)
                        {
                            enPassantCapture = new ChessPosition(m_enPassantPos.getRow()+1, m_enPassantPos.getColumn());
                        }
                        else
                        {
                            enPassantCapture = new ChessPosition(m_enPassantPos.getRow()-1, m_enPassantPos.getColumn());
                        }
                        if (enPassantCapture.equals(end_pos))
                        {
                            m_board.addPiece(m_enPassantPos, null);
                        }
                        m_enPassantPos = null;
                    }
                }
                else
                {
                    m_enPassantPos = null;
                }


                if (move.getPromotionPiece() != null)
                {
                    m_board.addPiece(end_pos, new ChessPiece(myColor, move.getPromotionPiece()));
                }
                else
                {
                    m_board.addPiece(end_pos, myPiece);
                }
                m_board.addPiece(start_pos, null);


                if (m_teamTurn == TeamColor.WHITE)
                {
                    m_teamTurn = TeamColor.BLACK;
                }
                else
                {
                    m_teamTurn = TeamColor.WHITE;
                }
                System.out.println("The final board is: \n" + m_board);
            }
            else {
                throw new InvalidMoveException();
            }
        }
        else
        {
            throw new InvalidMoveException();
        }
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
                ChessPiece myPiece = m_board.getPiece(new ChessPosition(y,x));
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
                ChessPiece myPiece = m_board.getPiece(new ChessPosition(y,x));
                if (myPiece != null && myPiece.getTeamColor() != teamColor) {
                    Collection<ChessMove> pieceMoves = myPiece.pieceMoves(m_board, new ChessPosition(y, x));
                    for (ChessMove move : pieceMoves) {
                        if (move.getEndPosition().equals(kingPos)) {
                            return true;
                        }
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
        if (teamColor != m_teamTurn || !isInCheck(teamColor))
        {
            return false;
        }
        for (int y = 1; y < 9; y ++) {
            for (int x = 1; x < 9; x++)
            {
                ChessPiece myPiece = m_board.getPiece(new ChessPosition(y,x));
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
        if (m_teamTurn != teamColor || isInCheck(teamColor))
        {
            return false;
        }
        else
        {
            for (int y = 1; y < 9; y ++) {
                for (int x = 1; x < 9; x++)
                {
                    ChessPiece myPiece = m_board.getPiece(new ChessPosition(y,x));
                    if (myPiece == null)
                    {
                        continue;
                    }
                    if (myPiece.getTeamColor() == teamColor)
                    {
                        if (!this.validMoves(new ChessPosition(y,x)).isEmpty())
                        {
                            return false;
                        }
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
        m_board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard()
    {
        return m_board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return m_teamTurn == chessGame.m_teamTurn && Objects.equals(m_board, chessGame.m_board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_teamTurn, m_board);
    }
}
