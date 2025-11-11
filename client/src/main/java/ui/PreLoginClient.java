package ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Exception.ResponseException;
import model.*;

public class PreLoginClient
{
    private String mServerUrl;
    private final ServerFacade mFacade;

    public PreLoginClient(String aUrl, ServerFacade aFacade)
    {
        mServerUrl = aUrl;
        mFacade = aFacade;
    }

    public String help()
    {
        return null;
    }

    public List<String> evaluate(String aInput)
    {
        try
        {
            String[] tokens = aInput.toLowerCase().split(" ");
            String action = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            switch (action) {
                case "1", "register" ->
                {
                    if (params.length == 3)
                    {
                        RegisterResult result = mFacade.register(new RegisterRequest(params[0], params[1], params[2]));
                        return new ArrayList<>(List.of(
                                "register",
                                String.format("Registered and logged in as %s",result.username()),
                                result.authToken()));
                    }
                }
                case "2", "login" ->
                {
                    if (params.length == 2)
                    {
                        LoginResult result = mFacade.login(new LoginRequest(params[0], params[1]));
                        return new ArrayList<>(List.of(
                                "login",
                                String.format("Logged in as %s",result.username()),
                                result.authToken()));
                    }
                }
                case "3", "quit" ->
                {
                    if (params.length == 0)
                    {
                        return new ArrayList<>(List.of("quit", "Exiting chess"));
                    }
                }
                default ->
                {
                    return new ArrayList<>(List.of("help", ""));
                }
            }
            return new ArrayList<>(List.of("help", ""));
        }
        catch (ResponseException e)
        {
            return new ArrayList<>(List.of(e.getMessage()));
        }
    }
}
