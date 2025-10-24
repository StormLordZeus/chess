package dataaccess;

import chess.ChessMove;
import chess.InvalidMoveException;
import model.GameData;

import java.util.Set;

public class SQLGameDAO implements GameDAO
{
    @Override
    public GameData createGame(String gameName) throws DataAccessException
    {
        return null;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException
    {
        return null;
    }

    @Override
    public Set<GameData> listGames() throws DataAccessException
    {
        return Set.of();
    }

    @Override
    public void updateGame(int gameID, String color, String username, ChessMove move) throws DataAccessException, InvalidMoveException
    {

    }

    @Override
    public void clearGames()
    {

    }
}
