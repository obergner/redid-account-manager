package io.clerks.redis;

import io.clerks.Account;
import io.clerks.DuplicateAccountUuidException;
import io.clerks.EmbeddedRedisServer;
import io.clerks.MMAIdAlreadyMappedToAccountException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class RedisAccountManagerTest {

    private static final int PORT = 6379;

    @ClassRule
    public static final EmbeddedRedisServer EMBEDDED_REDIS_SERVER = EmbeddedRedisServer.listenOnPort(PORT);

    private static final RedisAccountManager OBJECT_UNDER_TEST = new RedisAccountManager("127.0.0.1", PORT);

    @BeforeClass
    public static void initializeObjectUnderTest() throws IOException {
        OBJECT_UNDER_TEST.initialize();
    }

    @AfterClass
    public static void destroyObjectUnderTest() {
        OBJECT_UNDER_TEST.destroy();
    }

    @Rule
    public final TestName testName = new TestName();

    @Test
    public void createAccountShouldStoreAccountInRedis() throws Exception {
        final String accountName = this.testName.getMethodName();
        final long mmaId = 17866534277L;

        final Account newAccount = OBJECT_UNDER_TEST.createAccount(accountName, mmaId);
        assertNotNull(newAccount);

        final boolean accountHasBeenStored = EMBEDDED_REDIS_SERVER.client().hexists("account:uuid:" + newAccount.getUuid().toString(), "uuid");
        assertTrue(accountHasBeenStored);

        final String storedAccountUuid = EMBEDDED_REDIS_SERVER.client().hget("account:uuid:" + newAccount.getUuid().toString(), "uuid");
        assertEquals(newAccount.getUuid().toString(), storedAccountUuid);

        final String storedAccountName = EMBEDDED_REDIS_SERVER.client().hget("account:uuid:" + newAccount.getUuid().toString(), "name");
        assertEquals(newAccount.getName(), storedAccountName);

        final String storedAccountMmaId = EMBEDDED_REDIS_SERVER.client().hget("account:uuid:" + newAccount.getUuid().toString(), "mma");
        assertEquals(newAccount.getMmaId(), Long.parseLong(storedAccountMmaId));

        final String secondaryMmaIdIndex = EMBEDDED_REDIS_SERVER.client().hget("account:mma:index", String.valueOf(newAccount.getMmaId()));
        assertEquals(newAccount.getUuid().toString(), secondaryMmaIdIndex);
    }

    @Test(expected = DuplicateAccountUuidException.class)
    public void createAccountShouldRejectDuplicateAccountUuid() throws Exception {
        final UUID duplicateUuid = UUID.randomUUID();

        final String firstAccountName = this.testName.getMethodName() + "_1";
        final long firstMmaId = 4566789L;
        final Account firstAccount = new Account(duplicateUuid, firstAccountName, firstMmaId);

        final String secondAccountName = this.testName.getMethodName() + "_2";
        final long secondMmaId = 78234567L;
        final Account secondAccount = new Account(duplicateUuid, secondAccountName, secondMmaId);

        OBJECT_UNDER_TEST.createAccount(firstAccount);
        OBJECT_UNDER_TEST.createAccount(secondAccount);
    }

    @Test(expected = MMAIdAlreadyMappedToAccountException.class)
    public void createAccountShouldRejectDuplicateMMA() throws Exception {
        final String firstAccountName = this.testName.getMethodName() + "_1";
        final String secondAccountName = this.testName.getMethodName() + "_2";
        final long duplicateMmaId = 17866534277L;

        OBJECT_UNDER_TEST.createAccount(firstAccountName, duplicateMmaId);
        OBJECT_UNDER_TEST.createAccount(secondAccountName, duplicateMmaId);
    }

    @Test
    public void accountByMmaIdShouldReturnExistingAccount() throws Exception {
        final String accountName = this.testName.getMethodName();
        final long mmaId = 2347812399675L;

        final Account newAccount = OBJECT_UNDER_TEST.createAccount(accountName, mmaId);

        final Account storedAccount = OBJECT_UNDER_TEST.accountByMmaId(mmaId);
        assertEquals(newAccount, storedAccount);
    }

    @Test
    public void accountByMmaIdShouldReturnNullIfMatchingAccountDoesNotExist() throws Exception {
        final long mmaId = 73478123336721334L;

        final Account storedAccount = OBJECT_UNDER_TEST.accountByMmaId(mmaId);
        assertNull(storedAccount);
    }
}
