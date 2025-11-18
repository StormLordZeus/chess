package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import exception.ResponseException;
import websocket.GameHandler;
import websocket.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static chess.ChessPiece.PieceType.PAWN;
import static ui.EscapeSequences.*;

public class ReplLoop implements GameHandler {
    private final PreLoginClient mPreLogClient;
    private final PostLoginClient mPostLogClient;
    private final GameplayClient mGameClient;
    private List<String> mPreResult;
    Scanner mScanner = new Scanner(System.in);
    private final String mUrl;
    private String mAuthToken;
    private ChessGame mGame;
    private String mColor;
    private boolean mObserve;

    public ReplLoop (String aUrl)
    {
        mUrl = aUrl;
        ServerFacade facade = new ServerFacade(aUrl);
        mPreLogClient = new PreLoginClient(facade);
        mPostLogClient = new PostLoginClient(facade);
        mGameClient = new GameplayClient();
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
                    if (action.equals("observe"))
                    {
                        mObserve = true;
                    }
                    System.out.println();
                    mColor = mPostResult.get(2);
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

    private void gameplayLoop(String aGameID) throws ResponseException {
        WebSocketFacade mWebFacade;
        try
        {
            mWebFacade = new WebSocketFacade(mUrl, this);
        }
        catch (ResponseException e)
        {
            System.out.println("Error: Failed to establish a websocket connection. Exiting gameplay loop");
            return;
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        int gameID = Integer.parseInt(aGameID);
        System.out.print(mGameClient.help());
        if (mObserve)
        {
            mWebFacade.connect(mAuthToken, gameID, null);
        }
        else
        {
            mWebFacade.connect(mAuthToken, gameID, mColor);
        }

        List<String> gameResult = new ArrayList<>();
        gameResult.add("");
        while (!gameResult.getFirst().equals("leave"))
        {
            String line = mScanner.nextLine();

            try
            {
                gameResult = mGameClient.evaluate(line);
                System.out.println(SET_TEXT_COLOR_BLUE + gameResult.get(1));
                String action = gameResult.getFirst();
                switch (action) {
                    case "redraw" ->
                    {
                        DrawBoard.drawChessBoard(mColor, mGame.getBoard());
                        System.out.println(SET_TEXT_COLOR_BLUE + mGame.getTeamTurn() + "'s turn");
                    }
                    case "move" ->
                    {
                        String moveString = gameResult.getLast();
                        ChessPosition start = new ChessPosition(moveString.charAt(1) - '0',
                                (moveString.charAt(0) - 'a') + 1);

                        ChessPiece piece = mGame.getBoard().getPiece(start);
                        if (!piece.getTeamColor().toString().equals(mColor))
                        {
                            System.out.println("Error: You cannot make moves for the other player");
                        }
                        else
                        {
                            ChessPosition end = new ChessPosition(moveString.charAt(3) - '0',
                                    (moveString.charAt(2) - 'a') + 1);
                            ChessPiece.PieceType promotion = getPromotionType(start, end);

                            ChessMove move = new ChessMove(start, end, promotion);
                            mWebFacade.makeMove(mAuthToken, gameID, move);
                        }
                    }
                    case "highlight" ->
                    {
                        String positionString = gameResult.getLast();
                        ChessPosition highlight = new ChessPosition(positionString.charAt(1) - '0',
                                (positionString.charAt(0) - 'a') + 1);
                        DrawBoard.highlightSquares(mGame, highlight);
                        DrawBoard.drawChessBoard(mColor, mGame.getBoard());
                    }
                    case "resign" ->
                    {
                        line = mScanner.nextLine().toLowerCase();
                        if (line.equals("yes"))
                        {
                            mWebFacade.resignGame(mAuthToken, gameID);
                        }
                    }
                }
                if (!action.equals("resign"))
                {
                    System.out.print("\n" + RESET_TEXT_COLOR + "[GAMEPLAY] >>> " + SET_TEXT_COLOR_GREEN);
                }
            }
            catch (Throwable e)
            {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        mWebFacade.leaveGame(mAuthToken, gameID, mColor);
        System.out.println();
    }


    private ChessPiece.PieceType getPromotionType(ChessPosition aStart, ChessPosition aEnd) {
        String line = "";
        if (mGame.getBoard().getPiece(aStart).getPieceType() == PAWN)
        {
            if ((mColor.equals("BLACK") && aEnd.getRow() == 1) ||(mColor.equals("WHITE") && aEnd.getRow() == 8))
            {
                System.out.println("What piece would you like to promote to? [Q|R|B|N]");
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }

        ChessPiece.PieceType promotion = null;
        while (promotion == null) {
            line = mScanner.nextLine().toLowerCase();

            switch (line.toLowerCase()) {
                case "b": {
                    promotion = ChessPiece.PieceType.BISHOP;
                    break;
                }
                case "q": {
                    promotion = ChessPiece.PieceType.QUEEN;
                    break;
                }
                case "r": {
                    promotion = ChessPiece.PieceType.ROOK;
                    break;
                }
                case "n": {
                    promotion = ChessPiece.PieceType.KNIGHT;
                    break;
                }
                default:
                {
                    System.out.println("Please enter a valid piece type. [Q|R|B|N]");
                    break;
                }
            }

        }
        return promotion;
    }

    @Override
    public void printMessage(String aMessage)
    {
        ServerMessage messageParent = new Gson().fromJson(aMessage, ServerMessage.class);
        switch (messageParent.getServerMessageType())
        {
            case ERROR ->
            {
                ErrorMessage error = new Gson().fromJson(aMessage, ErrorMessage.class);
                System.out.print("\n" + error.getError());
                System.out.print("\n" + RESET_TEXT_COLOR + "[GAMEPLAY] >>> " + SET_TEXT_COLOR_GREEN);
            }
            case LOAD_GAME ->
            {
                LoadGameMessage gameMessage = new Gson().fromJson(aMessage, LoadGameMessage.class);
                mGame = gameMessage.getGame();
                DrawBoard.drawChessBoard(mColor, mGame.getBoard());
                System.out.println(SET_TEXT_COLOR_BLUE + mGame.getTeamTurn() + "'s turn");
                System.out.print("\n" + RESET_TEXT_COLOR + "[GAMEPLAY] >>> " + SET_TEXT_COLOR_GREEN);

            }
            case NOTIFICATION ->
            {
                NotificationMessage notification = new Gson().fromJson(aMessage, NotificationMessage.class);
                System.out.print("\n" + notification.getMessage());
                System.out.print("\n" + RESET_TEXT_COLOR + "[GAMEPLAY] >>> " + SET_TEXT_COLOR_GREEN);
            }
        }
    }
}
