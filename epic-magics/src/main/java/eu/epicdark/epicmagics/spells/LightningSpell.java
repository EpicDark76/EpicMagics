package eu.epicdark.epicmagics.spells;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class LightningSpell extends RangedMagicSpell{

	public LightningSpell() {
		super(Material.EMERALD, 16, 20, 200);
	}

	@Override
	protected boolean handle(Player player, Location location) {
		Block block = location.getBlock();
		player.getWorld().strikeLightning(block.getLocation().toCenterLocation());
		return true;
	}

}
