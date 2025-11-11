package ui;

public class GameplayClient
{
    private String mServerUrl;
    private final ServerFacade mFacade;

    public GameplayClient(String aUrl, ServerFacade aFacade)
    {
        mServerUrl = aUrl;
        mFacade = aFacade;
    }

    public String help()
    {
        return null;
    }

    public String evaluate(String aInput)
    {
        return null;
    }
}
