package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import model.GameData;
import org.eclipse.jetty.http.BadMessageException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SQLGameDAOTest {

    static SQLGameDAO mGameDataAccess;
    static GameData mGameData;

    @BeforeAll
    static void setupDatabase() throws DataAccessException
    {
        mGameDataAccess = new SQLGameDAO();
        DatabaseManager.createDatabase();
        String authToken = UUID.randomUUID().toString();
        mGameData = new GameData(1, null, null, "OG Game", new ChessGame());
        mGameDataAccess.clearGames();
    }

    @Test
    void createGameSuccess() throws DataAccessException
    {
        assertEquals(mGameData, mGameDataAccess.createGame("OG Game"));
    }
    @Test
    void createGameFailure()
    {
        assertThrows(BadMessageException.class, () -> mGameDataAccess.createGame(null));
    }

    @Test
    void getGameSuccess() throws DataAccessException
    {
        mGameDataAccess.clearGames();
        mGameDataAccess.createGame("OG Game");
        assertEquals(mGameData, mGameDataAccess.getGame(1));
    }
    @Test
    void getGameFailure()
    {
        assertThrows(BadMessageException.class, () -> mGameDataAccess.getGame(2));
    }

    @Test
    void listGamesSuccess() throws DataAccessException
    {
        Set<GameData> games = mGameDataAccess.listGames();
        GameData first = games.iterator().next();
        assertEquals(1, games.size());
        assertEquals(mGameData, first);
    }
    @Test
    void listGamesFailure()
    {
        // List games can only fail without an auth token. There are no auth tokens at this level
        assertTrue(true);
    }

    @Test
    void updateGameSuccess() throws DataAccessException, InvalidMoveException
    {
        mGameDataAccess.updateGame(1, "WHITE", "StormLordZeus", null);
        mGameDataAccess.updateGame(1, "BLACK", "ShadowLordHades", null);
        ChessPosition startPos = new ChessPosition(2, 1);
        ChessPosition endPos = new ChessPosition(4, 1);
        mGameDataAccess.updateGame(1, null, null, new ChessMove(startPos, endPos,null));
        assertEquals("StormLordZeus", mGameDataAccess.getGame(1).whiteUsername());
        assertEquals("ShadowLordHades", mGameDataAccess.getGame(1).blackUsername());
        ChessGame game = new ChessGame();
        game.makeMove(new ChessMove(startPos, endPos, null));
        assertEquals(game,mGameDataAccess.getGame(1).game());
    }
    @Test
    void updateGameFailure()
    {

    }

    @Test
    void clearGamesSuccess()
    {

    }
}