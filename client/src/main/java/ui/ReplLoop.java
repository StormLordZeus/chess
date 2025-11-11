package ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class ReplLoop {
    private final PreLoginClient mPreLogClient;
    private final PostLoginClient mPostLogClient;
    private final GameplayClient mGameClient;
    private final ServerFacade mFacade;
    Scanner mScanner = new Scanner(System.in);

    public ReplLoop (String aUrl)
    {
        mFacade = new ServerFacade(aUrl);
        mPreLogClient = new PreLoginClient(aUrl, mFacade);
        mPostLogClient = new PostLoginClient(aUrl, mFacade);
        mGameClient = new GameplayClient(aUrl, mFacade);
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
        while (!preResult.getFirst().equals("quit"))
        {
            System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN );
            String line = mScanner.nextLine();

            try
            {
                preResult = mPreLogClient.evaluate(line);
                System.out.print(SET_TEXT_COLOR_BLUE + preResult.get(1));
                if (preResult.getFirst().equals("login") || preResult.getFirst().equals("register"))
                {
                    System.out.println();
                    postLoginLoop(preResult.getLast());
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

        List<String> postResult = new ArrayList<>();
        while (!postResult.getFirst().equals("quit"))
        {
            System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN );
            String line = mScanner.nextLine();

            try
            {
                postResult = mPostLogClient.evaluate(line, aAuthToken);
                System.out.print(SET_TEXT_COLOR_BLUE + postResult);
                if (postResult.getFirst().equals("join") || postResult.getFirst().equals("observe"))
                {
                    System.out.println();
                    gameplayLoop();
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

    private void gameplayLoop()
    {
        System.out.println(mGameClient.help());

        var gameResult = "";
        while (!gameResult.equals("quit"))
        {
            System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN );
            String line = mScanner.nextLine();

            try
            {
                gameResult = mGameClient.evaluate(line);
                System.out.print(SET_TEXT_COLOR_BLUE + gameResult);
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
