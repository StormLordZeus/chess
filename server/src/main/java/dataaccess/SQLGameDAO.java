package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.http.BadMessageException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDAO implements GameDAO
{
    private int numGames = 0;
    @Override
    public GameData createGame(String gameName) throws DataAccessException
    {
        try (Connection conn = DatabaseManager.getConnection())
        {
            String sql = "SELECT * FROM GameData WHERE gameName = ?";
            try(PreparedStatement ps = conn.prepareStatement(sql))
            {
                ps.setString(1,gameName);
                try (ResultSet rs = ps.executeQuery())
                {
                    if (rs.next())
                    {
                        throw new AlreadyTakenException("Error: Game name already taken");
                    }
                    else
                    {
                        sql = "INSERT INTO GameData (whiteUsername, blackUsername, gameName, game) Values (?, ?, ?, ?)";
                        int id = DatabaseManager.executeUpdate(sql, null, null, gameName, new Gson().toJson(new ChessGame()));
                        numGames++;
                        return new GameData(id, null, null, gameName, new ChessGame());
                    }
                }

            }
        }
        catch (SQLException e)
        {
            throw new DataAccessException("Error: Failed to connect to the database");
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException
    {
        try (Connection conn = DatabaseManager.getConnection())
        {
            String sql = "SELECT * FROM GameData WHERE gameID = ?";
            try(PreparedStatement ps = conn.prepareStatement(sql))
            {
                ps.setInt(1,gameID);
                try (ResultSet rs = ps.executeQuery())
                {
                    if (rs.next())
                    {
                        ChessGame game = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                        return new GameData(rs.getInt("gameID"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                game);
                    }
                    else
                    {
                        throw new BadMessageException("Error: Game does not exist");
                    }
                }

            }
        }
        catch (SQLException e)
        {
            throw new DataAccessException("Error: Failed to connect to the database");
        }
    }

    @Override
    public Set<GameData> listGames() throws DataAccessException
    {
        final Set<GameData> games = new HashSet<>();
        for (int i = 0; i < numGames; i++)
        {
            games.add(getGame(i + 1));
        }
        return games;
    }

    @Override
    public void updateGame(int gameID, String color, String username, ChessMove move) throws DataAccessException, InvalidMoveException
    {
        if (color != null && username != null)
        {
            if (color.equals("WHITE"))
            {
                String sql = "UPDATE GameData SET whiteUsername = ? WHERE gameID = ?";
                DatabaseManager.executeUpdate(sql, username, gameID);
            }
            else if (color.equals("BLACK"))
            {
                String sql = "UPDATE GameData SET blackUsername = ? WHERE gameID = ?";
                DatabaseManager.executeUpdate(sql, username, gameID);
            }
        }
        else if (move != null)
        {
            GameData game = getGame(gameID);
            game.game().makeMove(move);
            String sql = "UPDATE GameData SET game = ? WHERE gameID = ?";
            DatabaseManager.executeUpdate(sql, new Gson().toJson(game) ,gameID);
        }
        throw new BadMessageException("Error: No player or move specified to update");
    }

    @Override
    public void clearGames()
    {
        try {
            String sql = "TRUNCATE GameData";
            DatabaseManager.executeUpdate(sql);
            numGames = 0;
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException("Couldn't connect to the database when clearing");
        }
    }
}
