package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class DrawBoard
{
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    static void drawChessBoard(String aColor)
    {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);
        ChessGame game = new ChessGame();
        ChessBoard board = game.getBoard();
        if (aColor.equals("WHITE"))
        {
            for (int y = 1; y < 9; y++) {
                for (int x = 1; x < 9; x++) {
                    placePiece(out, board, x, y);

                }
                out.println("\u001b[0m"); // reset colors at end of line
            }
        }
        else if (aColor.equals("BLACK"))
        {
            for (int y = 8; y > 0; y--) {
                for (int x = 8; x > 0; x--) {
                    placePiece(out, board, x, y);

                }
                out.println("\u001b[0m"); // reset colors at end of line
            }
        }


        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void placePiece(PrintStream aOut, ChessBoard aBoard, int aX, int aY)
    {
        String pieceString;
        ChessPiece piece = aBoard.getPiece(new ChessPosition(aY, aX));
        if (piece == null)
        {
            pieceString = EMPTY;
        }
        else
        {
            pieceString = piece.toString();
        }
        if ((aX + aY + 1) % 2 == 0) {
            aOut.print(SET_BG_COLOR_DARK_GREY + pieceString);
        } else {
            aOut.print(SET_BG_COLOR_LIGHT_GREY + pieceString);
        }
    }

    static void drawRowOfSquares(PrintStream out) {

        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                setWhite(out);

                if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                    int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                    int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

                    out.print(EMPTY.repeat(prefixLength));
                    printPlayer(out, "X");
                    out.print(EMPTY.repeat(suffixLength));
                }
                else {
                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                }

                if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                    // Draw vertical column separator.
                    setBlack(out);
                    out.print("|");
                }

                setBlack(out);
            }

            out.println();
        }
    }
    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void printPlayer(PrintStream out, String player) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(player);

        setWhite(out);
    }
}
