package bot.util.pages.builders;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.javacord.api.interaction.MessageComponentInteraction;

import bot.util.EmbedUtils.EmbedField;
import bot.util.pages.messages.PagedMessage;
import bot.util.pages.messages.PagedPickerMessage;
import bot.util.pages.messages.PagedPickerMessage.OnPickHandler;

public class PagedPickerMessageBuilder<T> extends PagedPickerWithDetailsMessageBuilder<T> {

	protected OnPickHandler<T> onPick = null;

	protected PagedPickerMessageBuilder(final PagedPickerMessageBuilder<T> builder) {
		super(builder);
		this.onPick = builder.onPick;
	}

	public PagedPickerMessageBuilder(final String title, final int fieldsPerPage) {
		super(title, fieldsPerPage);
	}

	public PagedPickerMessageBuilder(final String title, final int fieldsPerPage, final List<EmbedField> fields) {
		super(title, fieldsPerPage, fields);
	}

	public PagedPickerMessageBuilder(final String title, final int fieldsPerPage, final List<EmbedField> fields,
			final List<T> items) {
		super(title, fieldsPerPage, fields, items);
	}

	@Override
	public PagedPickerMessageBuilder<T> title(final String x) {
		title = x;
		return this;
	}

	@Override
	public PagedPickerMessageBuilder<T> description(final String x) {
		description = x;
		return this;
	}

	@Override
	public PagedPickerMessageBuilder<T> imgUrl(final String x) {
		imgUrl = x;
		return this;
	}

	@Override
	public PagedPickerMessageBuilder<T> footer(final String x) {
		footer = x;
		return this;
	}

	@Override
	public PagedPickerMessageBuilder<T> fields(final List<EmbedField> x) {
		fields = x;
		return this;
	}

	@Override
	public PagedPickerMessageBuilder<T> addField(final EmbedField x) {
		fields.add(x);
		return this;
	}

	@Override
	public PagedPickerMessageBuilder<T> items(final List<T> x) {
		items = x;
		return this;
	}

	@Override
	public PagedPickerMessageBuilder<T> page(final int x) {
		page = x;
		return this;
	}

	@Override
	public PagedPickerMessageBuilder<T> onRemove(final Consumer<MessageComponentInteraction> x) {
		onRemove = x;
		return this;
	}

	public final PagedPickerMessageBuilder<T> onPick(final BiConsumer<MessageComponentInteraction, T> x) {
		return this.onPick((in, page, item) -> x.accept(in, item));
	}

	public final PagedPickerMessageBuilder<T> onPick(final OnPickHandler<T> x) {
		onPick = x;
		return new PagedPickerMessageBuilder<>(this);
	}

	@Override
	public PagedMessage build() {
		final PagedMessage msg = new PagedPickerMessage<>(splitToPages(), footer, onRemove, items, onPick,
				fieldsPerPage);
		msg.setPage(page);
		return msg;
	}
}
