package com.github.JHXSMatthew.Listener;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;


import com.github.JHXSMatthew.Core;
import com.github.JHXSMatthew.Game.Game.GameState;
import com.github.JHXSMatthew.Util.BlockUtils;

public class BlockListener implements Listener{

	@EventHandler
	public void onBreak(BlockBreakEvent evt){
		
		if(Core.get().getCurrentGame().getGameStateString().contains("游戏") && Core.get().getCurrentGame().getGameState() != GameState.Teleporting){
			if(evt.getBlock().getState() instanceof Chest){
				if(Core.get().getCurrentGame().isChestLocation(evt.getBlock().getLocation())) Core.get().getCurrentGame().removeChestLocation(evt.getBlock().getLocation());;
			}
			return;
		}
		
		
		evt.setCancelled(true);
	   
	}
	
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent evt){
		if(evt.isCancelled()){
			return;
		}
		if(Core.get().getChunk().shouldKeepLoad(evt.getChunk())){
			evt.setCancelled(true);
		}
	}
	
	@EventHandler
	public void OnPlace(BlockPlaceEvent evt){
		if(Core.get().getCurrentGame().getGameStateString().contains("游戏") && Core.get().getCurrentGame().getGameState() != GameState.Teleporting){
	
			
			return;
		}
		
		evt.setCancelled(true);
	   
	}
	
	@EventHandler
    public void onBlockFall(EntityChangeBlockEvent evt) {
        if ((evt.getEntityType() == EntityType.FALLING_BLOCK)) {
        	
           	if(evt.getEntity().hasMetadata("CHEST_FALL")){
           		new BukkitRunnable(){
           			public void run(){
           				BlockUtils.playBoomEffect(evt.getBlock().getLocation());
           				evt.getBlock().setType(Material.CHEST);
						Chest c = (Chest) evt.getBlock().getState();
						c.getInventory().setContents(Core.get().getCc().generateLoot(26));
						Core.get().getCurrentGame().sendToAllSound(Sound.ANVIL_LAND);
						c.getLocation().getWorld().playEffect(c.getLocation(), Effect.EXPLOSION_HUGE, 1);
						Core.get().getCurrentGame().addChestLocation(evt.getBlock().getLocation());
           			}
           		}.runTaskLater(Core.get(), 1);
           	}else if(evt.getEntity().hasMetadata("TNT_PRIMED")){
           		new BukkitRunnable(){
           			public void run(){
           				BlockUtils.playBoomEffect(evt.getBlock().getLocation());
           				evt.getBlock().setType(Material.AIR);
           				TNTPrimed tnt = (TNTPrimed) evt.getBlock().getLocation().getWorld().spawn(evt.getBlock().getLocation(), TNTPrimed.class);
           				tnt.setIsIncendiary(true);
           				
           			}
           		}.runTaskLater(Core.get(), 1);
           	}else if(evt.getEntity().hasMetadata("HEAVY_SAND")){
           		
               	
           		Location l = evt.getEntity().getLocation();
           		Location lBelow = l.clone();
           		lBelow.setY(lBelow.getY() - 1);		
           		if(lBelow.getBlock().getType() != Material.AIR){
           			lBelow.getBlock().setType(Material.AIR);
           			FallingBlock fb =l.getWorld().spawnFallingBlock(lBelow, evt.getTo(), (byte) evt.getData());
        			fb.setMetadata("HEAVY_SAND", new FixedMetadataValue(Core.get(),"123"));
        			fb.setCustomName(ChatColor.RED + ChatColor.BOLD.toString() + "坍塌方块");
        			fb.setCustomNameVisible(true);
           			evt.setCancelled(true);
           		}
        	}else if(evt.getEntity().hasMetadata("cancelBlock")){
        		 evt.setCancelled(true);
                 FallingBlock fb = (FallingBlock) evt.getEntity();
                 fb.getWorld().playSound(fb.getLocation(), Sound.STEP_STONE, 1, 1);
                 evt.getEntity().remove();
        	}
        }

    }
	
}
