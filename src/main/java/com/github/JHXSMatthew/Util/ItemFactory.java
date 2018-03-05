package com.github.JHXSMatthew.Util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class ItemFactory {
	
	
	  public static ItemStack create(Material material, byte data, String displayName, String... lore) {
	        ItemStack itemStack = new MaterialData(material, data).toItemStack(1);
	        ItemMeta itemMeta = itemStack.getItemMeta();
	        itemMeta.setDisplayName(displayName);
	        if (lore != null) {
	            List<String> finalLore = new ArrayList<>();
	            for (String s : lore)
	                if (s != null)
	                    finalLore.add(s.replace('&', ChatColor.COLOR_CHAR));
	            itemMeta.setLore(finalLore);
	        }
	        itemStack.setItemMeta(itemMeta);
	        return itemStack;
	    }

}
