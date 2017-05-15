package pl.xcrafters.xcrbungeeconnect.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import pl.xcrafters.xcrbungeeconnect.ConnectPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.UUID;

public class CDebugCommand extends Command {

    ConnectPlugin plugin;

    public CDebugCommand(ConnectPlugin plugin) {
        super("cdebug", "connect.debug");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    public void execute(final CommandSender sender, String[] args) {
        if(args.length != 1) {
            sender.sendMessage("§cPoprawne uzycie: /cdebug <nick>");
            return;
        }

        final String nick = args[0];

        ProxyServer.getInstance().getScheduler().runAsync(plugin, new Runnable() {
            public void run() {
                Jedis jedis = null;

                try {
                    jedis = plugin.redisManager.getPool().getResource();

                    jedis.srem("connect_nicks", nick);
                    jedis.hdel("connect_player_uuids", nick.toLowerCase());
                    jedis.hdel("connect_player_nicks", nick.toLowerCase());

                    sender.sendMessage("§aOdbugowano gracza " + nick + "!");
                } catch (JedisConnectionException ex) {
                    if(jedis != null) {
                        plugin.redisManager.getPool().returnBrokenResource(jedis);
                    }
                } finally {
                    plugin.redisManager.getPool().returnResource(jedis);
                }
            }
        });
    }

}
