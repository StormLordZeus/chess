package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage
{
    ChessGame game;
    public LoadGameMessage(ServerMessageType type, ChessGame aGame)
    {
        super(type);
        game = aGame;
    }

    public ChessGame getGame()
    {
        return game;
    }
}
