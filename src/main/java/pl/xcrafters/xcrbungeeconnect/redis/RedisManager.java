package pl.xcrafters.xcrbungeeconnect.redis;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.xcrafters.xcrbungeeconnect.ConnectPlugin;
import pl.xcrafters.xcrbungeeconnect.events.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RedisManager {

    ConnectPlugin plugin;

    public RedisManager(ConnectPlugin plugin) {
        this.plugin = plugin;

        pool = new JedisPool(new JedisPoolConfig(), plugin.configManager.redisHost, 6379, 60000);
        subscriber = pool.getResource();

        this.instance = plugin.generateRandomString();
    }

    JedisPool pool;
    Jedis subscriber;

    public JedisPool getPool() {
        return this.pool;
    }

    String instance;

    public void subscribe(final JedisPubSub pubSub, final String... channels) {
        ProxyServer.getInstance().getScheduler().runAsync(plugin, new Runnable() {
            public void run() {
                subscriber.subscribe(pubSub, channels);
            }
        });
    }

    public String getInstance() {
        return instance;
    }

    Gson gson = new Gson();

    public void preLoginPlayer(UUID uuid, String nick, String ip) {
        JsonObject object = new JsonObject();
        object.addProperty("uuid", uuid.toString());
        object.addProperty("nick", nick);
        object.addProperty("ip", ip);
        object.addProperty("instance", instance);

        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            jedis.publish("ConnectPlayerPreLogin", gson.toJson(object));
        } catch (JedisConnectionException ex) {
            if(jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            pool.returnResource(jedis);
        }

        ConnectPlayerPreLoginEvent preLoginEvent = new ConnectPlayerPreLoginEvent(uuid, nick, ip);
        ProxyServer.getInstance().getPluginManager().callEvent(preLoginEvent);
    }

    public void addPlayer(ProxiedPlayer player) {
        UUID uuid = player.getUniqueId();
        String nick = player.getName();
        String ip = player.getAddress().getAddress().getHostAddress();

        JsonObject object = new JsonObject();
        object.addProperty("uuid", uuid.toString());
        object.addProperty("nick", nick);
        object.addProperty("ip", ip);
        object.addProperty("instance", instance);

        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            jedis.sadd("connect_players", uuid.toString());
            jedis.sadd("connect_nicks", nick);
            jedis.hset("connect_player_uuids", nick.toLowerCase(), uuid.toString());
            jedis.hset("connect_player_nicks", nick.toLowerCase(), nick);
            jedis.hset("connect_player_ips", uuid.toString(), ip);

            jedis.publish("ConnectPlayerLogin", gson.toJson(object));
        } catch (JedisConnectionException ex) {
            if(jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            pool.returnResource(jedis);
        }

        ConnectPlayerLoginEvent event = new ConnectPlayerLoginEvent(uuid, nick, ip);
        ProxyServer.getInstance().getPluginManager().callEvent(event);
    }

    public void connectServer(ProxiedPlayer player, ServerInfo info) {
        UUID uuid = player.getUniqueId();
        String nick = player.getName();
        String ip = player.getAddress().getAddress().getHostAddress();

        JsonObject object = new JsonObject();
        object.addProperty("uuid", uuid.toString());
        object.addProperty("nick", nick);
        object.addProperty("ip", ip);
        object.addProperty("info", info.getName());
        object.addProperty("instance", instance);

        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            jedis.hset("connect_player_servers", uuid.toString(), info.getName());

            jedis.publish("ConnectPlayerServerConnect", gson.toJson(object));
        } catch (JedisConnectionException ex) {
            if(jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            pool.returnResource(jedis);
        }

        ConnectPlayerConnectServerEvent event = new ConnectPlayerConnectServerEvent(uuid, nick, ip, info);
        ProxyServer.getInstance().getPluginManager().callEvent(event);
    }

    public void disconnectServer(ProxiedPlayer player, ServerInfo info) {
        UUID uuid = player.getUniqueId();
        String nick = player.getName();
        String ip = player.getAddress().getAddress().getHostAddress();

        JsonObject object = new JsonObject();
        object.addProperty("uuid", uuid.toString());
        object.addProperty("nick", nick);
        object.addProperty("ip", ip);
        object.addProperty("info", info.getName());
        object.addProperty("instance", instance);

        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            String serverName = jedis.hget("connect_player_servers", uuid.toString());

            if(serverName != null && serverName.equals(info.getName())) {
                jedis.hdel("connect_player_servers", uuid.toString());
            }

            jedis.publish("ConnectPlayerServerDisconnect", gson.toJson(object));
        } catch (JedisConnectionException ex) {
            if(jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            pool.returnResource(jedis);
        }

        ConnectPlayerDisconnectServerEvent event = new ConnectPlayerDisconnectServerEvent(uuid, nick, ip, info);
        ProxyServer.getInstance().getPluginManager().callEvent(event);
    }

    public void removePlayer(ProxiedPlayer player) {
        UUID uuid = player.getUniqueId();
        String nick = player.getName();
        String ip = player.getAddress().getAddress().getHostAddress();

        JsonObject object = new JsonObject();
        object.addProperty("uuid", uuid.toString());
        object.addProperty("nick", nick);
        object.addProperty("ip", ip);
        object.addProperty("instance", instance);

        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            jedis.srem("connect_players", uuid.toString());
            jedis.srem("connect_nicks", nick);
            jedis.hdel("connect_player_uuids", nick.toLowerCase());
            jedis.hdel("connect_player_nicks", nick.toLowerCase());
            jedis.hdel("connect_player_ips", uuid.toString());
            jedis.hdel("connect_player_servers", uuid.toString());

            jedis.publish("ConnectPlayerDisconnect", gson.toJson(object));
        } catch (JedisConnectionException ex) {
            if(jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            pool.returnResource(jedis);
        }

        ConnectPlayerDisconnectEvent event = new ConnectPlayerDisconnectEvent(uuid, nick, ip);
        ProxyServer.getInstance().getPluginManager().callEvent(event);
    }

    public List<String> getNicks() {
        List<String> nicks = new ArrayList();

        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            nicks.addAll(jedis.smembers("connect_nicks"));
        } catch (JedisConnectionException ex) {
            if(jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            pool.returnResource(jedis);
        }

        return nicks;
    }

    public boolean isOnline(UUID uuid) {
        if(uuid == null) {
            return false;
        }

        boolean online = false;

        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            online = jedis.sismember("connect_players", uuid.toString());
        } catch (JedisConnectionException ex) {
            if(jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            pool.returnResource(jedis);
        }

        return online;
    }

    public boolean isOnline(String nick) {
        return getUUID(nick) != null;
    }

    public String getExactNick(String nick) {
        if(nick == null) {
            return null;
        }

        String exactNick = null;

        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            exactNick = jedis.hget("connect_player_nicks", nick.toLowerCase());
        } catch (JedisConnectionException ex) {
            if(jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            pool.returnResource(jedis);
        }

        return exactNick;
    }

    public String getIP(UUID uuid) {
        if(uuid == null) {
            return null;
        }

        String ip = null;

        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            ip = jedis.hget("connect_player_ips", uuid.toString());
        } catch (JedisConnectionException ex) {
            if(jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            pool.returnResource(jedis);
        }

        return ip;
    }

    public String getIP(String nick) {
        return getIP(getUUID(nick));
    }

    public UUID getUUID(String nick) {
        if(nick == null) {
            return null;
        }

        UUID uuid = null;

        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            String uuidString = jedis.hget("connect_player_uuids", nick.toLowerCase());

            if(uuidString != null) {
                uuid = UUID.fromString(uuidString);
            }
        } catch (JedisConnectionException ex) {
            if(jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            pool.returnResource(jedis);
        }

        return uuid;
    }

    public ServerInfo getServer(UUID uuid) {
        if(uuid == null) {
            return null;
        }

        ServerInfo server = null;

        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            String serverName = jedis.hget("connect_player_servers", uuid.toString());

            if(serverName != null) {
                server = ProxyServer.getInstance().getServerInfo(serverName);
            }
        } catch (JedisConnectionException ex) {
            if(jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            pool.returnResource(jedis);
        }

        return server;
    }

    public ServerInfo getServer(String nick) {
        return getServer(getUUID(nick));
    }

    public void broadcastMessage(String message, String permission) {
        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            JsonObject object = new JsonObject();
            object.addProperty("instance", instance);
            object.addProperty("message", message);

            if(permission != null) {
                object.addProperty("permission", permission);
            }

            jedis.publish("ConnectBroadcastMessage", gson.toJson(object));
        } catch (JedisConnectionException ex) {
            if(jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            pool.returnResource(jedis);
        }
    }

    public void sendMessage(UUID uuid, String message) {
        if(uuid == null) {
            return;
        }

        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            JsonObject object = new JsonObject();
            object.addProperty("uuid", uuid.toString());
            object.addProperty("message", message);

            jedis.publish("ConnectSendMessage", gson.toJson(object));
        } catch (JedisConnectionException ex) {
            if(jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            pool.returnResource(jedis);
        }
    }

    public void sendMessage(String nick, String message) {
        sendMessage(getUUID(nick), message);
    }

    public void kickPlayer(UUID uuid, String reason) {
        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            JsonObject object = new JsonObject();
            object.addProperty("uuid", uuid.toString());
            object.addProperty("reason", reason);

            jedis.publish("ConnectKickPlayer", gson.toJson(object));
        } catch (JedisConnectionException ex) {
            if(jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            pool.returnResource(jedis);
        }
    }

    public void kickPlayer(String nick, String reason) {
        kickPlayer(getUUID(nick), reason);
    }

    public void kickIP(String ip, String reason) {
        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            JsonObject object = new JsonObject();
            object.addProperty("instance", instance);
            object.addProperty("ip", ip);
            object.addProperty("reason", reason);

            jedis.publish("ConnectKickPlayer", gson.toJson(object));
        } catch (JedisConnectionException ex) {
            if(jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            pool.returnResource(jedis);
        }
    }

}
