package ui;

import websocket.WebSocketFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class ReplLoop {
    private final PreLoginClient mPreLogClient;
    private final PostLoginClient mPostLogClient;
    private final GameplayClient mGameClient;
    private List<String> mPreResult;
    private List<String> mPostResult;
    Scanner mScanner = new Scanner(System.in);
    private final WebSocketFacade mWebFacade = new WebSocketFacade();

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

        mPreResult = new ArrayList<>();
        mPreResult.add("");
        while (!mPreResult.getFirst().equals("quit"))
        {
            System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED_OUT] >>> " + SET_TEXT_COLOR_GREEN );
            String line = mScanner.nextLine();

            try
            {
                mPreResult = mPreLogClient.evaluate(line);
                System.out.print(SET_TEXT_COLOR_BLUE + mPreResult.get(1));
                if (mPreResult.getFirst().equals("login") || mPreResult.getFirst().equals("register"))
                {
                    postLoginLoop(mPreResult.getLast());
                    if (!mPreResult.getFirst().equals("quit")) 
                    {
                        System.out.println(mPreLogClient.help());
                    }
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

        mPostResult = new ArrayList<>();
        mPostResult.add("");
        while (!mPostResult.getFirst().equals("logout"))
        {
            System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED_IN] >>> " + SET_TEXT_COLOR_GREEN );
            String line = mScanner.nextLine();

            try
            {
                mPostResult = mPostLogClient.evaluate(line, aAuthToken);
                System.out.print(SET_TEXT_COLOR_BLUE + mPostResult.get(1));
                String action = mPostResult.getFirst();
                if (action.equals("join") || action.equals("observe"))
                {
                    System.out.println();
                    gameplayLoop(mPostResult.getLast());
                    if (!mPostResult.getFirst().equals("quit"))
                    {
                        System.out.println(mPostLogClient.help());
                    }
                }
                if (mPostResult.getFirst().equals("quit"))
                {
                    mPreResult.clear();
                    mPreResult.add("quit");
                    break;
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
        while (!gameResult.getFirst().equals("exit"))
        {
            DrawBoard.drawChessBoard(aColor);
            System.out.print("\n" + RESET_TEXT_COLOR + "[GAMEPLAY] >>> " + SET_TEXT_COLOR_GREEN );
            String line = mScanner.nextLine();

            try
            {
                gameResult = mGameClient.evaluate(line);
                System.out.print(SET_TEXT_COLOR_BLUE + gameResult.get(1));
                if (gameResult.getFirst().equals("quit"))
                {
                    mPreResult.clear();
                    mPreResult.add("quit");
                    mPostResult.clear();
                    mPostResult.add("quit");
                    break;
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

}
