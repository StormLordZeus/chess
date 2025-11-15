package ui;

import websocket.WebSocketFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class GameplayClient
{
    private final ServerFacade mServerFacade;

    public GameplayClient(ServerFacade aFacade)
    {
        mServerFacade = aFacade;
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
            case "1", "leave" ->
            {
                if (params.length == 0)
                {
                    return new ArrayList<>(List.of("leave", "Leaving chess game"));
                }
            }
            case "2", "redraw" ->
            {
                if (params.length == 0)
                {
                    return new ArrayList<>(List.of("redraw", "Drawing the Chess Board"));
                }
            }
            case "3", "move" ->
            {
                if (params.length == 0)
                {
                    return new ArrayList<>(List.of("move", "Making a move"));
                }
            }
            case "4", "resign" ->
            {
                if (params.length == 0)
                {
                    return new ArrayList<>(List.of("resign", "Are you sure you want to resign?"));
                }
            }
            case "5", "highlight" ->
            {
                if (params.length == 0)
                {
                    return new ArrayList<>(List.of("highlight", "Highlighting legal moves"));
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
