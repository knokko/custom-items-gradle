package nl.knokko.core.plugin.player;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Players {
	
	public static Player getOnline(String name) {
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		for (Player player : players)
			if (player.getName().equals(name))
				return player;
		return null;
	}
}