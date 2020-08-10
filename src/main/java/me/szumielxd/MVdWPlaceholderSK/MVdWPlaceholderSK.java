package me.szumielxd.MVdWPlaceholderSK;

import java.io.IOException;
import java.lang.reflect.Field;

import org.bukkit.plugin.java.JavaPlugin;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.internal.PlaceholderPack;
import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;

public class MVdWPlaceholderSK extends JavaPlugin {
	
	
	private static MVdWPlaceholderSK instance;
	private SkriptAddon addon;
	
	
	public void onEnable() {
		
		instance = this;
		try {
			getSkriptInstance();
			(this.addon = getSkriptInstance()).loadClasses("me.szumielxd.MVdWPlaceholderSK.skript", "events", "expressions");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static MVdWPlaceholderSK getInstance() {
		
		if (instance == null) throw new IllegalStateException();
		return instance;
		
	}
	
	
	public SkriptAddon getSkriptInstance() {
		
		if(addon == null) addon = Skript.registerAddon(this);
		return addon;
		
	}
	
	
	public static boolean isPlaceholderRegistered(String prefix) {
		try {
			Field f = PlaceholderAPI.class.getDeclaredField("customPlaceholders");
			f.setAccessible(true);
			PlaceholderPack pack = (PlaceholderPack) f.get(null);
			f.setAccessible(false);
			return pack.getPlaceholderReplacer("{"+prefix+"}") != null;
		} catch (NoSuchFieldException|SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public static void unregisterPlaceholder(String prefix) {
		try {
			Field f = PlaceholderAPI.class.getDeclaredField("customPlaceholders");
			f.setAccessible(true);
			PlaceholderPack pack = (PlaceholderPack) f.get(null);
			pack.removePlaceholder("{"+prefix+"}");
			f.setAccessible(false);
		} catch (NoSuchFieldException|SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	

}
