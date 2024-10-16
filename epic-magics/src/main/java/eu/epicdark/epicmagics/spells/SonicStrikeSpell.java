package eu.epicdark.epicmagics.spells;

import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import eu.epicdark.epicmagics.EpicMagics;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class SonicStrikeSpell extends RangedMagicSpell{

	private static final int soundVolume = 7;
	private static final int height = 1000;
	
	public SonicStrikeSpell() {
		super(Material.ECHO_SHARD, 1, minutes(10), 180);
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
		
		world.playSound(location, Sound.ENTITY_WARDEN_SONIC_CHARGE, SoundCategory.AMBIENT, soundVolume, 0.5f);
		
		new BukkitRunnable() {

			@Override
			public void run() {
				
				Location target = location.toHighestLocation(HeightMap.MOTION_BLOCKING);
				for(int y = target.getBlockY(); y < target.getBlockY()+height; y += 2) {
					world.spawnParticle(Particle.SONIC_BOOM, target.getX(), y, target.getZ(), 1, 0, 0, 0, 0, null, true);
				}
				
				world.playSound(location, Sound.ITEM_TOTEM_USE, SoundCategory.AMBIENT, soundVolume, 1.2f);
				world.spawnParticle(Particle.EXPLOSION_EMITTER, target, 20, 5, 5, 5, 0, null, true);
				world.createExplosion(target.add(0, 0.5, 0), 20, false, true, player);
				
			}
		}.runTaskLater(EpicMagics.INSTANCE, 70);
		
		return true;
	}

}
