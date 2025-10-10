package server;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.InvalidCredentialsException;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import model.LoginRequest;
import model.LoginResult;
import model.LogoutRequest;
import model.RegisterRequest;
import service.UserService;

import java.util.Map;


public class Server {

    private static final Gson mSerializer  = new Gson();
    private final Javalin mJavalin;
    private static final UserService mService = new UserService();
    private static String mAuthToken;

    public Server() {
        mJavalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        createHandlers();
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

    private static void handleRegister(Context ctx)
    {
        try {
            RegisterRequest request = mSerializer.fromJson(ctx.body(), RegisterRequest.class);
            String resultJson = new Gson().toJson(mService.register(request));
            ctx.status(200).result(resultJson).contentType("application/json");
        }
        catch (AlreadyTakenException e) {
            String errorJson = mSerializer.toJson(Map.of("message", e.getMessage()));
            ctx.status(403).result(errorJson).contentType("application/json");
        }

        catch (DataAccessException e) {
            String errorJson = mSerializer.toJson(Map.of("message", e.getMessage()));
            ctx.status(500).result(errorJson).contentType("application/json");
        }
    }

    private static void handleLogin(Context ctx)
    {
        try {
            LoginRequest request = mSerializer.fromJson(ctx.body(), LoginRequest.class);
            LoginResult result = mService.login(request);
            mAuthToken = result.authToken();
            String resultJson = new Gson().toJson(result);
            ctx.status(200).result(resultJson).contentType("application/json");
        }
        catch (InvalidCredentialsException e) {
            String errorJson = mSerializer.toJson(Map.of("message", e.getMessage()));
            ctx.status(403).result(errorJson).contentType("application/json");
        }

        catch (DataAccessException e) {
            String errorJson = mSerializer.toJson(Map.of("message", e.getMessage()));
            ctx.status(500).result(errorJson).contentType("application/json");
        }
    }

    private static void handleLogout(Context ctx)
    {
        try {
            LogoutRequest request = mSerializer.fromJson(ctx.header("authorization"), LogoutRequest.class);
            mService.logout(request);
            ctx.status(200);
        }
        catch (UnauthorizedResponse e) {
            String errorJson = mSerializer.toJson(Map.of("message", e.getMessage()));
            ctx.status(401).result(errorJson).contentType("application/json");
        }

        catch (DataAccessException e) {
            String errorJson = mSerializer.toJson(Map.of("message", e.getMessage()));
            ctx.status(500).result(errorJson).contentType("application/json");
        }

    }

    private static void handleListGames(Context ctx)
    {

    }

    private static void handleCreateGame(Context ctx)
    {

    }

    private static void handleJoinGame(Context ctx)
    {

    }

    private static void handleClear(Context ctx)
    {

    }

    private static <T> T getBodyObject(Context ctx, Class<T> clazz) {
        T body = mSerializer.fromJson(ctx.body(), clazz);
        if (body == null) {
            throw new RuntimeException("Missing request body");
        }
        return body;
    }
}
