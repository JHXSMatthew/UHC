package com.github.JHXSMatthew.Listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.JHXSMatthew.Core;
import com.github.JHXSMatthew.Config.Config;
import com.github.JHXSMatthew.Config.Message;
import com.github.JHXSMatthew.Game.Game;
import com.github.JHXSMatthew.Game.GamePlayer;
import com.github.JHXSMatthew.Game.Game.GameState;
import com.github.JHXSMatthew.Game.GameTeam;
import com.github.JHXSMatthew.Util.PotionUlt;

public class PlayerListener implements Listener{
	private ArrayList<String> noSpam;
	
	public PlayerListener(){
		noSpam = new ArrayList<String>();
			
		new BukkitRunnable(){
			@Override
			public void run() {
				noSpam.clear();
			}
			
		}.runTaskTimerAsynchronously(Core.get(), 20, 100);
	}
	
	
	@EventHandler
	public void onPreJoin(PlayerLoginEvent evt){
		if(Core.get().getCurrentGame().getGameState() == GameState.Teleporting){
			evt.disallow(PlayerLoginEvent.Result.KICK_FULL, "游戏开始中,无法加入 .");
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent evt){
		Player player = evt.getPlayer(); // The player who joined
	  	GamePlayer gp =  Core.get().getPc().createGamePlayer(player);
	  	gp.showToAll();
	  	if(Core.get().getCurrentGame() == null){
	  		player.kickPlayer("Not-Ready");
	  	}
	    Core.get().getCurrentGame().GameJoin(gp);
	    evt.setJoinMessage("");
	}

	@EventHandler
	public void onHeldItem (PlayerItemHeldEvent event){
		ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());
		if(item.getType() == Material.EMPTY_MAP){
			item.setDurability((short) (item.getDurability() + 5));
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent evt){
		Player p = evt.getEntity();
		GamePlayer gp = Core.get().getPc().getGamePlayer(p);
		Game g = Core.get().getCurrentGame();
		evt.setDeathMessage("");
		if(g == null){
			System.out.print("BUG ON DEATH");
		}
		
		
		p.setHealth(p.getMaxHealth());
		p.getWorld().strikeLightningEffect(p.getLocation());
		if(p.getKiller() instanceof Player){
			GamePlayer GKiller = Core.get().getPc().getGamePlayer(p.getKiller());
			GKiller.increaseStack();
			if(GKiller.getStack() > 1){
				g.sendToAllMessage(p.getKiller().getDisplayName() + Core.getMsg().getMessage("player-kill-stack1")  + GKiller.getStack() + Core.getMsg().getMessage("player-kill-stack2"));
				GKiller.get().addPotionEffect(new PotionEffect(PotionEffectType.SPEED,40,2));
				GKiller.sendMessage(Core.getMsg().getMessage("player-kill-buff1"));
				double healthReady = GKiller.get().getHealth() + (GKiller.getStack() %4);
				if(healthReady <= GKiller.get().getMaxHealth()){
					GKiller.get().setHealth(healthReady);
				}else{
					GKiller.get().setHealth(GKiller.get().getMaxHealth());
					GKiller.sendMessage(Core.getMsg().getMessage("player-kill-max"));
				}
				GKiller.sendMessage(Core.getMsg().getMessage("player-kill-health1")+ (GKiller.getStack() %4) + Core.getMsg().getMessage("player-kill-health2"));
				if(GKiller.getStack() % 5 == 0 ){
					if(GKiller.getStack() == 5){
						g.sendToAllSound(Sound.WITHER_SPAWN);
					}else{
						g.sendToAllSound(Sound.WITHER_DEATH);
					}
				}
				g.sendToAllSound(Sound.WOLF_PANT);
				
				if(GKiller.getStack() > GKiller.getGs().getStack() ){
					GKiller.getGs().addStack();
				}
			}
			
			GKiller.getGs().addKills();
			
			
			g.sendToAllMessage(p.getKiller().getDisplayName() + Core.getMsg().getMessage("player-death-msg1") + p.getDisplayName()  + Core.getMsg().getMessage("player-death-msg2"));
			gp.sendTitle(Core.getMsg().getMessage("player-death-title"));
			gp.getGs().addDeath();
			
		}else {
			g.sendToAllMessage( p.getDisplayName() + Core.getMsg().getMessage("player-death-msg3")  );
		}
		
		
		g.joinSpec(gp);
		
	}
	

	
	@EventHandler
	public void onHealthControl(EntityRegainHealthEvent evt){
		if(evt.getRegainReason() == RegainReason.MAGIC || evt.getRegainReason() == RegainReason.MAGIC_REGEN ){
			return;
		}
		evt.setCancelled(true);
	}
	
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent evt){
	    Player player = evt.getPlayer(); // The player who joined
	    GamePlayer p =  Core.get().getPc().getGamePlayer(player);
	    Game g = Core.get().getCurrentGame();
	    if(g == null){
	    	return;
	    }
	    p.getGs().save();
	    g.GameQuit(p);
	    Core.get().getPc().removeGamePlayer(p.get().getName());
	    
	    
	    if(g.getGameStateString().contains("游戏")  && Bukkit.getOnlinePlayers().size() == 0){
	    	Bukkit.shutdown();
	    }
	    try{
		    if(g.getGameStateString().contains("游戏") && g.getGameState()!=GameState.Finish){
		    	if(evt.getPlayer().getGameMode() != GameMode.SPECTATOR 
		    			&& !evt.getPlayer().getWorld().getName().contains("lobby")){
			    	
					    for(ItemStack item : evt.getPlayer().getInventory().getContents()){
					    	if(item == null)
					    		continue;
					    	if(item.getType() == Material.AIR)
					    		continue;
					    	evt.getPlayer().getWorld().dropItemNaturally(evt.getPlayer().getLocation(), item);
					    }
					    for(ItemStack item: evt.getPlayer().getInventory().getArmorContents()){
					    	if(item == null)
					    		continue;
					    	if(item.getType() == Material.AIR)
					    		continue;
					    	evt.getPlayer().getWorld().dropItemNaturally(evt.getPlayer().getLocation(), item);
					    }
				    }
		    	}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    evt.getPlayer().getInventory().clear();
	    evt.getPlayer().getInventory().setArmorContents(null);
	    evt.setQuitMessage("");
	}
	
	
	@EventHandler
	public void handleFoodLevel(FoodLevelChangeEvent evt){
		Game g = Core.get().getCurrentGame();
		if(g.getGameStateString().contains("游戏") && g.getGameState() != GameState.Nopvp){
			return;
		}
		evt.setCancelled(true);
	}
	
	@EventHandler
	public void handleItemDrop(PlayerDropItemEvent evt){
		if(!Core.get().getCurrentGame().getGameStateString().contains("游戏") ||  Core.get().getCurrentGame().getGameState() == GameState.Teleporting){
			evt.setCancelled(true);
		}
	}
	
	//feature
	@EventHandler
	public void chat(AsyncPlayerChatEvent evt){
		evt.setCancelled(true);
		boolean isAll = false;
		if(Core.get().getCurrentGame().getGameState() == GameState.Teleporting){
			evt.getPlayer().sendMessage(ChatColor.YELLOW + "YourCraft >> 传送中……请您耐心等待.禁言将在传送完毕后解除!");

			return;
		}
		
		if(noSpam.contains(evt.getPlayer().getName())){
			evt.getPlayer().sendMessage(ChatColor.YELLOW + "YourCraft >> 请减慢您的语速!");
			return;
		}

		if(evt.getMessage().startsWith("!") || evt.getMessage().startsWith("！") ){
			evt.setMessage(evt.getMessage().substring(1));
			isAll = true;
		}

	
		String pp = Core.chat.getPlayerPrefix(evt.getPlayer()).replace("&", "§");
		if(pp== null || (!pp.contains("VIP+") && !pp.contains("MVP"))){
			noSpam.add(evt.getPlayer().getName());
		}
		String realMsg  = null;
	
		GamePlayer p = Core.get().getPc().getGamePlayer(evt.getPlayer());
		GameTeam gt = p.getTeam();
		Game g = Core.get().getCurrentGame();
		
		if(p.isSpec()){
			realMsg=   "<观察者>"+pp + evt.getPlayer().getDisplayName() + ChatColor.GOLD + " >> " +ChatColor.GRAY + evt.getMessage();
			if(realMsg.contains("有钻石") || realMsg.contains("这里钻石") || realMsg.contains("坐标")){
				return;
			}
		}else{
			realMsg=   pp + evt.getPlayer().getDisplayName() + ChatColor.GOLD + " >> " +ChatColor.GRAY + evt.getMessage();
		}
		
		
		
		try{
			if(!p.notified && !p.isSpec() && Core.get().getCurrentGame().getGameStateString().contains("游戏")){
				p.get().sendMessage(Message.prefix + Core.getMsg().getMessage("notify-chat-format1"));
				p.get().sendMessage(Message.prefix + Core.getMsg().getMessage("notify-chat-format2"));
				p.notified = true;
			}
		}catch (Exception e){
			
		}
	
		
		
		if(isAll || (!Core.get().getCurrentGame().getGameStateString().contains("游戏") ||  Core.get().getCurrentGame().getGameState() == GameState.Teleporting) || p.isSpec() || gt == null){
			if(p.isSpec() && Core.get().getCurrentGame().getGameState() != GameState.Finish){
				Iterator<Player> iter = evt.getRecipients().iterator();
				while(iter.hasNext()){
					Player target = iter.next();
					if(target.getGameMode() != GameMode.SPECTATOR){
						continue;
					}
					target.sendMessage(realMsg);
				}
				return;
			}
			Core.get().getCurrentGame().sendAllChatMessage(realMsg);
		}else{
			gt.sendTeamMessage(realMsg);
		}
		
	}
	
	@EventHandler
	public void onItemClick(PlayerInteractEvent evt){
		if(Core.get().getCurrentGame().getGameStateString().contains("游戏") ){
			return;
		}
		
		if(evt.getAction() == Action.PHYSICAL){
			return;
		}
		
		ItemStack item = evt.getPlayer().getItemInHand();
		if(item == null){
			return;
		}
		
		if(!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()){
			return;
		}
		evt.setCancelled(true);
		
		String theName = item.getItemMeta().getDisplayName();
		
		evt.getPlayer().playSound(evt.getPlayer().getLocation(), Sound.ITEM_PICKUP, 1, 1);
		if(theName.contains("返回大厅")){	
			Core.get().getBc().quitSend(evt.getPlayer());
			
		}else if(theName.contains("离开队伍")){
			GamePlayer gp = Core.get().getPc().getGamePlayer(evt.getPlayer());
			if(gp == null){
				return;
			}
			if(gp.getTeam() == null){
				gp.sendMessage("您还没有队伍呢,蹲下加右键别的玩家或输入 /zu 玩家姓名 来与其他玩家组队.");
				return;
			}
			gp.getTeam().quitTeam(gp);
			
		}else if(theName.contains("职业")){
			evt.getPlayer().sendMessage(ChatColor.YELLOW +"YourCraft >> 尚未开放，敬请期待！");
			return;
		}else if(theName.contains("帮助")){
			Player p = evt.getPlayer();
			p.sendMessage("§6=======================================================");
			p.sendMessage("      §e§lUHC§b是一款以绝境生存PVP为主的小游戏");
			p.sendMessage("      §b你可与另外2名玩家组队,在极度艰苦的环境下共同生存下去,猎杀敌人.");
			p.sendMessage("      §b每一场游戏地图均是随机生成的,范围是2000*2000.");
			p.sendMessage("      §b在10分钟PVP保护时间结束后,边境将会向世界中心收缩,饥饿度也会开始消耗.");
			p.sendMessage("      §b另外,在极限模式下你不会恢复生命值,受到怪物伤害也会翻倍.");
			p.sendMessage("      §b连续击杀玩家将会恢复生命值,并获得加速BUFF. ");
			p.sendMessage("      §e§l祝你好运，愿君加冕!");
			p.sendMessage("      §7§lTIP: 你可以输入/zu 玩家名称 或 按住蹲下加右键玩家组队.");
			p.sendMessage("§6=======================================================");	
			return;
		}else if(theName.contains("数据统计")){
			Player p = evt.getPlayer();
			GamePlayer gp = Core.get().getPc().getGamePlayer(p);
			try{
				gp.getGs().show();
			}catch(Exception e){
				
			}
		}
		
		
	}
	
	@EventHandler
	public void onInventoryMove(InventoryClickEvent evt){
		if(!Core.get().getCurrentGame().getGameStateString().contains("游戏") ){
			evt.setCancelled(true);
			return;
		}
	
		
		Player p = (Player) evt.getWhoClicked();
		GamePlayer gp = Core.get().getPc().getGamePlayer(p);
		if(gp.isSpec()){
			if(evt.getCurrentItem() != null){
				if(evt.getCurrentItem().hasItemMeta() && evt.getCurrentItem().getItemMeta().hasDisplayName()){
					String name = evt.getCurrentItem().getItemMeta().getDisplayName();
					if(name.contains("返回大厅")){
						Core.get().getBc().quitSend(p);
					}
				}
			}
			
			evt.setCancelled(true);
			return;
		}
	
	
	}
	

	
	
	
	
	@EventHandler
	public void onWeather(WeatherChangeEvent evt){
		if(evt.getWorld().getName().equals("lobby")){
			
			evt.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onParty(PlayerInteractEntityEvent evt){
		if(Core.get().getCurrentGame().getGameStateString().contains("游戏") ){
			return;
		}
		
		if(!(evt.getRightClicked() instanceof Player) ){
			return;
		}
		Player p1 = (Player) evt.getRightClicked();
		Player p2 = evt.getPlayer();
		
		if(!p2.isSneaking()){
			return;
		}
		
		Core.get().getCurrentGame().teamPlayer(Core.get().getPc().getGamePlayer(p2),Core.get().getPc().getGamePlayer(p1));
		
	}
	
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onControlDamage(EntityDamageEvent evt){
		Entity e = evt.getEntity();
		if(!(e instanceof Player)){
			return;
		}
		Player p = (Player) e ;
		GamePlayer gp = Core.get().getPc().getGamePlayer(p);
		Game g = Core.get().getCurrentGame();
		if(g == null){
			evt.setCancelled(true);
			return;
		}
		if(g.getGameState() == GameState.Finish || g.getGameState() == GameState.Teleporting ){
			evt.setCancelled(true);
			return;
		}
		
	
		
		if(!Core.get().getCurrentGame().getGameStateString().contains("游戏") ){
			evt.setCancelled(true);
			if(evt.getCause() == DamageCause.VOID){
				gp.get().teleport(Core.lobby);
			}
			return;
		}
		
		if(Core.get().getCurrentGame().getGameState() == GameState.Nopvp  && Core.get().getCurrentGame().publicCount > 580  ){
			evt.setCancelled(true);
			return;
		}
		
		if(gp.isSpec()){
			if(evt.getCause() == DamageCause.VOID){
				g.teleportFirst(gp);
			}
			
			evt.setCancelled(true);
			gp.get().setGameMode(GameMode.SPECTATOR);
			return;
		}
		
		if(evt.getCause() == DamageCause.SUFFOCATION){
			if(p.getRemainingAir() != 0){
				Location current  = p.getLocation();
				if(Math.abs(current.getBlockX()) >= ((Core.get().getCurrentGame().getBorderSize()/2)+3) || Math.abs(current.getBlockZ()) >= ((Core.get().getCurrentGame().getBorderSize()/2)+3)){
					gp.sendMessage(ChatColor.YELLOW + " 赶快回到边境内去，你正在被边境侵蚀！");
					return;
				}
				
			}
		}
	
		
		if(!(evt instanceof EntityDamageByEntityEvent)){
			return;
		}
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent)evt;
		Entity damager = event.getDamager();
		if(damager instanceof Player){
			Player damager_Player =  (Player) damager;
			GamePlayer damager_GamePlayer = Core.get().getPc().getGamePlayer(damager_Player);
			if((gp.getTeam().isInTeam(damager_GamePlayer) && damager_GamePlayer.getTeam().isInTeam(gp) ) || g.getGameState().equals(GameState.Nopvp)) event.setCancelled(true);
			return;
		}
		
		if(damager instanceof Arrow){
			 ProjectileSource shooter =  ((Arrow)damager).getShooter();
			if(!(shooter instanceof Player)){
				return;
			}
			Player damager_Player =(Player) shooter;
			GamePlayer damager_GamePlayer = Core.get().getPc().getGamePlayer(damager_Player);
			if((gp.getTeam().isInTeam(damager_GamePlayer) && damager_GamePlayer.getTeam().isInTeam(gp)) || g.getGameState().equals(GameState.Nopvp)) event.setCancelled(true);
			return;
		}
		
	}
	
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onControlPostionSplash(PotionSplashEvent evt){
		ProjectileSource shooter = evt.getEntity().getShooter();
		if(!(shooter instanceof Player)){
			return;
		}
		System.out.print("event triggerd onControlPostionSplash");
		
		Player shooter_Player = (Player)shooter;
		GamePlayer shooter_GamePlayer = Core.get().getPc().getGamePlayer(shooter_Player);
		
		Collection<PotionEffect> ef = evt.getPotion().getEffects();
		Collection<LivingEntity> entity = evt.getAffectedEntities();
		for(LivingEntity e : entity){
			if(!(e instanceof Player)){
				continue;
			}
			
			Player p = (Player) e;
			GamePlayer gp = Core.get().getPc().getGamePlayer(p);
			//System.out.print("FOR " + p.getName());
			for(PotionEffect f : ef){
				//System.out.print("FOR Effect" + f.getType().getName() );
				if(PotionUlt.isNegativePotion(f.getType())){
					//System.out.print("Negative Effect");
					if(gp.getTeam().isInTeam(shooter_GamePlayer)) {
						evt.setIntensity(e, 0);
						//System.out.print("No harm onControlPostionSplash");
					}
				}else{
					//System.out.print("Positive Effect");
					if(!gp.getTeam().isInTeam(shooter_GamePlayer)) {
						evt.setIntensity(e, 0);
						//System.out.print("No good onControlPostionSplash");
					}
				}
			}
			
		}
	}
	
	
	
}
