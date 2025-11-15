package server;

import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketSessions
{
    public final Map<Integer, Session> connections = new HashMap<>();

    public void addSessionToGame(int aGameID, Session aSession) throws IOException
    {
        connections.put(aGameID, aSession);
        broadcastMessage(aSession, "");
    }

    public void removeSessionFromGame(int gameID, Session session)
    {
        connections.remove(gameID);
    }

    public void broadcastMessage(Session excludeSession, String message) throws IOException
    {
        for (Session con : connections.values())
        {
            if (con.isOpen())
            {
                if (!con.equals(excludeSession))
                {
                    con.getRemote().sendString(message);
                }
            }
        }
    }
}