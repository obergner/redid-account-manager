package io.clerks;

/**
 * Created by obergner on 01.11.14.
 */
public interface AccountManager {

    Account createAccount(final String name,
                          final long mmaId) throws DuplicateAccountUuidException, MMAIdAlreadyMappedToAccountException;
}
