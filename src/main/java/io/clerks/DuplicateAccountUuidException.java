package io.clerks;

import java.util.UUID;

public class DuplicateAccountUuidException extends AccountManagerException {

    private final UUID duplicateUuid;

    public DuplicateAccountUuidException(final UUID duplicateUuid, final Throwable cause) {
        super("Account with UUID '" + duplicateUuid + "' already exists", cause);
        this.duplicateUuid = duplicateUuid;
    }

    public UUID getDuplicateUuid() {
        return duplicateUuid;
    }
}
