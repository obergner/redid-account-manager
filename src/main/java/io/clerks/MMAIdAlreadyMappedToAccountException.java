package io.clerks;

/**
 * Created by obergner on 01.11.14.
 */
public class MMAIdAlreadyMappedToAccountException extends AccountManagerException {

    private final long alreadyMappedMmaId;

    public MMAIdAlreadyMappedToAccountException(final long alreadyMappedMmaId) {
        super("MMA '" + alreadyMappedMmaId + "' is already mapped to an Account");
        this.alreadyMappedMmaId = alreadyMappedMmaId;
    }

    public long getAlreadyMappedMmaId() {
        return alreadyMappedMmaId;
    }
}
