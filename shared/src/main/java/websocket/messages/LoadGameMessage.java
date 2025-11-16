package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage
{
    ChessGame mGame;
    public LoadGameMessage(ServerMessageType type, ChessGame aGame)
    {
        super(type);
        mGame = aGame;
    }

    public ChessGame getGame()
    {
        return mGame;
    }
}
