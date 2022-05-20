package bot.util;

import static bot.util.Utils.max;
import static java.util.Arrays.asList;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import bot.util.images.AnimatedGifReader;
import bot.util.images.ImageFrame;

public class ImageUtils {
	public static class ImageData {
		public final BufferedImage[] images;

		public ImageData(final BufferedImage[] images) {
			this.images = images;
		}

		public ImageData(final BufferedImage image) {
			images = new BufferedImage[] { image };
		}
	}

	public static class PixelColorRGBA {
		public int r;
		public int g;
		public int b;
		public int a;

		public PixelColorRGBA(final int r, final int g, final int b, final int a) {
			this.r = r;
			this.g = g;
			this.b = b;
			this.a = a;
		}

		public PixelColorRGBA(final int rgba) {
			r = rgba & 0xFF;
			g = (rgba >> 8) & 0xFF;
			b = (rgba >> 16) & 0xFF;
			a = (rgba >> 24) & 0xFF;
		}

		public int to32Bit() {
			return (a << 24) | (b << 16) | (g << 8) | r;
		}
	}

	private static byte[] readData(final String path) {
		try {
			final URL url = new URL(path);
			final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";

			final URLConnection con = url.openConnection();
			con.setRequestProperty("User-Agent", USER_AGENT);
			final InputStream in = con.getInputStream();
			final ByteArrayOutputStream out = new ByteArrayOutputStream();

			final byte[] b = new byte[8096];
			int length;

			while ((length = in.read(b)) != -1) {
				out.write(b, 0, length);
			}

			in.close();
			out.close();
			return out.toByteArray();
		} catch (final Exception e) {
			return new byte[0];
		}
	}

	public static ImageData getImageFromUrl(final String path) {
		final String format = path.substring(path.lastIndexOf('.') + 1).toLowerCase();
		try {
			final byte[] bytes = readData(path);

			if (asList("bmp", "jpg", "jpeg", "png", "webp").contains(format)) {
				return new ImageData(ImageIO.read(new ByteArrayInputStream(bytes)));
			}
			if (format.equals("gif")) {
				final ImageFrame[] frames = AnimatedGifReader.readGif(new ByteArrayInputStream(bytes));
				final BufferedImage[] images = new BufferedImage[frames.length];
				for (int i = 0; i < frames.length; i++) {
					images[i] = frames[i].image;
				}
				return new ImageData(images);
			}

			return null;
		} catch (final Exception e) {
			return null;
		}
	}

	public static BufferedImage rescaleToMaxSize(final BufferedImage img, final int maxWidth, final int maxHeight) {
		final double scale = max(1.0, 1.0 * img.getWidth() / maxWidth, 1.0 * img.getHeight() / maxHeight);
		final int w = (int) (img.getWidth() * scale);
		final int h = (int) (img.getHeight() * scale);

		final AffineTransform at = new AffineTransform();
		at.scale(scale, scale);
		final AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);

		final BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		scaleOp.filter(img, scaled);

		return scaled;
	}

	public static BufferedImage rescaleToSize(final BufferedImage img, final int w, final int h) {
		final AffineTransform at = new AffineTransform();
		at.scale(1.0 * w / img.getWidth(), 1.0 * h / img.getHeight());
		final AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);

		final BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		scaleOp.filter(img, scaled);

		return scaled;
	}
}
