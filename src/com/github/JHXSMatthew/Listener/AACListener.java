package com.github.JHXSMatthew.Listener;

import me.konsolas.aac.api.PlayerViolationCommandEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.github.JHXSMatthew.Core;
import com.github.JHXSMatthew.Game.Game.GameState;

public class AACListener implements Listener {
	
	  @EventHandler
	    public void onPlayerViolationCommand(PlayerViolationCommandEvent e) {
		  
		  		if(Core.get().getCurrentGame().getGameState() == GameState.Teleporting ||
		  				(Core.get().getCurrentGame().getGameState() == GameState.Nopvp && Core.get().getCurrentGame().publicCount > 570)){
		  		
		  			e.setCancelled(true);
		  		}
	    }
}
