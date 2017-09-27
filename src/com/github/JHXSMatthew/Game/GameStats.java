package com.github.JHXSMatthew.Game;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.JHXSMatthew.Core;
import com.github.JHXSMatthew.Util.SQLStatsContainer;
import org.bukkit.scheduler.BukkitRunnable;


public class GameStats {

	private String name;
	private int games = 0;
	private int wins = 0;
	private int kills = 0;
	private int death = 0;
	private int stack = 0;
	private int points = 0;
	private boolean isNew = true;
	private boolean changed = false;
	public int Ori_stack = 0;
	
	public GameStats(String name){
		this.name = name;
		load();
	}
	
	
	private void load(){
		if(Core.get().getSql()==null){
			return;
		}
		SQLStatsContainer ct = null;
		try {
			ct = Core.get().getSql().loadStats(name);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(ct == null){
			System.out.print("STATS LOAD ERROR");
			return;
		}
		if(!ct.New){
			games = ct.games;
			wins = ct.wins;
			isNew = ct.New;
			Ori_stack = ct.stack;
			kills = ct.kills;
			death = ct.death;
		}
		
	}
	
	
	
	public void save(){
		if(Core.get().getSql()==null){
			return;
		}
		if(changed || isNew){
			points = 5*kills + 1 *games + 100*wins + stack * stack * 7 ;
			new BukkitRunnable(){

				@Override
				public void run() {
					Core.get().getSql().savePlayerData(get());

				}
			}.runTaskAsynchronously(Core.get());
		}
	}
	
	public void show(){
		Player p  =  Bukkit.getPlayer(name);
		if(p == null){
			return;
		}
		p.sendMessage(ChatColor.GRAY + "---------"+ChatColor.GOLD + " UHC 极限生存 数据统计" + ChatColor.GRAY + " ---------");
		p.sendMessage(ChatColor.YELLOW + " 场次: " + ChatColor.GREEN + games);
		p.sendMessage(ChatColor.YELLOW + " 胜利: " + ChatColor.GREEN + wins);
		p.sendMessage(ChatColor.YELLOW + " 击杀: " + ChatColor.GREEN + kills);
		p.sendMessage(ChatColor.YELLOW + " 死亡: " + ChatColor.GREEN + death);
		if(death == 0){
			p.sendMessage(ChatColor.YELLOW + " K/D: " + ChatColor.GREEN + (float)wins);
		}else{
			p.sendMessage(ChatColor.YELLOW + " K/D: " + ChatColor.GREEN + (float)wins/(float)death);
		}
		p.sendMessage(ChatColor.YELLOW + " 最高连杀: " + ChatColor.GREEN + stack);
		p.sendMessage(ChatColor.YELLOW + " 综合积分: " + ChatColor.GREEN + points);

		
	}

	public GameStats get(){
		return this;
	}
	
	public boolean isNew(){
		return isNew;
	}
	
	public String getName(){
		return name;
	}
	
	public int getStack(){
		return stack;
	}
	
	public int getPoints(){
		return this.points;
	}
	
	public void addWin(){
		changed = true;
		wins ++;
	}
	
	public void addgames(){
		changed = true;
		games ++;
	}
	
	public void addDeath(){
		changed = true;
		death ++;
	}
	
	public void addKills(){
		changed = true;
		kills ++;
	}
	
	public void addStack(){
		changed = true;
		if(stack == 0){
			stack = 1;
		}
		stack ++;
	}
	
	
	
	public int getGames(){
		return games;
	}
	public int getWins(){
		return wins;
	}
	public int getKills(){
		return kills;
	}
	public int getDeath(){
		return death;
	}
}
