package pl.xcrafters.xcrbungeeconnect.events;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

public class ConnectPlayerDisconnectServerEvent extends Event {

    private UUID uuid;
    private String nick, ip;
    private ServerInfo info;

    public ConnectPlayerDisconnectServerEvent(UUID uuid, String nick, String ip, ServerInfo info) {
        this.uuid = uuid;
        this.nick = nick;
        this.ip = ip;
        this.info = info;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getNick() {
        return this.nick;
    }

    public String getIP() {
        return this.ip;
    }

    public ServerInfo getServerInfo() {
        return this.info;
    }

}
