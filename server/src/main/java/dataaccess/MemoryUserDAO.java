package dataaccess;

import model.UserData;

import java.util.HashSet;
import java.util.Set;


public class MemoryUserDAO implements UserDAO{
    private static final Set<UserData> users = new HashSet<>();
    @Override
    public void createUser(UserData user) throws DataAccessException
    {
        if (users.contains(user))
        {
            throw new DataAccessException("User already taken");
        }
        else
        {
            users.add(user);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException
    {
        for (UserData user : users) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        throw new DataAccessException("Incorrect username");
    }

    @Override
    public void clearUsers()
    {
        users.clear();
    }
}
