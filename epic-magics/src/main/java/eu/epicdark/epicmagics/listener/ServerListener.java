package eu.epicdark.epicmagics.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import eu.epicdark.epicmagics.EpicMagics;
import io.papermc.paper.event.server.ServerResourcesReloadedEvent;

public class ServerListener implements Listener{
	
	@EventHandler
	public void onResourcesReload(ServerResourcesReloadedEvent event) {
		EpicMagics.INSTANCE.loadDatapacks();
	}

}
