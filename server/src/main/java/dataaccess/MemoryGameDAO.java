package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import model.GameData;

import java.util.HashSet;
import java.util.Set;

public class MemoryGameDAO implements GameDAO
{
    private static final Set<GameData> games = new HashSet<>();
    private int gameIDCounter = 0;

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        for (GameData game : games) {
            if (game.gameName().equals(gameName)) {
                throw new DataAccessException("Game name already taken");
            }
        }
        GameData newGame = new GameData(gameIDCounter, null, null, gameName, new ChessGame());
        gameIDCounter++;
        games.add(newGame);
        return newGame;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData game : games) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        throw new DataAccessException("Game does not exist");
    }

    @Override
    public Set<GameData> listGames() throws DataAccessException {
        if (games.isEmpty())
        {
            throw new DataAccessException("No games available to list");
        }
        return games;
    }

    @Override
    public void updateGame(int gameID, String color, String username, ChessMove move) throws DataAccessException, InvalidMoveException {
        GameData game = getGame(gameID);
        if (color != null && username != null)
        {
            if (color.equals("WHITE"))
            {
                if (game.whiteUsername() != null)
                {
                    throw new DataAccessException("White Player already taken");
                }
                GameData newGame = new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game());
                games.remove(game);
                games.add(newGame);
            }
            else if (color.equals("BLACK"))
            {
                if (game.blackUsername() != null)
                {
                    throw new DataAccessException("Black Player already taken");
                }
                GameData newGame = new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game());
                games.remove(game);
                games.add(newGame);
            }
        }
        else if (move != null)
        {
            game.game().makeMove(move);
        }
        throw new DataAccessException("No player or move specified to update");
    }

    @Override
    public void clearGames()
    {
        games.clear();
    }
}
