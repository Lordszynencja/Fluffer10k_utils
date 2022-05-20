package bot.util.pages.messages;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.ButtonStyle;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.MessageComponentInteraction;

import bot.util.pages.Page;

public class PagedPickerWithDetailsMessage<T> extends PagedPickerMessage<T> {
	private final Function<T, EmbedBuilder> dataToEmbed;

	public PagedPickerWithDetailsMessage(final List<Page> pages, final String footer,
			final Consumer<MessageComponentInteraction> onRemove, final List<T> data,
			final Function<T, EmbedBuilder> dataToEmbed, final int itemsOnPage) {
		super(pages, footer, onRemove, data, null, itemsOnPage);

		this.dataToEmbed = dataToEmbed;
	}

	protected Button makeBackButton() {
		return Button.create(getActionCustomId() + " back", ButtonStyle.DANGER, "Back");
	}

	@Override
	protected void pick(final MessageComponentInteraction interaction, final int id) {
		interaction.createOriginalMessageUpdater()//
				.addEmbed(dataToEmbed.apply(items.get(id)))//
				.addComponents(ActionRow.of(makeBackButton()))//
				.update();
	}

	@Override
	public void handleAction(final MessageComponentInteraction interaction, final String action,
			final String[] tokens) {
		switch (action) {
		case "back":
			sendCurrentPage(interaction);
			break;
		default:
			super.handleAction(interaction, action, tokens);
		}
	}

}
