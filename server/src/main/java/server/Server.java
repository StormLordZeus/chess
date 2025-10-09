package server;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import model.RegisterRequest;
import model.RegisterResult;
import service.UserService;

import java.util.Map;


public class Server {

    private static final Gson serializer  = new Gson();
    private final Javalin mJavalin;
    private static final UserService mService = new UserService();

    public Server() {
        mJavalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        mJavalin.post("/user", Server::handleRegister);
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
            RegisterRequest request = serializer.fromJson(ctx.body(), RegisterRequest.class);
            System.out.println("I have created the request object: " + request.username() + " and " + request.password());
            RegisterResult result = mService.register(request);
            System.out.println("I have created the result object");
            String resultJson = new Gson().toJson(result);
            System.out.println("I have created the JSON result object");
            ctx.status(200).result(resultJson).contentType("application/json");
        }
        catch (AlreadyTakenException e) {
            String json = serializer.toJson(Map.of("message", e.getMessage()));
            ctx.status(403).result(json).contentType("application/json");
        }

        catch (DataAccessException e) {
            String json = serializer.toJson(Map.of("message", e.getMessage()));
            ctx.status(500).result(json).contentType("application/json");
        }
    }

    private static <T> T getBodyObject(Context ctx, Class<T> clazz) {
        T body = serializer.fromJson(ctx.body(), clazz);
        if (body == null) {
            throw new RuntimeException("Missing request body");
        }
        return body;
    }
}
