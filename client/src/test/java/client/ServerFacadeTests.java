package client;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;
import exception.ResponseException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests
{

    private static Server mServer;
    private static ServerFacade mFacade;

    @BeforeAll
    public static void init()
    {
        mServer = new Server();
        var port = mServer.run(0);
        System.out.println("Started test HTTP server on " + port);
        mFacade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer()
    {
        mServer.stop();
    }


    @Test
    void registerSuccess() throws ResponseException
    {
        mFacade.clear();
        RegisterResult result = mFacade.register(
                new RegisterRequest("Christopher", "123", "Code"));
        assertEquals("Christopher", result.username());
    }

    @Test
    void registerFailure() throws ResponseException
    {
        mFacade.clear();
        assertThrows(ResponseException.class, () ->
                mFacade.register(new RegisterRequest(null, "123", "Code")));
    }

    @Test
    void loginSuccess() throws ResponseException
    {
        mFacade.clear();
        mFacade.register(new RegisterRequest("Christopher", "123", "Code"));
        LoginResult result2 = mFacade.login(new LoginRequest("Christopher", "123"));
        assertEquals("Christopher", result2.username());
    }

    @Test
    void loginFailure() throws ResponseException
    {
        mFacade.clear();
        assertThrows(ResponseException.class, () ->
                mFacade.login(new LoginRequest("Christopher", "123")));
    }

    @Test
    void logoutSuccess() throws ResponseException
    {
        mFacade.clear();
        RegisterResult result = mFacade.register(new RegisterRequest("Christopher", "123", "Code"));
        mFacade.logout(new LogoutRequest(result.authToken()));
        assertTrue(true);
    }

    @Test
    void logoutFailure() throws ResponseException
    {
        mFacade.clear();
        RegisterResult result = mFacade.register(new RegisterRequest("Christopher", "123", "Code"));
        mFacade.logout(new LogoutRequest(result.authToken()));
        assertThrows(ResponseException.class, () -> mFacade.logout(new LogoutRequest(result.authToken())));
    }

    @Test
    void listGamesSuccess() throws ResponseException
    {
        mFacade.clear();
        RegisterResult result = mFacade.register(new RegisterRequest("Christopher", "123", "Code"));
        mFacade.createGame(new CreateGameRequest("GameOne", result.authToken()));
        ListGamesResult result2 = mFacade.listGames(new ListGamesRequest(result.authToken()));
        Set<GameData> games = result2.games();
        GameData first = games.iterator().next();
        assertEquals(1, first.gameID());
    }

    @Test
    void listGamesFailure() throws ResponseException
    {
        mFacade.clear();
        RegisterResult result = mFacade.register(new RegisterRequest("Christopher", "123", "Code"));
        mFacade.logout(new LogoutRequest(result.authToken()));
        assertThrows(ResponseException.class, () -> mFacade.listGames(new ListGamesRequest(result.authToken())));
    }

    @Test
    void createGameSuccess() throws ResponseException
    {
        mFacade.clear();
        RegisterResult result = mFacade.register(new RegisterRequest("Christopher", "123", "Code"));
        CreateGameResult result2 = mFacade.createGame(new CreateGameRequest("GameOne", result.authToken()));
        assertEquals(1, result2.gameID());
    }

    @Test
    void createGameFailure() throws ResponseException
    {
        mFacade.clear();
        RegisterResult result = mFacade.register(new RegisterRequest("Christopher", "123", "Code"));
        mFacade.createGame(new CreateGameRequest("GameOne", result.authToken()));
        assertThrows(ResponseException.class, () -> mFacade.createGame(new CreateGameRequest("GameOne", result.authToken())));
    }

    @Test
    void joinGameSuccess() throws ResponseException
    {
        mFacade.clear();
        RegisterResult result = mFacade.register(new RegisterRequest("Christopher", "123", "Code"));
        CreateGameResult result2 = mFacade.createGame(new CreateGameRequest("GameOne", result.authToken()));
        mFacade.joinGame(new JoinGameRequest(result2.gameID(), "WHITE", result.authToken()));
        ListGamesResult result3 = mFacade.listGames(new ListGamesRequest(result.authToken()));
        Set<GameData> games = result3.games();
        GameData first = games.iterator().next();
        assertEquals("Christopher", first.whiteUsername());
    }

    @Test
    void joinGameFailure() throws ResponseException
    {
        mFacade.clear();
        RegisterResult result = mFacade.register(new RegisterRequest("Christopher", "123", "Code"));
        mFacade.createGame(new CreateGameRequest("GameOne", result.authToken()));
        assertThrows(ResponseException.class, () -> mFacade.joinGame(new JoinGameRequest(2, "WHITE", result.authToken())));
    }

    @Test
    void clearSuccess() throws ResponseException
    {
        mFacade.clear();
        RegisterResult result = mFacade.register(new RegisterRequest("Christopher", "123", "Code"));
        mFacade.createGame(new CreateGameRequest("GameOne", result.authToken()));
        mFacade.clear();
        assertThrows(ResponseException.class, () -> mFacade.listGames(new ListGamesRequest(result.authToken())));
    }

}
