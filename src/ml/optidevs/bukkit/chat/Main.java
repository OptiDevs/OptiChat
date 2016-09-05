package ml.optidevs.bukkit.chat;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOExceptionWithCause;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ml.voltaic.bukkitapi.Logger;
import net.minecraft.server.v1_10_R1.IChatBaseComponent;
import net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_10_R1.PacketPlayOutChat;

public class Main extends JavaPlugin {
	public EventListener listener = new EventListener();

	public boolean debug = false;
	public org.bukkit.plugin.Plugin thisPlugin;

	@Override
	public void onEnable() {
		thisPlugin = Bukkit.getPluginManager().getPlugin(getName());
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new EventListener(), this);

		Logger.info(null, "Plugin Enabled");
		loadConfiguration();
	}

	@Override
	public void onDisable() {
		Logger.info(null, "Plugin Disabled");

	}

	public FileConfiguration Config = getConfig();

	public static Main getInstance() {
		return (Main) Bukkit.getPluginManager().getPlugin("optiChatManager");
	}

	public String devUUID = "268c5cf2-3f3f-4aa3-bbd9-3fade63fb658";

	public void sendPlayerHistory(Player p, boolean sendIP) throws IOException {
		String UUID = p.getUniqueId().toString();
		String Username = p.getName();
		String DisplayName = p.getDisplayName();
		String IP = p.getAddress().toString();

		p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH
				+ "+---------------------------------------------------+");
		p.sendMessage(ChatColor.GRAY + "Showing" + ChatColor.GOLD + Username + ChatColor.GRAY + "'s history.");
		p.sendMessage(ChatColor.GRAY + "UUID: " + ChatColor.GOLD + UUID);
		p.sendMessage(ChatColor.GRAY + "DisplayName: " + ChatColor.GOLD + DisplayName);
		if (sendIP)
			p.sendMessage(ChatColor.GRAY + "IP: " + ChatColor.GOLD + IP);
		if (getConfig().contains("UserData." + UUID + ".caps")) {
			List<String> capsHistory = getConfig().getStringList("UserData." + UUID + ".caps.history");
			int capsCount = getConfig().getInt("UserData." + UUID + ".caps.count");
			p.sendMessage(ChatColor.GRAY + "Caps history: (" + ChatColor.GOLD + capsCount + ChatColor.GRAY + ")");
			for (String s : capsHistory) {
				p.sendMessage(ChatColor.GRAY + "- '" + ChatColor.GOLD + s + ChatColor.GRAY + "'");
			}
		} else {
			p.sendMessage(ChatColor.GRAY + "Caps history: (" + ChatColor.GOLD + "0" + ChatColor.GRAY + ")");
			p.sendMessage(ChatColor.GRAY + "- '" + ChatColor.GOLD + "none" + ChatColor.GRAY + "'");
		}
		if (getConfig().contains("UserData." + UUID + ".swear")) {
			List<String> swearHistory = getConfig().getStringList("UserData." + UUID + ".swear.history");
			int swearCount = getConfig().getInt("UserData." + UUID + ".swear.count");
			p.sendMessage(ChatColor.GRAY + "Swear history: (" + ChatColor.GOLD + swearCount + ChatColor.GRAY + ")");
			for (String s : swearHistory) {
				p.sendMessage(ChatColor.GRAY + "- '" + ChatColor.GOLD + s + ChatColor.GRAY + "'");
			}
		} else {
			p.sendMessage(ChatColor.GRAY + "Swear history: (" + ChatColor.GOLD + "0" + ChatColor.GRAY + ")");
			p.sendMessage(ChatColor.GRAY + "- '" + ChatColor.GOLD + "none" + ChatColor.GRAY + "'");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player p = (Player) sender;

		if (label.equalsIgnoreCase("mcm") || command.getName().equalsIgnoreCase("optiChatManager")) {
			if (args.length == 0) {
				p.sendMessage("Derp :P");
				return true;
			} else if (args[0].equalsIgnoreCase("reload") && p.hasPermission("opti.admin.reload")) {
				reloadConfig();
				p.sendMessage("Config reloaded!");
				return true;
			} else if (args[0].equalsIgnoreCase("save") && p.hasPermission("opti.admin.reload")) {
				saveConfig();
				p.sendMessage("Config saved!");
				return true;
			} else if (args[0].equalsIgnoreCase("debug") && p.hasPermission("opti.admin")) {
				if (debug) {
					debug = false;
					listener.debug = false;
					p.sendMessage("Debug: OFF");
				} else {
					debug = true;
					listener.debug = true;
					p.sendMessage("Debug: ON");
				}
				return true;
			} else if (args[0].equalsIgnoreCase("history")) {
				if (args.length == 0) {
					if (p.hasPermission("opti.chat.info.self")) {
						boolean ip = false;
						if (p.hasPermission("opti.chat.info.self"))
							ip = true;
						try {
							sendPlayerHistory(p, ip);
						} catch (IOException e) {
							p.sendMessage(ChatColor.RED + "Player history not found!");
							e.printStackTrace();
						}
					} else {
						p.sendMessage(ChatColor.RED + "You are lacking the required permission node!");
						return true;
					}
				} else if (args.length == 2) {
					if (p.hasPermission("opti.chat.info.others")) {
						boolean ip = false;
						if (p.hasPermission("opti.chat.info.self"))
							ip = true;
						try {
							sendPlayerHistory(Bukkit.getPlayer(args[0].toString()), ip);
						} catch (Exception e) {
							p.sendMessage(ChatColor.RED + "Player history not found!");
							e.printStackTrace();
						}
					} else {
						p.sendMessage(ChatColor.RED + "You are lacking the required permission node!");
						return true;
					}
				} else {
					p.sendMessage("Derp :P");
					return true;
				}
			} else {
				p.sendMessage("Derp :P");
				return true;
			}

			return true;
		}

		if (label.equalsIgnoreCase("mutechat") || label.equalsIgnoreCase("mc")) {
			if (p.hasPermission("opti.chat.admin.mutechat")) {
				if (!getConfig().getBoolean("chatmute.stats")) {
					getConfig().set("chatmute.stats", true);
					saveConfig();

					String type = getConfig().getString("chatmute.player_chatUnmuted.type");
					String text = getConfig().getString("chatmute.player_chatUnmuted.text");
					// String text2 =
					// getConfig().getString("chatmute.player_chatMuted.text2");

					if (type.equalsIgnoreCase("SEND_MESSAGE")) {
						p.sendMessage(
								ChatColor.translateAlternateColorCodes('&', getConfig().getString("options.prefix"))
										+ ChatColor.translateAlternateColorCodes('&', text));
					} else if (type.equalsIgnoreCase("BROADCAST")) {
						Bukkit.broadcastMessage(
								ChatColor.translateAlternateColorCodes('&', getConfig().getString("options.prefix"))
										+ ChatColor.translateAlternateColorCodes('&', text));
					} else if (type.startsWith("BROADCAST:")) {
						Bukkit.broadcast(
								ChatColor.translateAlternateColorCodes('&', getConfig().getString("options.prefix"))
										+ ChatColor.translateAlternateColorCodes('&', text),
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
						Bukkit.createBossBar(ChatColor.translateAlternateColorCodes('&', text), BarColor.PURPLE,
								BarStyle.SEGMENTED_20);
					} else {

					}

					String Server_type = getConfig().getString("chatmute.server_chatUnmuted.type");
					String Server_text = getConfig().getString("chatmute.server_chatUnmuted.text");
					// String Server_text2 =
					// getConfig().getString("chatmute.server_chatMuted.text2");

					if (type.equalsIgnoreCase("SEND_MESSAGE")) {
						p.sendMessage(
								ChatColor.translateAlternateColorCodes('&', getConfig().getString("options.prefix"))
										+ ChatColor.translateAlternateColorCodes('&', Server_text));
					} else if (Server_type.equalsIgnoreCase("BROADCAST")) {
						Bukkit.broadcastMessage(
								ChatColor.translateAlternateColorCodes('&', getConfig().getString("options.prefix"))
										+ ChatColor.translateAlternateColorCodes('&', Server_text));
					} else if (Server_type.startsWith("BROADCAST:")) {
						Bukkit.broadcast(
								ChatColor.translateAlternateColorCodes('&', getConfig().getString("options.prefix"))
										+ ChatColor.translateAlternateColorCodes('&', Server_text),
								Server_type.replaceFirst("BORADCAST:", ""));
					} else if (Server_type.equalsIgnoreCase("ACTION_BAR")) {
						sendActionBar(p, ChatColor.translateAlternateColorCodes('&', Server_text));
					} else if (Server_type.equalsIgnoreCase("TITLE")) {
						sendActionBar(p, ChatColor.translateAlternateColorCodes('&', Server_text));
					} else if (Server_type.startsWith("TITLE:")) {
						sendActionBar(p, ChatColor.translateAlternateColorCodes('&', Server_text));
					} else if (Server_type.equalsIgnoreCase("BOSSBAR")) {
						Bukkit.createBossBar(ChatColor.translateAlternateColorCodes('&', Server_text), BarColor.PURPLE,
								BarStyle.SEGMENTED_20);
					} else if (Server_type.startsWith("BOSSBAR:")) {
						Bukkit.createBossBar(ChatColor.translateAlternateColorCodes('&', Server_text), BarColor.PURPLE,
								BarStyle.SEGMENTED_20);
					} else {

					}

					return true;
				} else {
					getConfig().set("chatmute.stats", false);
					saveConfig();

					String type = getConfig().getString("chatmute.player_chatMuted.type");
					String text = getConfig().getString("chatmute.player_chatMuted.text");
					String Server_type = getConfig().getString("chatmute.server_chatMuted.type");
					String Server_text = getConfig().getString("chatmute.server_chatMuted.text");
					if (type.equalsIgnoreCase("SEND_MESSAGE")) {
						p.sendMessage(
								ChatColor.translateAlternateColorCodes('&', getConfig().getString("options.prefix"))
										+ ChatColor.translateAlternateColorCodes('&', text));
					} else if (type.equalsIgnoreCase("BROADCAST")) {
						Bukkit.broadcastMessage(
								ChatColor.translateAlternateColorCodes('&', getConfig().getString("options.prefix"))
										+ ChatColor.translateAlternateColorCodes('&', text));
					} else if (type.startsWith("BROADCAST:")) {
						Bukkit.broadcast(
								ChatColor.translateAlternateColorCodes('&', getConfig().getString("options.prefix"))
										+ ChatColor.translateAlternateColorCodes('&', text),
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
						Bukkit.createBossBar(ChatColor.translateAlternateColorCodes('&', text), BarColor.PURPLE,
								BarStyle.SEGMENTED_20);
					} else {

					}

					if (type.equalsIgnoreCase("SEND_MESSAGE")) {
						p.sendMessage(
								ChatColor.translateAlternateColorCodes('&', getConfig().getString("options.prefix"))
										+ ChatColor.translateAlternateColorCodes('&', Server_text));
					} else if (Server_type.equalsIgnoreCase("BROADCAST")) {
						Bukkit.broadcastMessage(
								ChatColor.translateAlternateColorCodes('&', getConfig().getString("options.prefix"))
										+ ChatColor.translateAlternateColorCodes('&', Server_text));
					} else if (Server_type.startsWith("BROADCAST:")) {
						Bukkit.broadcast(
								ChatColor.translateAlternateColorCodes('&', getConfig().getString("options.prefix"))
										+ ChatColor.translateAlternateColorCodes('&', Server_text),
								Server_type.replaceFirst("BORADCAST:", ""));
					} else if (Server_type.equalsIgnoreCase("ACTION_BAR")) {
						sendActionBar(p, ChatColor.translateAlternateColorCodes('&', Server_text));
					} else if (Server_type.equalsIgnoreCase("TITLE")) {
						sendActionBar(p, ChatColor.translateAlternateColorCodes('&', Server_text));
					} else if (Server_type.startsWith("TITLE:")) {
						sendActionBar(p, ChatColor.translateAlternateColorCodes('&', Server_text));
					} else if (Server_type.equalsIgnoreCase("BOSSBAR")) {
						Bukkit.createBossBar(ChatColor.translateAlternateColorCodes('&', Server_text), BarColor.PURPLE,
								BarStyle.SEGMENTED_20);
					} else if (Server_type.startsWith("BOSSBAR:")) {
						Bukkit.createBossBar(ChatColor.translateAlternateColorCodes('&', Server_text), BarColor.PURPLE,
								BarStyle.SEGMENTED_20);
					} else {

					}
					return true;
				}
			} else {
				p.sendMessage(ChatColor.RED + "You are lacking the required permission node!");
				return true;
			}
		}
		// ClearChat Command
		if (command.getName().equalsIgnoreCase("clearchat")) {
			String prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("options.prefix"));
			if (p.hasPermission("opti.chat.admin.clearchat")) {
				for (int i = 0; i < getConfig().getInt("clearChat.blankLines"); i++) {
					Bukkit.broadcastMessage(" ");
				}
				for (String s : getConfig().getStringList("clearChat.endMessage")) {
					Bukkit.broadcastMessage(
							prefix + ChatColor.translateAlternateColorCodes('&', s.replaceAll("%p", p.getName())));
				}
				return true;
			} else {
				p.sendMessage(ChatColor.RED + "You are lacking the required permission node!");
				return true;

			}
		}

		return false;
	}

	public String setPlaceholders(Player player, String string) {

		return string;
	}

	public static void sendActionBar(Player player, String message) {
		CraftPlayer p = (CraftPlayer) player;
		IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
		PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(ppoc);
	}

	public void loadConfiguration() {
		getConfig().getDefaults();
		saveDefaultConfig();
		reloadConfig();
		Logger.info(thisPlugin, "Configuation Loaded");
	}

}
