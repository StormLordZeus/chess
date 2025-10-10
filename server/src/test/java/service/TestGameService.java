package service;

import chess.InvalidMoveException;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.*;
import org.eclipse.jetty.http.BadMessageException;

public class TestGameService
{
    private static String mAuthToken;

    public static void main(String[] args) {
        MemoryGameDAO gameData = new MemoryGameDAO();
        MemoryAuthDAO authData = new MemoryAuthDAO();
        MemoryUserDAO userData = new MemoryUserDAO();

        TestGameService myTester = new TestGameService();
        GameService tester = new GameService(gameData, authData);
        UserService userServer = new UserService(userData, authData);


        try {
            RegisterResult myUser = userServer.register(new RegisterRequest("StormLordZeus", "123", "blank"));
            mAuthToken = myUser.authToken();
        }
        catch (DataAccessException e)
        {
            System.out.println("Failed to register user before testing");
        }

        // Test CreateGame
        System.out.println("Testing GameService. Begin by creating game: FirstGame");
        myTester.testCreateGame(tester, "FirstGame");
        System.out.println("Attempting to recreate FirstGame");
        myTester.testCreateGame(tester, "FirstGame");
        System.out.println();

        // Test JoinGame
        System.out.println("Joining FirstGame as player white as StormLordZeus");
        myTester.testJoinGame(tester, 1, "WHITE");
        System.out.println("Joining FirstGame as player black as StormLordZeus");
        myTester.testJoinGame(tester, 1, "BLACK");
        System.out.println("Joining FirstGame again as player white as StormLordZeus");
        myTester.testJoinGame(tester, 1, "WHITE");
        System.out.println("Joining FirstGame again as player black as StormLordZeus");
        myTester.testJoinGame(tester, 1, "BLACK");
        System.out.println("Joining an unknown gameID as white as StormLordZeus");
        myTester.testJoinGame(tester, 100, "WHITE");
        System.out.println();

        // Test ListGames
        System.out.println("Listing all games. First create SecondGame and join as white");
        myTester.testCreateGame(tester, "SecondGame");
        myTester.testJoinGame(tester, 2, "WHITE");
        myTester.testListGames(tester);
        System.out.println("I will test the negative ListGames test after testing clear so there are no games to list");
        System.out.println();

        // Test clearGames
        System.out.println("Testing clearGames");
        myTester.testClearGames(tester);
        System.out.println("Proving games have been cleared by listing no games");
        myTester.testListGames(tester);
        System.out.println();

        System.out.println("Testing of UserService complete");
    }

    public TestGameService()
    {

    }

    public void testCreateGame(GameService tester, String gameName)
    {
        try
        {
            CreateGameRequest requestTest = new CreateGameRequest(gameName, mAuthToken);
            CreateGameResult result = tester.createGame(requestTest);
            System.out.println("Successfully Created game with ID " + result.gameID());
        }
        catch (DataAccessException e)
        {
            System.out.println("Successfully threw exception with error: " + e.getMessage());
        }
    }

    public void testListGames(GameService tester)  {
        try
        {
            ListGamesRequest requestTest = new ListGamesRequest(mAuthToken);
            ListGamesResult result = tester.listGames(requestTest);
            System.out.println("Successfully listing games: " + result.games().toString());
        }
        catch (DataAccessException e)
        {
            System.out.println("Successfully threw exception with error: " + e.getMessage());
        }
    }

    public void testJoinGame(GameService tester, int gameID, String color)  {
        try
        {
            JoinGameRequest requestTest = new JoinGameRequest(gameID,color,mAuthToken);
            tester.joinGame(requestTest);
            System.out.println("Successfully joined the game with ID " + gameID + " as " + color);
        }
        catch (BadMessageException e)
        {
            System.out.println("Successfully threw exception with bad message error: " + e.getMessage());
        }
        catch (DataAccessException e)
        {
            System.out.println("Successfully threw exception with error: " + e.getMessage());
        }
        catch (InvalidMoveException e) {
            System.out.println("How did you even trigger this error? " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void testClearGames(GameService tester)
    {
        tester.clearGames();
        System.out.println("Successfully cleared all games");
    }
}
