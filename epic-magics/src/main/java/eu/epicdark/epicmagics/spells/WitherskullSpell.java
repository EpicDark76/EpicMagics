package eu.epicdark.epicmagics.spells;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;

public class WitherskullSpell extends MagicSpell{
	
	public WitherskullSpell() {
		super(Material.WITHER_SKELETON_SKULL, 1, minutes(1));
	}

	@Override
	protected boolean handle(Player player) {
		WitherSkull skull = player.launchProjectile(WitherSkull.class);
		if(Math.random()<=0.1) { //10% chance skull is blue (charged)
			skull.setCharged(true);
			skull.setVelocity(skull.getVelocity().multiply(1.8D));
		}
		return true;
	}
	
	@Override
	protected void playCastingSound(Player player) {
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, castingCategory, 1, 1);
	}

}
