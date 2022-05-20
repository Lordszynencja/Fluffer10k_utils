package bot.util.pages.messages;

import static bot.util.CollectionUtils.toArray;

import java.util.List;
import java.util.function.Consumer;

import org.javacord.api.entity.message.Messageable;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.ButtonStyle;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.interaction.SlashCommandInteraction;

import bot.util.pages.Page;

public class PagedMessage {
	private String sid;

	protected final List<Page> pages;
	protected final String footer;
	protected int page = 0;

	private final Consumer<MessageComponentInteraction> onRemove;

	public PagedMessage(final List<Page> pages, final String footer,
			final Consumer<MessageComponentInteraction> onRemove) {
		this.pages = pages;
		this.footer = footer;
		this.onRemove = onRemove;
	}

	protected String getActionCustomId() {
		return "paged " + sid;
	}

	protected ActionRow[] createActions() {
		if (pages.size() == 1) {
			return toArray(ActionRow.of(Button.create(getActionCustomId() + " remove", ButtonStyle.DANGER, "Remove")));
		}

		if (page == 0) {
			return toArray(ActionRow.of(Button.create(getActionCustomId() + " next", ButtonStyle.PRIMARY, "Next"), //
					Button.create(getActionCustomId() + " remove", ButtonStyle.DANGER, "Remove")));
		}
		if (page == pages.size() - 1) {
			return toArray(
					ActionRow.of(Button.create(getActionCustomId() + " previous", ButtonStyle.PRIMARY, "Previous"), //
							Button.create(getActionCustomId() + " remove", ButtonStyle.DANGER, "Remove")));
		}

		return toArray(ActionRow.of(Button.create(getActionCustomId() + " previous", ButtonStyle.PRIMARY, "Previous"), //
				Button.create(getActionCustomId() + " next", ButtonStyle.PRIMARY, "Next"), //
				Button.create(getActionCustomId() + " remove", ButtonStyle.DANGER, "Remove")));
	}

	public EmbedBuilder makeEmbed() {
		return pages.get(page).toEmbed()//
				.setFooter(String.format(footer, page + 1, pages.size()));
	}

	public void sendCurrentPageFirstTime(final SlashCommandInteraction interaction) {
		interaction.createImmediateResponder().addEmbed(makeEmbed()).addComponents(createActions()).respond();
	}

	public void sendCurrentPageFirstTime(final Messageable messageable) {
		messageable.sendMessage(makeEmbed(), createActions());
	}

	public void sendCurrentPageFirstTime(final MessageComponentInteraction interaction) {
		sendCurrentPage(interaction);
	}

	public void sendCurrentPage(final MessageComponentInteraction interaction) {
		interaction.createOriginalMessageUpdater().addEmbed(makeEmbed()).addComponents(createActions()).update();
	}

	public void sendNextPage(final MessageComponentInteraction interaction) {
		if (page < pages.size() - 1) {
			page++;
		}

		sendCurrentPage(interaction);
	}

	public void sendPreviousPage(final MessageComponentInteraction interaction) {
		if (page > 0) {
			page--;
		}

		sendCurrentPage(interaction);
	}

	public void setSid(final String sid) {
		this.sid = sid;
	}

	public PagedMessage setPage(final int newPage) {
		page = newPage;
		if (page < 0) {
			page = 0;
		}
		if (page >= pages.size()) {
			page = pages.size() - 1;
		}

		return this;
	}

	public void handleAction(final MessageComponentInteraction interaction, final String action,
			final String[] tokens) {
		switch (action) {
		case "next":
			sendNextPage(interaction);
			break;
		case "previous":
			sendPreviousPage(interaction);
			break;
		case "remove":
			onRemove.accept(interaction);
			break;
		}
	}
}
