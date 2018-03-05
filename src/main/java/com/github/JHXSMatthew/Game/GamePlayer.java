package com.github.JHXSMatthew.Game;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.JHXSMatthew.Core;
import com.github.JHXSMatthew.Config.Message;
import com.github.JHXSMatthew.Util.ItemFactory;

public class GamePlayer {

	private Player p = null;
	private GameTeam team = null;
	private GameStats gs = null;
	private int stack = 0;
	private boolean isSpectate = false;
	private List<String> requestList = null;
	
	public boolean notified = false;
	
	
	public GamePlayer(Player arg){
		p = arg;
		gs = new GameStats(p.getName());
		requestList = new ArrayList<String>();
		
	}
	
	public boolean isInRequestList(GamePlayer gp){
		return requestList.contains(gp.get().getName());
	}
	
	public void removeFromRequestList(GamePlayer gp){
		 requestList.remove(gp.get().getName());
	}
	public void addRequestList(GamePlayer gp){
		if(!requestList.contains(gp.get().getName())){
			 requestList.add(gp.get().getName());
		}
		
		 this.sendMessage(ChatColor.GRAY + " 您邀请玩家 " + gp.get().getName() + " 组队,请求已发送,他可以蹲下并右击您组队." );
		 gp.sendMessage(ChatColor.GRAY + " 玩家 " + p.getName() + " 邀请您组队. 您可以蹲下并右击 "+ p.getName() +" 组队." );
	}
	
	
	
	public int getStack(){
		return stack;
	}
	
	public void increaseStack(){
		stack ++;
	}
	
	public GameStats getGs(){
		return gs;
	}
	
	public Player get(){
		return p;
	}
	
	public GameTeam getTeam(){
		return team;
	}
	
	public void setTeam(GameTeam gt){
		team = gt;
	}
	
	public void showToAll(){
		for(Player p : Bukkit.getOnlinePlayers()){
			p.hidePlayer(this.p);
			p.showPlayer(this.p);
		}
	}
	
	public void setGameJoin(){
		p.setMaxHealth(40);
		p.setHealth(p.getMaxHealth());
		
		p.setLevel(0);
		p.setFoodLevel(20);
		p.setExp(0);
		p.setGameMode(GameMode.ADVENTURE);
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		for(PotionEffect pt : p.getActivePotionEffects()){
			p.removePotionEffect(pt.getType());
		}
		
		try{
			p.teleport(Core.lobby);
		}catch(Exception e){
			
		}
	
		p.getInventory().setItem(0, ItemFactory.create(Material.GOLD_AXE,(byte) 0, ChatColor.GREEN + "职业", ChatColor.GRAY + "尚未开放"));
		p.getInventory().setItem(1, ItemFactory.create(Material.BOOK,(byte) 0, ChatColor.GREEN + "帮助", ChatColor.GRAY + "右击查看"));
		p.getInventory().setItem(2, ItemFactory.create(Material.ARROW,(byte) 0, ChatColor.GREEN + "数据统计", ChatColor.GRAY + "右击查看数据统计"));

		
		p.getInventory().setItem(7, ItemFactory.create(Material.PAPER,(byte) 0, ChatColor.GREEN + "离开队伍", ChatColor.GRAY + "点击离开队伍"));

		p.getInventory().setItem(8, ItemFactory.create(Material.WATCH,(byte) 0, ChatColor.GREEN + "返回大厅", ChatColor.GRAY + "点击离开队伍"));

		
	
	}
	
	public void setGameBegin(){
		p.setMaxHealth(40);
		p.setLevel(0);
		p.setExp(0);
		p.setFoodLevel(20);
		p.setHealth(p.getMaxHealth());
		p.setGameMode(GameMode.SURVIVAL);
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		
		p.getInventory().setItem(8, ItemFactory.create(Material.COMPASS,(byte) 0, ChatColor.GREEN + "指友针", ChatColor.GRAY + "显示队友的位置"));
		p.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
		p.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
		p.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
		p.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));


		
		for(PotionEffect pt : p.getActivePotionEffects()){
			p.removePotionEffect(pt.getType());
		}
		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,300,5));

	}
	
	public void setGameQuit(){
		try{
			p.teleport(Core.lobby);
		}catch(Exception e){
			
		}
		p.setMaxHealth(40);
		p.setHealth(p.getMaxHealth());
		p.setGameMode(GameMode.SPECTATOR);
		
		if(team != null){
			if(Core.get().getCurrentGame().getGameStateString().contains("等待")){
				team.quitTeam(this);
			}else{
				team.DiePlayer(this);
			}
		}
	}
	
	public void setGameSpec(){
		p.setGameMode(GameMode.SPECTATOR);
		p.setMaxHealth(40);
		p.setHealth(p.getMaxHealth());
		p.setLevel(0);
		p.setExp(0);
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		
		p.getInventory().setItem(8, ItemFactory.create(Material.SLIME_BALL, (byte) 0, ChatColor.WHITE + "返回大厅", "点击返回大厅"));
		
		this.isSpectate = true;
		
		
		if(team == null){
			return;
		}
		
		team.DiePlayer(this);
		
		
	}
	
	
	public boolean isSpec(){
		return this.isSpectate;
	}
	

	public boolean isInTeam(){
		return team == null ? false : true;
	}
	
	public void sendActionBar(String msg){
		Core.getNms().sendActionBar(p, msg);
	}
	
	public void sendTitle(String msg){
		Core.getNms().sendTitle(p, 0,  60 , 20, " ", msg);
	}
	
	public void sendTitle(String title, String msg){
		Core.getNms().sendTitle(p, 0,  60 , 20, title, msg);
	}
	
	public void sendMessage(String chat){
		p.sendMessage(Message.prefix + chat);
	}
}
