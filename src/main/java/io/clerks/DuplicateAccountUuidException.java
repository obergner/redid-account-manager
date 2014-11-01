package io.clerks;

import java.util.UUID;

/**
 * Created by obergner on 01.11.14.
 */
public class DuplicateAccountUuidException extends AccountManagerException {

    private final UUID duplicateUuid;

    public DuplicateAccountUuidException(final UUID duplicateUuid) {
        super("Account with UUID '" + duplicateUuid + "' already exists");
        this.duplicateUuid = duplicateUuid;
    }

    public UUID getDuplicateUuid() {
        return duplicateUuid;
    }
}
