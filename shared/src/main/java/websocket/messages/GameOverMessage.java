package websocket.messages;

public class GameOverMessage extends ServerMessage
{
    private EndType mEndType;
    private String mLoser;
    private String mWinner;

    public enum EndType {
        CHECKMATE,
        STALEMATE,
        RESIGN
    }

    public GameOverMessage(ServerMessageType type1, EndType type2, String winner, String loser)
    {
        super(type1);
        mEndType = type2;
        mWinner = winner;
        mLoser = loser;
    }

    @Override
    public String toString()
    {
        return mLoser + " has resigned. " + mWinner + " wins the game!\n";
    }
}
