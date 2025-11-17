package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.commands.UserJoinCommand;

import java.io.IOException;
import java.net.URI;

public class WebSocketFacade extends Endpoint {
    private Session mSession;
    private final GameHandler mHandler;

    public WebSocketFacade(String url, GameHandler handler) throws Exception
    {
        this.mHandler = handler;
        URI socketURI = new URI(url.replace("http", "ws") + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, socketURI);
    }

    @Override
    public void onOpen(Session session, EndpointConfig config)
    {
        System.out.println("Client connected!");
        this.mSession = session;
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                mHandler.printMessage(message);
            }
        });
        System.out.println("Handler is connected!");
    }

    public void connect(String aAuthToken, int aGameID, String aColor) throws ResponseException
    {
        try
        {
            System.out.println("Websocket connection has begun");
            UserJoinCommand connect = new UserJoinCommand(UserGameCommand.CommandType.CONNECT, aAuthToken, aGameID, aColor);
            System.out.println("Sending the connect message via the basic remote");
            mSession.getBasicRemote().sendText(new Gson().toJson(connect));
            System.out.println("Message to server finished sending!");
        }
        catch (IOException e)
        {
            throw new ResponseException(e.getMessage());
        }
    }

    public void makeMove(String aAuthToken, int aGameID, ChessMove aMove) throws ResponseException
    {
        try
        {
            MakeMoveCommand move = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, aAuthToken, aGameID, aMove);
            mSession.getBasicRemote().sendText(new Gson().toJson(move));
        }
        catch (IOException e)
        {
            throw new ResponseException(e.getMessage());
        }
    }

    public void leaveGame(String aAuthToken, int aGameID, String aColor) throws ResponseException
    {
        try
        {
            UserJoinCommand leave = new UserJoinCommand(UserGameCommand.CommandType.LEAVE, aAuthToken, aGameID, aColor);
            mSession.getBasicRemote().sendText(new Gson().toJson(leave));
            if (mSession != null && mSession.isOpen())
            {
                mSession.close();
            }
        }
        catch (IOException e)
        {
            throw new ResponseException(e.getMessage());
        }
    }

    public void resignGame(String aAuthToken, int aGameID) throws ResponseException
    {
        try
        {
            UserGameCommand resign = new UserGameCommand(UserGameCommand.CommandType.RESIGN, aAuthToken, aGameID);
            mSession.getBasicRemote().sendText(new Gson().toJson(resign));
        }
        catch (IOException e)
        {
            throw new ResponseException(e.getMessage());
        }
    }
}