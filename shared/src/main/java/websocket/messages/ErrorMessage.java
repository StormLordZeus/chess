package websocket.messages;

public class ErrorMessage extends ServerMessage
{
    String errorMessage;
    public ErrorMessage(ServerMessageType type, String aError)
    {
        super(type);
        errorMessage = aError;
    }

    public String getError()
    {
        return errorMessage;
    }
}
