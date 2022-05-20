package bot.util.pages.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.MessageComponentInteraction;

import bot.util.EmbedUtils.EmbedField;
import bot.util.pages.Page;
import bot.util.pages.messages.PagedMessage;
import bot.util.pages.messages.PagedPickerMessage.OnPickHandler;
import bot.util.pages.messages.PagedPickerWithConfirmationMessage;

public class PagedPickerWithConfirmationMessageBuilder<T> {
	private static void defaultOnRemove(final MessageComponentInteraction interaction) {
		interaction.acknowledge();
		interaction.getMessage().delete();
	}

	protected String title;
	protected String description = null;
	protected String imgUrl = null;
	protected String footer = "page %1$s of %2$s";

	protected List<EmbedField> fields = new ArrayList<>();
	protected List<T> items = new ArrayList<>();

	protected int page;

	protected Consumer<MessageComponentInteraction> onRemove = PagedPickerWithConfirmationMessageBuilder::defaultOnRemove;
	protected Function<T, EmbedBuilder> dataToEmbed = null;
	protected OnPickHandler<T> onConfirm = null;

	protected final int fieldsPerPage;

	protected PagedPickerWithConfirmationMessageBuilder(final PagedPickerWithConfirmationMessageBuilder<T> builder) {
		this.title = builder.title;
		description = builder.description;
		imgUrl = builder.imgUrl;
		footer = builder.footer;

		fields = builder.fields;
		items = builder.items;

		onRemove = builder.onRemove;
		dataToEmbed = builder.dataToEmbed;
		onConfirm = builder.onConfirm;

		this.fieldsPerPage = builder.fieldsPerPage;
	}

	public PagedPickerWithConfirmationMessageBuilder(final String title, final int fieldsPerPage) {
		this.title = title;
		this.fieldsPerPage = fieldsPerPage;
	}

	public PagedPickerWithConfirmationMessageBuilder(final String title, final int fieldsPerPage,
			final List<EmbedField> fields) {
		this.title = title;
		this.fieldsPerPage = fieldsPerPage;
		this.fields = fields;
	}

	public PagedPickerWithConfirmationMessageBuilder(final String title, final int fieldsPerPage,
			final List<EmbedField> fields, final List<T> items) {
		this.title = title;
		this.fieldsPerPage = fieldsPerPage;
		this.fields = fields;
		this.items = items;
	}

	public PagedPickerWithConfirmationMessageBuilder<T> title(final String x) {
		title = x;
		return this;
	}

	public PagedPickerWithConfirmationMessageBuilder<T> description(final String x) {
		description = x;
		return this;
	}

	public PagedPickerWithConfirmationMessageBuilder<T> imgUrl(final String x) {
		imgUrl = x;
		return this;
	}

	public PagedPickerWithConfirmationMessageBuilder<T> footer(final String x) {
		footer = x;
		return this;
	}

	public PagedPickerWithConfirmationMessageBuilder<T> fields(final List<EmbedField> x) {
		fields = x;
		return this;
	}

	public PagedPickerWithConfirmationMessageBuilder<T> addField(final EmbedField x) {
		fields.add(x);
		return this;
	}

	public PagedPickerWithConfirmationMessageBuilder<T> items(final List<T> x) {
		items = x;
		return this;
	}

	public PagedPickerWithConfirmationMessageBuilder<T> page(final int x) {
		page = x;
		return this;
	}

	public PagedPickerWithConfirmationMessageBuilder<T> onRemove(final Consumer<MessageComponentInteraction> x) {
		onRemove = x;
		return this;
	}

	public PagedPickerWithConfirmationMessageBuilder<T> dataToEmbed(final Function<T, EmbedBuilder> x) {
		dataToEmbed = x;
		return this;
	}

	public final PagedPickerWithConfirmationMessageBuilder<T> onConfirm(
			final BiConsumer<MessageComponentInteraction, T> x) {
		return this.onConfirm((in, page, item) -> x.accept(in, item));
	}

	public final PagedPickerWithConfirmationMessageBuilder<T> onConfirm(final OnPickHandler<T> x) {
		onConfirm = x;
		return new PagedPickerWithConfirmationMessageBuilder<T>(this);
	}

	protected List<Page> splitToPages() {
		final List<Page> pages = new ArrayList<>(fields.size() / fieldsPerPage + 1);
		int count = 0;
		List<EmbedField> pageFields = new ArrayList<>(fieldsPerPage);
		for (final EmbedField field : fields) {
			pageFields.add(new EmbedField((count + 1) + ". " + field.name, field.value, field.inline));
			count++;
			if (count == fieldsPerPage) {
				pages.add(new Page(title, description, pageFields, imgUrl));
				pageFields = new ArrayList<>(fieldsPerPage);
				count = 0;
			}
		}
		if (!pageFields.isEmpty()) {
			pages.add(new Page(title, description, pageFields, imgUrl));
		}
		if (pages.isEmpty()) {
			pages.add(new Page(title, description, new ArrayList<>(), imgUrl));
		}

		return pages;
	}

	public PagedMessage build() {
		final PagedMessage msg = new PagedPickerWithConfirmationMessage<>(splitToPages(), footer, onRemove, items,
				dataToEmbed, onConfirm, fieldsPerPage);
		msg.setPage(page);
		return msg;
	}
}
