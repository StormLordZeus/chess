package server;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.commands.UserJoinCommand;
import websocket.messages.CheckMessage;
import websocket.messages.GameOverMessage;
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
                UserJoinCommand joinAction = new Gson().fromJson(ctx.message(), UserJoinCommand.class);
                try
                {
                    AuthData auth = mAuthData.getAuth(action.getAuthToken());
                }
                catch (DataAccessException e)
                {

                }
                sessions.addSessionToGame(action.getGameID(), ctx.session);
                sessions.broadcastMessage(ctx.session, "");
            }
            case MAKE_MOVE ->
            {
                MakeMoveCommand moveAction = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
                AuthData auth;
                try
                {
                    auth = mAuthData.getAuth(action.getAuthToken());
                }
                catch (DataAccessException e)
                {
                    return;
                }
                try
                {
                    mGameData.updateGame(moveAction.getGameID(), null,null, moveAction.getMove());
                    GameData game = mGameData.getGame(moveAction.getGameID());
                    ChessGame gameBoard = game.game();
                    if (auth.username().equals(game.whiteUsername()))
                    {
                        if (gameBoard.isInCheck(ChessGame.TeamColor.BLACK))
                        {
                            sessions.broadcastMessage(null, new CheckMessage(
                                    ServerMessage.ServerMessageType.NOTIFICATION, game.blackUsername()));
                        }
                    }
                    else
                    {
                        if (gameBoard.isInCheck(ChessGame.TeamColor.WHITE))
                        {
                            sessions.broadcastMessage(null, new CheckMessage(
                                    ServerMessage.ServerMessageType.NOTIFICATION, game.whiteUsername()));
                        }
                    }
                }
                catch (DataAccessException e)
                {
                    throw new RuntimeException(e);
                }
                catch (InvalidMoveException e)
                {
                    return;
                }
            }
            case LEAVE ->
            {
                UserJoinCommand leaveAction = new Gson().fromJson(ctx.message(), UserJoinCommand.class);
                AuthData auth;
                try
                {
                    auth = mAuthData.getAuth(action.getAuthToken());
                }
                catch (DataAccessException e)
                {
                    return;
                }
                try
                {
                    if (leaveAction.getColor() != null)
                    {
                        mGameData.updateGame(action.getGameID(), leaveAction.getColor(), auth.username(), null);
                    }
                }
                catch (DataAccessException e)
                {
                    throw new RuntimeException(e);
                }
                catch (InvalidMoveException e)
                {
                    return;
                }
            }
            case RESIGN ->
            {
                AuthData auth;
                String enemy;
                try
                {
                    auth = mAuthData.getAuth(action.getAuthToken());
                }
                catch (DataAccessException e)
                {
                    return;
                }
                try
                {
                    GameData game = mGameData.getGame(action.getGameID());
                    enemy = auth.username().equals(game.whiteUsername()) ? game.blackUsername() : game.whiteUsername();
                }
                catch (DataAccessException e)
                {
                    throw new RuntimeException(e);
                }
                sessions.broadcastMessage(null, new GameOverMessage(
                        ServerMessage.ServerMessageType.NOTIFICATION,
                        GameOverMessage.EndType.RESIGN,
                        enemy,
                        auth.username()));
            }
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) throws IOException
    {

    }
}