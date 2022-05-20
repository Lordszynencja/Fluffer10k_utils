package bot.util.modularPrompt;

import static bot.util.RandomUtils.getRandomLong;
import static bot.util.TimerUtils.startTimedEvent;
import static bot.util.apis.MessageUtils.sendEphemeralMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.interaction.SlashCommandInteraction;

import bot.util.apis.CommandHandlers;

public class ModularPromptUtils {
	public static final String actionId = "modular_prompt";

	private final Map<String, ModularPrompt> prompts = new HashMap<>();
	private final Map<String, Long> promptOwners = new HashMap<>();

	private long id = getRandomLong(Long.MAX_VALUE);

	public ModularPromptUtils(final CommandHandlers commandHandlers) {
		commandHandlers.addMessageComponentHandler(actionId, this::handleAction);
	}

	public void addMessage(final ModularPrompt prompt, final SlashCommandInteraction interaction) {
		addMessage(prompt, interaction.getUser().getId(), interaction, prompt::send);
	}

	public void addMessage(final ModularPrompt prompt, final MessageComponentInteraction interaction) {
		addMessage(prompt, interaction.getUser().getId(), interaction, prompt::send);
	}

	public void addMessageForEveryone(final ModularPrompt prompt, final SlashCommandInteraction interaction) {
		addMessage(prompt, null, interaction, prompt::send);
	}

	public void addMessageForEveryone(final ModularPrompt prompt, final MessageComponentInteraction interaction) {
		addMessage(prompt, null, interaction, prompt::send);
	}

	private <T> void addMessage(final ModularPrompt prompt, final Long userId, final T interaction,
			final Consumer<T> sender) {
		prompt.sid = id++ + "";
		prompts.put(prompt.sid, prompt);
		if (userId != null) {
			promptOwners.put(prompt.sid, userId);
		}
		sender.accept(interaction);
		startTimedEvent(() -> prompts.remove(prompt.sid), System.currentTimeMillis() + 60 * 60 * 1000);
	}

	private void handleAction(final MessageComponentInteraction interaction) {
		final String[] tokens = interaction.getCustomId().split(" ");
		final String sid = tokens[1];
		final ModularPrompt prompt = prompts.get(sid);
		if (prompt == null) {
			sendEphemeralMessage(interaction, "Message timed out");
			return;
		}

		final long userId = interaction.getUser().getId();
		if (promptOwners.getOrDefault(sid, userId) != userId) {
			sendEphemeralMessage(interaction, "The message you try to interact with is not yours");
			return;
		}

		final String action = tokens[2];
		prompt.handleAction(interaction, action, tokens);
	}
}
