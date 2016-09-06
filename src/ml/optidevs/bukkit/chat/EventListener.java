package ml.optidevs.bukkit.chat;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import net.minecraft.server.v1_10_R1.IChatBaseComponent;
import net.minecraft.server.v1_10_R1.PacketPlayOutChat;
import net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer;

public class EventListener implements Listener {

	public boolean debug = false;

	@EventHandler(priority = EventPriority.HIGHEST)
	public void chatMuteCheck(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (debug)
			p.sendMessage("Checking message for MuteChat");
		boolean stats = Main.getInstance().getConfig().getBoolean("chatmute.stats");

		if (stats || p.hasPermission("opti.chat.bypass.mute")) {
			if (debug)
				p.sendMessage("Chat is NOT muted");
		} else if (!stats) {
			String type = Main.getInstance().getConfig().getString("chatmute.player_triesChat.type");
			String text = Main.getInstance().getConfig().getString("chatmute.player_triesChat.text");
			runType(p, type, text);
			e.setCancelled(true);
			if (debug)
				p.sendMessage("Chat is muted, canceling message");
		}

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		if (debug)
			p.sendMessage("Checking message for commands");
		boolean stats = Main.getInstance().getConfig().getBoolean("chatmute.stats");
		// TODO: Redo command checking section
		if (!stats && !p.hasPermission("opti.chat.bypass.mute")) {
			for (String s : Main.getInstance().getConfig().getStringList("chatmute.blockedCommands")) {
				String message = e.getMessage();
				if (message.toLowerCase().startsWith(s.toLowerCase(), 1)) {
					String type = Main.getInstance().getConfig().getString("chatmute.player_triesCommand.type");
					String text = Main.getInstance().getConfig().getString("chatmute.player_triesCommand.text");
					runType(p, type, text);
					e.setCancelled(true);
				}
			}
		}

	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void chatADCheck(AsyncPlayerChatEvent e) {
		boolean debug = false;
		Player p = e.getPlayer();
		if (debug)
			p.sendMessage("Checking message for AD's");
		boolean stats = Main.getInstance().getConfig().getBoolean("chatmute.stats");
		boolean loggedAD = false;
		boolean notiAD = false;

		if (debug)
			p.sendMessage("Checking stats and permission");
		if (stats && !p.hasPermission("opti.chat.bypass.ad")) {
			if (debug)p.sendMessage("Chat is on and player does not bypass AntiAD");
			final String s = e.getMessage();
			String[] words = s.split("\\s+");
			String[] wordsO = s.split("\\s+");
			String letter = ".";
			for (int i = 0; i < words.length; i++) {
				if (debug)p.sendMessage("Checking for ad in " + words[i]);
				if(words[i].contains(".")){
					words[i].replaceAll(".", "");
					if(words[i].contains(".")){
						if (debug)p.sendMessage("Ad found in " + words[i]);
				    	String type = Main.getInstance().getConfig().getString("AntiAD.actionOne.type");
						String text = Main.getInstance().getConfig().getString("AntiAD.actionOne.text");
						String type2 = Main.getInstance().getConfig().getString("AntiAD.actionTwo.type");
						String text2 = Main.getInstance().getConfig().getString("AntiAD.actionTwo.text");

						if (debug)
							p.sendMessage("Running types");
						if (type.equalsIgnoreCase("REPLACE_WORD")) {
							if (debug)
								p.sendMessage("REPLACE_WORD");
							String msg = s.replace(wordsO[i], text);
							e.setMessage(msg);
						} else if (type.equalsIgnoreCase("CANCEL_MESSAGE")) {
							if (debug)
								p.sendMessage("CANCEL_MESSAGE");
							e.setCancelled(true);
						} else if (!notiAD) {
							runType(p, type, text);
						}
						if (type2.equalsIgnoreCase("REPLACE_WORD")) {
							if (debug)
								p.sendMessage("REPLACE_WORD");
							String msg = s.replace(wordsO[i], text2);
							e.setMessage(msg);
						} else if (type2.equalsIgnoreCase("CANCEL_MESSAGE")) {
							if (debug)
								p.sendMessage("CANCEL_MESSAGE");
							e.setCancelled(true);
						} else if (!notiAD) {
							runType(p, type2, text2);
						}
						
						notiAD = true;
						if (!loggedAD) {
							if (debug)
								p.sendMessage("Logging caps");
							loggedAD = true;
							// UserData.logUser(p, UserData.logType.CAPS,
							// originalMessage);
						}
					}
				}
			    if(words[i].indexOf(letter, s.indexOf(letter) + 1) > -1){
					
			    }
			}
		}

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void chatCapsCheck(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (debug)
			p.sendMessage("Checking message for CAPS");
		final String originalMessage = e.getMessage();
		boolean stats = Main.getInstance().getConfig().getBoolean("chatmute.stats");
		boolean loggedCaps = false;
		boolean notiCaps = false;

		if (!e.getPlayer().hasPermission("opti.chat.bypass.caps")) {
			if (debug)
				p.sendMessage("Player does not bypass caps");
			if (stats || !p.hasPermission("opti.chat.bypass.mute")) {
				if (debug)
					p.sendMessage("Player does not bypass mute, and chat is enabled");
				int minLength = Main.getInstance().getConfig().getInt("AntiCaps.minLength");
				int precent = Main.getInstance().getConfig().getInt("AntiCaps.maxCapsPercent");
				if (e.getMessage().length() >= minLength && getUppercasePercentage(e.getMessage()) >= precent) {
					if (debug)
						p.sendMessage("Checked message for precent and minLength = True");
					String type = Main.getInstance().getConfig().getString("AntiCaps.actionOne.type");
					String text = Main.getInstance().getConfig().getString("AntiCaps.actionOne.text");
					String type2 = Main.getInstance().getConfig().getString("AntiCaps.actionTwo.type");
					String text2 = Main.getInstance().getConfig().getString("AntiCaps.actionTwo.text");

					if (debug)
						p.sendMessage("Running types");
					if (type.equalsIgnoreCase("SET_LOWERCASE")) {
						e.setMessage(e.getMessage().toLowerCase());
					} else if (type.equalsIgnoreCase("CANCEL_MESSAGE")) {
						e.setCancelled(true);
					} else if (!notiCaps) {
						runType(p, type, text);
					}
					if (type2.equalsIgnoreCase("SET_LOWERCASE")) {
						e.setMessage(e.getMessage().toLowerCase());
					} else if (type2.equalsIgnoreCase("CANCEL_MESSAGE")) {
						e.setCancelled(true);
					} else if (!notiCaps) {
						runType(p, type2, text2);
					}

					if (!notiCaps) {
						Bukkit.broadcast(ChatColor.GRAY + p.getDisplayName() + ChatColor.DARK_GRAY + " > "
								+ ChatColor.GOLD + "Used to many caps", "opti.chat.admin.notify.caps");
						Bukkit.broadcast(" Message: " + originalMessage, "opti.chat.admin.notify.caps");
					}

					notiCaps = true;
					if (!loggedCaps) {
						if (debug)
							p.sendMessage("Logging caps");
						loggedCaps = true;
						// UserData.logUser(p, UserData.logType.CAPS,
						// originalMessage);
					}
				}
			}
		}

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void chatSwearCheck(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (debug)
			p.sendMessage("Checking message for swearing");
		final String originalMessage = e.getMessage();
		boolean stats = Main.getInstance().getConfig().getBoolean("chatmute.stats");
		boolean loggedSwear = false;
		boolean notiSwear = false;

		if (!p.hasPermission("opti.chat.bypass.swear")) {
			if (debug)
				p.sendMessage("Player does not bypass swear");
			if (stats || p.hasPermission("opti.chat.bypass.mute")) {
				if (debug)
					p.sendMessage("Player does not bypass mute and mute is enabled");
				for (int i = 0; i < Main.getInstance().getConfig().getList("AntiSwear.words").toArray().length; i++) {
					String word = Main.getInstance().getConfig().getList("AntiSwear.words").toArray()[i].toString();
					if (debug)
						p.sendMessage("Looking for word: " + i);
					if (isWordSwearWhitelist(e.getMessage()))
						break;
					if (StringUtils.containsIgnoreCase(e.getMessage(), word)) {
						if (debug)
							p.sendMessage("Word found!");
						String type = Main.getInstance().getConfig().getString("AntiSwear.actionOne.type");
						String text = Main.getInstance().getConfig().getString("AntiSwear.actionOne.text");
						if (debug)
							p.sendMessage("Running types");
						if (type.equalsIgnoreCase("REPLACE_WORD")) {
							e.setMessage(e.getMessage().replaceAll("(?i)" + word, text));
						} else if (type.equalsIgnoreCase("CANCEL_MESSAGE")) {
							e.setCancelled(true);
						} else if (!notiSwear) {
							runType(p, type, text);
						}

						String type2 = Main.getInstance().getConfig().getString("AntiSwear.actionTwo.type");
						String text2 = Main.getInstance().getConfig().getString("AntiSwear.actionTwo.text");

						if (type2.equalsIgnoreCase("REPLACE_WORD")) {
							e.setMessage(e.getMessage().replaceAll("(?i)" + word, text2));
						} else if (type2.equalsIgnoreCase("CANCEL_MESSAGE") && !notiSwear) {
							e.setCancelled(true);
						} else if (!notiSwear) {
							runType(p, type2, text2);
						}
						if (!notiSwear) {
							Bukkit.broadcast(ChatColor.GRAY + p.getDisplayName() + ChatColor.DARK_GRAY + " > "
									+ ChatColor.GOLD + "Sent swear word(s)!", "opti.chat.admin.notify.swear");
							Bukkit.broadcast(" Message: " + originalMessage, "opti.chat.admin.notify.swear");
						}

						notiSwear = true;
						if (!loggedSwear) {
							loggedSwear = true;
							if (debug)
								p.sendMessage("Logging swear");
							// UserData.logUser(p, UserData.logType.SWEAR,
							// originalMessage);

						}
					}
				}
			}
		}

	}

	public void runType(Player p, String type, String text) {
		if (debug)
			p.sendMessage("Running type " + type + " with text " + text);
		String prefix = Lang.PREFIX.toString());
		if (type.equalsIgnoreCase("SEND_MESSAGE")) {
			p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', text));
		} else if (type.equalsIgnoreCase("BROADCAST")) {
			Bukkit.broadcastMessage(prefix + ChatColor.translateAlternateColorCodes('&', text));
		} else if (type.startsWith("BROADCAST:")) {
			Bukkit.broadcast(prefix + ChatColor.translateAlternateColorCodes('&', text),
					type.replaceFirst("BORADCAST:", ""));
		} else if (type.equalsIgnoreCase("ACTION_BAR")) {
			sendActionBar(p, ChatColor.translateAlternateColorCodes('&', text));
		} else if (type.equalsIgnoreCase("TITLE")) {
			sendActionBar(p, ChatColor.translateAlternateColorCodes('&', text));
		} else if (type.startsWith("TITLE:")) {
			sendActionBar(p, ChatColor.translateAlternateColorCodes('&', text));
		} else if (type.equalsIgnoreCase("BOSSBAR")) {
			Bukkit.createBossBar(ChatColor.translateAlternateColorCodes('&', text), BarColor.PURPLE,
					BarStyle.SEGMENTED_20);
		} else if (type.startsWith("BOSSBAR:")) {
			Bukkit.createBossBar((ChatColor.translateAlternateColorCodes('&', text)), BarColor.PURPLE,
					BarStyle.SEGMENTED_20);
		}
	}

	public void runType(Player p, String type, String text, String[] args) {
		// String prefix = ChatColor.translateAlternateColorCodes('&',
		// Main.getInstance().getConfig().getString("options.prefix"));

	}

	public boolean isWordSwearWhitelist(String word) {
		for (String s : Main.getInstance().getConfig().getStringList("AntiSwear.whitelist")) {
			if (word.contains("(?i)" + s))
				return true;
		}
		return false;
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
		String upperCase = Main.getInstance().getConfig().getString("AntiCaps.uppercase-characters");
		return upperCase.contains(string);
	}

	public static void sendActionBar(Player player, String message) {
		CraftPlayer p = (CraftPlayer) player;
		IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
		PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(ppoc);
	}

}
