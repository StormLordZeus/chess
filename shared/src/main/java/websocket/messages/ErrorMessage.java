package websocket.messages;

public class ErrorMessage extends ServerMessage
{
    String mError;
    public ErrorMessage(ServerMessageType type, String aError)
    {
        super(type);
        mError = aError;
    }

    public String getError()
    {
        return mError;
    }
}
