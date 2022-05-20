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

public class PagedPickerWithConfirmationMessage<T> extends PagedPickerWithDetailsMessage<T> {

	private final Function<T, EmbedBuilder> dataToEmbed;
	private final OnPickHandler<T> onConfirm;

	public PagedPickerWithConfirmationMessage(final List<Page> pages, final String footer,
			final Consumer<MessageComponentInteraction> onRemove, final List<T> data,
			final Function<T, EmbedBuilder> dataToEmbed, final OnPickHandler<T> onConfirm, final int itemsOnPage) {
		super(pages, footer, onRemove, data, dataToEmbed, itemsOnPage);

		this.dataToEmbed = dataToEmbed;
		this.onConfirm = onConfirm;
	}

	private ActionRow makeConfirmationButtons(final int id) {
		return ActionRow.of(Button.create(getActionCustomId() + " pick_confirm " + id, ButtonStyle.SUCCESS, "Pick"),
				makeBackButton());
	}

	@Override
	protected void pick(final MessageComponentInteraction interaction, final int id) {
		interaction.createOriginalMessageUpdater().addEmbed(dataToEmbed.apply(items.get(id)))//
				.addComponents(makeConfirmationButtons(id))//
				.update();
	}

	@Override
	public void handleAction(final MessageComponentInteraction interaction, final String action,
			final String[] tokens) {
		int id;
		switch (action) {
		case "pick_confirm":
			id = Integer.valueOf(tokens[3]);
			onConfirm.handle(interaction, page, items.get(id));
			break;
		default:
			super.handleAction(interaction, action, tokens);
		}
	}

}
