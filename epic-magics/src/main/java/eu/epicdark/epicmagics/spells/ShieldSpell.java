package eu.epicdark.epicmagics.spells;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
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
	private static final Particle particle = Particle.ENCHANTED_HIT;
	
	public ShieldSpell() {
		super(Material.SHIELD, 1, duration + minutes(5));
		Bukkit.getPluginManager().registerEvents(this, EpicMagics.INSTANCE);
	}

	@Override
	protected boolean handle(Player player) {
		if(shieldedPlayers.contains(player.getUniqueId())) {
			return false;
		}
		shieldedPlayers.add(player.getUniqueId());
		
		final float frequency = 2;
		float div = (1f/frequency);
		final float dest = duration*div;
		
		new BukkitRunnable() {
			
			int currentTime = 0;
			int p = 0;
			
			@Override
			public void run() {
				
				if(currentTime >= dest) {
					if(shieldedPlayers.contains(player.getUniqueId())) {
						shieldedPlayers.remove(player.getUniqueId());
						player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, castingCategory, 1, 0.8f);
					}
					cancel();
					return;
				}
				
				push(player);
				
				if(p == 0) {
					p = 1;
					//every second loop, spawn particles
					
					Location l = player.getLocation().clone();
					for(double phi = 0; phi <= Math.PI; phi += Math.PI / 10) {
	                    double y = (radius-1) * Math.cos(phi) + 1.5;
	                    for(double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 20) {
	                        double x = radius * Math.cos(theta) * Math.sin(phi);
	                        double z = radius * Math.sin(theta) * Math.sin(phi);

	                        l.add(x, y, z);
	                        l.getWorld().spawnParticle(particle, l, 1, 0F, 0F, 0F, 0.01);
	                        l.subtract(x, y, z);
	                    }
	                }
					
				}else {
					p = 0;
				}
				
				currentTime++;
			}
		}.runTaskTimer(EpicMagics.INSTANCE, 0, (long) frequency);
		
		return true;
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if(shieldedPlayers.isEmpty()) {
			return;
		}
		if(shieldedPlayers.contains(player.getUniqueId())) {
			push(player);
			return;
		}
	}
	
	private void push(Player player) {
		for(Entity en : player.getNearbyEntities(radius, radius, radius)) {
			if(!(en instanceof LivingEntity)) {
				continue;
			}
			if(en.isDead()) {
				continue;
			}
			if(en instanceof Player && ((Player)en).getGameMode() == GameMode.CREATIVE && ((Player)en).isOp()) {
				continue;
			}
			if(en instanceof Player && player.spigot().getHiddenPlayers().contains(en)) {
				continue;
			}
			en.setVelocity(getVelocity(player.getLocation(), en.getLocation()));
		}
	}
	
	private Vector getVelocity(Location origin, Location location) {
		return location.toVector().subtract(origin.toVector()).normalize().multiply(1.8d).add(new Vector(0, 0.3, 0));
	}

}
