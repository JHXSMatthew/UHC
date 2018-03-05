package com.github.JHXSMatthew.Util;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class LocationUtil {

	
	public static Location getNearBySaftLocation(World w,int x, int z,int redis,boolean spawnWood){
		//long time = System.currentTimeMillis();
		int copy_x = x - redis;
		int copy_z = z - redis;
		Random r = new Random();
		int diff = r.nextInt(redis * 2);
		Block b = w.getHighestBlockAt(copy_x + diff,copy_z + diff);
		int count = 0;
		while(b == null ){
			count ++;
			diff = r.nextInt(redis * 2);
			b = w.getHighestBlockAt(copy_x + diff,copy_z + diff);
			if(count > 30){
				b = w.getBlockAt(x,85,z);
				break;
			}
		}
		
		Location to = b.getLocation();
		if(spawnWood){
			b.setType(Material.LOG);
		}
		to.setY(b.getLocation().getY() + 6);
	
		//System.out.print("Running GetNearLocation cost: " + ((System.currentTimeMillis() - time) / 1000));
		return to;
	}
	
	
}
