package eu.epicdark.epicmagics.listener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import eu.epicdark.epicmagics.EpicMagics;
import eu.epicdark.epicmagics.spells.MagicSpell;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class InteractListener implements Listener {
	
	private static Set<UUID> SPAM_PREVENTION = new HashSet<>();
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if(!event.hasItem()) {
			return;
		}
		if(!event.getAction().isRightClick()) {
			return;
		}
		if(event.hasBlock() && event.getClickedBlock().getType().isInteractable()) {
			return;
		}
		Player player = event.getPlayer();
		if(!player.getInventory().getItemInMainHand().equals(EpicMagics.MAGICSTICK)) {
			return;
		}
		ItemStack material = player.getInventory().getItemInOffHand();
		if(material.isEmpty()) {
			return;
		}
		MagicSpell spell = MagicSpell.byMaterial(material.getType());
		if(spell == null) {
			return;
		}
		event.setCancelled(true);

		int cost = spell.getCost();
		if(material.getAmount() < cost && player.getGameMode() != GameMode.CREATIVE) {
			player.sendActionBar(Component.text("Not enough substenance!").color(NamedTextColor.RED));
			return;
		}
		
		if(player.hasCooldown(spell.getMaterial())) {
			player.sendActionBar(Component.text("Still on cooldown!").color(NamedTextColor.RED));
			return;
		}
		
		if(SPAM_PREVENTION.contains(player.getUniqueId())) {
			return;
		}else {
			SPAM_PREVENTION.add(player.getUniqueId());
			new BukkitRunnable() {
				
				@Override
				public void run() {
					SPAM_PREVENTION.remove(player.getUniqueId());
				}
			}.runTaskLaterAsynchronously(EpicMagics.INSTANCE, 2);
		}
		
		spell.apply(player);
		
	}

}
