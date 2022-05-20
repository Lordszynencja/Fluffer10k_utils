package bot.util.pages;

import static bot.util.EmbedUtils.makeEmbed;

import java.util.ArrayList;
import java.util.List;

import org.javacord.api.entity.message.embed.EmbedBuilder;

import bot.util.EmbedUtils.EmbedField;

public class Page {
	public final String title;
	public final String description;
	public final List<EmbedField> fields;
	public final String imgUrl;

	public Page(final String title, final String description) {
		this.title = title;
		this.description = description;
		fields = new ArrayList<>();
		imgUrl = null;
	}

	public Page(final String title, final String description, final List<EmbedField> fields) {
		this.title = title;
		this.description = description;
		this.fields = fields == null ? new ArrayList<>() : fields;
		imgUrl = null;
	}

	public Page(final String title, final String description, final List<EmbedField> fields, final String imgUrl) {
		this.title = title;
		this.description = description;
		this.fields = fields == null ? new ArrayList<>() : fields;
		this.imgUrl = imgUrl;
	}

	public Page addField(final EmbedField field) {
		fields.add(field);
		return this;
	}

	public EmbedBuilder toEmbed() {
		return makeEmbed(title, description, imgUrl, fields);
	}
}