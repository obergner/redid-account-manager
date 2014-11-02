package io.clerks;

public class MMAIdAlreadyMappedToAccountException extends AccountManagerException {

    private final long alreadyMappedMmaId;

    public MMAIdAlreadyMappedToAccountException(final long alreadyMappedMmaId, final Throwable cause) {
        super("MMA '" + alreadyMappedMmaId + "' is already mapped to an Account", cause);
        this.alreadyMappedMmaId = alreadyMappedMmaId;
    }

    public long getAlreadyMappedMmaId() {
        return alreadyMappedMmaId;
    }
}
