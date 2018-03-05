package com.github.JHXSMatthew.Util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.github.JHXSMatthew.Core;



public class BlockUtils {
	
	
	   public static void playBoomEffect(final Location loc) {
	        new BukkitRunnable() {
	            int i = 1;

	            @Override
	            public void run() {
	                if (i == 5) {
	                    cancel();
	                }
	              
	                for (Block b : BlockUtils.getBlocksInRadius(loc.clone().add(0, -1, 0), i, true)) {
	                    if (b.getLocation().getBlockY() == loc.getBlockY() - 1) {
	                        if (b.getType() != Material.AIR
	                                && b.getType() != Material.SIGN_POST
	                                && b.getType() != Material.CHEST
	                                && b.getType() != Material.STONE_PLATE
	                                && b.getType() != Material.WOOD_PLATE
	                                && b.getType() != Material.WALL_SIGN
	                                && b.getType() != Material.WALL_BANNER
	                                && b.getType() != Material.STANDING_BANNER
	                                && b.getType() != Material.CROPS
	                                && b.getType() != Material.LONG_GRASS
	                                && b.getType() != Material.SAPLING
	                                && b.getType() != Material.DEAD_BUSH
	                                && b.getType() != Material.RED_ROSE
	                                && b.getType() != Material.RED_MUSHROOM
	                                && b.getType() != Material.BROWN_MUSHROOM
	                                && b.getType() != Material.TORCH
	                                && b.getType() != Material.LADDER
	                                && b.getType() != Material.VINE
	                                && b.getType() != Material.DOUBLE_PLANT
	                                && b.getType() != Material.PORTAL
	                                && b.getType() != Material.CACTUS
	                                && b.getType() != Material.WATER
	                                && b.getType() != Material.STATIONARY_WATER
	                                && b.getType() != Material.LAVA
	                                && b.getType() != Material.STATIONARY_LAVA
	                                
	                               
	                                && net.minecraft.server.v1_8_R3.Block.getById(b.getTypeId()).getMaterial().isSolid()
	                                && b.getType().getId() != 43
	                                && b.getType().getId() != 44
	                                && b.getRelative(BlockFace.UP).getType() == Material.AIR) {
	                            FallingBlock fb = loc.getWorld().spawnFallingBlock(b.getLocation().clone().add(0, 1.1f, 0), b.getType(), b.getData());
	                            fb.setVelocity(new Vector(0, 0.3f, 0));
	                            fb.setDropItem(false);
	                            fb.setMetadata("cancelBlock", new FixedMetadataValue(Core.get(),"yes"));
	                           
	                            for (Entity ent : fb.getNearbyEntities(1, 1, 1)) {
	                                if (ent.getType() != EntityType.FALLING_BLOCK)
	    
	                                        MathUtils.applyVelocity(ent, new Vector(0, 0.5, 0));
	                            }
	                        }
	                    }
	                }
	                i++;
	            }
	        }.runTaskTimer(Core.get(), 0, 1);
	    }
	   
	   
	   
	   
	    public static List<Block> getBlocksInRadius(Location location, int radius, boolean hollow) {
	        List<Block> blocks = new ArrayList<Block>();

	        int bX = location.getBlockX();
	        int bY = location.getBlockY();
	        int bZ = location.getBlockZ();

	        for (int x = bX - radius; x <= bX + radius; x++) {
	            for (int y = bY - radius; y <= bY + radius; y++) {
	                for (int z = bZ - radius; z <= bZ + radius; z++) {

	                    double distance = ((bX - x) * (bX - x) + (bY - y) * (bY - y) + (bZ - z) * (bZ - z));

	                    if (distance < radius * radius
	                            && !(hollow && distance < ((radius - 1) * (radius - 1)))) {
	                        Location l = new Location(location.getWorld(), x, y, z);
	                        if (l.getBlock().getType() != Material.BARRIER)
	                            blocks.add(l.getBlock());
	                    }
	                }
	            }
	        }
	        return blocks;
	    }

}
