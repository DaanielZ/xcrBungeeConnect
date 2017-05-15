package pl.xcrafters.xcrbungeeconnect.listeners;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;
import pl.xcrafters.xcrbungeeconnect.ConnectPlugin;
import pl.xcrafters.xcrbungeeconnect.events.*;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

public class RedisListener extends JedisPubSub {

    ConnectPlugin plugin;

    public RedisListener(ConnectPlugin plugin) {
        this.plugin = plugin;
        plugin.redisManager.subscribe(this, "ConnectBroadcastMessage", "ConnectSendMessage", "ConnectKickPlayer", "ConnectKickIP", "ConnectPlayerPreLogin", "ConnectPlayerLogin", "ConnectPlayerServerConnect", "ConnectPlayerServerDisconnect", "ConnectPlayerDisconnect", "ConnectExecuteCommand");
    }

    Gson gson = new Gson();

    public void onMessage(String channel, String json) {
        JsonObject object = gson.fromJson(json, JsonObject.class);

        if(channel.equals("ConnectBroadcastMessage")) {
            String instance = object.get("instance").getAsString();
            BaseComponent[] message = ComponentSerializer.parse(object.get("message").getAsString());

            if(instance.equals(plugin.redisManager.getInstance())) {
                return;
            }

            if(object.get("permission") != null) {
                String permission = object.get("permission").getAsString();

                for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    if(player.hasPermission(permission)) {
                        player.sendMessage(message);
                    }
                }
            } else {
                ProxyServer.getInstance().broadcast(message);
            }
        } else if(channel.equals("ConnectSendMessage")) {
            UUID uuid = UUID.fromString(object.get("uuid").getAsString());
            String message = object.get("message").getAsString();

            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

            if(player == null) {
                return;
            }

            player.sendMessage(message);
        } else if(channel.equals("ConnectKickPlayer")) {
            UUID uuid = UUID.fromString(object.get("uuid").getAsString());
            String reason = object.get("reason").getAsString();

            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

            if(player == null) {
                return;
            }

            player.disconnect(reason);
        } else if(channel.equals("ConnectKickIP")) {
            String instance = object.get("instance").getAsString();
            String ip = object.get("ip").getAsString();
            String reason = object.get("reason").getAsString();

            if(instance.equals(plugin.redisManager.getInstance())) {
                return;
            }

            for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if(player.getAddress().getAddress().getHostAddress().equals(ip)) {
                    player.disconnect(reason);
                }
            }
        } else if(channel.equals("ConnectPlayerPreLogin")) {
            UUID uuid = UUID.fromString(object.get("uuid").getAsString());
            String nick = object.get("nick").getAsString();
            String ip = object.get("ip").getAsString();
            String instance = object.get("instance").getAsString();

            if(plugin.redisManager.getInstance().equals(instance)) {
                return;
            }

            ConnectPlayerPreLoginEvent event = new ConnectPlayerPreLoginEvent(uuid, nick, ip);
            ProxyServer.getInstance().getPluginManager().callEvent(event);
        } else if(channel.equals("ConnectPlayerLogin")) {
            UUID uuid = UUID.fromString(object.get("uuid").getAsString());
            String nick = object.get("nick").getAsString();
            String ip = object.get("ip").getAsString();
            String instance = object.get("instance").getAsString();

            if (plugin.redisManager.getInstance().equals(instance)) {
                return;
            }

            ConnectPlayerLoginEvent event = new ConnectPlayerLoginEvent(uuid, nick, ip);
            ProxyServer.getInstance().getPluginManager().callEvent(event);
        } else if(channel.equals("ConnectPlayerServerConnect")) {
            UUID uuid = UUID.fromString(object.get("uuid").getAsString());
            String nick = object.get("nick").getAsString();
            String ip = object.get("ip").getAsString();
            ServerInfo info = ProxyServer.getInstance().getServerInfo(object.get("info").getAsString());
            String instance = object.get("instance").getAsString();

            if (plugin.redisManager.getInstance().equals(instance)) {
                return;
            }

            ConnectPlayerConnectServerEvent event = new ConnectPlayerConnectServerEvent(uuid, nick, ip, info);
            ProxyServer.getInstance().getPluginManager().callEvent(event);
        } else if(channel.equals("ConnectPlayerServerDisconnect")) {
            UUID uuid = UUID.fromString(object.get("uuid").getAsString());
            String nick = object.get("nick").getAsString();
            String ip = object.get("ip").getAsString();
            ServerInfo info = ProxyServer.getInstance().getServerInfo(object.get("info").getAsString());
            String instance = object.get("instance").getAsString();

            if (plugin.redisManager.getInstance().equals(instance)) {
                return;
            }

            ConnectPlayerDisconnectServerEvent event = new ConnectPlayerDisconnectServerEvent(uuid, nick, ip, info);
            ProxyServer.getInstance().getPluginManager().callEvent(event);
        } else if(channel.equals("ConnectPlayerDisconnect")) {
            UUID uuid = UUID.fromString(object.get("uuid").getAsString());
            String nick = object.get("nick").getAsString();
            String ip = object.get("ip").getAsString();
            String instance = object.get("instance").getAsString();

            if(plugin.redisManager.getInstance().equals(instance)) {
                return;
            }

            ConnectPlayerDisconnectEvent event = new ConnectPlayerDisconnectEvent(uuid, nick, ip);
            ProxyServer.getInstance().getPluginManager().callEvent(event);
        } else if(channel.equals("ConnectExecuteCommand")) {
            String cmd = object.get("command").getAsString();
            String instance = object.get("instance").getAsString();

            if(plugin.redisManager.getInstance().equals(instance)) {
                return;
            }

            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), cmd);
        }
    }

    public void onSubscribe(String channel, int subscribedChannels) { }

    public void onUnsubscribe(String channel, int subscribedChannels) { }

    public void onPSubscribe(String pattern, int subscribedChannels) { }

    public void onPUnsubscribe(String pattern, int subscribedChannels) { }

    public void onPMessage(String pattern, String channel, String message) { }

}
