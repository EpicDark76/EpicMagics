package eu.epicdark.epicmagics.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import eu.epicdark.epicmagics.EpicMagics;

public class MagicstickCommand implements CommandExecutor{

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		
		if(!sender.hasPermission("epicmagics.cmd.magicstick")) {
			return false;
		}
		
		if(!(sender instanceof Player)) {
			return false;
		}
		
		Player player = (Player) sender;
		
		player.getInventory().addItem(EpicMagics.MAGICSTICK);
		
		return false;
	}

}
