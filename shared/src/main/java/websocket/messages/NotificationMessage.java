package websocket.messages;

public class NotificationMessage extends ServerMessage
{
    String mMessage;

    public NotificationMessage(ServerMessageType type, String aMessage) {
        super(type);
        mMessage = aMessage;
    }

    public String getMessage()
    {
        return mMessage;
    }
}
