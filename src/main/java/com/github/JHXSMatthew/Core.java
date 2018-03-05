package com.github.JHXSMatthew;

import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.JHXSMatthew.Config.Config;
import com.github.JHXSMatthew.Config.Message;
import com.github.JHXSMatthew.Controller.BungeeController;
import com.github.JHXSMatthew.Controller.ChestControl;
import com.github.JHXSMatthew.Controller.ChunkController;
import com.github.JHXSMatthew.Controller.MapController;
import com.github.JHXSMatthew.Controller.MySQLController;
import com.github.JHXSMatthew.Controller.PlayerController;
import com.github.JHXSMatthew.Controller.WorldController;
import com.github.JHXSMatthew.Game.Game;
import com.github.JHXSMatthew.Game.GamePlayer;
import com.github.JHXSMatthew.Game.Game.GameState;
import com.github.JHXSMatthew.Listener.BlockListener;
import com.github.JHXSMatthew.Listener.PlayerListener;
import com.github.JHXSMatthew.Util.NMS;
import com.github.JHXSMatthew.Util.NMSHandler;

public class Core extends JavaPlugin{

	private static Core instance;
	public static double boarderSize = 2000;
	public static int chunkNum = 64;
	
	public static Location lobby;
	
	private MapController mc = null;
	private WorldController wc = null;
	private Game current = null;
	private static Message msg = null;
	private static NMS nms = null;
	private BungeeController bc = null;
	private PlayerController pc = null;
	private static Config confi = null;
	private MySQLController sql = null;
	private ChestControl chest = null;
	private ChunkController chunk = null;
	
	public static Permission permission = null;
	public static Economy economy = null;
	public static Chat chat = null;
	
	public void onEnable(){
		
		Logger logger = Logger.getLogger("Minecraft");
		
		instance = this;
		// YOU CAN ALWAYS SET THIS FLAG TO DISABLE LOADING FROM CONFIG
		if(!Config.NON_CONFIG_MODE){
			if(getConfig() == null){
				saveDefaultConfig();
			}
			Config.loadConfig(getConfig());
		}

		logger.info("============ UHC初始化 ============");
		logger.info("-->加载配置文件");
		msg = new Message();
		logger.info("    Msg done");
		confi = new Config();
		logger.info("    Cfg done");
		
 		logger.info("-->加载控制器");
		nms = new NMSHandler();
		logger.info("    NMS done");
		wc = new WorldController();
		logger.info("    WC done");
		mc = new MapController();
		logger.info("    MC done");
		bc = new BungeeController();
		logger.info("    BC done");
		pc = new PlayerController();
		logger.info("    PC done");

		if(Config.USING_MYSQL){
			sql = new MySQLController();
			sql.openConnection();
			logger.info("    SQL done");
		}

		chest = new ChestControl();
		logger.info("    CC done");
		chunk = new ChunkController();
		logger.info("    Chunk done");

 		logger.info("-->加载事件");
 		getServer().getPluginManager().registerEvents(bc, this);
 		getServer().getMessenger().registerOutgoingPluginChannel(this, "LobbyConnect");
 		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
 		getServer().getPluginManager().registerEvents(new BlockListener(), this);


 		
 		try{
			logger.info("-->注册权限经济绑定");
			setupEconomy();
			logger.info("    --Eco done");
			setupChat();	
			logger.info("    --Chat done");
			setupPermissions();
			logger.info("    --Perm done");
			logger.info("  <<完成!");
			}catch(Exception e ){
				logger.info("  <<无法加载!");
			}
		
		
	//finally setup a game
		
		current = new Game();
		logger.info("============ 初始化完毕 ============");
		
		lobby = Bukkit.getWorld("lobby").getSpawnLocation();
 		lobby.getWorld().setTime(1000);
 		lobby.getWorld().setStorm(false);
 		lobby.getWorld().setGameRuleValue("doDaylightCycle", "false");
	}
	
	public void onDisable(){
		for(Player p : Bukkit.getOnlinePlayers()){
			pc.getGamePlayer(p).getGs().save();
			p.kickPlayer("大厅已满");
		}
	}

	
	public static Core get(){
		return instance;
	}
	
	public PlayerController getPc(){
		return pc;
	}
	
	public ChunkController getChunk(){
		return chunk;
	}
	
	public BungeeController getBc(){
		return bc;
	}
	public MySQLController getSql(){
		return sql;
	}
	public ChestControl getCc(){
		return chest;
	}
	
	public WorldController getWc(){
		return wc;
	}
	public MapController getMc(){
		return mc;
	}
	public Game getCurrentGame(){
		return current;
	}
	public static NMS getNms(){
		return nms;
	}
	public static Message getMsg(){
		return msg;
	}
	
    private boolean setupPermissions(){
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    private boolean setupChat(){
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }

    private boolean setupEconomy(){
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
	
	public boolean onCommand(CommandSender sender, Command cmd, String cl, String[] args) {
		if(current.getGameStateString().contains("游戏")){
			return false;
		}
		if(sender.isOp()){
			if(args.length == 1){
				if(args[0].equals("start")){
					if(current.getGameState() == GameState.Lobby || current.getGameState() == GameState.Starting){
						current.changeState(GameState.Teleporting);
					}
					return true;
				}
			}
		}
		
		if(args.length < 1){
			sender.sendMessage(Message.prefix + ChatColor.GRAY + " 使用 /zu 玩家姓名 或 shift加右键点击玩家组队.");
			return true;
		}
		if(Bukkit.getPlayer(args[0]) == null ||  !Bukkit.getPlayer(args[0]).isOnline()){
			sender.sendMessage(Message.prefix + ChatColor.GRAY + " 使用 /zu 玩家姓名 或 shift加右键点击玩家组队.");

			return true;
		}
		GamePlayer a = pc.getGamePlayer((Player)sender);
		GamePlayer b = pc.getGamePlayer(Bukkit.getPlayer(args[0]));
		if(a==null || b == null){
			sender.sendMessage(Message.prefix + ChatColor.GRAY + " 玩家名称不存在，建议您使用shift加右键组队.");
			return true;
		}
		
		current.teamPlayer(a, b);
		
		return true;
		
	
	}
}
