package eu.epicdark.epicmagics.spells;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.util.Vector;

public class TntSpell extends MagicSpell{

	public TntSpell() {
		super(Material.TNT, 1, seconds(10));
	}

	@Override
	protected void playCastingSound(Player player) {
		super.playCastingSound(player);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_TNT_PRIMED, 1, 1);
	}
	
	@Override
	protected boolean handle(Player player) {
		
		TNTPrimed tnt = player.getWorld().spawn(player.getEyeLocation(), TNTPrimed.class);
		tnt.setVelocity(player.getEyeLocation().getDirection().clone().multiply(1.6).add(new Vector(0, 0.4, 0)));
		tnt.setSource(player);
		
		return true;
	}

}
