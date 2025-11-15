package server;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final WebSocketSessions sessions = new WebSocketSessions();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws IOException
    {
        UserGameCommand action = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        switch (action.getCommandType())
        {
            case CONNECT ->
            {
                sessions.addSessionToGame(action.getGameID(), ctx.session);
            }
            case MAKE_MOVE ->
            {
                MakeMoveCommand moveAction = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
            }
            case LEAVE ->
            {

            }
            case RESIGN ->
            {

            }
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) throws IOException
    {

    }
}