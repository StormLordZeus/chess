package websocket;

import jakarta.websocket.*;

public class WebSocketFacade extends Endpoint implements MessageHandler.Whole<String>
{

    @Override
    public void onOpen(Session session, EndpointConfig config) {}

    @Override
    public void onMessage(String message) {}
}