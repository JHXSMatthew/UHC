package com.github.JHXSMatthew.Util;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.github.JHXSMatthew.Core;
import com.github.JHXSMatthew.Game.GamePlayer;

public class EntityUtil {
	 
    public static Player getTargetPlayer(final Player player) {
        return getTarget(player, player.getWorld().getPlayers());
    }
 
    public static org.bukkit.entity.Entity getTargetEntity(final org.bukkit.entity.Entity entity) {
        return getTarget(entity, entity.getWorld().getEntities());
    }
 
    public static <T extends org.bukkit.entity.Entity> T getTarget(final org.bukkit.entity.Entity entity, final Iterable<T> entities) {
        if (entity == null)
            return null;
        T target = null;
        final double threshold = 1;
        for (final T other : entities) {
            final Vector n = other.getLocation().toVector().subtract(entity.getLocation().toVector());
            if (entity.getLocation().getDirection().normalize().crossProduct(n).lengthSquared() < threshold && n.normalize().dot(entity.getLocation().getDirection().normalize()) >= 0) {
                if (target == null || target.getLocation().distanceSquared(entity.getLocation()) > other.getLocation().distanceSquared(entity.getLocation()))
                    if(target instanceof Player){
                    	if(((Player)target ).getGameMode() == GameMode.SPECTATOR) continue;
                    }
                	target = other;
            }
        }
        return target;
    }
    
    public static GamePlayer getNearBySurvivalPlayer(GamePlayer gp){
    	for(Entity e :gp.get().getNearbyEntities(15, 15, 15)){
    		if(e instanceof Player){
    			GamePlayer temp = Core.get().getPc().getGamePlayer((Player)e);
    			if(temp.isSpec()){
    				continue;
    			}
    			return temp;
    		}
    	}
		return null;
    }
}
