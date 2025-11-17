package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.*;

public class WebSocketSessions
{
    public final Map<String, Session> connections = new HashMap<>();

    public void addSessionToGame(String aUsername, Session aSession)
    {
        connections.put(aUsername, aSession);
    }

    public void removeSessionFromGame(Session session)
    {
        connections.entrySet().removeIf(entry -> entry.getValue().equals(session));
    }

    public void broadcastMessage(Session excludeSession, ServerMessage message) throws IOException
    {

        for (Session con : connections.values())
        {
            if (con.isOpen())
            {
                if (!con.equals(excludeSession))
                {
                    con.getRemote().sendString(new Gson().toJson(message));
                }
            }
        }
        System.out.println("Message has been broadcast");
    }
}