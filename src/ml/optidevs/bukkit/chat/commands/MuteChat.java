package ml.optidevs.bukkit.chat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import ml.optidevs.bukkit.chat.Lang;
import ml.optidevs.bukkit.chat.Main;
import ml.optidevs.bukkit.chat.Perm;

public class MuteChat implements CommandExecutor, Listener {
	private Main m = null;

	public MuteChat(Main main) {
		m = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender.hasPermission(Perm.ADMIN.MUTECHAT))) {
			sender.sendMessage(Lang.NO_PERMS.toString());
			return true;
		}
		if (!m.getConfig().getBoolean("chatmute.stats")) {
			m.getConfig().set("chatmute.stats", true);
			m.saveConfig();

			String type = m.getConfig().getString("chatmute.player_chatUnmuted.type");
			String text = m.getConfig().getString("chatmute.player_chatUnmuted.text");
			String Server_type = m.getConfig().getString("chatmute.server_chatUnmuted.type");
			String Server_text = m.getConfig().getString("chatmute.server_chatUnmuted.text");

			m.runType((Player) sender, Server_type, Server_text);
			m.runType((Player) sender, type, text);

			return true;
		} else {
			m.getConfig().set("chatmute.stats", false);
			m.saveConfig();

			String type = m.getConfig().getString("chatmute.player_chatMuted.type");
			String text = m.getConfig().getString("chatmute.player_chatMuted.text");
			String Server_type = m.getConfig().getString("chatmute.server_chatMuted.type");
			String Server_text = m.getConfig().getString("chatmute.server_chatMuted.text");
			m.runType((Player) sender, Server_type, Server_text);
			m.runType((Player) sender, type, text);
			return true;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void chatMuteCheck(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		boolean stats = m.getConfig().getBoolean("chatmute.stats");
		if (!(stats || p.hasPermission(Perm.BYPASS.MUTE))) {
			String type = m.getConfig().getString("chatmute.player_triesChat.type");
			String text = m.getConfig().getString("chatmute.player_triesChat.text");
			Main.getInstance().runType(p, type, text);
			e.setCancelled(true);
		} else {
			return;
		}

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		boolean stats = m.getConfig().getBoolean("chatmute.stats");

		if (p.isOp())
			return;

		if (p.hasPermission(Perm.BYPASS.MUTE))
			return;

		if (!stats) {
			for (String s : m.getConfig().getStringList("chatmute.blockedCommands")) {
				String message = e.getMessage();
				if (message.toLowerCase().startsWith(s.toLowerCase(), 1)) {
					String type = m.getConfig().getString("chatmute.player_triesCommand.type");
					String text = m.getConfig().getString("chatmute.player_triesCommand.text");
					m.runType(p, type, text);
					e.setCancelled(true);
				}
			}
		}

	}

}
