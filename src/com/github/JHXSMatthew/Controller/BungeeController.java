package com.github.JHXSMatthew.Controller;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import com.github.JHXSMatthew.Core;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class BungeeController implements Listener {

	
	public void quitSend(Player p){
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(p.getName());
		  try{
		  Core.get().getPc().getGamePlayer(p).getGs().show();
		  }catch(Exception e){};
		  p.sendPluginMessage(Core.get(), "LobbyConnect", out.toByteArray());
	}
	
	@EventHandler
	public void motdChanger(ServerListPingEvent evt){
		if(Core.get().getCurrentGame() == null){
			evt.setMotd(ChatColor.RED + "loading");
			return;
		}
		
		evt.setMotd(Core.get().getCurrentGame().getGameStateString());
		
	}
}
