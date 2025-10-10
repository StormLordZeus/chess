package service;

import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.RegisterRequest;
import model.RegisterResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {

    @Test
    void registerSuccess() throws DataAccessException
    {
        MemoryUserDAO userData = new MemoryUserDAO();
        MemoryAuthDAO authData = new MemoryAuthDAO();
        UserService tester = new UserService(userData, authData);

        RegisterRequest requestTest = new RegisterRequest(
                "StormLordZeus", "I'm really cool", "myEmailIsBetterThanYours@ImAwesome.com");
        RegisterResult result = tester.register(requestTest);
        assertEquals("StormLordZeus", result.username());
    }

    @Test
    void registerFailure()
    {
        MemoryUserDAO userData = new MemoryUserDAO();
        MemoryAuthDAO authData = new MemoryAuthDAO();
        UserService tester = new UserService(userData, authData);

        RegisterRequest requestTest = new RegisterRequest(
                "StormLordZeus", "I'm really cool", "myEmailIsBetterThanYours@ImAwesome.com");
        assertThrows(AlreadyTakenException.class, () -> tester.register(requestTest));
    }

    @Test
    void loginSuccess()
    {

    }

    @Test
    void loginFailure() {
    }

    @Test
    void logoutSuccess() {
    }

    @Test
    void logoutFailure() {
    }

    @Test
    void clearUsersSuccess() {
    }
}