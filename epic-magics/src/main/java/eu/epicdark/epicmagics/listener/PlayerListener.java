package eu.epicdark.epicmagics.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class PlayerListener implements Listener{
	
	@EventHandler
	public void onToggleFlight(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();
		/*
		if(event.isFlying()) {
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					
					if(player == null || !player.isFlying()) {
						cancel();
						return;
					}
					if(player.getGameMode() == GameMode.SPECTATOR) {
						return;
					}
					
					double y = Math.sin(player.getWorld().getGameTime()*0.03D) * (1D/300D);
					Vector velocity = new Vector(0D, y, 0D);
					player.setVelocity(player.getVelocity().clone().add(velocity));
				}
			}.runTaskTimer(EpicMagics.INSTANCE, 0, 1);
			
		}
		*/
	}

}
