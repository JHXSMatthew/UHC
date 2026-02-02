package com.github.JHXSMatthew.Config;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {

	public boolean isSetUp = false;
	public Location lobby = null;
	public static int wimMoney = 30;
	public static int killMoney = 5;
	public static int RealMoney = 2;

	public static boolean NON_CONFIG_MODE = false;

	// 统计系统配置
	public static boolean ENABLE_STATISTICS = true;
	public static boolean USING_MYSQL = true;

	// 数据库连接配置
	public static String SQL_ADDRESS = "127.0.0.1";
	public static int SQL_PORT = 3306;
	public static String SQL_USER_NAME = "root";
	public static String SQL_PASSWORD = "";
	public static String SQL_DB_NAME = "minecraft";
	public static String SQL_TABLE_NAME = "UHC";
	public static String LOBBY_WORLD_NAME = "lobby";

	// 游戏设置配置
	public static double START_PERCENTAGE = 90.0;
	public static int MAX_PLAYERS = 99;
	public static int START_COUNTDOWN = 300;
	public static int NO_PVP_DURATION = 600;
	public static int BORDER_SHRINK_DURATION = 3600;
	public static double INITIAL_BORDER_SIZE = 2000.0;
	public static int CHUNK_PRELOAD_COUNT = 64;

	// 职业系统配置
	public static boolean CLASSES_ENABLED = false;

	public static void loadConfig(FileConfiguration config){
		if(config == null){
			return;
		}
		
		// 加载统计系统配置
		ENABLE_STATISTICS = config.getBoolean("statistics.enable", true);
		USING_MYSQL = config.getBoolean("statistics.sql.enable", true);
		
		if(USING_MYSQL){
			SQL_ADDRESS = config.getString("statistics.sql.address", "127.0.0.1");
			SQL_PORT = config.getInt("statistics.sql.port", 3306);
			SQL_USER_NAME = config.getString("statistics.sql.userName", "root");
			SQL_PASSWORD = config.getString("statistics.sql.password", "");
			SQL_DB_NAME = config.getString("statistics.sql.database", "minecraft");
			SQL_TABLE_NAME = config.getString("statistics.sql.table", "UHC");
		}else{
			// TODO: implement the yaml statistics storage, tho it's not necessary
		}
		
		// 加载大厅世界配置
		LOBBY_WORLD_NAME = config.getString("lobbyWorldName", "lobby");
		
		// 加载游戏设置
		START_PERCENTAGE = config.getDouble("gameSettings.startPercentage", 90.0);
		MAX_PLAYERS = config.getInt("gameSettings.maxPlayers", 99);
		START_COUNTDOWN = config.getInt("gameSettings.startCountdown", 300);
		NO_PVP_DURATION = config.getInt("gameSettings.noPvPDuration", 600);
		BORDER_SHRINK_DURATION = config.getInt("gameSettings.borderShrinkDuration", 3600);
		INITIAL_BORDER_SIZE = config.getDouble("gameSettings.initialBorderSize", 2000.0);
		CHUNK_PRELOAD_COUNT = config.getInt("gameSettings.chunkPreloadCount", 64);
		
		// 加载职业系统配置
		CLASSES_ENABLED = config.getBoolean("classes.enabled", false);

	}


}
