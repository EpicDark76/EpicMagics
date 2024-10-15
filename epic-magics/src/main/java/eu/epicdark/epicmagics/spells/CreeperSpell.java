package eu.epicdark.epicmagics.spells;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;

public class CreeperSpell extends MobSpell{

	public CreeperSpell() {
		super(Material.GUNPOWDER, 16, seconds(10), 50, Particle.FIREWORK);
	}

	@Override
	public LivingEntity createEntity(Location location) {
		Creeper creeper = location.getWorld().spawn(location, Creeper.class);
		return creeper;
	}

}
