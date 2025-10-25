package service;

import dataaccess.*;
import io.javalin.http.UnauthorizedResponse;
import model.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {

    SQLUserDAO userData = new SQLUserDAO();
    SQLAuthDAO authData = new SQLAuthDAO();
    String mAuthToken;
    UserService mTester;

    @Test
    @Order (1)
    void registerSuccess() throws DataAccessException
    {
        mTester = new UserService(userData, authData);
        RegisterRequest requestTest = new RegisterRequest(
                "StormLordZeus", "I'm really cool", "myEmailIsBetterThanYours@ImAwesome.com");
        RegisterResult result = mTester.register(requestTest);
        assertEquals("StormLordZeus", result.username());
    }

    @Test
    @Order (2)
    void registerFailure()
    {
        RegisterRequest requestTest = new RegisterRequest(
                "StormLordZeus", "I'm really cool", "myEmailIsBetterThanYours@ImAwesome.com");
        assertThrows(AlreadyTakenException.class, () -> mTester.register(requestTest));
    }

    @Test
    @Order (3)
    void loginSuccess() throws DataAccessException
    {
        LoginRequest requestTest = new LoginRequest("StormLordZeus", "I'm really cool");
        LoginResult result = mTester.login(requestTest);
        mAuthToken = result.authToken();
        assertEquals("StormLordZeus", result.username());
    }

    @Test
    @Order (4)
    void loginFailure()
    {
        LoginRequest requestTest = new LoginRequest("StormLordZeus", "I'm not so cool");
        assertThrows(InvalidCredentialsException.class, () -> mTester.login(requestTest));
    }

    @Test
    @Order (5)
    void logoutSuccess() throws DataAccessException
    {
        LogoutRequest requestTest = new LogoutRequest(mAuthToken);
        mTester.logout(requestTest);
        assertTrue(true);
    }

    @Test
    @Order (6)
    void logoutFailure()
    {
        LogoutRequest requestTest = new LogoutRequest(mAuthToken);
        assertThrows(UnauthorizedResponse.class, () -> mTester.logout(requestTest));
    }

    @Test
    @Order (7)
    void clearUsersSuccess() throws DataAccessException
    {
        mTester.clearUsers();
        assertTrue(true);
    }
}