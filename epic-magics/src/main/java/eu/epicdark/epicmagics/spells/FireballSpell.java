package eu.epicdark.epicmagics.spells;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;

public class FireballSpell extends MagicSpell{

	public FireballSpell() {
		super(Material.FIRE_CHARGE, 1, 20);
	}

	@Override
	protected boolean handle(Player player) {
		Fireball fireball = player.launchProjectile(Fireball.class);
		fireball.setIsIncendiary(true);
		return true;
	}
	
	@Override
	protected void playCastingSound(Player player) {
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, castingCategory, 1, 1);
	}

}
