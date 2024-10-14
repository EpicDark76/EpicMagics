package eu.epicdark.epicmagics.spells;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;

public abstract class MobSpell extends AnimatedRangedMagicSpell {

	protected MobSpell(Material material, int cost, int cooldown, int range, Particle particle) {
		super(material, cost, cooldown, range, particle);
	}

	public abstract LivingEntity createEntity(Location location);

	@Override
	protected boolean action(Location target) {
		target.getWorld().spawnParticle(Particle.EXPLOSION, target, 20, 1, 1, 1, 0.1, null, true);
		LivingEntity entity = createEntity(target);
		return entity != null;
	}

}
