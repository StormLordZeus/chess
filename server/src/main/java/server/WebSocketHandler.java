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
        AuthData auth = authenticate(aAction.getAuthToken());
        if (auth == null)
        {
            sendError(aCtx, "Error: User isn't logged in\n");
            return;
        }

        UserJoinCommand joinAction = new Gson().fromJson(aJson, UserJoinCommand.class);
        sessions.addSessionToGame(auth.username(), aCtx.session);

        String message = auth.username() + " has connected";
        if (joinAction.getColor() != null)
        {
            message += " as " + joinAction.getColor() + "\n";
        }
        else
        {
            message += " as an observer\n";
        }

        GameData game;
        try
        {
            game = mGameData.getGame(aAction.getGameID());
        }
        catch (DataAccessException e)
        {
            sendError(aCtx, e.getMessage());
            return;
        }

        LoadGameMessage loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game.game());
        aCtx.session.getRemote().sendString(new Gson().toJson(loadGame));
        sessions.broadcastMessage(aCtx.session, new NotificationMessage(
                ServerMessage.ServerMessageType.NOTIFICATION, message));
    }

    public void makeMove(UserGameCommand aAction, String aJson, WsMessageContext aCtx) throws IOException
    {
        AuthData auth = authenticate(aAction.getAuthToken());
        if (auth == null)
        {
            sendError(aCtx, "Error: User isn't logged in\n");
            return;
        }

        MakeMoveCommand moveAction = new Gson().fromJson(aJson, MakeMoveCommand.class);
        try
        {
            GameData preGame = mGameData.getGame(moveAction.getGameID());
            if (preGame.gameOver())
            {
                sendError(aCtx, "Error: Game is over. No moves can be made");
                return;
            }

            ChessGame preGameBoard = preGame.game();
            mGameData.updateGame(moveAction.getGameID(), null,null, moveAction.getMove());
            GameData game = mGameData.getGame(moveAction.getGameID());
            ChessGame gameBoard = game.game();

            ChessPiece piece = preGameBoard.getBoard().getPiece(moveAction.getMove().getStartPosition());
            String message = auth.username() + " has moved a " + piece.getPieceType() + " from " +
                    moveAction.getMove().getStartPosition() + " to " + moveAction.getMove().getEndPosition();
            if (moveAction.getMove().getPromotionPiece() != null)
            {
                message += " and promoted it to a " + moveAction.getMove().getPromotionPiece();
            }

            sessions.broadcastMessage(null, new LoadGameMessage(
                    ServerMessage.ServerMessageType.LOAD_GAME, gameBoard));

            sessions.broadcastMessage(aCtx.session, new NotificationMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION, message));

            message = "";
            if (auth.username().equals(game.whiteUsername()))
            {
                String blackUsername = game.blackUsername() == null ? "Black" : game.blackUsername();
                if (gameBoard.isInCheckmate(ChessGame.TeamColor.BLACK))
                {
                    message = blackUsername + " has been checkmated. " +
                            game.whiteUsername() + " has won the game!\n";
                    mGameData.gameOver(aAction.getGameID());
                }
                else if (gameBoard.isInStalemate(ChessGame.TeamColor.BLACK))
                {
                    message = blackUsername + " has been stalemated. " +
                            game.whiteUsername() + " has won the game!\n";
                    mGameData.gameOver(aAction.getGameID());
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
                    mGameData.gameOver(aAction.getGameID());
                }
                else if (gameBoard.isInStalemate(ChessGame.TeamColor.WHITE))
                {
                    message = whiteUsername + " has been stalemated. " +
                            game.blackUsername() + " has won the game!\n";
                    mGameData.gameOver(aAction.getGameID());
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
        catch (DataAccessException | InvalidMoveException e)
        {
            sendError(aCtx, e.getMessage());
        }
    }

    public void leaveGame(UserGameCommand aAction, String aJson, WsMessageContext aCtx) throws IOException
    {
        AuthData auth = authenticate(aAction.getAuthToken());
        if (auth == null)
        {
            sendError(aCtx, "Error: User isn't logged in\n");
            return;
        }

        UserJoinCommand leaveAction = new Gson().fromJson(aJson, UserJoinCommand.class);
        try
        {
            if (leaveAction.getColor() != null)
            {
                mGameData.updateGame(aAction.getGameID(), leaveAction.getColor(), auth.username(), null);
            }
        }
        catch (DataAccessException | InvalidMoveException e)
        {
            sendError(aCtx, e.getMessage());
        }

        sessions.broadcastMessage(aCtx.session, new NotificationMessage(
                ServerMessage.ServerMessageType.NOTIFICATION, auth.username() + " has left the game\n"));
    }

    public void resign(UserGameCommand aAction, WsMessageContext aCtx) throws IOException
    {
        AuthData auth = authenticate(aAction.getAuthToken());
        if (auth == null)
        {
            sendError(aCtx,"Error: User isn't logged in\n");
            return;
        }

        try
        {
            GameData preGame = mGameData.getGame(aAction.getGameID());
            if (preGame.gameOver()) {
                sendError(aCtx, "Error: Game is already over. Game cannot be resigned");
                return;
            }
        }
        catch (DataAccessException e)
        {
            sendError(aCtx, e.getMessage());
            return;
        }

        String enemy;
        try
        {
            GameData game = mGameData.getGame(aAction.getGameID());
            if (auth.username().equals(game.whiteUsername()))
            {
                enemy = game.blackUsername();
                enemy = enemy == null ? "Black" : enemy;
            }
            else
            {
                enemy = game.whiteUsername();
                enemy = enemy == null ? "White" : enemy;
            }
            mGameData.gameOver(aAction.getGameID());
        }
        catch (DataAccessException e)
        {
            sendError(aCtx, e.getMessage());
            return;
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