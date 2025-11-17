package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ui.EscapeSequences.*;

public class DrawBoard
{
    private static final List<ChessPosition> mHighlights = new ArrayList<>();
    private static ChessPosition mPieceHighlight;

    static void drawChessBoard(String aColor, ChessBoard aBoard)
    {
        System.out.println();
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);
        if (aColor.equals("WHITE"))
        {
            printHeader(out, aColor);

            for (int y = 8; y > 0; y--)
            {
                out.print(y + " ");
                for (int x = 1; x < 9; x++)
                {
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
            for (int y = 1; y < 9; y++)
            {
                out.print(y + " ");
                for (int x = 8; x > 0; x--)
                {
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
        mPieceHighlight = null;
        mHighlights.clear();
    }

    public static void highlightSquares(ChessGame aGame, ChessPosition pieceLocation)
    {
        mPieceHighlight = pieceLocation;
        Collection<ChessMove> moves = aGame.validMoves(pieceLocation);
        for (ChessMove move : moves)
        {
            mHighlights.add(move.getEndPosition());
        }
    }

    private static void placePiece(PrintStream aOut, ChessBoard aBoard, int aX, int aY)
    {
        String pieceString;
        ChessPosition position = new ChessPosition(aY, aX);
        ChessPiece piece = aBoard.getPiece(position);
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

        if (position.equals(mPieceHighlight))
        {
            aOut.print(SET_BG_COLOR_GREEN + pieceString);
        }
        else if (mHighlights.contains(position))
        {
            if ((aX + aY) % 2 == 0)
            {
                aOut.print(SET_BG_COLOR_MAGENTA + pieceString);
            }
            else
            {
                aOut.print(SET_BG_COLOR_RED + pieceString);
            }
        }
        else if ((aX + aY) % 2 == 0)
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
                if ((i % 3) == 2)
                {
                    aOut.print(" " + c + " ");
                }
                else
                {
                    aOut.print(" " + c + "  ");
                }
                i++;
            }
        }
        else
        {
            for (char c = 'h'; c >= 'a'; c--)
            {
                if ((i % 3) == 2)
                {
                    aOut.print(" " + c + " ");
                }
                else
                {
                    aOut.print(" " + c + "  ");
                }
                i++;
            }
        }
        aOut.println();
    }

}
