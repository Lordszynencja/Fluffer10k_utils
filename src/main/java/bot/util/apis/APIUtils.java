package bot.util.apis;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class APIUtils {
	public static boolean isTestServer(final long serverId) {
		return serverId == 883720051142836224L;
	}

	public static boolean isTestServer(final Server s) {
		return s != null && isTestServer(s.getId());
	}

	public final String botName;
	public final Config config;
	public final DiscordApi api;
	public final CommandHandlers commandHandlers;
	public final MessageUtils messageUtils;
	public InitUtils initUtils = new InitUtils();

	public APIUtils(final String botName, final String inviteLink, final String configFileName,
			final List<Intent> privilegedIntentsWanted) throws IOException {
		this.botName = botName;
		config = new Config(configFileName);

		api = new DiscordApiBuilder()//
				.setToken(config.getString("token"))//
				.setAllNonPrivilegedIntents()//
				.setAllIntentsWhere(intent -> !intent.isPrivileged() || privilegedIntentsWanted.contains(intent))//
				.login()//
				.join();

		api.setMessageCacheSize(50, 3600);
		commandHandlers = new CommandHandlers(this, inviteLink);
		messageUtils = new MessageUtils(this);
	}

	public Set<String> getCommands() {
		return api.getGlobalSlashCommands().join().stream().map(cmd -> cmd.getName()).collect(Collectors.toSet());
	}

	public KnownCustomEmoji getEmojiByNameFromMyServer(final String emojiName) {
		for (final KnownCustomEmoji emoji : api.getServerById(883720051142836224L).get()
				.getCustomEmojisByName(emojiName)) {
			return emoji;
		}

		return null;
	}

	public String getEmojiStringByNameFromMyServer(final String emojiName) {
		for (final KnownCustomEmoji emoji : api.getServerById(883720051142836224L).get()
				.getCustomEmojisByName(emojiName)) {
			return emoji.getMentionTag();
		}

		return null;
	}

	public void endInit() throws IOException {
		initUtils.init();
		initUtils = null;
		commandHandlers.addSlashCommands();

		api.addSlashCommandCreateListener(commandHandlers::handleSlashCommand);
		api.addMessageComponentCreateListener(commandHandlers::handleMessageComponent);

		final List<String> cmds = commandHandlers.getCmdList();
		System.out.println(botName + " started, cmd count: " + cmds.size() + "\ncmds:" + cmds);
		messageUtils.sendMessageToMe(botName + " started");
	}

	public boolean isServerOk(final long serverId) {
		return isTestServer(serverId) == config.getBoolean("debug");
	}

	public boolean isServerOk(final Server server) {
		return isTestServer(server) == config.getBoolean("debug");
	}

	public User getUser(final long userId) {
		return api.getCachedUserById(userId).orElseGet(() -> api.getUserById(userId).join());
	}
}
