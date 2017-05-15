package pl.xcrafters.xcrbungeeconnect;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.List;
import java.util.UUID;

public class ConnectAPI {

    public static void broadcastMessage(String message) {
        broadcastMessage(message, true);
    }

    public static void broadcastMessage(String message, boolean sendToCurrentServer) {
        broadcastMessage(message, null, sendToCurrentServer);
    }

    public static void broadcastMessage(String message, String permission) {
        broadcastMessage(message, permission, true);
    }

    public static void broadcastMessage(String message, String permission, boolean sendToCurrentServer) {
        broadcastMessage(TextComponent.fromLegacyText(message), permission, sendToCurrentServer);
    }

    public static void broadcastMessage(BaseComponent base) {
        broadcastMessage(base, true);
    }

    public static void broadcastMessage(BaseComponent base, boolean sendToCurrentServer) {
        broadcastMessage(base, null, sendToCurrentServer);
    }

    public static void broadcastMessage(BaseComponent base, String permission) {
        broadcastMessage(base, permission, true);
    }

    public static void broadcastMessage(BaseComponent base, String permission, boolean sendToCurrentServer) {
        if(sendToCurrentServer) {
            if (permission != null) {
                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    if (player.hasPermission(permission)) {
                        player.sendMessage(base);
                    }
                }
            } else {
                ProxyServer.getInstance().broadcast(base);
            }
        }

        ConnectPlugin.getInstance().redisManager.broadcastMessage(ComponentSerializer.toString(base), permission);
    }

    public static void broadcastMessage(BaseComponent[] base) {
        broadcastMessage(base, null, true);
    }

    public static void broadcastMessage(BaseComponent[] base, boolean sendToCurrentServer) {
        broadcastMessage(base, null, sendToCurrentServer);
    }

    public static void broadcastMessage(BaseComponent[] base, String permission) {
        broadcastMessage(base, permission, true);
    }

    public static void broadcastMessage(BaseComponent[] base, String permission, boolean sendToCurrentServer) {
        if(sendToCurrentServer) {
            if (permission != null) {
                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    if (player.hasPermission(permission)) {
                        player.sendMessage(base);
                    }
                }
            } else {
                ProxyServer.getInstance().broadcast(base);
            }
        }

        ConnectPlugin.getInstance().redisManager.broadcastMessage(ComponentSerializer.toString(base), permission);
    }

    public static List<String> getNicks() {
        return ConnectPlugin.getInstance().redisManager.getNicks();
    }

    public static boolean isOnline(UUID uuid) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

        if(player != null) {
            return true;
        }

        return ConnectPlugin.getInstance().redisManager.isOnline(uuid);
    }

    public static boolean isOnline(String nick) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(nick);

        if(player != null) {
            return true;
        }

        return ConnectPlugin.getInstance().redisManager.isOnline(nick);
    }

    public static UUID getUUID(String nick) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(nick);

        if(player != null) {
            return player.getUniqueId();
        }

        return ConnectPlugin.getInstance().redisManager.getUUID(nick);
    }

    public static String getExactNick(String nick) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(nick);

        if(player != null) {
            return player.getName();
        }

        return ConnectPlugin.getInstance().redisManager.getExactNick(nick);
    }

    public static String getIP(UUID uuid) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

        if(player != null) {
            return player.getAddress().getAddress().getHostAddress();
        }

        return ConnectPlugin.getInstance().redisManager.getIP(uuid);
    }

    public static String getIP(String nick) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(nick);

        if(player != null) {
            return player.getAddress().getAddress().getHostAddress();
        }

        return ConnectPlugin.getInstance().redisManager.getIP(nick);
    }

    public static ServerInfo getServer(UUID uuid) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

        if(player != null) {
            return player.getServer().getInfo();
        }

        return ConnectPlugin.getInstance().redisManager.getServer(uuid);
    }

    public static ServerInfo getServer(String nick) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(nick);

        if(player != null) {
            return player.getServer().getInfo();
        }

        return ConnectPlugin.getInstance().redisManager.getServer(nick);
    }

    public static void sendMessage(UUID uuid, String message) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

        if(player != null) {
            player.sendMessage(message);
            return;
        }

        ConnectPlugin.getInstance().redisManager.sendMessage(uuid, message);
    }

    public static void sendMessage(String nick, String message) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(nick);

        if(player != null) {
            player.sendMessage(message);
            return;
        }

        ConnectPlugin.getInstance().redisManager.sendMessage(nick, message);
    }

    public static void kickPlayer(UUID uuid, String reason) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

        if(player != null) {
            player.disconnect(reason);
            return;
        }

        ConnectPlugin.getInstance().redisManager.kickPlayer(uuid, reason);
    }

    public static void kickPlayer(String nick, String reason) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(nick);

        if(player != null) {
            player.disconnect(reason);
            return;
        }

        ConnectPlugin.getInstance().redisManager.kickPlayer(nick, reason);
    }

    public static void kickIP(String ip, String reason) {
        for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if(player.getAddress().getAddress().getHostAddress().equals(ip)) {
                player.disconnect(reason);
            }
        }

        ConnectPlugin.getInstance().redisManager.kickIP(ip, reason);
    }

}
