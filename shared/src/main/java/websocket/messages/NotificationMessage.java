package websocket.messages;

public class NotificationMessage extends ServerMessage
{
    String message;

    public NotificationMessage(ServerMessageType type, String aMessage) {
        super(type);
        message = aMessage;
    }

    public String getMessage()
    {
        return message;
    }
}
