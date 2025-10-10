package dataaccess;

import io.javalin.http.UnauthorizedResponse;
import model.AuthData;

import java.util.HashSet;
import java.util.Set;

public class MemoryAuthDAO implements AuthDAO{
    private static final Set<AuthData> authTokens = new HashSet<>();

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        if (authTokens.contains(auth))
        {
            throw new DataAccessException("Error: Auth Token already exists");
        }
        authTokens.add(auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData auth : authTokens)
        {
            if (auth.authToken().equals(authToken))
            {
                return auth;
            }
        }
        throw new UnauthorizedResponse("Error: No auth token found");
    }

    @Override
    public void deleteAuth(AuthData auth) throws DataAccessException {
        authTokens.remove(auth);
    }

    @Override
    public void clearAuths() {
        authTokens.clear();
    }
}
