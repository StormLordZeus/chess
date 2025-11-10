package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        DatabaseManager.executeUpdate(sql, user.username(),
                BCrypt.hashpw(user.password(), BCrypt.gensalt()), user.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException
    {
        try (Connection conn = DatabaseManager.getConnection())
        {
            String sql = "SELECT * FROM UserData WHERE username = ?";
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
    public void clearUsers() throws DataAccessException
    {
        String sql = "TRUNCATE UserData";
        DatabaseManager.executeUpdate(sql);
    }
}
