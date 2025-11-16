package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint implements MessageHandler.Whole<String>
{
    private final Session mSession;
    private final GameHandler mMessageHandler;

    public WebSocketFacade(String aUrl, GameHandler aHandler) throws ResponseException
    {
        try
        {
            aUrl = aUrl.replace("http", "ws");
            URI socketURI = new URI(aUrl + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            mSession = container.connectToServer(this, socketURI);
            mMessageHandler = aHandler;

            //set message handler
            mSession.addMessageHandler((Whole<String>) message -> {
                ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                mMessageHandler.printMessage(notification);
            });
        }
        catch (DeploymentException | IOException | URISyntaxException ex)
        {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void connect(String aAuthToken, int aGameID) throws ResponseException
    {
        try
        {
            UserGameCommand connect = new UserGameCommand(UserGameCommand.CommandType.CONNECT, aAuthToken, aGameID);
            mSession.getBasicRemote().sendText(new Gson().toJson(connect));
        }
        catch (IOException e)
        {
            throw new ResponseException(e.getMessage());
        }
    }

    public void makeMove(String aAuthToken, int aGameID, ChessMove aMove) throws ResponseException
    {
        try {
            MakeMoveCommand move = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, aAuthToken, aGameID, aMove);
            mSession.getBasicRemote().sendText(new Gson().toJson(move));
        }
        catch (IOException e)
        {
            throw new ResponseException(e.getMessage());
        }
    }

    public void leaveGame(String aAuthToken, int aGameID) throws ResponseException
    {
        try {
            UserGameCommand connect = new UserGameCommand(UserGameCommand.CommandType.LEAVE, aAuthToken, aGameID);
            mSession.getBasicRemote().sendText(new Gson().toJson(connect));
        }
        catch (IOException e)
        {
            throw new ResponseException(e.getMessage());
        }
    }

    public void resignGame(String aAuthToken, int aGameID) throws ResponseException
    {
        try {
            UserGameCommand connect = new UserGameCommand(UserGameCommand.CommandType.RESIGN, aAuthToken, aGameID);
            mSession.getBasicRemote().sendText(new Gson().toJson(connect));
        }
        catch (IOException e)
        {
            throw new ResponseException(e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {}

    @Override
    public void onMessage(String message) {}
}