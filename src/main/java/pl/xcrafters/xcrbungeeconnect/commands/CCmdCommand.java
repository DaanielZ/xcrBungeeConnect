package pl.xcrafters.xcrbungeeconnect.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import pl.xcrafters.xcrbungeeconnect.ConnectPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class CCmdCommand extends Command {

    ConnectPlugin plugin;

    public CCmdCommand(ConnectPlugin plugin) {
        super("ccmd", "connect.cmd");
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    private Gson gson = new Gson();

    @Override
    public void execute(final CommandSender sender, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Poprawne uzycie: /ccmd <komenda bez />");
            return;
        }

        String cmd = args[0];
        for (int i = 1; i < args.length; i++) {
            cmd += " " + args[i];
        }

        final JsonObject object = new JsonObject();
        object.addProperty("command", cmd);
        object.addProperty("instance", plugin.redisManager.getInstance());

        ProxyServer.getInstance().getScheduler().runAsync(plugin, new Runnable() {
            public void run() {
                Jedis jedis = null;

                try {
                    jedis = plugin.redisManager.getPool().getResource();

                    jedis.publish("ConnectExecuteCommand", gson.toJson(object));

                    sender.sendMessage("§aWykonano komende /" + object.get("command").getAsString() + " na wszystkich instancjach!");
                } catch (JedisConnectionException ex) {
                    if(jedis != null) {
                        plugin.redisManager.getPool().returnBrokenResource(jedis);
                    }
                } finally {
                    plugin.redisManager.getPool().returnResource(jedis);
                }
            }
        });

        ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), cmd);
    }

}
