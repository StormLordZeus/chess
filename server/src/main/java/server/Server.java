package server;

import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import model.RegisterRequest;
import service.UserService;

public class Server {

    private final Javalin mJavalin;
    private static final UserService mService = new UserService();

    public Server() {
        mJavalin = Javalin.create(config -> config.staticFiles.add("web"));

        mJavalin.post("/user", Server::handleRegister);
        // Register your endpoints and exception handlers here.

    }

    public int run(int desiredPort) {
        mJavalin.start(desiredPort);
        return mJavalin.port();
    }

    public void stop() {
        mJavalin.stop();
    }

    private static void handleRegister(Context ctx) throws DataAccessException
    {
        RegisterRequest request = ctx.bodyAsClass(RegisterRequest.class);
        mService.register(request);
    }
}
