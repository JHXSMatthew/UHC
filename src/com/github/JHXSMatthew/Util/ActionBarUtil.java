package com.github.JHXSMatthew.Util;

import org.bukkit.ChatColor;

public class ActionBarUtil {

	
	public static String getActionBarString(double percentage){
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.GREEN + "■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		sb.setCharAt((int) (25*percentage), ChatColor.COLOR_CHAR);
		sb.setCharAt((int) (25*percentage) + 1, '7');

		return sb.toString();
	}
}
