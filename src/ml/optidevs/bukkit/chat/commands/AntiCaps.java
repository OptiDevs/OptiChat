package ml.optidevs.bukkit.chat.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import ml.optidevs.bukkit.chat.Main;

public class AntiCaps implements Listener {
	private Main m = null;

	public AntiCaps(Main main) {
		m = main;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void chatCapsCheck(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		final String originalMessage = e.getMessage();
		boolean stats = m.getConfig().getBoolean("chatmute.stats");
		boolean loggedCaps = false;
		boolean notiCaps = false;

		if (!e.getPlayer().hasPermission("opti.chat.bypass.caps")) {
			if (stats || !p.hasPermission("opti.chat.bypass.mute")) {
				int minLength = m.getConfig().getInt("AntiCaps.minLength");
				int precent = m.getConfig().getInt("AntiCaps.maxCapsPercent");
				if (e.getMessage().length() >= minLength && getUppercasePercentage(e.getMessage()) >= precent) {
					String type = m.getConfig().getString("AntiCaps.actionOne.type");
					String text = m.getConfig().getString("AntiCaps.actionOne.text");
					String type2 = m.getConfig().getString("AntiCaps.actionTwo.type");
					String text2 = m.getConfig().getString("AntiCaps.actionTwo.text");

					if (type.equalsIgnoreCase("SET_LOWERCASE")) {
						e.setMessage(e.getMessage().toLowerCase());
					} else if (type.equalsIgnoreCase("CANCEL_MESSAGE")) {
						e.setCancelled(true);
					} else if (!notiCaps) {
						m.runType(p, type, text);
					}
					if (type2.equalsIgnoreCase("SET_LOWERCASE")) {
						e.setMessage(e.getMessage().toLowerCase());
					} else if (type2.equalsIgnoreCase("CANCEL_MESSAGE")) {
						e.setCancelled(true);
					} else if (!notiCaps) {
						m.runType(p, type2, text2);
					}

					if (!notiCaps) {
						Bukkit.broadcast(ChatColor.GRAY + p.getDisplayName() + ChatColor.DARK_GRAY + " > "
								+ ChatColor.GOLD + "Used to many caps", "opti.chat.admin.notify.caps");
						Bukkit.broadcast(" Message: " + originalMessage, "opti.chat.admin.notify.caps");
					}

					notiCaps = true;
					if (!loggedCaps) {
						loggedCaps = true;
						// UserData.logUser(p, UserData.logType.CAPS,
						// originalMessage);
					}
				}
			}
		}

	}

	public double getUppercasePercentage(String string) {
		double upperCaseL = 0.0D;
		for (int i = 0; i < string.length(); i++) {
			if (isUppercase(string.substring(i, i + 1))) {
				upperCaseL += 1.0D;
			}
		}
		return upperCaseL / string.length() * 100.0D;
	}

	public boolean isUppercase(String string) {
		String upperCase = m.getConfig().getString("AntiCaps.uppercase-characters");
		return upperCase.contains(string);
	}
}
