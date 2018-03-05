package com.github.JHXSMatthew.Game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import com.github.JHXSMatthew.Core;
import com.github.JHXSMatthew.Util.ChunkUtils;
import com.github.JHXSMatthew.Util.LocationUtil;

public class GameTeam {
	private List<GamePlayer> players = new ArrayList<GamePlayer>();
	
	public boolean isTeleported = false;
	public boolean isSpec = false;
	
	
	private Location spawn = null;
	
	
	
	
	public boolean JoinTeam(GamePlayer gp, boolean notify){
		if(players.size() >= 3 ){
			if(notify){
				gp.sendMessage(ChatColor.RED + " 您尝试加入的队伍已满.");
			}
			return false;
		}
		if(gp.isInTeam()){
			gp.getTeam().quitTeam(gp);
		}
		
		players.add(gp);
		for(GamePlayer temp : players){
			if(gp != temp){
				temp.sendMessage(ChatColor.YELLOW  + " 玩家 " + gp.get().getName() + " 加入了您的队伍 " + ChatColor.GREEN + "  (" +players.size() + "/" + "3)");
			}else{
				temp.sendMessage(ChatColor.YELLOW  + " 您加入了一个队伍...");

			}
		}
	
		gp.setTeam(this);
		return true;
		
		
	}
	
	public GamePlayer getFirstButNot(GamePlayer gp){
		GamePlayer temp = null;
		if(gp == null){
			try{
				 temp = players.get(0);
			}catch(Exception e){
				
			}
			return temp;
		}else{
			temp = gp;
			for(GamePlayer tem : players){
				if(tem != gp){
					temp = tem;
					break;
				}
			}
			return temp;
		}
	}
	
	
	public void loadSpawn(){
		if(spawn == null){
			//spawn = Core.get().getMc().getRandomLocation((int)Core.boarderSize,0);
			spawn = Core.get().getChunk().popALocation();
			if(spawn == null){
				System.out.print("EXCEPTION WHEN POP, NEW NULL ");
				spawn = Core.get().getMc().getRandomLocation((int)Core.boarderSize,0);
				ChunkUtils.loadNearByChunks(spawn.getChunk(), 4);
			}
		}
	}
	
	
	
	public void setTeamTeleporting(){
		
		if(spawn == null){
			loadSpawn();
			System.out.print("ONE TEAM : spawn is null");
		}
		
		if(!spawn.getChunk().isLoaded()){
			spawn.getChunk().load(false);
			System.out.print("ONE TEAM : spawnchunk NOT loaded");

		}
		
		for(GamePlayer gp : players){
			gp.get().teleport(LocationUtil.getNearBySaftLocation(spawn.getWorld(), spawn.getBlockX(), spawn.getBlockZ(), 7,true));
			gp.get().setVelocity(new Vector(0, 0, 0));
			gp.get().setFallDistance(0);
			gp.get().getInventory().clear();
		}
		
		
		isTeleported = true;
		
	}
	
	public void sendTeamMessage(String arg){
		for(GamePlayer gp : players){
			gp.get().sendMessage(ChatColor.GREEN + "[队伍] " + ChatColor.RESET + arg);
		}
		Bukkit.getLogger().info("TC : " + arg);
	}
	
	
	public void quitTeam(GamePlayer gp){
		
	
		
		players.remove(gp);
		gp.setTeam(null);
		
		for(GamePlayer temp : players){
			if(gp != temp){
				temp.sendMessage(ChatColor.YELLOW  + " 玩家 " + gp.get().getName() + " 退出了您的队伍 " + ChatColor.GREEN + "  (" +players.size() + "/" + "3)");
			}else{
				temp.sendMessage(ChatColor.YELLOW  + " 您退出了一个队伍...");

			}
		}
		
		if(players.size() == 0){
			disband();
			return;
		}
		
		
	}
	
	
	public void DiePlayer(GamePlayer gp){
		players.remove(gp);
		gp.setTeam(null);
		for(GamePlayer temp : players){
			temp.get().playSound(temp.get().getLocation(), Sound.VILLAGER_NO, 1, 1);
			temp.sendTitle(ChatColor.RED + "队友 " + ChatColor.GREEN + gp.get().getName() + ChatColor.RED  + " 已阵亡");
		}
		CheckDead();
	}
	
	public void setAllScoreBoard(Scoreboard sb){
		for(GamePlayer gp : players){
			gp.get().setScoreboard(sb);
		}
	}
	
	public void CheckDead(){
		if(players.size() != 0){
			return;
		}
		
		disband();
	}
	
	public void disband(){
		Core.get().getCurrentGame().disbandTeam(this);
	}
	
	public boolean isInTeam(GamePlayer gp){
		return players.contains(gp);
	}

}
