package bot.util.images;

import java.awt.image.BufferedImage;

public class ImageFrame {
	public final int delay;
	public final BufferedImage image;
	public final String disposal;
	public final int width, height;

	public ImageFrame(final BufferedImage image, final int delay, final String disposal, final int width,
			final int height) {
		this.image = image;
		this.delay = delay;
		this.disposal = disposal;
		this.width = width;
		this.height = height;
	}

	public ImageFrame(final BufferedImage image) {
		this.image = image;
		delay = -1;
		disposal = null;
		width = -1;
		height = -1;
	}
}
