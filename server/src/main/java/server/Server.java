package server;

import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.websocket.WsContext;
import model.*;
import org.eclipse.jetty.http.BadMessageException;
import service.GameService;
import service.UserService;

import java.util.Map;


public class Server {

    private static final Gson SERIALIZER = new Gson();
    private final Javalin mJavalin;
    private static UserService mUserService;
    private static GameService mGameService;


    public Server() {
        SQLUserDAO userData = new SQLUserDAO();
        SQLAuthDAO authData = new SQLAuthDAO();
        SQLGameDAO gameData = new SQLGameDAO();
        try {
            DatabaseManager.createDatabase();
        }
        catch (DataAccessException e)
        {
            System.out.println("The error creating the database was " + e.getMessage());
        }

        mUserService = new UserService(userData, authData);
        mGameService = new GameService(gameData, authData);
        mJavalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        createHandlers();

        WebSocketHandler handler = new WebSocketHandler(gameData, authData);
        mJavalin.ws("/ws", ws -> {
            ws.onConnect(handler);
            ws.onMessage(handler);
            ws.onClose(handler);
        });
    }

    private void createHandlers()
    {
        mJavalin.post("/user", Server::handleRegister);
        mJavalin.post("/session", Server::handleLogin);
        mJavalin.delete("/session", Server::handleLogout);
        mJavalin.get("/game", Server::handleListGames);
        mJavalin.post("/game", Server::handleCreateGame);
        mJavalin.put("/game", Server::handleJoinGame);
        mJavalin.delete("db", Server::handleClear);
    }

    public int run(int desiredPort) {
        mJavalin.start(desiredPort);
        return mJavalin.port();
    }

    public void stop() {
        mJavalin.stop();
    }

    private static void displayErrorMessage(Exception e, int errorCode, Context ctx)
    {
        String errorJson = SERIALIZER.toJson(Map.of("message", e.getMessage()));
        ctx.status(errorCode).result(errorJson).contentType("application/json");
    }

    private static void handleRegister(Context ctx)
    {
        try {
            RegisterRequest request = SERIALIZER.fromJson(ctx.body(), RegisterRequest.class);
            String resultJson = SERIALIZER.toJson(mUserService.register(request));
            ctx.status(200).result(resultJson).contentType("application/json");
        }
        catch (AlreadyTakenException e) {
            displayErrorMessage(e, 403, ctx);
        }
        catch (BadMessageException e)
        {
            displayErrorMessage(e, 400, ctx);
        }
        catch (DataAccessException e) {
            displayErrorMessage(e, 500, ctx);
        }
    }

    private static void handleLogin(Context ctx)
    {
        try {
            LoginRequest request = SERIALIZER.fromJson(ctx.body(), LoginRequest.class);
            String resultJson = SERIALIZER.toJson(mUserService.login(request));
            ctx.status(200).result(resultJson).contentType("application/json");
        }
        catch (InvalidCredentialsException e) {
            displayErrorMessage(e, 401, ctx);
        }
        catch (BadMessageException e)
        {
            displayErrorMessage(e, 400, ctx);
        }
        catch (DataAccessException e) {
            displayErrorMessage(e, 500, ctx);
        }
    }

    private static void handleLogout(Context ctx)
    {
        try {
            LogoutRequest request = new LogoutRequest(ctx.header("authorization"));
            mUserService.logout(request);
            ctx.status(200);
        }
        catch (UnauthorizedResponse e)
        {
            displayErrorMessage(e, 401, ctx);
        }
        catch (DataAccessException e)
        {
            displayErrorMessage(e, 500, ctx);
        }

    }

    private static void handleListGames(Context ctx)
    {
        try {
            ListGamesRequest request = new ListGamesRequest(ctx.header("authorization"));
            String resultJson = SERIALIZER.toJson(mGameService.listGames(request));
            ctx.status(200).result(resultJson).contentType("application/json");
        }
        catch (UnauthorizedResponse e)
        {
            displayErrorMessage(e, 401, ctx);
        }
        catch (DataAccessException e)
        {
            displayErrorMessage(e, 500, ctx);
        }
    }

    private static void handleCreateGame(Context ctx)
    {
        try {
            String authToken = ctx.header("authorization");
            CreateGameRequest bodyRequest = SERIALIZER.fromJson(ctx.body(), CreateGameRequest.class);
            CreateGameRequest request = new CreateGameRequest(bodyRequest.gameName(), authToken);
            String resultJson = SERIALIZER.toJson(mGameService.createGame(request));
            ctx.status(200).result(resultJson).contentType("application/json");
        }
        catch (UnauthorizedResponse e)
        {
            displayErrorMessage(e, 401, ctx);
        }
        catch (AlreadyTakenException e)
        {
            displayErrorMessage(e, 403, ctx);
        }
        catch (BadMessageException e)
        {
            displayErrorMessage(e, 400, ctx);
        }
        catch (DataAccessException e)
        {
            displayErrorMessage(e, 500, ctx);
        }

    }

    private static void handleJoinGame(Context ctx)
    {
        try {
            String authToken = ctx.header("authorization");
            JoinGameRequest bodyRequest = SERIALIZER.fromJson(ctx.body(), JoinGameRequest.class);
            JoinGameRequest request = new JoinGameRequest(bodyRequest.gameID(), bodyRequest.playerColor(), authToken);
            mGameService.joinGame(request);
            ctx.status(200);
        }
        catch (UnauthorizedResponse e)
        {
            displayErrorMessage(e, 401, ctx);
        }
        catch (AlreadyTakenException e)
        {
            displayErrorMessage(e, 403, ctx);
        }
        catch (BadMessageException e)
        {
            displayErrorMessage(e, 400, ctx);
        }
        catch (DataAccessException | InvalidMoveException e)
        {
            displayErrorMessage(e, 500, ctx);
        }

    }

    private static void handleClear(Context ctx)
    {
        try {
            mUserService.clearUsers();
            mGameService.clearGames();
            ctx.status(200);
        }
        catch (DataAccessException e)
        {
            displayErrorMessage(e, 500, ctx);
        }
    }
}
