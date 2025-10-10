package dataaccess;

import io.javalin.http.UnauthorizedResponse;
import model.AuthData;

import java.util.HashSet;
import java.util.Set;

public class MemoryAuthDAO implements AuthDAO{
    private static final Set<AuthData> AUTH_TOKENS = new HashSet<>();

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        if (AUTH_TOKENS.contains(auth))
        {
            throw new DataAccessException("Error: Auth Token already exists");
        }
        AUTH_TOKENS.add(auth);
    }

    @Override
    public AuthData getAuth(String authToken) {
        for (AuthData auth : AUTH_TOKENS)
        {
            if (auth.authToken().equals(authToken))
            {
                return auth;
            }
        }
        throw new UnauthorizedResponse("Error: No auth token found");
    }

    @Override
    public void deleteAuth(AuthData auth) {
        AUTH_TOKENS.remove(auth);
    }

    @Override
    public void clearAuths() {
        AUTH_TOKENS.clear();
    }
}
