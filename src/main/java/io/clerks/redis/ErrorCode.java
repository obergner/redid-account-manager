package io.clerks.redis;

final class ErrorCode {

    static final String NOT_FOUND = "NotFound";

    static final String DUPLICATE_ACCOUNT_UUID = "DuplicateAccountUUID";

    static final String DUPLICATE_MMA = "DuplicateMMA";

    private ErrorCode() {
        // Do not instantiate
    }
}
