package websocket.commands;

public class UserJoinCommand extends UserGameCommand
{
    private String mColor;
    public UserJoinCommand(CommandType commandType, String authToken, Integer gameID, String color)
    {
        super(commandType, authToken, gameID);
        mColor = color;
    }
}
