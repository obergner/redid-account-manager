package io.clerks.redis;

import io.clerks.EmbeddedRedisServer;
import org.junit.ClassRule;
import org.junit.Test;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class LuaScriptRegistrarTest {

    @ClassRule
    public static final EmbeddedRedisServer EMBEDDED_REDIS_SERVER = EmbeddedRedisServer.listenOnDefaultPort();

    @Test
    public void shouldSuccessfullyRegisterAllScripts() throws IOException {
        final JedisPool redisClientPool = new JedisPool(new JedisPoolConfig(), "127.0.0.1", 6379);
        final LuaScriptRegistrar objectUnderTest = new LuaScriptRegistrar(redisClientPool);

        final LuaScriptRegistrar.ScriptHandles handles = objectUnderTest.register();

        assertNotNull(handles);
    }
}
