import chess.*;
import ui.ReplLoop;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        ReplLoop myLoop = new ReplLoop("http://localhost:8080");
        myLoop.run();
    }
}