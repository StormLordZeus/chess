package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLUserDAOTest {
    static SQLUserDAO mUserDataAccess;
    static UserData mUser;

    @BeforeAll
    static void setupDatabase() throws DataAccessException
    {
        mUserDataAccess = new SQLUserDAO();
        mUser = new UserData("Me", "yeah", "gmail");
        DatabaseManager.createDatabase();
        mUserDataAccess.clearUsers();
    }

    @Test
    void createUserSuccess() throws DataAccessException
    {
        mUserDataAccess.createUser(mUser);
        assertNotNull(mUserDataAccess.getUser("Me"));
    }
    @Test
    void createUserFailure()
    {
        try
        {
            mUserDataAccess.clearUsers();
            mUserDataAccess.createUser(mUser);
            assertThrows(AlreadyTakenException.class, () -> mUserDataAccess.createUser(mUser));
        } catch (DataAccessException e)
        {
            assertTrue(true);
        }
    }

    @Test
    void getUserSuccess() throws DataAccessException
    {
        mUserDataAccess.clearUsers();
        mUserDataAccess.createUser(mUser);
        assertNotNull(mUserDataAccess.getUser("Me"));
    }

    @Test
    void getUserFailure() throws DataAccessException
    {
        assertNull(mUserDataAccess.getUser("Hello?"));
    }

    @Test
    void clearUsersSuccess() throws DataAccessException
    {
        mUserDataAccess.clearUsers();
        mUserDataAccess.createUser(mUser);
        mUserDataAccess.clearUsers();
        assertNull(mUserDataAccess.getUser("Hello?"));
    }
}