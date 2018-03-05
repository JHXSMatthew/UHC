package com.github.JHXSMatthew.Util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;

public class ChunkUtils {

	
	public static List<Chunk> loadNearByChunks(Chunk k, int redius){
		if(redius % 2 !=0 ) redius ++;
		List<Chunk> list = new ArrayList<Chunk>();
		list.add(k);
		for(int i = -redius ; i < redius+1 ; i ++){
			for(int j = -redius; j < redius+1  ; j ++){
				Chunk temp = k.getWorld().getChunkAt(k.getX() * i * 16, k.getZ() * j * 16);
				k.getWorld().loadChunk(temp);
				list.add(temp);
			}
		}
		return list;
	}
}
