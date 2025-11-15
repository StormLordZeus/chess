package websocket.messages;

public class CheckMessage extends ServerMessage
{
    private String mPlayer;

    public CheckMessage(ServerMessageType type, String username)
    {
        super(type);
        mPlayer = username;
    }

    @Override
    public String toString()
    {
        return mPlayer + " is in check";
    }
}
