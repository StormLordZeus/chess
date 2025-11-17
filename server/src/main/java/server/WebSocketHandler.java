package server;

import chess.ChessGame;
import chess.ChessPiece;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.commands.UserJoinCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final WebSocketSessions sessions = new WebSocketSessions();
    private final GameDAO mGameData;
    private final AuthDAO mAuthData;

    public WebSocketHandler(GameDAO aGameData, AuthDAO aAuthData)
    {
        mGameData = aGameData;
        mAuthData = aAuthData;
    }

    @Override
    public void handleConnect(WsConnectContext ctx)
    {
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext aCtx) throws IOException
    {
        String json = aCtx.message();
        UserGameCommand action = new Gson().fromJson(json, UserGameCommand.class);
        switch (action.getCommandType())
        {
            case CONNECT -> connect(action, json, aCtx);
            case MAKE_MOVE -> makeMove(action, json, aCtx);
            case LEAVE -> leaveGame(action, json, aCtx);
            case RESIGN -> resign(action, aCtx);
        }
    }

    public void connect(UserGameCommand aAction, String aJson, WsMessageContext aCtx) throws IOException
    {
        System.out.println("We HAVE arrived in the connect function");
        AuthData auth = authenticate(aAction.getAuthToken());
        if (auth == null)
        {
            sendError(aCtx, "Error: User isn't logged in\n");
            return;
        }

        System.out.println("We are fully authenticated");
        UserJoinCommand joinAction = new Gson().fromJson(aJson, UserJoinCommand.class);
        sessions.addSessionToGame(aAction.getGameID(), aCtx.session);

        String message = auth.username() + " has connected";
        if (joinAction.getColor() != null)
        {
            message += " as " + joinAction.getColor() + "\n";
        }
        else
        {
            message += " as an observer\n";
        }
        System.out.print("Our message is: " + message);

        GameData game;
        try
        {
            game = mGameData.getGame(aAction.getGameID());
        }
        catch (DataAccessException e)
        {
            sendError(aCtx, "Failed to connect to the SQL database or game does exist");
            return;
        }

        System.out.println("We are about to send data back to the client");
        LoadGameMessage loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game.game());
        aCtx.session.getRemote().sendString(new Gson().toJson(loadGame));
        System.out.println("Sending a join notification to all other clients!");
        sessions.broadcastMessage(aCtx.session, new NotificationMessage(
                ServerMessage.ServerMessageType.NOTIFICATION, message));
    }

    public void makeMove(UserGameCommand aAction, String aJson, WsMessageContext aCtx) throws IOException
    {
        System.out.println("We HAVE arrived in the makeMove function");
        AuthData auth = authenticate(aAction.getAuthToken());
        if (auth == null)
        {
            sendError(aCtx, "Error: User isn't logged in\n");
            return;
        }

        MakeMoveCommand moveAction = new Gson().fromJson(aJson, MakeMoveCommand.class);
        try
        {
            System.out.println("Start position: " + moveAction.getMove().getStartPosition());
            System.out.println("End position: " + moveAction.getMove().getEndPosition());
            ChessGame preGameBoard = mGameData.getGame(moveAction.getGameID()).game();
            mGameData.updateGame(moveAction.getGameID(), null,null, moveAction.getMove());
            GameData game = mGameData.getGame(moveAction.getGameID());
            ChessGame gameBoard = game.game();
            System.out.println("The board is: " + gameBoard.getBoard());

            ChessPiece piece = preGameBoard.getBoard().getPiece(moveAction.getMove().getStartPosition());
            String message = auth.username() + " has moved a " + piece.getPieceType() + " from " +
                    moveAction.getMove().getStartPosition() + " to " + moveAction.getMove().getEndPosition();
            sessions.broadcastMessage(aCtx.session, new NotificationMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION, message));


            sessions.broadcastMessage(null, new LoadGameMessage(
                    ServerMessage.ServerMessageType.LOAD_GAME, gameBoard));

            message = "";
            if (auth.username().equals(game.whiteUsername()))
            {
                System.out.println("Black's username is " + game.blackUsername());
                String blackUsername = game.blackUsername() == null ? "Black" : game.blackUsername();
                if (gameBoard.isInCheckmate(ChessGame.TeamColor.BLACK))
                {
                    message = blackUsername + " has been checkmated. " +
                            game.whiteUsername() + " has won the game!\n";
                }
                else if (gameBoard.isInStalemate(ChessGame.TeamColor.BLACK))
                {
                    message = blackUsername + " has been stalemated. " +
                            game.whiteUsername() + " has won the game!\n";
                }
                else if (gameBoard.isInCheck(ChessGame.TeamColor.BLACK))
                {
                    message = blackUsername + " is in check.";
                }
                if (!message.isEmpty())
                {
                    sessions.broadcastMessage(null, new NotificationMessage(
                            ServerMessage.ServerMessageType.NOTIFICATION, message));
                }
            }
            else
            {
                String whiteUsername = game.whiteUsername() == null ? "White" : game.whiteUsername();
                if (gameBoard.isInCheckmate(ChessGame.TeamColor.WHITE))
                {
                    message = whiteUsername + " has been checkmated. " +
                            game.blackUsername() + " has won the game!\n";
                }
                else if (gameBoard.isInStalemate(ChessGame.TeamColor.WHITE))
                {
                    message = whiteUsername + " has been stalemated. " +
                            game.blackUsername() + " has won the game!\n";
                }
                else if (gameBoard.isInCheck(ChessGame.TeamColor.WHITE))
                {
                    message = whiteUsername + " is in check.";
                }
                if (!message.isEmpty())
                {
                    sessions.broadcastMessage(null, new NotificationMessage(
                            ServerMessage.ServerMessageType.NOTIFICATION, message));
                }
            }
        }
        catch (DataAccessException e)
        {
            sendError(aCtx, "Error: Failed to connect to the SQL database\n");
        }
        catch (InvalidMoveException e) {
            sendError(aCtx, "Error: Illegal move specified or it is not your turn." +
                            " Please enter a legal move or wait until it is your turn\n");
        }
    }

    public void leaveGame(UserGameCommand aAction, String aJson, WsMessageContext aCtx) throws IOException
    {
        System.out.println("We HAVE arrived in the leaveGame function");
        AuthData auth = authenticate(aAction.getAuthToken());
        if (auth == null)
        {
            sendError(aCtx, "Error: User isn't logged in\n");
            return;
        }

        UserJoinCommand leaveAction = new Gson().fromJson(aJson, UserJoinCommand.class);
        System.out.println("The leave action has color: " + leaveAction.getColor() + " and the username is " + auth.username());
        try
        {
            if (leaveAction.getColor() != null)
            {
                System.out.println("Updating the game to remove the player");
                mGameData.updateGame(aAction.getGameID(), leaveAction.getColor(), auth.username(), null);
                GameData game = mGameData.getGame(aAction.getGameID());
                System.out.println("The players are white: " + game.whiteUsername() + " and black " + game.blackUsername());
            }
        }
        catch (DataAccessException e)
        {
            sendError(aCtx, "Error: Failed to connect to the SQL database, or bad input\n");
        }
        catch (InvalidMoveException e)
        {
            sendError(aCtx, "Error: This is impossible to trigger as move is null\n");
        }

        sessions.broadcastMessage(aCtx.session, new NotificationMessage(
                ServerMessage.ServerMessageType.NOTIFICATION, auth.username() + " has left the game\n"));
    }

    public void resign(UserGameCommand aAction, WsMessageContext aCtx) throws IOException
    {
        System.out.println("We HAVE arrived in the resign function");
        AuthData auth = authenticate(aAction.getAuthToken());
        if (auth == null)
        {
            sendError(aCtx,"Error: User isn't logged in\n");
            return;
        }

        String enemy;
        try
        {
            GameData game = mGameData.getGame(aAction.getGameID());
            enemy = auth.username().equals(game.whiteUsername()) ? game.blackUsername() : game.whiteUsername();
            enemy = enemy == null ? "White" : game.whiteUsername();
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }

        String message = auth.username() + " has resigned. " + enemy + " has won the game!\n";
        sessions.broadcastMessage(null, new NotificationMessage(
                ServerMessage.ServerMessageType.NOTIFICATION, message));
    }

    private AuthData authenticate(String aAuthToken)
    {
        try
        {
            return mAuthData.getAuth(aAuthToken);
        }
        catch (DataAccessException e)
        {
            return null;
        }
    }

    private void sendError(WsMessageContext aCtx, String aError) throws IOException
    {
        ErrorMessage error = new ErrorMessage(
                ServerMessage.ServerMessageType.ERROR, aError);
        aCtx.session.getRemote().sendString(new Gson().toJson(error));
    }

    @Override
    public void handleClose(WsCloseContext ctx)
    {
        sessions.removeSessionFromGame(ctx.session);
    }
}