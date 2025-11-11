package ui;

import model.*;

import java.util.*;

import Exception.ResponseException;

public class PostLoginClient
{
    private String mServerUrl;
    private final ServerFacade mFacade;

    public PostLoginClient(String aUrl, ServerFacade aFacade)
    {
        mServerUrl = aUrl;
        mFacade = aFacade;
    }

    public String help()
    {
        return null;
    }

    public List<String> evaluate(String aInput, String aAuthtoken)
    {
        try
        {
            String[] tokens = aInput.toLowerCase().split(" ");
            String action = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            switch (action) {
                case "1", "create" ->
                {
                    if (params.length == 1)
                    {
                        CreateGameResult result = mFacade.createGame(new CreateGameRequest(params[0],aAuthtoken));
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
                                "Listing all games",
                                listGamesString(result.games())));
                    }
                }
                case "3", "quit" ->
                {
                    return new ArrayList<>(List.of("quit", "Exiting chess"));
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

    private String listGamesString(Set<GameData> aGames)
    {

    }
}
