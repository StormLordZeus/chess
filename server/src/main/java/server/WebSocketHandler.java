package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.websocket.*;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final WebSocketSessions sessions = new WebSocketSessions();
    private GameDAO mGameData;
    private AuthDAO mAuthData;

    public WebSocketHandler(GameDAO aGameData, AuthDAO aAuthData)
    {
        mGameData = aGameData;
        mAuthData = aAuthData;
    }

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
                try {
                    AuthData auth = mAuthData.getAuth(action.getAuthToken());
                }
                catch (DataAccessException e) {

                }
                sessions.addSessionToGame(action.getGameID(), ctx.session);
                sessions.broadcastMessage(ctx.session, "");
            }
            case MAKE_MOVE ->
            {
                MakeMoveCommand moveAction = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
            }
            case LEAVE ->
            {
                try
                {
                    mAuthData.getAuth(action.getAuthToken());
                }
                catch (DataAccessException e)
                {

                }
                mGameData.updateGame(action.getGameID(),);
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