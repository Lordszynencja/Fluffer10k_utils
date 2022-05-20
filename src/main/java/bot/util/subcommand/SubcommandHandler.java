package bot.util.subcommand;

import static bot.util.CollectionUtils.mapToList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;

import bot.util.apis.CommandHandlers.SlashCommandHandler;

public class SubcommandHandler implements SlashCommandHandler {

	private final int level;
	private final Map<String, Subcommand> subcommands = new HashMap<>();

	public SubcommandHandler(final Subcommand... subcommands) {
		this(0, subcommands);
	}

	public SubcommandHandler(final int level, final Subcommand... subcommands) {
		this.level = level;
		for (final Subcommand subcommand : subcommands) {
			this.subcommands.put(subcommand.cmd, subcommand);
		}
	}

	public List<SlashCommandOption> options() {
		return mapToList(subcommands.values(), subcommand -> subcommand.option);
	}

	@Override
	public void handle(final SlashCommandInteraction interaction) throws Exception {
		SlashCommandInteractionOption option = interaction.getOptionByIndex(0).get();
		for (int i = 0; i < level; i++) {
			option = option.getOptionByIndex(0).get();
		}
		subcommands.get(option.getName()).handle(interaction);
	}
}
