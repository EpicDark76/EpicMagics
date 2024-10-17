package eu.epicdark.epicmagics.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.entity.Item;

public class CauldronData {
	private static final HashMap<Block, ArrayList<Item>> ITEMS = new HashMap<>();
	
	private final Block block;
	private final Block blockBelow;
	
	private final boolean hasCampfire;
	private final boolean hasSoulCampfire;
	private final boolean isLit; //this won't check in recipe process if blockBelow is campfire, only if a campfire is lit if there is any
	
	private final boolean hasWater;
	private final boolean hasLava;
	
	private int level;
	
	public CauldronData(Block block) {
		if(!block.getType().name().contains("CAULDRON")) {
			throw new IllegalArgumentException("Cannot initialize CauldronData of block cause block is no cauldron");
		}
		this.block = block;
		
		blockBelow = block.getLocation().subtract(0, 1, 0).getBlock();
		this.hasCampfire = blockBelow.getType().equals(Material.CAMPFIRE);
        this.hasSoulCampfire = !hasCampfire && blockBelow.getType().equals(Material.SOUL_CAMPFIRE);
        
        if (hasCampfire || hasSoulCampfire) {
            Campfire campfire = (Campfire) blockBelow.getBlockData();
            this.isLit = campfire.isLit();
        } else {
            this.isLit = false;
        }
        
        if(block.getBlockData() instanceof Levelled) {
			Levelled levelled = (Levelled) block.getBlockData();
			this.level = levelled.getLevel();
		}else {
			this.level = 0;
		}
        
        if(block.getType().equals(Material.LAVA_CAULDRON)) {
        	hasLava = true;
        	hasWater = false;
        	this.level = 3;
        }else if(block.getType() == Material.WATER_CAULDRON) {
        	hasWater = true;
        	hasLava = false;
        }else {
        	hasWater = false;
        	hasLava = false;
        }
        
	}
	
	public Block getBlock() {
		return block;
	}

	public boolean hasCampfire() {
		return hasCampfire;
	}

	public boolean hasSoulCampfire() {
		return hasSoulCampfire;
	}

	public boolean isLit() {
		return isLit;
	}

	public boolean hasWater() {
		return hasWater;
	}

	public boolean hasLava() {
		return hasLava;
	}

	public int getLevel() {
		return level;
	}
	
	public boolean isEmpty() {
		return !hasLava && !hasWater && level == 0;
	}
	
	public Block getBlockBelow() {
		return blockBelow;
	}
	
	
	
	
	public static HashMap<Block, ArrayList<Item>> getItemMap() {
		return ITEMS;
	}
	
	public static ArrayList<Item> getContainedItems(Block block) {
		if(!ITEMS.containsKey(block)) {
			return new ArrayList<Item>();
		}
		return ITEMS.get(block);
	}

}
