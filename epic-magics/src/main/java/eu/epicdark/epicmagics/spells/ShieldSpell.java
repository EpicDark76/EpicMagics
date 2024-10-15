package eu.epicdark.epicmagics.spells;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import eu.epicdark.epicmagics.EpicMagics;

public class ShieldSpell extends MagicSpell implements Listener{

	private static final HashSet<UUID> shieldedPlayers = new HashSet<>();
	private static final int duration = 30*20; //duration of shield spell in ticks
	private static final int radius = 4;
	private static final Particle particle = Particle.CRIT;
	
	public ShieldSpell() {
		super(Material.SHIELD, 1, duration + minutes(5));
	}

	@Override
	protected boolean handle(Player player) {
		shieldedPlayers.add(player.getUniqueId());
		
		final int frequency = 10;
		new BukkitRunnable() {
			
			int currentTime = 0;
			
			@Override
			public void run() {
				
				if(currentTime >= duration*(frequency/20)) {
					if(shieldedPlayers.contains(player.getUniqueId())) {
						shieldedPlayers.remove(player.getUniqueId());
						player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, castingCategory, 1, 0.8f);
					}
					cancel();
					return;
				}
				
				
				
				currentTime++;
			}
		}.runTaskTimer(EpicMagics.INSTANCE, 0, frequency);
		
		return true;
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if(shieldedPlayers.isEmpty()) {
			return;
		}
		if(shieldedPlayers.contains(player.getUniqueId())) {
			
			for(Entity en : player.getNearbyEntities(radius, radius, radius)) {
				if(!(en instanceof LivingEntity)) {
					continue;
				}
				if(en instanceof Player && ((Player)en).getGameMode() == GameMode.CREATIVE && ((Player)en).isOp()) {
					continue;
				}
				en.setVelocity(getVelocity(player.getLocation(), en.getLocation()));
			}
			
			return;
		}
		
		if(player.getGameMode() == GameMode.CREATIVE && player.isOp()) {
			return;
		}
		
		for(Entity en : player.getNearbyEntities(radius, radius, radius)) {
			if(!(en instanceof Player)) {
				continue;
			}
			Player source = (Player) en;
			if(!shieldedPlayers.contains(source.getUniqueId())) {
				continue;
			}
			player.setVelocity(getVelocity(source.getLocation(), player.getLocation()));
		}
	}
	
	private Vector getVelocity(Location origin, Location location) {
		return origin.toVector().subtract(location.toVector()).normalize().multiply(2d);
	}

}
