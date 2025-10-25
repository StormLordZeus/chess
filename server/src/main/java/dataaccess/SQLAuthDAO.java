package dataaccess;

import io.javalin.http.UnauthorizedResponse;
import model.AuthData;
import org.eclipse.jetty.http.BadMessageException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class SQLAuthDAO implements AuthDAO
{
    @Override
    public void createAuth(AuthData auth) throws DataAccessException
    {
        if (auth.authToken() == null || auth.username() == null)
        {
            throw new BadMessageException("Error: AuthData had a null field.");
        }

        try
        {
            getAuth(auth.authToken());
            throw new AlreadyTakenException("Error: Auth Token already exists");
        }
        catch (UnauthorizedResponse e)
        {
            String sql = "INSERT INTO AuthData (authToken, username) Values (?, ?)";
            DatabaseManager.executeUpdate(sql, auth.authToken(), auth.username());
        }

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
        getAuth(auth.authToken());
        String sql = "DELETE FROM AuthData WHERE authToken = ?";
        DatabaseManager.executeUpdate(sql, auth.authToken());
    }

    @Override
    public void clearAuths() throws DataAccessException
    {
        try {
            String sql = "TRUNCATE AuthData";
            DatabaseManager.executeUpdate(sql);
        }
        catch (DataAccessException e)
        {
            throw new DataAccessException("Error: Couldn't connect to the database when clearing");
        }
    }
}
