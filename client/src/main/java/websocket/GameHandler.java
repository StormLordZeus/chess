package websocket;

import websocket.messages.ServerMessage;

public interface GameHandler
{
    public void printMessage(ServerMessage aMessage);

    public void updateGame(int aGameID);
}
