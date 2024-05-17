package bot.util.apis;

import static bot.util.apis.MessageUtils.sendEphemeralMessage;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;

import bot.util.TimerUtils;

public class CommandHandlers {
	public static interface ExitHandler {
		void handle() throws Exception;
	}

	public static interface SlashCommandHandler {
		void handle(SlashCommandInteraction interaction) throws Exception;
	}

	public static interface MessageComponentHandler {
		void handle(MessageComponentInteraction interaction) throws Exception;
	}

	private static final List<ExitHandler> exitHandlers = new ArrayList<>();

	private final String inviteLink;
	private final APIUtils apiUtils;

	private final Map<Long, Map<String, SlashCommandHandler>> serverSlashCommandHandlers = new HashMap<>();
	private final Map<String, SlashCommandHandler> slashCommandHandlers = new HashMap<>();
	private final Map<String, MessageComponentHandler> messageComponentHandlers = new HashMap<>();
	private final Map<String, String> commandAliases = new HashMap<>();

	private final Map<String, SlashCommandBuilder> slashCommandBuilders = new HashMap<>();
	private Set<String> addedCommands = new HashSet<>();
	private final Set<String> wantedCommands = new HashSet<>();
	private final Set<String> refreshedCommands = new HashSet<>();

	public static String exitMessage = null;

	public static void addOnExit(final ExitHandler handler) {
		exitHandlers.add(handler);
	}

	private static void handleExit(final SlashCommandInteraction interaction) throws Exception {
		if (interaction.getUser().getId() != MessageUtils.myId) {
			sendEphemeralMessage(interaction, "You can't use this command");
			return;
		}

		exitMessage = interaction.getArgumentStringValueByName("msg").orElse("bug fixes");
		sendEphemeralMessage(interaction, "Exiting, " + exitHandlers.size() + " exit handlers to run").join();
		for (final ExitHandler handler : exitHandlers) {
			handler.handle();
		}
		TimerUtils.onExit();

		System.out.println("Stopping the bot");
		System.exit(0);
	}

	private void handleInviteLink(final SlashCommandInteraction interaction) throws Exception {
		sendEphemeralMessage(interaction, String.format("Invite link for the %s is: %s", apiUtils.botName, inviteLink))
				.join();
	}

	public CommandHandlers(final APIUtils apiUtils, final String inviteLink) {
		this.inviteLink = inviteLink;
		this.apiUtils = apiUtils;
		addedCommands = apiUtils.getCommands();

		addSlashCommandHandler("exit", CommandHandlers::handleExit,
				SlashCommand.with("exit", "exit the bot (for bot owner only)", //
						asList(SlashCommandOption.create(SlashCommandOptionType.STRING, "msg", "message", true))));
		if (inviteLink != null) {
			addSlashCommandHandler("invite_link", this::handleInviteLink,
					SlashCommand.with("invite_link", "Get invite link for the bot"));
		}
		addMessageComponentHandler("do_nothing", interaction -> interaction.acknowledge());
	}

	public void addSlashCommandHandler(final String cmd, final SlashCommandHandler handler,
			final SlashCommandBuilder slashCommand) {
		if (slashCommandHandlers.get(cmd) != null) {
			throw new RuntimeException("Command " + cmd + " already exists!");
		}

		commandAliases.put(cmd, cmd);
		slashCommandHandlers.put(cmd, handler);
		wantedCommands.add(cmd);
		slashCommandBuilders.put(cmd, slashCommand);
	}

	public void addServerSlashCommandHandler(final long serverId, final String cmd, final SlashCommandHandler handler) {
		Map<String, SlashCommandHandler> serverCommands = serverSlashCommandHandlers.get(serverId);
		if (serverCommands == null) {
			serverCommands = new HashMap<>();
			serverSlashCommandHandlers.put(serverId, serverCommands);
		}

		if (serverCommands.get(cmd) != null) {
			throw new RuntimeException("Command " + cmd + " already exists for server " + serverId + "!");
		}

		serverCommands.put(cmd, handler);
	}

	public void addSlashCommandHandler(final String cmd, final SlashCommandHandler handler,
			final SlashCommandBuilder slashCommand, final boolean update) {
		addSlashCommandHandler(cmd, handler, slashCommand);
		if (update) {
			addRefreshedCommand(cmd);
		}
	}

	public void addMessageComponentHandler(final String cmd, final MessageComponentHandler handler) {
		if (messageComponentHandlers.get(cmd) != null) {
			throw new RuntimeException("Command " + cmd + " already exists!");
		}

		messageComponentHandlers.put(cmd, handler);
	}

	public void addCommandAlias(final String cmd, final String alias, final SlashCommandBuilder slashCommand) {
		commandAliases.put(alias, cmd);
		wantedCommands.add(alias);
		slashCommandBuilders.put(alias, slashCommand);
	}

	public void addRefreshedCommand(final String cmd) {
		refreshedCommands.add(cmd);
	}

	private void addCommand(final String cmd) {
		addedCommands.add(cmd);
		slashCommandBuilders.get(cmd).createGlobal(apiUtils.api).join();
	}

	public void addSlashCommands() {
		for (final String cmd : wantedCommands) {
			if (!addedCommands.contains(cmd) || refreshedCommands.contains(cmd)) {
				addCommand(cmd);
			}
		}

		apiUtils.api.getGlobalSlashCommands().join().forEach(cmd -> {
			final String name = cmd.getName();
			if (!wantedCommands.contains(name)) {
				cmd.delete();
				System.out.println("Removed " + name);
				addedCommands.remove(name);
			}
		});
	}

	private String getOptionString(final SlashCommandInteractionOption option) {
		return option.getName() + (option.isSubcommandOrGroup() ? //
				(" " + option.getOptions().stream().map(this::getOptionString).collect(joining(" ")))//
				: (": " + option.getStringRepresentationValue().get()));
	}

	private String getAllOptionsString(final SlashCommandInteraction interaction) {
		return "/" + interaction.getCommandName() + " "
				+ interaction.getOptions().stream().map(this::getOptionString).collect(joining(" "));
	}

	public void handleSlashCommand(final SlashCommandCreateEvent e) {
		final SlashCommandInteraction interaction = e.getSlashCommandInteraction();
		try {
			final String cmd = interaction.getCommandName();

			final Server server = e.getInteraction().getServer().orElse(null);
			if (!apiUtils.isServerOk(server)) {
				return;
			}

			if (server != null) {
				final Map<String, SlashCommandHandler> serverCommands = serverSlashCommandHandlers.get(server.getId());
				if (serverCommands != null) {
					final SlashCommandHandler serverCommandHandler = serverCommands.get(cmd);
					if (serverCommandHandler != null) {
						serverCommandHandler.handle(interaction);
						return;
					}
				}
			}

			slashCommandHandlers.get(commandAliases.get(cmd)).handle(interaction);
		} catch (final Exception ex) {
			apiUtils.messageUtils.sendExceptionToMe(getAllOptionsString(interaction), ex);
		}
	}

	public void handleMessageComponent(final MessageComponentCreateEvent e) {
		try {
			final MessageComponentInteraction interaction = e.getMessageComponentInteraction();

			if (!apiUtils.isServerOk(interaction.getServer().orElse(null))) {
				return;
			}

			final String cmd = interaction.getCustomId().split(" ")[0];
			messageComponentHandlers.get(cmd).handle(interaction);
		} catch (final Exception ex) {
			apiUtils.messageUtils
					.sendExceptionToMe("message component: " + e.getMessageComponentInteraction().getCustomId(), ex);
		}
	}

	public List<String> getCmdList() {
		return addedCommands.stream().sorted().collect(toList());
	}

}
