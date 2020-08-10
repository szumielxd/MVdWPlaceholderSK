package me.szumielxd.MVdWPlaceholderSK.skript.events;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptConfig;
import ch.njol.skript.SkriptEventHandler;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SelfRegisteringSkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.util.Getter;
import ch.njol.skript.util.Task;
import me.szumielxd.MVdWPlaceholderSK.MVdWPlaceholderSK;
import me.szumielxd.MVdWPlaceholderSK.placeholderAPI.MVdWPAPIEvent;
import me.szumielxd.MVdWPlaceholderSK.placeholderAPI.MVdWPAPIListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ch.njol.skript.registrations.EventValues;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import be.maximvdw.placeholderapi.PlaceholderAPI;

@Name("On Placeholder Request")
@Description("Called whenever a placeholder is requested")
@Examples("on placeholder request with prefix \"example\":\n\tif the identifier is \"name\": # example_name\n\t\tset the result to player's name\n\telse if the identifier is \"uuid\": # example_uuid\n\t\tset the result to the player's uuid\n\telse if the identifier is \"money\": # example_money\n\t\tset the result to \"$%{money::%player's uuid%}%\"")
public class SKPlaceholderRequestEvent extends SelfRegisteringSkriptEvent {

	static {
		Skript.registerEvent("Placeholder Request", SKPlaceholderRequestEvent.class, MVdWPAPIEvent.class, "(mvdw[-]placeholder[api]|mvdw) request with [the] prefix %string%");
		EventValues.registerEventValue(MVdWPAPIEvent.class, Player.class, new Getter<Player, MVdWPAPIEvent>() {
			@Override
			public Player get(MVdWPAPIEvent e) {
				return e.getPlayer();
			}
		}, 0);
		EventValues.registerEventValue(MVdWPAPIEvent.class, String.class, new Getter<String, MVdWPAPIEvent>() {
			@Override
			public String get(MVdWPAPIEvent e) {
				return e.getIdentifier();
			}
		}, 0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(final Literal<?>[] args, final int matchedPattern, final SkriptParser.ParseResult parser) {
		prefix = ((Literal<String>) args[0]).getSingle();
		if ("".equals(prefix) || prefix.equals("*")) {
			Skript.error(prefix + " is not a valid placeholder", ErrorQuality.SEMANTIC_ERROR);
			return false;
		}
		if(MVdWPlaceholderSK.isPlaceholderRegistered(prefix)) {
			Skript.error("Placeholder with prefix '" + prefix + "' is already registered in MVdWPlaceholderAPI", ErrorQuality.SEMANTIC_ERROR);
			return false;
		}
		return true;
	}

	private String prefix;
	final static HashMap<String, Trigger> triggers = new HashMap<>();
	final static HashMap<String, MVdWPAPIListener> listeners = new HashMap<>();
	
	private static boolean registeredExecutor = false;
	private final EventExecutor executor = new EventExecutor() {
		
		@Override
		public void execute(final Listener l, final Event e) throws EventException {
			if (e == null)
				return;
			MVdWPAPIEvent ev = (MVdWPAPIEvent)e;
			if(ev.getPrefix() != null) {
				final Trigger tr = triggers.get(ev.getPrefix());
				if(tr != null) {
					
					if(!ev.isAsynchronous()) {
						SkriptEventHandler.logTriggerStart(tr);
						tr.execute(e);
						SkriptEventHandler.logTriggerEnd(tr);
					} else {
						Task.callSync(new Callable<Void>() {
							@Override
							public Void call() throws Exception {
								SkriptEventHandler.logTriggerStart(tr);
								tr.execute(e);
								SkriptEventHandler.logTriggerEnd(tr);
								return null;
							}
						});
					}
				}
			}
			return;
		}
	};

	@Override
	public String toString(Event e, boolean debug) {
		return "placeholder request" + (prefix != null ? (" with prefix \"" + prefix + "\"") : "");
	}

	@Override
	public void register(Trigger tr) {
		triggers.put(prefix, tr);
		MVdWPAPIListener l = new MVdWPAPIListener(prefix);
		listeners.put(prefix, l);
		PlaceholderAPI.registerPlaceholder(MVdWPlaceholderSK.getInstance(), prefix, l);
		if (!registeredExecutor) {
			Bukkit.getPluginManager().registerEvent(MVdWPAPIEvent.class, new Listener() {}, SkriptConfig.defaultEventPriority.value(), executor, MVdWPlaceholderSK.getInstance(), true);
			registeredExecutor = true;
		}
	}

	@Override
	public void unregister(Trigger tr) {
		for(String key : new HashSet<>(triggers.keySet())) {
			if(triggers.get(key) != tr) continue;
			triggers.remove(key);
			listeners.remove(key);
			MVdWPlaceholderSK.unregisterPlaceholder(key);
		}
	}

	@Override
	public void unregisterAll() {
		for(String key : new HashSet<>(triggers.keySet())) {
			triggers.remove(key);
			listeners.remove(key);
			MVdWPlaceholderSK.unregisterPlaceholder(key);
		}
	}

}