package ui;

import model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Exception.ResponseException;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class GameplayClient
{
    private final ServerFacade mFacade;

    public GameplayClient(ServerFacade aFacade)
    {
        mFacade = aFacade;
    }

    public String help()
    {
        return "Type the number or string of the action you want to select\n" +
                SET_TEXT_COLOR_BLUE + "1: quit" + RESET_TEXT_COLOR + " - playing chess\n" +
                SET_TEXT_COLOR_BLUE + "2: help" + RESET_TEXT_COLOR + " - with possible commands\n";
    }

    public List<String> evaluate(String aInput)
    {

        String[] tokens = aInput.split(" ");
        String action = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        switch (action.toLowerCase()) {
            case "1", "quit" ->
            {
                if (params.length == 0)
                {
                    return new ArrayList<>(List.of("quit", "Exiting chess"));
                }
            }
            default ->
            {
                return new ArrayList<>(List.of("help", help()));
            }
        }
        return new ArrayList<>(List.of("help", help()));
    }
}
