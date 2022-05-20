package bot.util.pages.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.javacord.api.interaction.MessageComponentInteraction;

import bot.util.EmbedUtils.EmbedField;
import bot.util.pages.Page;
import bot.util.pages.messages.PagedMessage;

public final class PagedMessageBuilder<T> extends PagedPickerMessageBuilder<T> {
	private final List<Page> pages = new ArrayList<>();

	public PagedMessageBuilder() {
		this(null);
	}

	public PagedMessageBuilder(final String title) {
		super(title, 5);
	}

	public PagedMessageBuilder(final String title, final int fieldsPerPage) {
		super(title, fieldsPerPage);
	}

	public PagedMessageBuilder(final String title, final int fieldsPerPage, final List<EmbedField> fields) {
		super(title, fieldsPerPage, fields);
	}

	public PagedMessageBuilder<T> addSimplePage(final String title, final String description) {
		pages.add(new Page(title, description));
		return this;
	}

	@Override
	public PagedMessageBuilder<T> title(final String x) {
		title = x;
		return this;
	}

	@Override
	public PagedMessageBuilder<T> description(final String x) {
		description = x;
		return this;
	}

	@Override
	public PagedMessageBuilder<T> imgUrl(final String x) {
		imgUrl = x;
		return this;
	}

	@Override
	public PagedMessageBuilder<T> footer(final String x) {
		footer = x;
		return this;
	}

	@Override
	public PagedMessageBuilder<T> fields(final List<EmbedField> x) {
		fields = x;
		return this;
	}

	@Override
	public PagedMessageBuilder<T> addField(final EmbedField x) {
		fields.add(x);
		return this;
	}

	@Override
	public PagedMessageBuilder<T> page(final int x) {
		page = x;
		return this;
	}

	@Override
	public PagedMessageBuilder<T> onRemove(final Consumer<MessageComponentInteraction> x) {
		onRemove = x;
		return this;
	}

	@Override
	protected List<Page> splitToPages() {
		if (!this.pages.isEmpty()) {
			return this.pages;
		}

		final List<Page> pages = new ArrayList<>(fields.size() / fieldsPerPage + 1);
		int count = 0;
		List<EmbedField> pageFields = new ArrayList<>(fieldsPerPage);
		for (final EmbedField field : fields) {
			pageFields.add(field);
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

	@Override
	public PagedMessage build() {
		final PagedMessage msg = new PagedMessage(splitToPages(), footer, onRemove);
		msg.setPage(page);
		return msg;
	}
}
