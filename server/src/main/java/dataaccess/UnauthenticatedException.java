package dataaccess;

public class UnauthenticatedException extends DataAccessException {
    public UnauthenticatedException(String message) {
        super(message);
    }
}
