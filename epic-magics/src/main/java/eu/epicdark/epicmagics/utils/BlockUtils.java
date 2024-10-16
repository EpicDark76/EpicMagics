package eu.epicdark.epicmagics.utils;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class BlockUtils {
	
	public static Set<Block> sphereAround(Location location, int radius) {
		Set<Block> sphere = new HashSet<Block>();
		Block center = location.getBlock();
		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {
					Block b = center.getRelative(x, y, z);
					if (center.getLocation().distance(b.getLocation()) <= radius) {
						sphere.add(b);
					}
				}

			}
		}
		return sphere;
	}

}
