package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import model.GameData;
import org.eclipse.jetty.http.BadMessageException;

import java.util.HashSet;
import java.util.Set;

public class MemoryGameDAO implements GameDAO
{
    private static final Set<GameData> GAMES = new HashSet<>();
    private int gameIDCounter = 1;

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        for (GameData game : GAMES) {
            if (game.gameName().equals(gameName)) {
                throw new AlreadyTakenException("Error: Game name already taken");
            }
        }
        GameData newGame = new GameData(gameIDCounter, null, null, gameName, new ChessGame(), false);
        gameIDCounter++;
        GAMES.add(newGame);
        return newGame;
    }

    @Override
    public GameData getGame(int gameID)  {
        for (GameData game : GAMES) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        throw new BadMessageException("Error: Game does not exist");
    }

    @Override
    public Set<GameData> listGames()
    {
        return GAMES;
    }

    @Override
    public void updateGame(int gameID, String color, String username, ChessMove move) throws DataAccessException, InvalidMoveException
    {
        GameData game = getGame(gameID);
        if (color != null && username != null)
        {
            if (color.equals("WHITE"))
            {
                if (game.whiteUsername() != null)
                {
                    throw new AlreadyTakenException("Error: White Player already taken");
                }
                GameData newGame = new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game(), false);
                GAMES.remove(game);
                GAMES.add(newGame);
                return;
            }
            else if (color.equals("BLACK"))
            {
                if (game.blackUsername() != null)
                {
                    throw new AlreadyTakenException("Error: Black Player already taken");
                }
                GameData newGame = new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game(), false);
                GAMES.remove(game);
                GAMES.add(newGame);
                return;
            }
        }
        else if (move != null)
        {
            game.game().makeMove(move);
            return;
        }
        throw new BadMessageException("Error: No player or move specified to update");
    }

    @Override
    public void gameOver(int gameID)
    {

    }

    @Override
    public void clearGames()
    {
        GAMES.clear();
    }

    @Override
    public void leaveGame(int gameID, String color) {

    }
}
