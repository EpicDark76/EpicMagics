package eu.epicdark.epicmagics.spells;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ShieldSpell extends MagicSpell{

	public ShieldSpell() {
		super(Material.SHIELD, 1, minutes(5));
	}

	@Override
	protected boolean handle(Player player) {
		return false;
	}

}
