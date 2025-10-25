package dataaccess;

import chess.ChessMove;
import chess.InvalidMoveException;
import model.GameData;

import java.util.Set;

public interface GameDAO {
    GameData createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    Set<GameData> listGames() throws DataAccessException;
    void updateGame(int gameID, String color, String username, ChessMove move)
            throws DataAccessException, InvalidMoveException;
    void clearGames() throws DataAccessException;
}
