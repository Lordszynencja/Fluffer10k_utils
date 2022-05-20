package bot.util.modularPrompt;

import java.util.function.Consumer;

import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.ButtonStyle;
import org.javacord.api.interaction.MessageComponentInteraction;

public interface ModularPromptButton {
	public static ModularPromptButton button(final String label, final ButtonStyle style,
			final Consumer<MessageComponentInteraction> onClick) {
		return new ModularPromptButtonWithLabel(label, style, onClick, false);
	}

	public static ModularPromptButton button(final String label, final ButtonStyle style,
			final Consumer<MessageComponentInteraction> onClick, final boolean disabled) {
		return new ModularPromptButtonWithLabel(label, style, onClick, disabled);
	}

	public static ModularPromptButton button(final Emoji emoji, final ButtonStyle style,
			final Consumer<MessageComponentInteraction> onClick) {
		return new ModularPromptButtonWithEmoji(emoji, style, onClick, false);
	}

	public static ModularPromptButton button(final Emoji emoji, final ButtonStyle style,
			final Consumer<MessageComponentInteraction> onClick, final boolean disabled) {
		return new ModularPromptButtonWithEmoji(emoji, style, onClick, disabled);
	}

	static class ModularPromptButtonWithLabel implements ModularPromptButton {
		public final String label;
		public final ButtonStyle style;
		public final Consumer<MessageComponentInteraction> onClick;
		public final boolean disabled;

		public ModularPromptButtonWithLabel(final String label, final ButtonStyle style,
				final Consumer<MessageComponentInteraction> onClick, final boolean disabled) {
			this.label = label;
			this.style = style;
			this.onClick = onClick;
			this.disabled = disabled;
		}

		@Override
		public void click(final MessageComponentInteraction in) {
			onClick.accept(in);
		}

		@Override
		public Button build(final String id) {
			return Button.create(id, style, label, disabled);
		}
	}

	static class ModularPromptButtonWithEmoji implements ModularPromptButton {
		public final Emoji emoji;
		public final ButtonStyle style;
		public final Consumer<MessageComponentInteraction> onClick;
		public final boolean disabled;

		public ModularPromptButtonWithEmoji(final Emoji emoji, final ButtonStyle style,
				final Consumer<MessageComponentInteraction> onClick, final boolean disabled) {
			this.emoji = emoji;
			this.style = style;
			this.onClick = onClick;
			this.disabled = disabled;
		}

		@Override
		public void click(final MessageComponentInteraction in) {
			onClick.accept(in);
		}

		@Override
		public Button build(final String id) {
			return Button.create(id, style, null, emoji, disabled);
		}
	}

	public void click(MessageComponentInteraction in);

	public Button build(String id);
}
