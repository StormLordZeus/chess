package ui;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class GameplayClient
{
    public String help()
    {
        return "Type the number or string of the action you want to select\n" +
                SET_TEXT_COLOR_BLUE + "1: leave" + RESET_TEXT_COLOR + " - the game\n" +
                SET_TEXT_COLOR_BLUE + "2: redraw" + RESET_TEXT_COLOR + " - the board\n" +
                SET_TEXT_COLOR_BLUE + "3: move <StartPosEndPos> ex (d2d4)" + RESET_TEXT_COLOR + " - a piece\n" +
                SET_TEXT_COLOR_BLUE + "4: resign" + RESET_TEXT_COLOR + " - the game\n" +
                SET_TEXT_COLOR_BLUE + "5: highlight <Position> ex (f7)" + RESET_TEXT_COLOR + " - legal moves\n" +
                SET_TEXT_COLOR_BLUE + "6: help" + RESET_TEXT_COLOR + " - with possible commands\n";
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
                if (params.length == 1)
                {
                    if (params[0].length() != 4)
                    {
                        return new ArrayList<>(List.of("help", "Error: Move had too many or too few characters. Valid moves have 4 characters"));
                    }
                    return new ArrayList<>(List.of("move", "Making a move", params[0]));
                }
            }
            case "4", "resign" ->
            {
                if (params.length == 0)
                {
                    return new ArrayList<>(List.of("resign", "Are you sure you want to resign? [YES|NO]"));
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
