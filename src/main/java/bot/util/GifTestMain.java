package bot.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import bot.util.images.AnimatedGifEncoder;

public class GifTestMain {

	private static BufferedImage createFrame(final int id) {
		final BufferedImage img = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);

		int c = (0xFF << 24) | (0xff << 16) | (0xFF << 8) | 0xFF;
		for (int x = 0; x < 128; x++) {
			for (int y = 0; y < 128; y++) {
				img.setRGB(x, y, c);
			}
		}

		c = (0xff << 16) | (0xFF << 8) | 0xFF;
		for (int i = 0; i < 48; i++) {
			img.setRGB((64 + id + i) % 128, 64 + i, c);
			img.setRGB((64 + id + i) % 128, 64 - i, c);
			img.setRGB(((64 + id) - i) % 128, 64 + i, c);
			img.setRGB(((64 + id) - i) % 128, 64 - i, c);
		}

		return img;
	}

	public static void main(final String[] args) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();

		final AnimatedGifEncoder encoder = new AnimatedGifEncoder();
		encoder.setRepeat(0);
		encoder.setQuality(1);
		encoder.setDispose(1);
		encoder.setFrameRate(32);
		encoder.start(out);
		for (int i = 0; i < 128; i++) {
			encoder.addFrame(createFrame(i));
		}
		encoder.finish();
		out.close();

		final FileOutputStream fout = new FileOutputStream("C:/users/szylew/desktop/giftest/test.gif");
		fout.write(out.toByteArray());
		fout.close();
	}
}
