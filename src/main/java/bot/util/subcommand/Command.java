package bot.util.subcommand;

import static java.util.Arrays.asList;

import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandOption;

import bot.util.apis.APIUtils;
import bot.util.apis.CommandHandlers.SlashCommandHandler;

public abstract class Command implements SlashCommandHandler {
	protected final SubcommandHandler subcommandHandler;

	public Command(final APIUtils apiUtils, final String cmd, final String description) {
		this(apiUtils, false, cmd, description);
	}

	public Command(final APIUtils apiUtils, final boolean refresh, final String cmd, final String description) {
		subcommandHandler = null;

		addHandler(apiUtils, cmd, SlashCommand.with(cmd, description), refresh);
	}

	public Command(final APIUtils apiUtils, final String cmd, final String description,
			final Subcommand... subcommands) {
		this(apiUtils, false, cmd, description, subcommands);
	}

	public Command(final APIUtils apiUtils, final boolean refresh, final String cmd, final String description,
			final Subcommand... subcommands) {
		subcommandHandler = new SubcommandHandler(subcommands);

		addHandler(apiUtils, cmd, SlashCommand.with(cmd, description, subcommandHandler.options()), refresh);
	}

	public Command(final APIUtils apiUtils, final String cmd, final String description,
			final SlashCommandOption... options) {
		this(apiUtils, false, cmd, description, options);
	}

	public Command(final APIUtils apiUtils, final boolean refresh, final String cmd, final String description,
			final SlashCommandOption... options) {
		subcommandHandler = null;

		addHandler(apiUtils, cmd, SlashCommand.with(cmd, description, asList(options)), refresh);
	}

	private void addHandler(final APIUtils apiUtils, final String cmd, final SlashCommandBuilder command,
			final boolean refresh) {
		apiUtils.commandHandlers.addSlashCommandHandler(cmd, this::handle, command, refresh);
	}
}
