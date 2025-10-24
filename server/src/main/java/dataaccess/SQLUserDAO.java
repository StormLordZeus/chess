package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Types.NULL;

public class SQLUserDAO implements UserDAO
{
    @Override
    public void createUser(UserData user) throws DataAccessException
    {
        if (getUser(user.username()) != null)
        {
            throw new AlreadyTakenException("Error: User already taken");
        }
        String sql = "INSERT INTO UserData (username, password, email) Values (?, ?, ?)";
        executeUpdate(sql, user.username(), user.password(), user.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException
    {
        try (Connection conn = DatabaseManager.getConnection())
        {
            String sql = "SELECT * FROM AuthData WHERE username = ?";
            try(PreparedStatement ps = conn.prepareStatement(sql))
            {
                ps.setString(1,username);
                try (ResultSet rs = ps.executeQuery())
                {
                    if (rs.next())
                    {
                        String password = rs.getString("password");
                        return new UserData(rs.getString("username"),password,rs.getString("email"));
                    }
                    else
                    {
                        return null;
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
    public void clearUsers()
    {
        try {
            String sql = "TRUNCATE UserData";
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
