package bot.util.modularPrompt;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.LowLevelComponent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.interaction.SlashCommandInteraction;

public class ModularPrompt {
	public static ModularPrompt prompt(final EmbedBuilder embed, final ModularPromptButton... buttons) {
		return new ModularPrompt(embed, buttons);
	}

	public String sid;
	private final EmbedBuilder embed;
	private final List<ModularPromptButton> buttons;

	public ModularPrompt(final EmbedBuilder embed, final ModularPromptButton... buttons) {
		this.embed = embed;
		this.buttons = new ArrayList<>(asList(buttons));
	}

	public ModularPrompt addButton(final ModularPromptButton button) {
		buttons.add(button);
		return this;
	}

	private ActionRow[] getActions() {
		final List<ActionRow> rows = new ArrayList<>();
		List<LowLevelComponent> rowButtons = new ArrayList<>();

		int buttonId = 0;
		for (final ModularPromptButton button : buttons) {
			if (rowButtons.size() >= 5 || (button == null && !rowButtons.isEmpty())) {
				rows.add(ActionRow.of(rowButtons));
				rowButtons = new ArrayList<>();
			}
			if (button != null) {
				rowButtons.add(button.build(ModularPromptUtils.actionId + " " + sid + " " + buttonId));
			}
			buttonId++;
		}
		if (!rowButtons.isEmpty()) {
			rows.add(ActionRow.of(rowButtons));
		}

		return rows.toArray(new ActionRow[0]);
	}

	public void send(final SlashCommandInteraction interaction) {
		interaction.createImmediateResponder().addEmbed(embed).addComponents(getActions()).respond();
	}

	public void send(final MessageComponentInteraction interaction) {
		interaction.createOriginalMessageUpdater().addEmbed(embed).addComponents(getActions()).update();
	}

	public void handleAction(final MessageComponentInteraction interaction, final String action,
			final String[] tokens) {
		buttons.get(Integer.valueOf(action)).click(interaction);
	}
}
