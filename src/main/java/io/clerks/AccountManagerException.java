package io.clerks;

/**
 * Created by obergner on 01.11.14.
 */
public abstract class AccountManagerException extends RuntimeException {

    public AccountManagerException() {
        super();
    }

    public AccountManagerException(final String message) {
        super(message);
    }

    public AccountManagerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AccountManagerException(final Throwable cause) {
        super(cause);
    }
}
