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

    static void drawChessBoard(String aColor, ChessBoard aBoard)
    {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);
        if (aColor.equals("WHITE"))
        {
            printHeader(out, aColor);

            for (int y = 8; y > 0; y--) {
                out.print(y + " ");
                for (int x = 1; x < 9; x++) {
                    placePiece(out, aBoard, x, y);

                }
                out.print(RESET_BG_COLOR);
                out.print(RESET_TEXT_COLOR);
                out.print(" " + y);
                out.println("\u001b[0m"); // reset colors at end of line
            }

            printHeader(out, aColor);
        }
        else if (aColor.equals("BLACK"))
        {
            printHeader(out, aColor);
            for (int y = 1; y < 9; y++) {
                out.print(y + " ");
                for (int x = 8; x > 0; x--) {
                    placePiece(out, aBoard, x, y);
                }
                out.print(RESET_BG_COLOR);
                out.print(RESET_TEXT_COLOR);
                out.print(" " + y);
                out.println("\u001b[0m"); // reset colors at end of line
            }
            printHeader(out, aColor);
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
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
            {
                aOut.print(SET_TEXT_COLOR_WHITE);
            }
            else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK)
            {
                aOut.print(SET_TEXT_COLOR_BLACK);
            }
            pieceString = piece.toString();
        }

        if ((aX + aY) % 2 == 0)
        {
            aOut.print(SET_BG_COLOR_DARK_GREEN + pieceString);
        }
        else
        {
            aOut.print(SET_BG_COLOR_LIGHT_GREY + pieceString);
        }
    }

    private static void printHeader(PrintStream aOut, String aColor)
    {
        // file letters at the top
        aOut.print(RESET_TEXT_COLOR + "  "); // left margin
        int i = 1;
        if (aColor.equals("WHITE"))
        {
            for (char c = 'a'; c <= 'h'; c++)
            {
                if ((i % 3) == 2) {
                    aOut.print(" " + c + " ");
                } else {
                    aOut.print(" " + c + "  ");
                }
                i++;
            }
        }
        else
        {
            for (char c = 'h'; c >= 'a'; c--)
            {
                if ((i % 3) == 2) {
                    aOut.print(" " + c + " ");
                } else {
                    aOut.print(" " + c + "  ");
                }
                i++;
            }
        }
        aOut.println();
    }

}
