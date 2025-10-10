package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.http.UnauthorizedResponse;
import model.*;

public class TestUserService
{
    private String mAuthToken;

    public static void main(String[] args) {
        MemoryUserDAO userData = new MemoryUserDAO();
        MemoryAuthDAO authData = new MemoryAuthDAO();

        TestUserService myTester = new TestUserService();
        UserService tester = new UserService(userData, authData);

        // Test Register
        System.out.println("Testing UserService. Begin by registering StormLordZeus");
        myTester.testRegister(tester);
        System.out.println("Attempting to re-register StormLordZeus");
        myTester.testRegister(tester);
        System.out.println();

        // Test Login
        System.out.println("Logging in as StormLordZeus with the correct password");
        myTester.testLogin(tester, "StormLordZeus", "I'm really cool");
        System.out.println("Logging in as StormLordZeus with an incorrect password");
        myTester.testLogin(tester, "StormLordZeus", "I'm not so cool");
        System.out.println("Logging in as an unknown user");
        myTester.testLogin(tester, "LameoNotZeus", "I'm really cool");
        System.out.println();

        // Test Logout
        System.out.println("Logging StormLordZeus out");
        myTester.testLogout(tester);
        System.out.println("Attempting to log StormLordZeus out again");
        myTester.testLogout(tester);
        System.out.println();

        // Test clear
        System.out.println("Testing clear users");
        myTester.testClearUsers(tester);
        System.out.println("Proving users have been cleared by attempting to log in as StormLordZeus with correct password");
        myTester.testLogin(tester, "StormLordZeus", "I'm really cool");
        System.out.println();

        System.out.println("Testing of UserService complete");
    }

    public TestUserService()
    {

    }

    public void testRegister(UserService tester)
    {
        try
        {
            RegisterRequest requestTest = new RegisterRequest(
                    "StormLordZeus", "I'm really cool", "myEmailIsBetterThanYours@ImAwesome.com");
            RegisterResult result = tester.register(requestTest);
            System.out.println("Successfully registered " + result.username() + " With auth token: " + result.authToken());

        }
        catch (DataAccessException e)
        {
            System.out.println("Successfully threw exception with error: " + e.getMessage());
        }
    }

    public void testLogin(UserService tester, String username, String password)  {
        try
        {
            LoginRequest requestTest = new LoginRequest(username, password);
            LoginResult result = tester.login(requestTest);
            mAuthToken = result.authToken();
            System.out.println("Successfully logged in as " + result.username() + " With auth token: " + result.authToken());
        }
        catch (DataAccessException e)
        {
            System.out.println("Successfully threw exception with error: " + e.getMessage());
        }
    }

    public void testLogout(UserService tester)  {
        try
        {
            LogoutRequest requestTest = new LogoutRequest(mAuthToken);
            tester.logout(requestTest);
            System.out.println("Successfully logged out user");
        }
        catch (UnauthorizedResponse e)
        {
            System.out.println("Successfully threw exception with unauthorized error: " + e.getMessage());
        }
        catch (DataAccessException e)
        {
            System.out.println("Successfully threw exception with error: " + e.getMessage());
        }
    }

    public void testClearUsers(UserService tester)  {
        tester.clearUsers();
        System.out.println("Successfully cleared all users");
    }
}
