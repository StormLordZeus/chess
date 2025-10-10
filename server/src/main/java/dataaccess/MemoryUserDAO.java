package dataaccess;

import model.UserData;

import java.util.HashSet;
import java.util.Set;


public class MemoryUserDAO implements UserDAO{
    private static final Set<UserData> USERS = new HashSet<>();
    @Override
    public void createUser(UserData user) throws DataAccessException
    {
        if (USERS.contains(user))
        {
            throw new DataAccessException("Error: User already taken");
        }
        else
        {
            USERS.add(user);
        }
    }

    @Override
    public UserData getUser(String username)
    {
        for (UserData user : USERS) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public void clearUsers()
    {
        USERS.clear();
    }
}
