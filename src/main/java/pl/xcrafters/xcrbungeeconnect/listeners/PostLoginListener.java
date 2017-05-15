package pl.xcrafters.xcrbungeeconnect.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.xcrafters.xcrbungeeconnect.ConnectPlugin;

public class PostLoginListener implements Listener {

    ConnectPlugin plugin;

    public PostLoginListener(ConnectPlugin plugin) {
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        final ProxiedPlayer player = event.getPlayer();

        plugin.redisManager.addPlayer(player);
    }

}
