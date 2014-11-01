package io.clerks;

import com.google.common.base.Preconditions;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by obergner on 01.11.14.
 */
public final class Account implements Serializable {

    private static final long serialVersionUID = 7954333235063995860L;

    public static final Account newAccount(final String name, final long mmaId) {
        return new Account(UUID.randomUUID(), name, mmaId);
    }

    private final UUID uuid;

    private final String name;

    private final long mmaId;

    public Account(final UUID uuid, final String name, final long mmaId) {
        this.uuid = Preconditions.checkNotNull(uuid);
        this.name = Preconditions.checkNotNull(name);
        this.mmaId = mmaId;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public long getMmaId() {
        return mmaId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Account account = (Account) o;

        if (mmaId != account.mmaId) return false;
        if (!name.equals(account.name)) return false;
        if (!uuid.equals(account.uuid)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (int) (mmaId ^ (mmaId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Account{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", mmaId=" + mmaId +
                '}';
    }
}
