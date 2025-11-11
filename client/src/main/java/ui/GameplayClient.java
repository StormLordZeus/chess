package ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                SET_TEXT_COLOR_BLUE + "1: exit" + RESET_TEXT_COLOR + " - the game\n" +
                SET_TEXT_COLOR_BLUE + "2: quit" + RESET_TEXT_COLOR + " - the program\n" +
                SET_TEXT_COLOR_BLUE + "3: help" + RESET_TEXT_COLOR + " - with possible commands\n";
    }

    public List<String> evaluate(String aInput)
    {

        String[] tokens = aInput.split(" ");
        String action = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        switch (action.toLowerCase()) {
            case "1", "exit" ->
            {
                if (params.length == 0)
                {
                    return new ArrayList<>(List.of("exit", "Exiting chess game"));
                }
            }
            case "2", "quit" ->
            {
                if (params.length == 0)
                {
                    return new ArrayList<>(List.of("quit", "Shutting down program"));
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
