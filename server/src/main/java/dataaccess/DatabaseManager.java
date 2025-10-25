package dataaccess;

import java.sql.*;
import java.util.Properties;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class DatabaseManager
{
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static
    {
        loadPropertiesFromResources();
    }

    private static final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS AuthData (
            authToken varchar(255) NOT NULL,
            username varchar(255) NOT NULL,
            PRIMARY KEY (authToken)
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS UserData (
            username varchar(255) NOT NULL,
            password varchar(255) NOT NULL,
            email varchar(255) NOT NULL,
            PRIMARY KEY (username)
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS GameData (
            gameID INT NOT NULL AUTO_INCREMENT,
            whiteUsername varchar(255),
            blackUsername varchar(255),
            gameName varchar(255) NOT NULL,
            game longtext NOT NULL,
            PRIMARY KEY (gameID)
            );
            """
    };

    /**
     * Creates the database if it does not already exist.
     */
    static public void createDatabase() throws DataAccessException
    {
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(statement))
        {
            preparedStatement.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("failed to create database", ex);
        }
        try (var conn = DatabaseManager.getConnection())
        {
            for (String createTableStatement : createStatements)
            {
                try (var preparedTableStatement = conn.prepareStatement(createTableStatement))
                {
                    preparedTableStatement.executeUpdate();
                }
            }
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("failed to create database tables", ex);
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DatabaseManager.getConnection()) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException
    {
        try
        {
            //do not wrap the following line with a try-with-resources
            var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setCatalog(databaseName);
            return conn;
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("failed to get connection", ex);
        }
    }

    private static void loadPropertiesFromResources()
    {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null)
            {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        }
        catch (Exception ex)
        {
            throw new RuntimeException("unable to process db.properties", ex);
        }
    }

    private static void loadProperties(Properties props)
    {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
    }

    public static int executeUpdate(String statement, Object... params) throws DataAccessException
    {
        try (Connection conn = DatabaseManager.getConnection())
        {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS))
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

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        }
        catch (SQLException e)
        {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }
}
