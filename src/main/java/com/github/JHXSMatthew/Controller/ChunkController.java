package com.github.JHXSMatthew.Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.JHXSMatthew.Core;
import com.github.JHXSMatthew.Util.ChunkUtils;

public class ChunkController {

	private List<Location> validLocationList = Collections.synchronizedList(new ArrayList<Location>());
	private List<Chunk> keepLoadingChunks = Collections.synchronizedList(new ArrayList<Chunk>());
	
	public void preLoadLocations(){
		for(int i = 0; i < 33; i ++){
			validLocationList.add( Core.get().getMc().getRandomLocation((int)Core.boarderSize,0));
		}
	}
	
	public void startLoadingTask(){
		new BukkitRunnable(){
			int maxPerTask = 1;

			public void run() {
				int taskCount = 0;
				for(Location l : validLocationList){
					if(taskCount >= maxPerTask) break;
					
					if(!l.getChunk().isLoaded()){
						List<Chunk> temp = ChunkUtils.loadNearByChunks(l.getChunk(), 4);
						for(Chunk c : temp){
							if(keepLoadingChunks.contains(c)){
								continue;
							}
							keepLoadingChunks.add(c);
						}
						//static things, remove
						temp.clear();
						temp = null;
						
						System.out.print("Preload chunk at " + l.getChunk().getX() + "," + l.getChunk().getZ() );
						maxPerTask ++;
						
					}
				}
				if(taskCount == 0 || Core.get().getCurrentGame().getGameStateString().contains("游戏中")){
					System.out.print("PreLoadChunk task Cancel!");
					cancel();
				}
			}
			
		}.runTaskTimer(Core.get(), 0, 1);
	}
	
	public boolean shouldKeepLoad(Chunk c){
		return this.keepLoadingChunks.contains(c);
	}
	public void clearChunkCache(){
		keepLoadingChunks.clear();
	}
	
	public Location popALocation (){
		if(!validLocationList.isEmpty()){
			Location l = validLocationList.get(0);
			validLocationList.remove(0);
			return l;
		}else{
			return null;
		}
		
	}
	
}
