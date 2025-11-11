package ui;

import model.*;

import java.util.*;

import exception.ResponseException;

import static ui.EscapeSequences.*;

public class PostLoginClient
{
    private final ServerFacade mFacade;
    private final Map<Integer, Integer> mNumToIDs;

    public PostLoginClient(ServerFacade aFacade)
    {
        mNumToIDs = new HashMap<>();
        mFacade = aFacade;
    }

    public String help()
    {
        return "Type the number or string of the action you want to select\n" +
                SET_TEXT_COLOR_BLUE + "1: create <NAME>" + RESET_TEXT_COLOR + " - a game\n" +
                SET_TEXT_COLOR_BLUE + "2: list" + RESET_TEXT_COLOR + " - games\n" +
                SET_TEXT_COLOR_BLUE + "3: join <ID> [BLACK|WHITE}" + RESET_TEXT_COLOR + " - a game\n" +
                SET_TEXT_COLOR_BLUE + "4: observe <ID>" + RESET_TEXT_COLOR + " - a game\n" +
                SET_TEXT_COLOR_BLUE + "5: logout" + RESET_TEXT_COLOR + " - when you are done\n" +
                SET_TEXT_COLOR_BLUE + "6: quit" + RESET_TEXT_COLOR + " - playing chess\n" +
                SET_TEXT_COLOR_BLUE + "7: help" + RESET_TEXT_COLOR + " - with possible commands\n";
    }

    public List<String> evaluate(String aInput, String aAuthtoken)
    {
        try
        {
            String[] tokens = aInput.split(" ");
            String action = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            switch (action.toLowerCase()) {
                case "1", "create" ->
                {
                    if (params.length == 1)
                    {
                        mFacade.createGame(new CreateGameRequest(params[0], aAuthtoken));
                        return new ArrayList<>(List.of(
                                "create",
                                String.format("Created a game with name: %s",params[0])));
                    }
                }
                case "2", "list" ->
                {
                    if (params.length == 0)
                    {
                        ListGamesResult result = mFacade.listGames(new ListGamesRequest(aAuthtoken));
                        return new ArrayList<>(List.of(
                                "list",
                                "Listing all games\n" + listGamesString(result.games())));
                    }
                }
                case "3", "join" ->
                {
                    if (params.length == 2)
                    {
                        int gameID = mNumToIDs.get(Integer.parseInt(params[0]));
                        System.out.println("GameID is " + gameID);
                        mFacade.joinGame(new JoinGameRequest(gameID,
                                params[1].toUpperCase(), aAuthtoken));
                        return new ArrayList<>(List.of(
                                "join",
                                "Joining game...",
                                params[1].toUpperCase()));
                    }
                }
                case "4", "observe" ->
                {
                    if (params.length == 1)
                    {
                        int gameID = mNumToIDs.get(Integer.parseInt(params[0]));
                        return new ArrayList<>(List.of(
                                "observe",
                                "Observing game...",
                                "WHITE"));
                    }
                }
                case "5", "logout" ->
                {
                    if (params.length == 0)
                    {
                        mFacade.logout(new LogoutRequest(aAuthtoken));
                        return new ArrayList<>(List.of(
                                "logout",
                                "Logging out"));
                    }
                }
                case "6", "quit" ->
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
        catch (ResponseException e)
        {
            return new ArrayList<>(List.of("", e.getMessage()));
        }
    }

    private String listGamesString(Set<GameData> aGames)
    {
        mNumToIDs.clear();
        String games = "***********************************\n";
        int gameNum = 1;
        for (GameData game: aGames)
        {
            mNumToIDs.put(gameNum, game.gameID());
            games += String.format("%d: %s\nWhite Player: %s\nBlack Player: %s\nBoard:\n%s\n", gameNum,
                game.gameName(), game.whiteUsername(), game.blackUsername(), game.game());
            gameNum++;
        }
        games += "***********************************\n";
        return games;
    }
}
