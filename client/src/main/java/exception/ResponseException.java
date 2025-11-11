package exception;

public class ResponseException extends Exception
{

    public ResponseException(String message)
    {
        super(message);
    }

    public static String statusMessage(int httpStatusCode)
    {
        return switch (httpStatusCode)
        {
            case 500 -> "Error: Server error";
            case 400 -> "Error: Bad request";
            case 401 -> "Error: Unauthorized Request";
            case 403 -> "Error: Already Taken";
            case 404 -> "Error: Object not found";
            default -> throw new IllegalArgumentException("Error: Unknown");
        };
    }
}