package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class ReplLoop {
    private final PreLoginClient mPreLogClient;
    private final PostLoginClient mPostLogClient;
    private final GameplayClient mGameClient;
    Scanner mScanner = new Scanner(System.in);

    public ReplLoop (String aUrl)
    {
        ServerFacade facade = new ServerFacade(aUrl);
        mPreLogClient = new PreLoginClient(facade);
        mPostLogClient = new PostLoginClient(facade);
        mGameClient = new GameplayClient(facade);
    }

    public void run()
    {
        System.out.println("Welcome to chess! Login to play");
        preLoginLoop();
    }

    private void preLoginLoop()
    {
        System.out.println(mPreLogClient.help());

        List<String> preResult = new ArrayList<>();
        preResult.add("");
        while (!preResult.getFirst().equals("quit"))
        {
            System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED_OUT] >>> " + SET_TEXT_COLOR_GREEN );
            String line = mScanner.nextLine();

            try
            {
                preResult = mPreLogClient.evaluate(line);
                System.out.print(SET_TEXT_COLOR_BLUE + preResult.get(1));
                if (preResult.getFirst().equals("login") || preResult.getFirst().equals("register"))
                {
                    System.out.println("Auth token is " + preResult.getLast());
                    postLoginLoop(preResult.getLast());
                    System.out.println(mPreLogClient.help());
                }
            }
            catch (Throwable e)
            {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void postLoginLoop(String aAuthToken)
    {
        System.out.println(mPostLogClient.help());
        System.out.println("My auth token is " + aAuthToken);

        List<String> postResult = new ArrayList<>();
        postResult.add("");
        while (!postResult.getFirst().equals("logout"))
        {
            System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED_IN] >>> " + SET_TEXT_COLOR_GREEN );
            String line = mScanner.nextLine();

            try
            {
                postResult = mPostLogClient.evaluate(line, aAuthToken);
                System.out.print(SET_TEXT_COLOR_BLUE + postResult.get(1));
                String action = postResult.getFirst();
                if (action.equals("join") || action.equals("observe"))
                {
                    System.out.println();
                    gameplayLoop(postResult.getLast());
                    System.out.println(mPostLogClient.help());
                }
            }
            catch (Throwable e)
            {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void gameplayLoop(String aColor)
    {
        System.out.println(mGameClient.help());

        List<String> gameResult = new ArrayList<>();
        gameResult.add("");
        while (!gameResult.getFirst().equals("quit"))
        {
            DrawBoard.drawChessBoard(aColor);
            System.out.print("\n" + RESET_TEXT_COLOR + "[GAMEPLAY] >>> " + SET_TEXT_COLOR_GREEN );
            String line = mScanner.nextLine();

            try
            {
                gameResult = mGameClient.evaluate(line);
                System.out.print(SET_TEXT_COLOR_BLUE + gameResult.get(1));
            }
            catch (Throwable e)
            {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

}
