package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand
{
    ChessMove mMove;
    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move) {
        super(commandType, authToken, gameID);
        mMove = move;
    }
}
