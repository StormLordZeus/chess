package websocket;

import websocket.messages.ServerMessage;

public interface GameHandler
{
    void printMessage(String aMessage);

    void updateGame(int aGameID);
}
