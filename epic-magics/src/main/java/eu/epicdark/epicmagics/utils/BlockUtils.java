package eu.epicdark.epicmagics.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class BlockUtils {
	
	public static Set<Block> surroundingBlocks(Location location, int radius, boolean sphere) {
		return surroundingBlocks(location, radius, sphere, null);
	}
	
	public static Set<Block> surroundingBlocks(Location location, int radius, boolean sphere, Predicate<Block> filter) {
		Set<Block> set = new HashSet<Block>();
		Block center = location.getBlock();
		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {
					Block b = center.getRelative(x, y, z);
					if(!filter.test(b)) {
						continue;
					}
					if (sphere && center.getLocation().distance(b.getLocation()) <= radius) {
						set.add(b);
					}else if(!sphere) {
						set.add(b);
					}
				}

			}
		}
		return set;
	}

}
