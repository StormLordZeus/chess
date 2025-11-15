package websocket.commands;

public class UserJoinCommand extends UserGameCommand
{
    private final String mColor;
    public UserJoinCommand(CommandType commandType, String authToken, Integer gameID, String color)
    {
        super(commandType, authToken, gameID);
        mColor = color;
    }

    public String getColor()
    {
        return mColor;
    }
}
