package com.github.JHXSMatthew.Controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.JHXSMatthew.Core;
import com.github.JHXSMatthew.Game.Game.GameState;

public class MapController {

	
	private static String worldName = "uhcWorld";
	private boolean isGood = false;
	DecimalFormat df = new DecimalFormat("0.00");
	
	World w;

	private float count = 0;
	
	public MapController(){
		try{
			Core.get().getWc().deleteWorld(worldName);
		}catch(Exception e){
			
		}
		
		w = Bukkit.createWorld(new WorldCreator(worldName));	
	}
	
	

	
	public void begainGenerate(){
		
		new BukkitRunnable(){
			int currentX = -Core.chunkNum;
			int currentZ = -Core.chunkNum;
			List<Chunk> loaded = new ArrayList<Chunk>();
			public void run(){
				for(int i = 0 ; i < Core.chunkNum ; i ++){
					Chunk k = w.getChunkAt(currentX, currentZ);
					k.load(true);
					loaded.add(k);
					
					if(loaded.size() > 2000){
						for(Chunk temp : loaded){
							temp.unload();
						}
						loaded.clear();
					}
					//w.unloadChunkRequest(currentX, currentZ);
					//w.unloadChunkRequest(currentX, currentZ);
					
					currentX += 1;
					if(currentX >= Core.chunkNum){
						currentZ += 1;
						currentX = -Core.chunkNum;
						count ++;
					}
				}
				if(currentZ >= Core.chunkNum){
					for(Chunk temp : loaded){
						temp.unload();
					}
					loaded.clear();
					
					w.save();
					System.out.print("Generation Done!");
					isGood = true;

					System.gc();
					Core.get().getCurrentGame().changeState(GameState.Lobby);
					
					cancel();
				}
				
				Core.get().getCurrentGame().updateScoreboard(false);
			}
			
		}.runTaskTimer(Core.get(), 0, 20);
	}
	
	public Location getRandomLocation(int redis, int escape){
		Random r = new Random();
		Location l  = null;
		try{
			l =  new Location(w, r.nextInt((int)(redis - (float)redis * 0.1)) - (int)(redis - (float)redis * 0.1)/2,  65, r.nextInt((int)(redis - (float)redis * 0.1)) - (int)(redis - (float)redis * 0.1)/2);
		}catch(Exception e){
			l =  new Location(w, r.nextInt(redis) ,  65 ,  r.nextInt(redis));
		}
		l = w.getHighestBlockAt(l).getLocation();
		Location lBelow = l;
		lBelow.setY(l.getY() - 1);
		if(escape > 10){
			return l;
		}
		return (l.getBlock().isLiquid() || lBelow.getBlock().isLiquid()) ? getRandomLocation(redis,escape + 1) : l; 	
	}
	
	public String getMapName(){
		return worldName;
	}
	
	public World getWorld(){
		return w;
	}
	
	public boolean isGood(){
		return isGood;
	}
	
	
	
	
	public String getPercentage(){
		return df.format((count/(Core.chunkNum*2)) * 100) +"%";
		
	}
	
}
