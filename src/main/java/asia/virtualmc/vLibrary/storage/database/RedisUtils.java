package asia.virtualmc.vLibrary.storage.database;

import asia.virtualmc.vLibrary.utilities.files.YAMLUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.plugin.Plugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtils {
    private static JedisPool jedisPool;
    private static final int DEFAULT_EXPIRY_SECONDS = 3600;

    /**
     * Initializes the Redis connection pool using configuration from redis.yml.
     *
     * @param plugin the plugin instance used to access the configuration file
     */
    public static void initialize(Plugin plugin) {
        YamlDocument yaml = YAMLUtils.getYamlDocument(plugin, "redis.yml");

        if (yaml == null) {
            plugin.getLogger().severe("Cannot load redis.yml because it does not exist!");
            return;
        }

        String host = yaml.getString("redis.host");
        String password = yaml.getString("redis.password");
        int port = yaml.getInt("redis.port");

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(16);
        poolConfig.setMaxIdle(8);
        poolConfig.setMinIdle(2);

        if (password != null && !password.isEmpty()) {
            jedisPool = new JedisPool(poolConfig, host, port, 2000, password);
        } else {
            jedisPool = new JedisPool(poolConfig, host, port);
        }
    }

    /**
     * Shuts down the Redis connection pool if it is open.
     */
    public static void shutdown() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
        }
    }

    /**
     * Sets a key-value pair in Redis with a default expiration time.
     *
     * @param key the key to store
     * @param value the value to associate with the key
     */
    public static void set(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value);
            jedis.expire(key, DEFAULT_EXPIRY_SECONDS);
        }
    }

    /**
     * Retrieves the value associated with the given key from Redis.
     * Automatically renews the default expiration time if the key exists.
     *
     * @param key the key to retrieve
     * @return the value associated with the key, or null if the key does not exist
     */
    public static String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            String value = jedis.get(key);
            if (value != null) {
                jedis.expire(key, DEFAULT_EXPIRY_SECONDS);
            }
            return value;
        }
    }

    /**
     * Deletes the specified key from Redis.
     *
     * @param key the key to delete
     */
    public static void delete(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }

    /**
     * Checks if the specified key exists in Redis.
     *
     * @param key the key to check
     * @return true if the key exists, false otherwise
     */
    public static boolean exists(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key);
        }
    }

    /**
     * Sets a key-value pair in Redis with a custom expiration time.
     *
     * @param key the key to store
     * @param value the value to associate with the key
     * @param seconds the time in seconds before the key expires
     */
    public static void setWithExpiry(String key, String value, int seconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, seconds, value);
        }
    }

    /**
     * Checks whether the Redis connection pool has been initialized.
     *
     * @return true if the connection pool exists, false otherwise
     */
    public static boolean hasConnection() {
        return jedisPool != null;
    }

    /**
     * Retrieves the DEFAULT_EXPIRY_SECONDS.
     *
     * @return the current value of DEFAULT_EXPIRY_SECONDS.
     */
    public static int getDefaultExpirySeconds() { return DEFAULT_EXPIRY_SECONDS; }
}