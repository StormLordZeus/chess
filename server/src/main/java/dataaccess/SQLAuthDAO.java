package dataaccess;

import io.javalin.http.UnauthorizedResponse;
import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Types.NULL;


public class SQLAuthDAO implements AuthDAO
{
    @Override
    public void createAuth(AuthData auth) throws DataAccessException
    {
        if (getAuth(auth.authToken()) == null)
        {
            throw new AlreadyTakenException("Error: Auth Token already exists");
        }
        String sql = "INSERT INTO AuthData (authToken, username) Values (?, ?)";
        executeUpdate(sql, auth.authToken(), auth.username());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException
    {
        try (Connection conn = DatabaseManager.getConnection())
        {
            String sql = "SELECT * FROM AuthData WHERE authToken = ?";
            try(PreparedStatement ps = conn.prepareStatement(sql))
            {
                ps.setString(1,authToken);
                try (ResultSet rs = ps.executeQuery())
                {
                    if (rs.next())
                    {
                        return new AuthData(rs.getString("authToken"),rs.getString("username"));
                    }
                    else
                    {
                        throw new UnauthorizedResponse("Error: No auth token found");
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
    public void deleteAuth(AuthData auth) throws DataAccessException
    {
        String sql = "DELETE FROM AuthData WHERE authToken = ?";
        executeUpdate(sql, auth.authToken());
    }

    @Override
    public void clearAuths()
    {
        try {
            String sql = "TRUNCATE AuthData";
            executeUpdate(sql);
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException("Couldn't connect to the database when clearing");
        }
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException
    {
        try (Connection conn = DatabaseManager.getConnection())
        {
            try (PreparedStatement ps = conn.prepareStatement(statement))
            {
                for (int i = 0; i < params.length; i++)
                {
                    Object param = params[i];
                    if (param instanceof String p)
                    {
                        ps.setString(i + 1, p);
                    }
                    else if (param == null)
                    {
                        ps.setNull(i + 1, NULL);
                    }
                }
                ps.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }
}
