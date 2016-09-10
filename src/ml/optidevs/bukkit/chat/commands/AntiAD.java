package ml.optidevs.bukkit.chat.commands;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import ml.optidevs.bukkit.chat.Main;
import ml.optidevs.bukkit.chat.Perm;

public class AntiAD implements Listener {
	private Main m = null;

	public AntiAD(Main main) {
		m = main;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void chatADCheck(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		boolean stats = m.getConfig().getBoolean("chatmute.stats");
		boolean loggedAD = false;
		boolean notiAD = false;

		if (stats && !p.hasPermission(Perm.BYPASS.AD)) {
			final String s = e.getMessage();
			String[] words = s.split("\\s+");
			String[] wordsO = s.split("\\s+");
			String letter = ".";
			for (int i = 0; i < words.length; i++) {
				if (words[i].contains(".")) {
					words[i].replaceAll(".", "");
					if (words[i].contains(".")) {
						String type = m.getConfig().getString("AntiAD.actionOne.type");
						String text = m.getConfig().getString("AntiAD.actionOne.text");
						String type2 = m.getConfig().getString("AntiAD.actionTwo.type");
						String text2 = m.getConfig().getString("AntiAD.actionTwo.text");

						if (type.equalsIgnoreCase("REPLACE_WORD")) {
							String msg = s.replace(wordsO[i], text);
							e.setMessage(msg);
						} else if (type.equalsIgnoreCase("CANCEL_MESSAGE")) {
							e.setCancelled(true);
						} else if (!notiAD) {
							m.runType(p, type, text);
						}
						if (type2.equalsIgnoreCase("REPLACE_WORD")) {
							String msg = s.replace(wordsO[i], text2);
							e.setMessage(msg);
						} else if (type2.equalsIgnoreCase("CANCEL_MESSAGE")) {
							e.setCancelled(true);
						} else if (!notiAD) {
							m.runType(p, type2, text2);
						}

						notiAD = true;
						if (!loggedAD) {
							loggedAD = true;
							// UserData.logUser(p, UserData.logType.CAPS,
							// originalMessage);
						}
					}
				}
				if (words[i].indexOf(letter, s.indexOf(letter) + 1) > -1) {

				}
			}
		}

	}
}
