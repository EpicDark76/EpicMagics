package eu.epicdark.epicmagics.spells;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public abstract class RangedMagicSpell extends MagicSpell{

	private final int range;
	
	protected RangedMagicSpell(Material material, int cost, int cooldown, int range) {
		super(material, cost, cooldown);
		this.range = range;
	}

	
	@Override
	protected boolean handle(Player player) {
		RayTraceResult result = player.rayTraceBlocks(range);
		if(result == null || result.getHitBlock() == null || result.getHitBlock().isEmpty() || result.getHitPosition() == null) {
			player.sendActionBar(Component.text("Out of range!").color(NamedTextColor.RED));
			return false;
		}
		return atTarget(player, result.getHitPosition().toLocation(player.getWorld()));
	}
	
	protected abstract boolean atTarget(Player player, Location target);

}
