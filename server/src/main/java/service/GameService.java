package service;

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
        AuthData auth = mAuthDataAccess.getAuth(request.authToken());
        if (auth == null)
        {
            throw new UnauthorizedResponse("Auth token not found");
        }
        return new ListGamesResult(mGameDataAccess.listGames());
    }

    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException
    {
        AuthData auth = mAuthDataAccess.getAuth(request.authToken());
        if (auth == null)
        {
            throw new UnauthorizedResponse("Auth token not found");
        }
        return new CreateGameResult(mGameDataAccess.createGame(request.gameName()).gameID());
    }
}
