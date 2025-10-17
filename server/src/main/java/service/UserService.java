package service;

import dataaccess.*;
import model.*;
import org.eclipse.jetty.http.BadMessageException;

import java.util.UUID;

public class UserService
{
    private static UserDAO mUserDataAccess;
    private static AuthDAO mAuthDataAccess;

    public UserService(UserDAO userData, AuthDAO authData)
    {
        mUserDataAccess = userData;
        mAuthDataAccess = authData;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException
    {
        if (request.username() == null)
        {
            throw new BadMessageException("Error: No Username Sent");
        }
        if (request.password() == null)
        {
            throw new BadMessageException("Error: No password Sent");
        }
        if (request.email() == null)
        {
            throw new BadMessageException("Error: No email Sent");
        }
        mUserDataAccess.createUser(new UserData(request.username(), request.password(), request.email()));

        String authToken = authorizeUser(request.username());

        return new RegisterResult(request.username(), authToken);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException
    {
        if (request.username() == null)
        {
            throw new BadMessageException("Error: No Username Sent");
        }
        if (request.password() == null)
        {
            throw new BadMessageException("Error: No password Sent");
        }
        UserData user = mUserDataAccess.getUser(request.username());
        if (user == null)
        {
            throw new InvalidCredentialsException("Error: Invalid Credentials");
        }
        if (!request.password().equals(user.password()))
        {
            throw new InvalidCredentialsException("Error: Invalid Credentials");
        }
        String authToken = authorizeUser(request.username());

        return new LoginResult(request.username(), authToken);
    }

    public void logout(LogoutRequest request) throws DataAccessException
    {
        AuthData auth = mAuthDataAccess.getAuth(request.authToken());
        mAuthDataAccess.deleteAuth(auth);
    }

    public void clearUsers()
    {
        mUserDataAccess.clearUsers();
        mAuthDataAccess.clearAuths();
    }

    private String authorizeUser(String username) throws DataAccessException
    {
        String authToken = UUID.randomUUID().toString();
        mAuthDataAccess.createAuth(new AuthData(authToken, username));
        return authToken;
    }
}
