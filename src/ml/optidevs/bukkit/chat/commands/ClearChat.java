package ml.optidevs.bukkit.chat.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ml.optidevs.bukkit.chat.Lang;
import ml.optidevs.bukkit.chat.Main;
import ml.optidevs.bukkit.chat.Perm;

public class ClearChat implements CommandExecutor {
	private Main m = null;

	public ClearChat(Main main) {
		m = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String prefix = Lang.PREFIX.toString();
		if (sender.hasPermission(Perm.ADMIN.CLEARCHAT)) {
			for (int i = 0; i < m.getConfig().getInt("clearChat.blankLines"); i++) {
				Bukkit.broadcastMessage("");
			}
			for (String s : m.getConfig().getStringList("clearChat.endMessage")) {
				Bukkit.broadcastMessage(
						prefix + ChatColor.translateAlternateColorCodes('&', s.replaceAll("%p", sender.getName())));
			}
			return true;
		} else {
			sender.sendMessage(Lang.NO_PERMS.toString());
			return true;

		}
	}
}
