package io.clerks.redis;

import com.google.common.base.Preconditions;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

/**
 * Created by obergner on 01.11.14.
 */
class LuaScriptRegistrar {

    final class ScriptHandles {

        public final String createAccountScriptSha;

        public final String getAccountScriptSha;

        ScriptHandles(final String createAccountScriptSha, final String getAccountScriptSha) {
            this.createAccountScriptSha = Preconditions.checkNotNull(createAccountScriptSha);
            this.getAccountScriptSha = Preconditions.checkNotNull(getAccountScriptSha);
        }
    }

    private static final String CREATE_ACCOUNT_SCRIPT_NAME = "create-account.lua";

    private static final String GET_ACCOUNT_SCRIPT_NAME = "get-account.lua";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final JedisPool redisClientPool;

    LuaScriptRegistrar(final JedisPool redisClientPool) {
        this.redisClientPool = Preconditions.checkNotNull(redisClientPool);
    }

    ScriptHandles register() throws IOException {
        this.log.info("Registering Lua scripts in Redis ...");
        final String createAccountHdl = registerScriptByName(CREATE_ACCOUNT_SCRIPT_NAME);
        final String getAccountHdl = registerScriptByName(GET_ACCOUNT_SCRIPT_NAME);
        this.log.info("Successfully registered all Lua scripts in Redis");
        return new ScriptHandles(createAccountHdl, getAccountHdl);
    }

    private String registerScriptByName(final String scriptName) throws IOException {
        this.log.info("Registering Lua script '{}' in Redis ...", scriptName);
        final String handle = registerScript(loadScript(scriptName));
        this.log.info("Successfully registered Lua script '{}' in Redis", scriptName);
        return handle;
    }

    private String registerScript(final String script) {
        try (final Jedis redisClient = this.redisClientPool.getResource()) {
            return redisClient.scriptLoad(script);
        }
    }

    private String loadScript(final String scriptName) throws IOException {
        final String scriptPath = "META-INF/scripts/" + scriptName;
        final URL scriptUrl = getClass().getClassLoader().getResource(scriptPath);
        if (scriptUrl == null) throw new FileNotFoundException(scriptPath);

        return IOUtils.toString(scriptUrl);
    }
}
