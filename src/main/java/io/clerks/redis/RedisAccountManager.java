package io.clerks.redis;

import io.clerks.Account;
import io.clerks.AccountManager;
import io.clerks.DuplicateAccountUuidException;
import io.clerks.MMAIdAlreadyMappedToAccountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisDataException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkState;

public final class RedisAccountManager implements AccountManager {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final JedisPool redisClientPool;

    private LuaScriptRegistrar.ScriptHandles scriptHandles;

    public RedisAccountManager(final String redisHost, final int redisPort) {
        this(new JedisPool(new JedisPoolConfig(), redisHost, redisPort));
    }

    public RedisAccountManager(final JedisPool redisClientPool) {
        this.redisClientPool = redisClientPool;
    }

    @Override
    public Account createAccount(final String name,
                                 final long mmaId) throws DuplicateAccountUuidException, MMAIdAlreadyMappedToAccountException {
        checkState(this.scriptHandles != null, "RedisAccountManager has not yet been initialized - did you forget to call #initialize()?");
        try (final Jedis redisClient = this.redisClientPool.getResource()) {
            this.log.info("Creating account [name:{}|mmaId:{}] ...", name, mmaId);
            final Account newAccount = Account.newAccount(name, mmaId);
            redisClient.evalsha(this.scriptHandles.createAccountScriptSha,
                    Collections.singletonList("account:mma:index"),
                    Arrays.asList(newAccount.getUuid().toString(), newAccount.getName(), String.valueOf(newAccount.getMmaId())));
            this.log.info("Successfully created new account {}", newAccount);

            return newAccount;
        } catch (final JedisDataException jde) {
            if (ErrorCode.DUPLICATE_ACCOUNT_UUID.equals(jde.getMessage())) {
                throw new DuplicateAccountUuidException(null);
            } else if (ErrorCode.DUPLICATE_MMA.equals(jde.getMessage())) {
                throw new MMAIdAlreadyMappedToAccountException(mmaId);
            }
            throw jde;
        }
    }

    public Account accountByMmaId(final long mmaId) {
        checkState(this.scriptHandles != null, "RedisAccountManager has not yet been initialized - did you forget to call #initialize()?");
        try (final Jedis redisClient = this.redisClientPool.getResource()) {
            this.log.info("Looking up account by MMA-ID [{}] ...", mmaId);
            final Object result = redisClient.evalsha(this.scriptHandles.getAccountScriptSha,
                    Collections.singletonList("account:mma:index"),
                    Collections.singletonList(String.valueOf(mmaId)));
            @SuppressWarnings("unchecked")
            final ArrayList<String> resultList = (ArrayList<String>) result;
            final Account account = new Account(UUID.fromString(resultList.get(1)), resultList.get(3), Long.parseLong(resultList.get(5)));
            this.log.info("Successfully looked up account {}", account);

            return account;
        } catch (final JedisDataException jde) {
            if (ErrorCode.NOT_FOUND.equals(jde.getMessage())) {
                return null;
            }
            throw jde;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Lifecycle management
    // -----------------------------------------------------------------------------------------------------------------

    @PostConstruct
    public void initialize() throws IOException {
        this.log.info("Initializing {} ...", this);
        final LuaScriptRegistrar registrar = new LuaScriptRegistrar(this.redisClientPool);
        this.scriptHandles = registrar.register();
        this.log.info("Successfully initialized {}", this);
    }

    @PreDestroy
    public void destroy() {
        this.log.info("Destroying {} ...", this);
        this.redisClientPool.destroy();
        this.log.info("Successfully destroyed {}", this);
    }
}
