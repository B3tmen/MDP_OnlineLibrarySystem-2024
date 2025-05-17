package org.unibl.etf.redis;

import org.unibl.etf.util.ConfigReader;
import redis.clients.jedis.Jedis;

public class RedisConnection {
    private static final ConfigReader config = ConfigReader.getInstance();

    private Jedis jedis;

    public RedisConnection() {
        jedis = new Jedis("localhost", config.getRedisPort());
    }

    public Jedis getJedis() {
        return jedis;
    }

    public void close() {
        if (jedis != null) {
            jedis.close();
        }
    }
}
