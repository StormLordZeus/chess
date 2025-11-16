package ui;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import websocket.GameHandler;
import websocket.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class ReplLoop implements GameHandler {
    private final PreLoginClient mPreLogClient;
    private final PostLoginClient mPostLogClient;
    private final GameplayClient mGameClient;
    private List<String> mPreResult;
    Scanner mScanner = new Scanner(System.in);
    private final String mUrl;
    private String mAuthToken;

    public ReplLoop (String aUrl)
    {
        mUrl = aUrl;
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
                    mAuthToken = mPreResult.getLast();
                    postLoginLoop();
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

    private void postLoginLoop()
    {
        System.out.println(mPostLogClient.help());

        List<String> mPostResult = new ArrayList<>();
        mPostResult.add("");
        while (!mPostResult.getFirst().equals("logout"))
        {
            System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED_IN] >>> " + SET_TEXT_COLOR_GREEN );
            String line = mScanner.nextLine();

            try
            {
                mPostResult = mPostLogClient.evaluate(line, mAuthToken);
                System.out.print(SET_TEXT_COLOR_BLUE + mPostResult.get(1));
                String action = mPostResult.getFirst();
                if (action.equals("join") || action.equals("observe"))
                {
                    System.out.println();
                    gameplayLoop(mPostResult.get(2), mPostResult.getLast());
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

    private void gameplayLoop(String aColor, String aGameID) throws ResponseException
    {
        WebSocketFacade mWebFacade;
        try
        {
            mWebFacade = new WebSocketFacade(mUrl, this);
        }
        catch (ResponseException e)
        {
            System.out.println("Failed to establish a websocket connection. Exiting gameplay loop");
            return;
        }
        int gameID = Integer.parseInt(aGameID);
        System.out.println(mGameClient.help());
        mWebFacade.connect(mAuthToken, gameID);

        List<String> gameResult = new ArrayList<>();
        gameResult.add("");
        DrawBoard.drawChessBoard(aColor);
        while (!gameResult.getFirst().equals("leave"))
        {
            System.out.print("\n" + RESET_TEXT_COLOR + "[GAMEPLAY] >>> " + SET_TEXT_COLOR_GREEN);
            String line = mScanner.nextLine();

            try
            {
                gameResult = mGameClient.evaluate(line);
                System.out.println(SET_TEXT_COLOR_BLUE + gameResult.get(1));
                String action = gameResult.getFirst();
                switch (action) {
                    case "redraw" -> DrawBoard.drawChessBoard(aColor);
                    case "move" -> {
                        String moveString = gameResult.getLast();
                        ChessPosition start = new ChessPosition(moveString.charAt(1), moveString.charAt(0));
                        ChessPosition end = new ChessPosition(moveString.charAt(3), moveString.charAt(2));
                        ChessPiece.PieceType promotion = getPieceType(moveString.charAt(5));
                        ChessMove move = new ChessMove(start, end, promotion);
                        mWebFacade.makeMove(mAuthToken, gameID, move);
                    }
                    case "resign" -> {
                        line = mScanner.nextLine().toLowerCase();
                        if (line.equals("yes")) {
                            mWebFacade.resignGame(mAuthToken, gameID);
                        }
                    }
                }
            }
            catch (Throwable e)
            {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        mWebFacade.leaveGame(mAuthToken, gameID);
        System.out.println();
    }

    private static ChessPiece.PieceType getPieceType(char promoType) {
        ChessPiece.PieceType promotion = null;
        switch (promoType)
        {
            case 'B':
            {
                promotion = ChessPiece.PieceType.BISHOP;
                break;
            }
            case 'Q':
            {
                promotion = ChessPiece.PieceType.QUEEN;
                break;
            }
            case 'R':
            {
                promotion = ChessPiece.PieceType.ROOK;
                break;
            }
            case 'N':
            {
                promotion = ChessPiece.PieceType.KNIGHT;
                break;
            }
        }
        return promotion;
    }

    @Override
    public void printMessage(ServerMessage aMessage)
    {

    }

    @Override
    public void updateGame(int aGameID)
    {

    }
}
