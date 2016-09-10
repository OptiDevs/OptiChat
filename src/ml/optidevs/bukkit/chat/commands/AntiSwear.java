package ml.optidevs.bukkit.chat.commands;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import ml.optidevs.bukkit.chat.Main;
import ml.optidevs.bukkit.chat.Perm;

public class AntiSwear implements Listener {
	private Main m = null;

	public AntiSwear(Main main) {
		m = main;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void chatSwearCheck(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		final String originalMessage = e.getMessage();
		boolean stats = m.getConfig().getBoolean("chatmute.stats");
		boolean loggedSwear = false;
		boolean notiSwear = false;

		if (!p.hasPermission(Perm.BYPASS.SWEAR)) {
			if (stats || p.hasPermission(Perm.BYPASS.MUTE)) {
				for (int i = 0; i < m.getConfig().getList("AntiSwear.words").toArray().length; i++) {
					String word = m.getConfig().getList("AntiSwear.words").toArray()[i].toString();
					if (isWordSwearWhitelist(e.getMessage()))
						break;
					if (StringUtils.containsIgnoreCase(e.getMessage(), word)) {
						String type = m.getConfig().getString("AntiSwear.actionOne.type");
						String text = m.getConfig().getString("AntiSwear.actionOne.text");
						if (type.equalsIgnoreCase("REPLACE_WORD")) {
							e.setMessage(e.getMessage().replaceAll("(?i)" + word, text));
						} else if (type.equalsIgnoreCase("CANCEL_MESSAGE")) {
							e.setCancelled(true);
						} else if (!notiSwear) {
							m.runType(p, type, text);
						}

						String type2 = m.getConfig().getString("AntiSwear.actionTwo.type");
						String text2 = m.getConfig().getString("AntiSwear.actionTwo.text");

						if (type2.equalsIgnoreCase("REPLACE_WORD")) {
							e.setMessage(e.getMessage().replaceAll("(?i)" + word, text2));
						} else if (type2.equalsIgnoreCase("CANCEL_MESSAGE") && !notiSwear) {
							e.setCancelled(true);
						} else if (!notiSwear) {
							m.runType(p, type2, text2);
						}
						if (!notiSwear) {
							Bukkit.broadcast(ChatColor.GRAY + p.getDisplayName() + ChatColor.DARK_GRAY + " > "
									+ ChatColor.GOLD + "Sent swear word(s)!", "opti.chat.admin.notify.swear");
							Bukkit.broadcast(" Message: " + originalMessage, "opti.chat.admin.notify.swear");
						}

						notiSwear = true;
						if (!loggedSwear) {
							loggedSwear = true;
							// UserData.logUser(p, UserData.logType.SWEAR,
							// originalMessage);

						}
					}
				}
			}
		}

	}

	public boolean isWordSwearWhitelist(String word) {
		for (String s : m.getConfig().getStringList("AntiSwear.whitelist")) {
			if (word.contains("(?i)" + s))
				return true;
		}
		return false;
	}
}
