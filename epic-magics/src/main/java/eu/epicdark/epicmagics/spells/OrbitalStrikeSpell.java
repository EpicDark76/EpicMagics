package eu.epicdark.epicmagics.spells;

import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import eu.epicdark.epicmagics.utils.Laser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class OrbitalStrikeSpell extends RangedMagicSpell {

	public OrbitalStrikeSpell() {
		super(Material.AMETHYST_SHARD, 1, minutes(10), 300);
	}
	
	@Override
	protected void playCastingSound(Player player) {
		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, castingCategory, 1, 1);
	}

	@Override
	protected boolean handle(Player player, Location location) {
		World world = player.getWorld();
		int highestY = world.getHighestBlockYAt(location);
		int currentY = location.getBlockY();

		if ((highestY - currentY) > 2) {
			player.sendActionBar(Component.text("Place the orbit can't reach!").color(NamedTextColor.RED));
			return false;
		}
		
		location = location.toHighestLocation(HeightMap.MOTION_BLOCKING);
		new Laser(location, player);
		return true;
	}

}
