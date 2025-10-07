package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import model.GameData;

import java.util.Collection;
import java.util.Set;

public interface GameDAO {
    GameData createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    Set<GameData> listGames() throws DataAccessException;
    GameData updateGame(int gameID, ChessGame.TeamColor color, String username, ChessMove move)
            throws DataAccessException, InvalidMoveException;
    void clearGames();
}
