package ml.optidevs.bukkit.chat;

public interface Perm {

	public static final String ALL = "Opti.Chat.*";

	public enum BYPASS {
		;

		// Bypass All modules
		public static final String ALL = "Opti.Chat.Bypass.*";

		// Bypass Anti-Ad
		public static final String AD = "Opti.Chat.Bypass.AD";

		// Bypass Anti-Swear
		public static final String SWEAR = "Opti.Chat.Bypass.SWEAR";

		// Bypass Anti-Caps
		public static final String CAPS = "Opti.Chat.Bypass.CAPS";

		// Bypass Mute Chat
		public static final String MUTE = "Opti.Chat.Bypass.MUTE";

		// Bypass Clear Chat (Not Used yet)
		public final String CLEAR = "Opti.Chat.Bypass.CLEARCHAT";
	}

	public enum ADMIN {
		;

		// Access to All modules
		public static final String ALL = "Opti.Chat.Admin.*";

		// Reload Plugin
		public static final String RELOAD = "Opti.Chat.Admin.RELOAD";

		// Mute Chat
		public static final String MUTECHAT = "Opti.Chat.Admin.MUTECHAT";

		// Un-mute Chat
		public static final String UNMUTECHAT = "Opti.Chat.Admin.UNMUTECHAT";

		// Clear Chat
		public static final String CLEARCHAT = "Opti.Chat.Admin.CLEARCHAT";

		// Clear Chat Silent
		public static final String CLEARCHAT_SILENT = "Opti.Chat.Admin.CLEARCHAT.SILENT";

	}

	public enum NOTIFY {
		;

		// NOTIFY All modules
		public static final String ALL = "Opti.Chat.NOTIFY.*";

		// NOTIFY Anti-Ad
		public static final String AD = "Opti.Chat.NOTIFY.AD";

		// NOTIFY Anti-Swear
		public static final String SWEAR = "Opti.Chat.NOTIFY.SWEAR";

		// NOTIFY Anti-Caps
		public static final String CAPS = "Opti.Chat.NOTIFY.CAPS";

		// NOTIFY Mute Chat
		public static final String MUTE = "Opti.Chat.NOTIFY.MUTE";

		// NOTIFY Clear Chat (Not Used yet)
		public final String CLEAR = "Opti.Chat.NOTIFY.CLEARCHAT";
	}
}
