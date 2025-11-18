package service;

import chess.InvalidMoveException;
import dataaccess.*;
import io.javalin.http.UnauthorizedResponse;
import model.*;
import org.eclipse.jetty.http.BadMessageException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GameServiceTest {
    SQLGameDAO gameData = new SQLGameDAO();
    SQLAuthDAO authData = new SQLAuthDAO();
    String mAuthToken;
    GameService mTester;

    @Test
    @Order (6)
    void listGamesSuccess() throws DataAccessException
    {
        ListGamesRequest requestTest = new ListGamesRequest(mAuthToken);
        ListGamesResult result = mTester.listGames(requestTest);
        Set<GameData> games = result.games();
        GameData first = games.iterator().next();
        assertEquals(1, first.gameID());
        assertEquals("Storm", first.whiteUsername());
    }

    @Test
    @Order (1)
    void listGamesFailure()
    {
        mTester = new GameService(gameData, authData);
        ListGamesRequest requestTest = new ListGamesRequest(mAuthToken);
        assertThrows(UnauthorizedResponse.class, () -> mTester.listGames(requestTest));
    }

    @Test
    @Order (2)
    void createGameSuccess() throws DataAccessException
    {
        mTester.clearGames();
        RegisterResult result = new UserService(new MemoryUserDAO(), authData).register(
                new RegisterRequest("Storm", "1", "no"));
        mAuthToken = result.authToken();
        CreateGameRequest requestTest = new CreateGameRequest("FirstGame", mAuthToken);
        CreateGameResult result2 = mTester.createGame(requestTest);
        assertEquals(1, result2.gameID());
    }

    @Test
    @Order (3)
    void createGameFailure()
    {
        CreateGameRequest requestTest = new CreateGameRequest("FirstGame", mAuthToken);
        assertThrows(AlreadyTakenException.class, () -> mTester.createGame(requestTest));
    }

    @Test
    @Order (4)
    void joinGameSuccess() throws DataAccessException, InvalidMoveException
    {
        JoinGameRequest requestTest = new JoinGameRequest(1,"WHITE",mAuthToken);
        mTester.joinGame(requestTest);
        assertTrue(true);
    }

    @Test
    @Order (5)
    void joinGameFailure()
    {
        JoinGameRequest requestTest = new JoinGameRequest(1,"BLUE",mAuthToken);
        assertThrows(BadMessageException.class, () -> mTester.joinGame(requestTest));
    }

    @Test
    @Order(7)
    void clearGamesSuccess() throws DataAccessException
    {
        mTester.clearGames();
        assertTrue(true);
    }
}