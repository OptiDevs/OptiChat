package ml.optidevs.bukkit.chat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import ml.optidevs.bukkit.chat.commands.AntiAD;
import ml.optidevs.bukkit.chat.commands.AntiCaps;
import ml.optidevs.bukkit.chat.commands.AntiSwear;
import ml.optidevs.bukkit.chat.commands.ClearChat;
import ml.optidevs.bukkit.chat.commands.MuteChat;

public class Main extends JavaPlugin {

	public boolean debug = false;
	public org.bukkit.plugin.Plugin thisPlugin;
	public static YamlConfiguration LANG;
	public static File LANG_FILE;
	public FileConfiguration Config = getConfig();
	public java.util.logging.Logger Logger = getServer().getLogger();

	@Override
	public void onEnable() {
		thisPlugin = Bukkit.getPluginManager().getPlugin(getName());
		registerEvents(this, new OtherEvents(this), new MuteChat(this), new AntiAD(this), new AntiSwear(this),
				new AntiCaps(this));
		loadLang();
		Logger.info("[OptiChat] Plugin Enabled");
		loadConfiguration();
		getCommand("ClearChat").setExecutor(new ClearChat(this));
		getCommand("MuteChat").setExecutor(new MuteChat(this));
		getCommand("MuteChat").setAliases(getConfig().getStringList("chatmute.Aliases"));
		getCommand("ClearChat").setAliases(getConfig().getStringList("clearChat.Aliases"));
		getCommand("OptiChat").setAliases(getConfig().getStringList("options.Aliases"));
	}

	@Override
	public void onDisable() {
		Logger.info("[OptiChat] Pl ugin Disabled");

	}

	public static void registerEvents(org.bukkit.plugin.Plugin plugin, Listener... listeners) {
		for (Listener listener : listeners) {
			Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
		}
	}

	public YamlConfiguration getLang() {
		return LANG;
	}

	public File getLangFile() {
		return LANG_FILE;
	}

	public static Main getInstance() {
		return (Main) Bukkit.getPluginManager().getPlugin("optiChatManager");
	}

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

		if (command.getName().equalsIgnoreCase("OptiChat")) {
			if (args.length == 0) {
				p.sendMessage("Derp :P");
				return true;
			} else if (args[0].equalsIgnoreCase("reload") && p.hasPermission(Perm.ADMIN.RELOAD)) {
				reloadConfig();
				p.sendMessage("Config reloaded!");
				return true;
			} else if (args[0].equalsIgnoreCase("save") && p.hasPermission(Perm.ADMIN.RELOAD)) {
				saveConfig();
				p.sendMessage("Config saved!");
				return true;
			} else if (args[0].equalsIgnoreCase("debug") && p.hasPermission(Perm.ADMIN.ALL)) {
				if (debug) {
					debug = false;
					p.sendMessage("Debug: OFF");
				} else {
					debug = true;
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
		return false;
	}

	public String setPlaceholders(Player player, String string) {

		return string;
	}

	public void loadConfiguration() {
		getConfig().getDefaults();
		saveDefaultConfig();
		reloadConfig();
		Logger.info("[OptiDevs] Configuation Loaded");
	}

	public void loadLang() {
		File lang = new File(getDataFolder(), "lang.yml");
		if (!lang.exists()) {
			try {
				getDataFolder().mkdir();
				lang.createNewFile();
				InputStream defConfigStream = this.getResource("lang.yml");
				if (defConfigStream != null) {
					YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(LANG_FILE);
					defConfig.save(lang);
					Lang.setFile(defConfig);
					return;
				}
			} catch (IOException e) {
				Logger.severe("[OptiChat] Couldn't create language file.");
				Logger.severe("[OptiChat] This is a fatal error. Now disabling");
			}
		}
		YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
		for (Lang item : Lang.values()) {
			if (conf.getString(item.getPath()) == null) {
				conf.set(item.getPath(), item.getDefault());
			}
		}
		Lang.setFile(conf);
		Main.LANG = conf;
		Main.LANG_FILE = lang;
		try {
			conf.save(getLangFile());
		} catch (IOException e) {
			e.printStackTrace();
			Logger.log(Level.SEVERE, "[OptiChat] Failed to save lang.yml.");
			Logger.log(Level.SEVERE, "[OptiChat] Report this stack trace to OptiDevs.");
		}
	}

	public void runType(CommandSender s, String type, String text) {
		if (s == null || type == null || text == null) {
			return;
		}

		String prefix = Lang.PREFIX.toString();
		if (type.equalsIgnoreCase("SEND_MESSAGE")) {
			s.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', text));
		} else if (type.equalsIgnoreCase("BROADCAST")) {
			Bukkit.broadcastMessage(prefix + ChatColor.translateAlternateColorCodes('&', text));
		} else if (type.startsWith("BROADCAST:")) {
			Bukkit.broadcast(prefix + ChatColor.translateAlternateColorCodes('&', text),
					type.replaceFirst("BORADCAST:", ""));
		}
	}
	

}
