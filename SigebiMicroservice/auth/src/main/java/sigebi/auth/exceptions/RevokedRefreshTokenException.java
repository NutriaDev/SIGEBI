package sigebi.auth.exceptions;

public class RevokedRefreshTokenException extends RuntimeException {

    public RevokedRefreshTokenException(String message) {
        super(message);
    }

    public RevokedRefreshTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
