package bot.util.apis;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.entity.channel.PrivateChannel;
import org.javacord.api.entity.channel.RegularServerChannel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.InteractionBase;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

public class MessageUtils {
	private static final String mentionRegex = "<@[!&]?[0-9]+>";
	private static final String mentionLookaheadPattern = "(?=" + mentionRegex + ")";
	private static final Pattern mentionPattern = Pattern.compile("<@[!&]?[0-9]+>");
	private static final Pattern userMentionPattern = Pattern.compile("<@[!]?[0-9]+>");

	public static final long myId = 289105579077664768L;

	private final APIUtils apiUtils;

	public MessageUtils(final APIUtils apiUtils) {
		this.apiUtils = apiUtils;
	}

	public ServerTextChannel getChannelById(final long channelId) {
		return apiUtils.api.getChannelById(channelId).map(c -> c.asServerTextChannel().orElse(null)).orElse(null);
	}

	public Message getMessageById(final TextChannel channel, final long messageId) {
		try {
			return apiUtils.api.getMessageById(messageId, channel).get();
		} catch (final Exception e) {
			return null;
		}
	}

	public CompletableFuture<Message> sendMessageOnServerChannel(final long channelId, final MessageBuilder msg) {
		final ServerTextChannel channel = getChannelById(channelId);
		if (channel == null) {
			return null;
		}

		return msg.send(channel);
	}

	public static List<String> splitLongMessage(String s) {
		final List<String> parts = new ArrayList<>();
		while (s.length() > 2000) {
			int pos = s.substring(0, 2000).lastIndexOf('\n');
			if (pos == -1) {
				pos = 2000;
			}
			parts.add(s.substring(0, pos));
			s = s.substring(pos);
		}
		parts.add(s);

		return parts;
	}

	public void sendMessageToUser(final long userId, final String msg) {
		final User user = apiUtils.api.getUserById(userId).join();
		final PrivateChannel userChannel = user.getPrivateChannel().orElseGet(() -> user.openPrivateChannel().join());

		for (final String msgPart : splitLongMessage(msg)) {
			userChannel.sendMessage(msgPart).join();
		}
	}

	public static void sendMessageToUser(final User user, final String msg) {
		final PrivateChannel userChannel = user.getPrivateChannel().orElseGet(() -> user.openPrivateChannel().join());
		userChannel.sendMessage(msg).join();
	}

	public void sendMessageToMe(final String msg) {
		sendMessageToUser(myId, msg);
	}

	private static String getFullStackTrace(final Exception e) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);

		return sw.toString();
	}

	public void sendExceptionToMe(final Exception e) {
		try {
			e.printStackTrace();
			sendMessageToMe(getFullStackTrace(e));
		} catch (final Exception e2) {
			e2.printStackTrace();
		}
	}

	public void sendExceptionToMe(final String msg, final Exception e) {
		final String exceptionMsg = msg + "\n" + getFullStackTrace(e);
		System.err.println(exceptionMsg);
		try {
			sendMessageToMe(exceptionMsg);
		} catch (final Exception e2) {
			e2.printStackTrace();
		}
	}

	public static CompletableFuture<InteractionOriginalResponseUpdater> sendEphemeralMessage(
			final SlashCommandCreateEvent e, final String msg) {
		return sendEphemeralMessage(e.getInteraction(), msg);
	}

	public static CompletableFuture<InteractionOriginalResponseUpdater> sendEphemeralMessage(
			final InteractionBase interaction, final String msg) {
		return interaction.createImmediateResponder().append(msg).setFlags(MessageFlag.EPHEMERAL).respond();
	}

	public static List<String> getMentions(final String msg) {
		final List<String> mentions = new ArrayList<>();
		if (msg == null || msg.length() == 0) {
			return mentions;
		}

		final Matcher matcher = mentionPattern.matcher(msg);
		while (matcher.find()) {
			mentions.add(matcher.group());
		}

		return mentions;
	}

	public static List<String> getUserMentions(final String msg) {
		final List<String> mentions = new ArrayList<>();
		if (msg == null || msg.length() == 0) {
			return mentions;
		}

		final Matcher matcher = userMentionPattern.matcher(msg);
		while (matcher.find()) {
			mentions.add(matcher.group());
		}

		return mentions;
	}

	private String getUserNameForMention(final String mention, final Server server) {
		String id = mention.substring(2, mention.length() - 1);

		if (id.startsWith("&")) {// role
			id = id.substring(1);
			final Optional<Role> role = server.getRoleById(id);
			if (role.isPresent()) {
				return role.get().getName();
			}
			return "unknown role";
		}
		if (id.startsWith("!")) {
			id = id.substring(1);
		}

		final User user = apiUtils.api.getUserById(id).join();
		return APIUtils.getUserName(user, server);
	}

	public String replaceMentionsWithUserNames(final String msg, final Server server) {
		if (msg == null || msg.length() == 0) {
			return msg;
		}

		final String[] parts = msg.split(mentionLookaheadPattern);
		if (parts[0].matches(mentionRegex + ".*")) {
			parts[0] = getUserNameForMention(parts[0].substring(0, parts[0].indexOf('>') + 1), server)
					+ parts[0].substring(parts[0].indexOf('>') + 1);
		}
		for (int i = 1; i < parts.length; i++) {
			final int pos = parts[i].indexOf('>');
			parts[i] = getUserNameForMention(parts[i].substring(0, pos + 1), server) + parts[i].substring(pos + 1);
		}

		return String.join("", parts);
	}

	public static List<Long> getMentionIds(final String msg) {
		return getMentions(msg).stream().map(m -> Long.valueOf(m.replaceAll("[<>@!&]", "")))
				.collect(Collectors.toList());
	}

	public static List<Long> getUserMentionIds(final String msg) {
		return getUserMentions(msg).stream().map(m -> Long.valueOf(m.replaceAll("[<>@!]", "")))
				.collect(Collectors.toList());
	}

	public static TextChannel getServerTextChannel(final InteractionBase interaction) {
		if (!interaction.getChannel().isPresent()) {
			return null;
		}

		final TextChannel channel = interaction.getChannel().get();
		if (channel.getType() == ChannelType.SERVER_PUBLIC_THREAD
				|| channel.getType() == ChannelType.SERVER_TEXT_CHANNEL) {
			return channel;
		}

		return null;
	}

	public static boolean isServerTextChannel(final InteractionBase interaction) {
		return getServerTextChannel(interaction) != null;
	}

	public static Server getServer(final TextChannel channel) {
		return channel.asServerChannel().map(c -> c.getServer()).orElse(null);
	}

	public static boolean isNSFWChannel(final InteractionBase interaction) {
		final TextChannel channel = getServerTextChannel(interaction);
		if (channel == null) {
			return false;
		}

		if (channel.getType() == ChannelType.SERVER_TEXT_CHANNEL) {
			return channel.asServerTextChannel().get().isNsfw();
		} else if (channel.getType() == ChannelType.SERVER_PUBLIC_THREAD) {
			final RegularServerChannel parentChannel = channel.asServerThreadChannel().get().getParent();
			return parentChannel.getType() == ChannelType.SERVER_TEXT_CHANNEL
					&& parentChannel.asServerTextChannel().get().isNsfw();
		}
		return false;
	}

	public Message getMessageByIds(final long channelId, final long messageId) {
		final TextChannel channel = apiUtils.api.getTextChannelById(channelId).get();
		return apiUtils.api.getMessageById(messageId, channel).join();
	}
}
