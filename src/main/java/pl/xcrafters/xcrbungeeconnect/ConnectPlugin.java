package pl.xcrafters.xcrbungeeconnect;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import pl.xcrafters.xcrbungeeconnect.commands.CCmdCommand;
import pl.xcrafters.xcrbungeeconnect.commands.CDebugCommand;
import pl.xcrafters.xcrbungeeconnect.listeners.*;
import pl.xcrafters.xcrbungeeconnect.redis.RedisManager;

import java.util.Random;

public class ConnectPlugin extends Plugin {

    public ConfigManager configManager;
    public RedisManager redisManager;

    PreLoginListener preLoginListener;
    LoginListener loginListener;
    PostLoginListener postLoginListener;
    PlayerDisconnectListener playerDisconnectListener;
    ServerConnectedListener serverConnectedListener;
    RedisListener redisListener;
    ProxyStopListener proxyStopListener;
    ServerDisconnectListener serverDisconnectListener;

    CDebugCommand cDebugCommand;
    CCmdCommand cCmdCommand;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.redisManager = new RedisManager(this);

        this.preLoginListener = new PreLoginListener(this);
        this.loginListener = new LoginListener(this);
        this.postLoginListener = new PostLoginListener(this);
        this.playerDisconnectListener = new PlayerDisconnectListener(this);
        this.serverConnectedListener = new ServerConnectedListener(this);
        this.redisListener = new RedisListener(this);
        this.proxyStopListener = new ProxyStopListener(this);
        this.serverDisconnectListener = new ServerDisconnectListener(this);

        this.cDebugCommand = new CDebugCommand(this);
        this.cCmdCommand = new CCmdCommand(this);

        instance = this;
    }

    @Override
    public void onDisable() {
        for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            redisManager.removePlayer(player);
        }
    }

    private static ConnectPlugin instance;

    public static ConnectPlugin getInstance() {
        return instance;
    }

    private static final String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    public String generateRandomString(){
        StringBuffer randStr = new StringBuffer();
        for(int i=0; i<8; i++){
            int number = getRandomNumber();
            char ch = CHAR_LIST.charAt(number);
            randStr.append(ch);
        }
        return randStr.toString();
    }

    private int getRandomNumber() {
        int randomInt = 0;
        Random randomGenerator = new Random();
        randomInt = randomGenerator.nextInt(CHAR_LIST.length());
        if (randomInt - 1 == -1) {
            return randomInt;
        } else {
            return randomInt - 1;
        }
    }

}
