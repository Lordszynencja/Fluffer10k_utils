package bot.util;

import java.util.List;

import org.javacord.api.entity.message.embed.EmbedBuilder;

public class EmbedUtils {
	public static class EmbedField {
		public final String name;
		public final String value;
		public final boolean inline;

		public EmbedField(final String name, final String value) {
			this.name = name;
			this.value = value;
			inline = false;
		}

		public EmbedField(final String name, final String value, final boolean inline) {
			this.name = name;
			this.value = value;
			this.inline = inline;
		}

		public void addTo(final EmbedBuilder embed) {
			embed.addField(name, value, inline);
		}
	}

	public static EmbedBuilder makeEmbed(final String title) {
		final EmbedBuilder embed = new EmbedBuilder();
		if (title != null) {
			embed.setTitle(title);
		}

		return embed;
	}

	public static EmbedBuilder makeEmbed(final String title, final String description) {
		final EmbedBuilder embed = makeEmbed(title);
		if (description != null) {
			embed.setDescription(description);
		}
		return embed;
	}

	public static EmbedBuilder makeEmbed(final String title, final String description, final String imageUrl) {
		final EmbedBuilder embed = makeEmbed(title, description);
		if (imageUrl != null) {
			embed.setImage(imageUrl);
		}
		return embed;
	}

	public static EmbedBuilder makeEmbed(final String title, final String description, final String imageUrl,
			final List<EmbedField> fields) {
		final EmbedBuilder embed = makeEmbed(title, description, imageUrl);
		fields.forEach(f -> f.addTo(embed));
		return embed;
	}
}
