package exception;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Exception {

    public enum Code {
        ServerError,
        ClientError,
        UnauthorizedError,
        AlreadyTakenError,
        NotFoundError,
    }

    final private Code code;

    public ResponseException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", code));
    }

    public static ResponseException fromJson(String json) {
        var map = new Gson().fromJson(json, HashMap.class);
        var status = Code.valueOf(map.get("status").toString());
        String message = map.get("message").toString();
        return new ResponseException(status, message);
    }

    public static Code fromHttpStatusCode(int httpStatusCode) {
        return switch (httpStatusCode) {
            case 500 -> Code.ServerError;
            case 400 -> Code.ClientError;
            case 401 -> Code.UnauthorizedError;
            case 403 -> Code.AlreadyTakenError;
            case 404 -> Code.NotFoundError;
            default -> throw new IllegalArgumentException("Unknown HTTP status code: " + httpStatusCode);
        };
    }

    public static String statusMessage(int httpStatusCode)
    {
        return switch (httpStatusCode) {
            case 500 -> "Error: " + httpStatusCode + " Server error";
            case 400 -> "Error: " + httpStatusCode + " Bad request";
            case 401 -> "Error: " + httpStatusCode + " Unauthorized Request";
            case 403 -> "Error: " + httpStatusCode + " Already Taken";
            case 404 -> "Error: " + httpStatusCode + " Object not found";
            default -> throw new IllegalArgumentException("Unknown HTTP status code: " + httpStatusCode);
        };
    }
}