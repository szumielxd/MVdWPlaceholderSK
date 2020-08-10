package me.szumielxd.MVdWPlaceholderSK.placeholderAPI;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MVdWPAPIEvent extends Event {

	
	private static HandlerList handlerList = new HandlerList();
	private Player player;
	private String identifier;
	private String result;
	private String prefix;
	
	
	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}
	
	
	public static HandlerList getHandlerList() {
		return handlerList;
	}
	
	public MVdWPAPIEvent(Player p, String prefix, String identifier) {
		this.identifier = identifier;
		this.player = p;
		this.prefix = prefix;
	}
	
	public MVdWPAPIEvent(Player p, String prefix, String identifier, boolean async) {
		super(async);
		this.identifier = identifier;
		this.player = p;
		this.prefix = prefix;
	}
	
	
	public void setResult(String result) {
		this.result = result;
	}
	
	
	public String getResult() {
		return this.result;
	}
	
	
	public String getIdentifier() {
		return this.identifier;
	}
	
	
	public Player getPlayer() {
		return this.player;
	}
	
	
	public String getPrefix() {
		return this.prefix;
	}
	
	
	

}
