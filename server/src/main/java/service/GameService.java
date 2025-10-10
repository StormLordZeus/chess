package service;

import chess.InvalidMoveException;
import dataaccess.AlreadyTakenException;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.http.UnauthorizedResponse;
import model.*;

public class GameService
{
    private static GameDAO mGameDataAccess;
    private static AuthDAO mAuthDataAccess;

    public GameService(GameDAO gameData, AuthDAO authData)
    {
        mGameDataAccess = gameData;
        mAuthDataAccess = authData;
    }

    public ListGamesResult listGames(ListGamesRequest request) throws DataAccessException
    {
        authenticate(request.authToken());
        return new ListGamesResult(mGameDataAccess.listGames());
    }

    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException
    {
        authenticate(request.authToken());
        return new CreateGameResult(mGameDataAccess.createGame(request.gameName()).gameID());
    }

    public void joinGame(JoinGameRequest request) throws DataAccessException, InvalidMoveException
    {
        AuthData auth = authenticate(request.authToken());
        GameData game = mGameDataAccess.getGame(request.gameID());
        mGameDataAccess.updateGame(request.gameID(), request.playerColor(), auth.username(), null);
    }

    public AuthData authenticate(String authToken) throws DataAccessException
    {
        AuthData auth = mAuthDataAccess.getAuth(authToken);
        if (auth == null)
        {
            throw new UnauthorizedResponse("Auth token not found");
        }
        return auth;
    }
}
