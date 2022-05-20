package bot.util.subcommand;

import static java.util.Arrays.asList;

import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;

import bot.util.apis.CommandHandlers.SlashCommandHandler;

public abstract class Subcommand implements SlashCommandHandler {
	private final int level;
	public final String cmd;
	public SlashCommandOption option;

	protected SlashCommandOption makeOption(final String cmd, final String optionDescription) {
		return SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, cmd, optionDescription);
	}

	protected SlashCommandOption makeOption(final String cmd, final String optionDescription,
			final SlashCommandOption... options) {
		return SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, cmd, optionDescription,
				asList(options));
	}

	protected Subcommand(final String cmd, final String optionDescription) {
		this(0, cmd, optionDescription);
	}

	protected Subcommand(final int level, final String cmd, final String optionDescription) {
		this.level = level;
		this.cmd = cmd;
		option = makeOption(cmd, optionDescription);
	}

	protected Subcommand(final String cmd, final String optionDescription, final SlashCommandOption... options) {
		this(0, cmd, optionDescription, options);
	}

	protected Subcommand(final int level, final String cmd, final String optionDescription,
			final SlashCommandOption... options) {
		this.level = level;
		this.cmd = cmd;
		option = makeOption(cmd, optionDescription, options);
	}

	protected SlashCommandInteractionOption getOption(final SlashCommandInteraction interaction) {
		SlashCommandInteractionOption option = interaction.getOptionByIndex(0).get();
		for (int i = 0; i < level; i++) {
			option = option.getOptionByIndex(0).get();
		}
		return option;
	}

}
