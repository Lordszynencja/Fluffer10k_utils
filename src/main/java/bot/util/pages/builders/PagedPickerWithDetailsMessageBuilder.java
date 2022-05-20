package bot.util.pages.builders;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.MessageComponentInteraction;

import bot.util.EmbedUtils.EmbedField;
import bot.util.pages.messages.PagedMessage;
import bot.util.pages.messages.PagedPickerWithDetailsMessage;

public class PagedPickerWithDetailsMessageBuilder<T> extends PagedPickerWithConfirmationMessageBuilder<T> {
	protected PagedPickerWithDetailsMessageBuilder(final PagedPickerWithDetailsMessageBuilder<T> builder) {
		super(builder);
	}

	public PagedPickerWithDetailsMessageBuilder(final String title, final int fieldsPerPage) {
		super(title, fieldsPerPage);
	}

	public PagedPickerWithDetailsMessageBuilder(final String title, final int fieldsPerPage,
			final List<EmbedField> fields) {
		super(title, fieldsPerPage, fields);
	}

	public PagedPickerWithDetailsMessageBuilder(final String title, final int fieldsPerPage,
			final List<EmbedField> fields, final List<T> items) {
		super(title, fieldsPerPage, fields, items);
	}

	@Override
	public PagedPickerWithDetailsMessageBuilder<T> title(final String x) {
		title = x;
		return this;
	}

	@Override
	public PagedPickerWithDetailsMessageBuilder<T> description(final String x) {
		description = x;
		return this;
	}

	@Override
	public PagedPickerWithDetailsMessageBuilder<T> imgUrl(final String x) {
		imgUrl = x;
		return this;
	}

	@Override
	public PagedPickerWithDetailsMessageBuilder<T> footer(final String x) {
		footer = x;
		return this;
	}

	@Override
	public PagedPickerWithDetailsMessageBuilder<T> fields(final List<EmbedField> x) {
		fields = x;
		return this;
	}

	@Override
	public PagedPickerWithDetailsMessageBuilder<T> addField(final EmbedField x) {
		fields.add(x);
		return this;
	}

	@Override
	public PagedPickerWithDetailsMessageBuilder<T> items(final List<T> x) {
		items = x;
		return this;
	}

	@Override
	public PagedPickerWithDetailsMessageBuilder<T> page(final int x) {
		page = x;
		return this;
	}

	@Override
	public PagedPickerWithDetailsMessageBuilder<T> onRemove(final Consumer<MessageComponentInteraction> x) {
		onRemove = x;
		return this;
	}

	@Override
	public final PagedPickerWithDetailsMessageBuilder<T> dataToEmbed(final Function<T, EmbedBuilder> x) {
		dataToEmbed = x;
		return new PagedPickerWithDetailsMessageBuilder<>(this);
	}

	@Override
	public PagedMessage build() {
		final PagedMessage msg = new PagedPickerWithDetailsMessage<>(splitToPages(), footer, onRemove, items,
				dataToEmbed, fieldsPerPage);
		msg.setPage(page);
		return msg;
	}

}
