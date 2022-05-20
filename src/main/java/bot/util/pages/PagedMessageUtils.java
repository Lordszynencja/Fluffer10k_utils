package bot.util.pages;

import static bot.util.RandomUtils.getRandomLong;
import static bot.util.TimerUtils.startTimedEvent;
import static bot.util.apis.MessageUtils.sendEphemeralMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.javacord.api.entity.message.Messageable;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.interaction.SlashCommandInteraction;

import bot.util.apis.CommandHandlers;
import bot.util.pages.messages.PagedMessage;

public class PagedMessageUtils {
	private final Map<String, PagedMessage> messages = new HashMap<>();
	private final Map<String, Long> messageOwners = new HashMap<>();

	private long id = getRandomLong(Long.MAX_VALUE);

	public PagedMessageUtils(final CommandHandlers commandHandlers) {
		commandHandlers.addMessageComponentHandler("paged", this::handleAction);
	}

	public void addMessage(final PagedMessage message, final SlashCommandInteraction interaction) {
		addMessage(message, interaction.getUser().getId(), interaction, message::sendCurrentPageFirstTime);
	}

	public void addMessage(final PagedMessage message, final MessageComponentInteraction interaction) {
		addMessage(message, interaction.getUser().getId(), interaction, message::sendCurrentPageFirstTime);
	}

	public void addMessage(final PagedMessage message, final long userId, final Messageable messageable) {
		addMessage(message, userId, messageable, message::sendCurrentPageFirstTime);
	}

	private <T> void addMessage(final PagedMessage message, final long userId, final T interaction,
			final Consumer<T> firstPageSender) {
		final String sid = id + "";
		id++;
		message.setSid(sid);
		messages.put(sid, message);
		messageOwners.put(sid, userId);
		firstPageSender.accept(interaction);
		startTimedEvent(() -> messages.remove(sid), System.currentTimeMillis() + 300 * 60 * 1000);
	}

	private void handleAction(final MessageComponentInteraction interaction) {
		final String[] tokens = interaction.getCustomId().split(" ");
		final String sid = tokens[1];
		final PagedMessage message = messages.get(sid);
		if (message == null) {
			sendEphemeralMessage(interaction, "Message timed out");
			return;
		}
		if (messageOwners.get(sid) != interaction.getUser().getId()) {
			sendEphemeralMessage(interaction, "The message you try to interact with is not yours");
			return;
		}

		final String action = tokens[2];
		message.handleAction(interaction, action, tokens);
	}
}
