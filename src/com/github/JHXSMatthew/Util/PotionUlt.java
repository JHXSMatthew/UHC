package com.github.JHXSMatthew.Util;

import org.bukkit.potion.PotionEffectType;

public class PotionUlt {

	
	public static boolean isNegativePotion(PotionEffectType pe){
		if(pe.equals(PotionEffectType.SLOW) || pe.equals(PotionEffectType.BLINDNESS) || pe.equals(PotionEffectType.CONFUSION) || pe.equals(PotionEffectType.HARM) 
				|| pe.equals( PotionEffectType.HUNGER) ||  pe.equals(PotionEffectType.POISON) ||  pe.equals(PotionEffectType.SLOW_DIGGING) ||  pe.equals(PotionEffectType.WEAKNESS) || pe.equals( PotionEffectType.WITHER)){
			return true;
		}
		return false;
	}
	

}
