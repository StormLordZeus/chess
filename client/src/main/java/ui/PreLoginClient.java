package ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import exception.ResponseException;
import model.*;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class PreLoginClient
{
    private final ServerFacade mFacade;

    public PreLoginClient(ServerFacade aFacade)
    {
        mFacade = aFacade;
    }

    public String help()
    {
        return "Type the number or string of the action you want to select\n" +
                SET_TEXT_COLOR_BLUE + "1: register <USERNAME> <PASSWORD> <EMAIL>" + RESET_TEXT_COLOR + " - to create an account\n" +
                SET_TEXT_COLOR_BLUE + "2: login <USERNAME> <PASSWORD>" + RESET_TEXT_COLOR + " - to play chess\n" +
                SET_TEXT_COLOR_BLUE + "3: quit" + RESET_TEXT_COLOR + " - playing chess\n" +
                SET_TEXT_COLOR_BLUE + "4: help" + RESET_TEXT_COLOR + " - with possible commands\n";
    }

    public List<String> evaluate(String aInput)
    {
        try
        {
            String[] tokens = aInput.split(" ");
            String action = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            switch (action.toLowerCase()) {
                case "1", "register" ->
                {
                    if (params.length == 3)
                    {
                        RegisterResult result = mFacade.register(new RegisterRequest(params[0], params[1], params[2]));
                        System.out.println("Logged in and my auth token is " + result.authToken());
                        return new ArrayList<>(List.of(
                                "register",
                                String.format("Registered and logged in as %s",result.username()),
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
                case "2", "login" ->
                {
                    if (params.length == 2)
                    {
                        LoginResult result = mFacade.login(new LoginRequest(params[0], params[1]));
                        System.out.println("Logged in and my auth token is " + result.authToken());
                        return new ArrayList<>(List.of(
                                "login",
                                String.format("Logged in as %s",result.username()),
                                result.authToken()));
                    }
                }
                default ->
                {
                    return new ArrayList<>(List.of("help", help()));
                }
            }
            return new ArrayList<>(List.of("help", help()));
        }
        catch (ResponseException e)
        {
            return new ArrayList<>(List.of("", e.getMessage()));
        }
    }
}
