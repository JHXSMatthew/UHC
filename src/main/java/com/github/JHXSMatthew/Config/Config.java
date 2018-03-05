package com.github.JHXSMatthew.Config;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {

	public boolean isSetUp = false;
	public Location lobby = null;
	public static int wimMoney = 30;
	public static int killMoney = 5;
	public static int RealMoney = 2;

	public static boolean NON_CONFIG_MODE = false;


	public static boolean ENABLE_STATISTICS = true;
	public static boolean USING_MYSQL = true;

	public static String SQL_ADDRESS = "127.0.0.1";
	public static int SQL_PORT = 3306;
	public static String SQL_USER_NAME = "root";
	public static String SQL_PASSWORD = "pwd";
	public static String SQL_DB_NAME = "mc";
	public static String SQL_TABLE_NAME = "uhc";


	public static void loadConfig(FileConfiguration config){
		if(config == null){
			return;
		}
		ENABLE_STATISTICS = config.getBoolean("statistics.enable");
		USING_MYSQL = config.getBoolean("statistics.sql.enable");
		if(USING_MYSQL){
			SQL_ADDRESS = config.getString("statistics.sql.address");
			SQL_PORT = config.getInt("statistics.sql.port");
			SQL_USER_NAME = config.getString("statistics.sql.userName");
			SQL_PASSWORD = config.getString("statistics.sql.password");
			SQL_DB_NAME = config.getString("statistics.sql.database");
			SQL_TABLE_NAME = config.getString("statistics.sql.table");
		}else{
			//TODO: implement the yaml statistics storage, tho it's not necessary
		}




	}


}
