package bot.util.pages.messages;

import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.javacord.api.entity.message.Messageable;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.ButtonStyle;
import org.javacord.api.entity.message.component.LowLevelComponent;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.interaction.SlashCommandInteraction;

import bot.util.pages.Page;

public class PagedPickerMessage<T> extends PagedMessage {
	public interface OnPickHandler<T> {
		void handle(MessageComponentInteraction interaction, int page, T item);
	}

	protected final List<T> items;
	private final OnPickHandler<T> onPick;
	private final int itemsOnPage;

	public PagedPickerMessage(final List<Page> pages, final String footer,
			final Consumer<MessageComponentInteraction> onRemove, final List<T> items, final OnPickHandler<T> onPick,
			final int itemsOnPage) {
		super(pages, footer, onRemove);
		this.items = items;
		this.onPick = onPick;
		this.itemsOnPage = itemsOnPage;
	}

	@Override
	protected ActionRow[] createActions() {
		final List<ActionRow> rows = new ArrayList<>();

		if (!items.isEmpty()) {
			List<LowLevelComponent> buttons = new ArrayList<>();
			final int minId = page * itemsOnPage;
			final int maxId = min(minId + itemsOnPage, items.size());
			int rowCount = 0;
			final int maxCount = (itemsOnPage) / ((itemsOnPage + 4) / 5);
			for (int id = minId; id < maxId; id++) {
				rowCount++;
				if (rowCount > maxCount) {
					rows.add(ActionRow.of(buttons));
					buttons = new ArrayList<>();
					rowCount = 0;
				}
				buttons.add(Button.create(getActionCustomId() + " pick " + id, ButtonStyle.SECONDARY,
						"" + (1 + id - minId)));
			}
			if (buttons.size() > 0) {
				rows.add(ActionRow.of(buttons));
			}
		}

		for (final ActionRow row : super.createActions()) {
			rows.add(row);
		}

		return rows.toArray(new ActionRow[rows.size()]);
	}

	@Override
	public void sendCurrentPageFirstTime(final SlashCommandInteraction interaction) {
		interaction.createImmediateResponder()//
				.addEmbed(makeEmbed())//
				.addComponents(createActions())//
				.respond();
	}

	@Override
	public void sendCurrentPageFirstTime(final Messageable messageable) {
		messageable.sendMessage(makeEmbed(), createActions());
	}

	@Override
	public void sendCurrentPage(final MessageComponentInteraction interaction) {
		interaction.createOriginalMessageUpdater()//
				.addEmbed(makeEmbed())//
				.addComponents(createActions())//
				.update();
	}

	protected void pick(final MessageComponentInteraction interaction, final int id) {
		onPick.handle(interaction, page, items.get(id));
	}

	@Override
	public void handleAction(final MessageComponentInteraction interaction, final String action,
			final String[] tokens) {
		switch (action) {
		case "pick":
			pick(interaction, Integer.valueOf(tokens[3]));
			break;
		default:
			super.handleAction(interaction, action, tokens);
		}
	}
}
