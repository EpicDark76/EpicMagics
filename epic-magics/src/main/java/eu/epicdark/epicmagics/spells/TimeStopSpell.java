package eu.epicdark.epicmagics.spells;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import eu.epicdark.epicmagics.EpicMagics;

public class TimeStopSpell extends MagicSpell{

	private HashMap<LivingEntity, Boolean> AIs = new HashMap<>();
	
	public TimeStopSpell() {
		super(Material.CLOCK, 1, minutes(2));
	}
	
	@Override
	protected void playCastingSound(Player player) {
		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, castingCategory, 1, 0.5f);
	}

	@Override
	protected boolean handle(Player player) {
		
		for(Entity entity : player.getNearbyEntities(20, 20, 20)) {
			if(!(entity instanceof LivingEntity)) {
				continue;
			}
			LivingEntity lEntity = (LivingEntity) entity;
			AIs.put(lEntity, lEntity.hasAI());
			lEntity.setAI(false);
			lEntity.setSilent(true);
		}
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				player.getWorld().playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, castingCategory, 1, 0.5f);
				for(LivingEntity entity : AIs.keySet()) {
					entity.setAI(AIs.get(entity));
					entity.setSilent(false);
				}
			}
		}.runTaskLater(EpicMagics.INSTANCE, minutes(1));
		
		return true;
	}

}
