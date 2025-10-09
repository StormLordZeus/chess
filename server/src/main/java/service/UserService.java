package service;

import dataaccess.*;
import io.javalin.http.UnauthorizedResponse;
import model.*;

import java.util.UUID;

public class UserService
{
    private static final MemoryUserDAO mUserDataAccess = new MemoryUserDAO();
    private static final MemoryAuthDAO mAuthDataAccess = new MemoryAuthDAO();

    public RegisterResult register(RegisterRequest request) throws DataAccessException
    {
        if (mUserDataAccess.getUser(request.username()) != null)
        {
            throw new AlreadyTakenException("Username already taken");
        }
        mUserDataAccess.createUser(new UserData(request.username(), request.password(), request.email()));

        String authToken = authorizeUser(request.username());

        return new RegisterResult(request.username(), authToken);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException
    {
        UserData user = mUserDataAccess.getUser(request.username());
        if (user == null)
        {
            throw new InvalidCredentialsException("Invalid Credentials");
        }
        if (!request.password().equals(user.password()))
        {
            throw new InvalidCredentialsException("Invalid Credentials");
        }
        String authToken = authorizeUser(request.username());

        return new LoginResult(request.username(), authToken);
    }

    public void logout(LogoutRequest request) throws DataAccessException
    {
        AuthData auth = mAuthDataAccess.getAuth(request.authToken());
        if (auth == null)
        {
            throw new UnauthorizedResponse("Auth token not found");
        }
        mAuthDataAccess.deleteAuth(auth);
    }

    public void clearUsers()
    {
        mUserDataAccess.clearUsers();
    }

    private String authorizeUser(String username) throws DataAccessException
    {
        String authToken = UUID.randomUUID().toString();
        mAuthDataAccess.createAuth(new AuthData(authToken, username));
        return authToken;
    }
}
