package com.github.JHXSMatthew.Controller;

import java.util.HashMap;
import org.bukkit.entity.Player;

import com.github.JHXSMatthew.Game.GamePlayer;

public class PlayerController {
	private HashMap<String,GamePlayer> map = null;
	
	public PlayerController(){
		map = new HashMap<String,GamePlayer>();
	}
	
	
	public GamePlayer createGamePlayer(Player p){
		if(map.containsKey(p.getName())){
			return map.get(p.getName());
		}
		GamePlayer gp = new GamePlayer(p);
		map.put(p.getName(), gp);
		return gp;
	}
	
	public void removeGamePlayer(String name){
		 map.remove(name);
	}
	
	public GamePlayer getGamePlayer(Player p){
		GamePlayer gp = map.get(p.getName());
		if(gp == null){
			return createGamePlayer(p);
		}
		return gp;
	}
}
