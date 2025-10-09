package service;

import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.*;

import java.util.UUID;

public class UserService
{
    private static final MemoryUserDAO userDataAccess = new MemoryUserDAO();
    private static final MemoryAuthDAO authDataAccess = new MemoryAuthDAO();

    public RegisterResult register(RegisterRequest request) throws DataAccessException
    {
        if (userDataAccess.getUser(request.username()) != null)
        {
            throw new AlreadyTakenException("Username already taken");
        }
        userDataAccess.createUser(new UserData(request.username(), request.password(), request.email()));

        String authToken = UUID.randomUUID().toString();
        authDataAccess.createAuth(new AuthData(authToken, request.username()));

        return new RegisterResult(request.username(), authToken);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException
    {
        return null;
    }
}
