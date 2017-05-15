package pl.xcrafters.xcrbungeeconnect.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import pl.xcrafters.xcrbungeeconnect.ConnectPlugin;

public class ProxyStopListener implements Listener {

    ConnectPlugin plugin;

    public ProxyStopListener(ConnectPlugin plugin) {
        this.plugin = plugin;
        ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
    }

    //TODO Tutaj był używany event z mojego forka BC, przydałoby się wykminić inną opcję bez modyfikacji BC na usuwanie graczy z pamięci przy wyłączaniu instancji.

//    @EventHandler
//    public void onProxyStop(ProxyStopEvent event) {
//        for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
//            plugin.redisManager.removePlayer(player);
//        }
//    }

}
