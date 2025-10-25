package dataaccess;

import io.javalin.http.UnauthorizedResponse;
import model.AuthData;
import org.eclipse.jetty.http.BadMessageException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

class SQLAuthDAOTest {

    static SQLAuthDAO mAuthDataAccess;
    static AuthData mAuthData;

    @BeforeAll
    static void setupDatabase() throws DataAccessException
    {
        mAuthDataAccess = new SQLAuthDAO();
        DatabaseManager.createDatabase();
        String authToken = UUID.randomUUID().toString();
        mAuthData = new AuthData(authToken, "StormLordZeus");

    }

    @Test
    void createAuthSuccess() throws DataAccessException
    {
        mAuthDataAccess.createAuth(mAuthData);
        assertTrue(true);
    }
    @Test
    void createAuthFailure()
    {
        assertThrows(BadMessageException.class, () -> mAuthDataAccess.createAuth(new AuthData(null,"MyUsername")));
    }

    @Test
    void getAuthSuccess() throws DataAccessException
    {
        mAuthDataAccess.clearAuths();
        mAuthDataAccess.createAuth(mAuthData);
        AuthData data = mAuthDataAccess.getAuth(mAuthData.authToken());
        assertEquals(mAuthData.authToken(), data.authToken());
        assertEquals(mAuthData.username(), data.username());
    }
    @Test
    void getAuthFailure()
    {
        assertThrows(UnauthorizedResponse.class, () -> mAuthDataAccess.getAuth("Best auth token ever"));
    }

    @Test
    void deleteAuthSuccess() throws DataAccessException
    {
        mAuthDataAccess.clearAuths();
        mAuthDataAccess.createAuth(mAuthData);
        mAuthDataAccess.deleteAuth(mAuthData);
        assertThrows(UnauthorizedResponse.class, () -> mAuthDataAccess.getAuth(mAuthData.authToken()));
    }
    @Test
    void deleteAuthFailure() throws DataAccessException
    {
        mAuthDataAccess.clearAuths();

        assertThrows(UnauthorizedResponse.class, () -> mAuthDataAccess.deleteAuth(mAuthData));

    }

    @Test
    void clearAuthsSuccess() throws DataAccessException
    {
        mAuthDataAccess.clearAuths();
        mAuthDataAccess.createAuth(mAuthData);
        mAuthDataAccess.clearAuths();
        assertThrows(UnauthorizedResponse.class, () -> mAuthDataAccess.getAuth(mAuthData.authToken()));

    }
}