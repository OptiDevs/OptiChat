package ml.optidevs.bukkit.chat;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * An enum for requesting strings from the language file.
 * 
 * @author gomeow
 */
public enum Lang {
	// Plugin Options
	PREFIX("prefix", "&bOptiChat &8&l»&7"), PLAYER_ONLY("player-only", "Sorry but that can only be run by a player!"),
	// Syntax
	INVALID_ARGS("invalid-args", "&cInvalid args!"),
	// Permissions
	NO_PERMS("no-permissions", "&cYou don''t have permission for that!");

	private String path;
	private String value;
	private static YamlConfiguration LANG;

	Lang(String path, String value, String... args) {
		this.path = path;
		this.value = value;
	}

	public static void setFile(YamlConfiguration config) {
		LANG = config;
	}

	@Override
	public String toString() {
		if (this == PREFIX)
			return ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, value)) + " ";
		return ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, value));
	}

	public String getDefault() {
		return this.value;
	}

	public String getPath() {
		return this.path;
	}
}
