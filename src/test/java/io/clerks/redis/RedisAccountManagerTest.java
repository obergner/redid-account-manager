package io.clerks.redis;

import io.clerks.Account;
import io.clerks.EmbeddedRedisServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.IOException;

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
    public static final void initializeObjectUnderTest() throws IOException {
        OBJECT_UNDER_TEST.initialize();
    }

    @AfterClass
    public static final void destroyObjectUnderTest() {
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
