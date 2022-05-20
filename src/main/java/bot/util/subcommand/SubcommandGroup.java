package bot.util.subcommand;

import static java.util.Arrays.asList;

import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;

public abstract class SubcommandGroup extends Subcommand {
	protected final SubcommandHandler subcommandHandler;

	@Override
	protected SlashCommandOption makeOption(final String cmd, final String optionDescription,
			final SlashCommandOption... options) {
		return SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND_GROUP, cmd, optionDescription,
				asList(options));
	}

	protected SubcommandGroup(final String cmd, final String optionDescription, final Subcommand... subcommands) {
		this(0, cmd, optionDescription, subcommands);
	}

	protected SubcommandGroup(final int level, final String cmd, final String optionDescription,
			final Subcommand... subcommands) {
		super(level, cmd, optionDescription);
		subcommandHandler = new SubcommandHandler(level + 1, subcommands);
		option = SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND_GROUP, cmd, optionDescription,
				subcommandHandler.options());
	}

	@Override
	public void handle(final SlashCommandInteraction interaction) throws Exception {
		subcommandHandler.handle(interaction);
	}
}
