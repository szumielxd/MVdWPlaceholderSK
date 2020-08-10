package me.szumielxd.MVdWPlaceholderSK.placeholderAPI;

import org.bukkit.Bukkit;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;

public class MVdWPAPIListener implements PlaceholderReplacer {


	private String identifier;
	
	
	public MVdWPAPIListener(String prefix) {
		this.identifier = prefix;
	}


	@Override
	public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
		MVdWPAPIEvent event;
		if(Bukkit.isPrimaryThread()) {
			event = new MVdWPAPIEvent(e.getPlayer(), identifier, e.getPlaceholder());
		}else {
			event = new MVdWPAPIEvent(e.getPlayer(), identifier, e.getPlaceholder(), true);
		}
		Bukkit.getPluginManager().callEvent(event);
		return event.getResult();
	}

}
