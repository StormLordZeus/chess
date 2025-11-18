package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.*;

public class WebSocketSessions
{
    private final Map<Integer, Map<String, Session>> connections = new HashMap<>();

    public void addSessionToGame(int gameID, String username, Session session) {
        connections
                .computeIfAbsent(gameID, myHash -> new HashMap<>())
                .put(username, session);
    }

    public void removeSession(Session session) {
        for (Map<String, Session> userMap : connections.values()) {
            Iterator<Map.Entry<String, Session>> userMapIterator = userMap.entrySet().iterator();
            while (userMapIterator.hasNext()) {
                if (userMapIterator.next().getValue().equals(session)) {
                    userMapIterator.remove();
                }
            }
        }
    }

    public void broadcastToGame(int gameID, Session excludeSession, ServerMessage message) throws IOException {
        Map<String, Session> userMap = connections.get(gameID);
        if (userMap == null) return; // no players in this game

        String json = new Gson().toJson(message);

        for (Session session : userMap.values()) {
            if (session.isOpen() && !session.equals(excludeSession)) {
                session.getRemote().sendString(json);
            }
        }

        System.out.println("Broadcast to game " + gameID + " complete.");
    }
}