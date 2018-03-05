package com.github.JHXSMatthew.Controller;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class ChestControl {
	
	HashMap<Integer,ItemStack> loot = new HashMap<Integer,ItemStack>();
	
	
	
	
	
	int index;
	

	public ChestControl(){
		this.index = 0;
		ItemStack item = new ItemStack (Material.WOOD_AXE);
		ItemMeta meta = item.getItemMeta();
		
		
		meta.setDisplayName(ChatColor.AQUA + "火鸟");
		meta.addEnchant(Enchantment.FIRE_ASPECT, 1, false);
		addItem(Material.WOOD_SWORD,(short)0,1,meta);
		
		addItem(Material.EMPTY_MAP,(short)0,1,null);

		addItem(Material.EMPTY_MAP,(short)0,1,null);
		addItem(Material.EMPTY_MAP,(short)0,1,null);
		addItem(Material.EMPTY_MAP,(short)0,1,null);

		
		addItem(Material.DIAMOND,(short)0,2,null);
		
		addItem(Material.IRON_INGOT,(short)0,3,null);
		
		
		addItem(Material.PISTON_BASE,(short)0,2,null);

		addItem(Material.DIAMOND_HELMET,(short)0,1,null);
		
		addItem(Material.ENCHANTMENT_TABLE,(short)0,1,null);
		
		addItem(Material.BOOKSHELF,(short)0,1,null);

		addItem(Material.SLIME_BALL,(short)0,16,null);

		
		addItem(Material.EXP_BOTTLE,(short)0,16,null);
		
		addItem(Material.FISHING_ROD,(short)0,1,null);

		addItem(Material.GOLDEN_APPLE,(short)0,1,null);

		addItem(Material.ENDER_PEARL,(short)0,1,null);

		
		addItem(Material.ARROW,(short)0,32,null);
		
	
		addItem(Material.PORK,(short)0,4,null);
		
		addItem(Material.BUCKET,(short)0,1,null);
		
		addItem(Material.APPLE,(short)0,3,null);
		
		addItem(Material.APPLE,(short)0,3,null);
		
		addItem(Material.APPLE,(short)0,3,null);
		
		addItem(Material.APPLE,(short)0,3,null);
		
		addItem(Material.APPLE,(short)0,3,null);
		
		
		
		addItem(Material.BOW,(short)0,1,null);
		
		
		addItem(Material.POTATO,(short)0,1,null);
		
		
		
		addItem(Material.POTION,(short)16417,1,null);
		
		addItem(Material.TNT,(short)0,3,null);
		
		
		addItem(Material.POTION,(short)16459,1,null);
		
		addItem(Material.POTION,(short)16453,2,null);

		addItem(Material.POTION,(short)16421,1,null);

		addItem(Material.POTION,(short)8261,1,null);

		addItem(Material.POTION,(short)8229,1,null);

		addItem(Material.POTION,(short)8193,1,null);

		addItem(Material.POTION,(short)8194,1,null);
		
		addItem(Material.POTION,(short)16385,1,null);
		
		addItem(Material.POTION,(short)16426,1,null);

		addItem(Material.POTION,(short)8269,1,null);
		
		addItem(Material.POTION,(short)16422,1,null);
		
		addItem(Material.POTION,(short)8193,1,null);

		addItem(Material.POTION,(short)8193,1,null);

		
		addItem(Material.POTION,(short)16424,1,null);
		
		addItem(Material.POTION,(short)16428,1,null);
		addItem(Material.POTION,(short)16428,1,null);
		addItem(Material.POTION,(short)16428,1,null);


		addItem(Material.MILK_BUCKET,(short)0,1,null);
		
	
	}
	
	
	
	
	public void addItem(Material material, short durability ,int amount ,ItemMeta meta){
		ItemStack item = new ItemStack(material);
		item.setAmount(amount);
		if(durability != 0){
			item.setDurability(durability);
		}
		if(meta != null){
			item.setItemMeta(meta);
		}
		this.loot.put(this.index,item);
		this.index ++;
	}
	
	public ItemStack randomItem(){
		Random r = new Random();
		ItemStack i = this.loot.get(r.nextInt(this.loot.size()));
		return i;
		
	}
	
	
	public ItemStack[] generateLoot(int size){
		int k = 0;
		int levelC = 0;
		int i=0;
		
		 HashMap<Integer,ItemStack> returnValue = new  HashMap<Integer,ItemStack>();
		 ItemStack air = new ItemStack(Material.AIR);
		 for(int var = 0 ; var <= size; var ++){
			 returnValue.put(var, air);
		//	 System.out.print("put " + var +  " Material " + air.getTypeId());
		 }

		
		levelC = 8;  
		
		
		
		while(i < levelC){
			
			Random r = new Random();
			
		//	System.out.print("=================");
		//	System.out.print("size :" + size);
		//	System.out.print("k :" + k);
		//	System.out.print("i :" + i);
		//	System.out.print("item :" + returnValue.get(k).getTypeId());
		//	System.out.print("=================");
					
			
			if(k < size){
				if(k == r.nextInt(size)){
					if(returnValue.get(k).getType().equals(Material.AIR)){

						returnValue.replace(k, randomItem());
		//				System.out.print("-----------------");
		//				System.out.print("size :" + size);
		//				System.out.print("k :" + k);
		//				System.out.print("i :" + i);
		//				System.out.print("item :" + returnValue.get(k).getTypeId());
		//				System.out.print("-----------------");
						i++;
					}
					
				}
			}else{
				k=0;
			}
			k++;
		
		}
			
			
	    ItemStack[] s = new ItemStack[returnValue.size() - 1];
	    for(int count = 0 ; count < returnValue.size() -1 ; count ++){
	    	
	    	
	    	s[count] = returnValue.get(count);
	    	
	    //	System.out.print("++++++++++++++");
	    //	System.out.print("Item " + s[count].getTypeId());
	    //	System.out.print("++++++++++++++");
	    	
	    }
		return s;
	}
	
	

	
}


