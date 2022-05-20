package bot.util.images;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AnimatedGifReader {
	public static ImageFrame[] readGif(final InputStream stream) throws IOException {
		final ArrayList<ImageFrame> frames = new ArrayList<ImageFrame>(2);

		final ImageReader reader = (ImageReader) ImageIO.getImageReadersByFormatName("gif").next();
		reader.setInput(ImageIO.createImageInputStream(stream));

		int lastx = 0;
		int lasty = 0;

		int width = -1;
		int height = -1;

		final IIOMetadata metadata = reader.getStreamMetadata();

		Color backgroundColor = null;

		if (metadata != null) {
			final IIOMetadataNode globalRoot = (IIOMetadataNode) metadata
					.getAsTree(metadata.getNativeMetadataFormatName());

			final NodeList globalColorTable = globalRoot.getElementsByTagName("GlobalColorTable");
			final NodeList globalScreeDescriptor = globalRoot.getElementsByTagName("LogicalScreenDescriptor");

			if (globalScreeDescriptor != null && globalScreeDescriptor.getLength() > 0) {
				final IIOMetadataNode screenDescriptor = (IIOMetadataNode) globalScreeDescriptor.item(0);

				if (screenDescriptor != null) {
					width = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenWidth"));
					height = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenHeight"));
				}
			}

			if (globalColorTable != null && globalColorTable.getLength() > 0) {
				final IIOMetadataNode colorTable = (IIOMetadataNode) globalColorTable.item(0);

				if (colorTable != null) {
					final String bgIndex = colorTable.getAttribute("backgroundColorIndex");

					IIOMetadataNode colorEntry = (IIOMetadataNode) colorTable.getFirstChild();
					while (colorEntry != null) {
						if (colorEntry.getAttribute("index").equals(bgIndex)) {
							final int red = Integer.parseInt(colorEntry.getAttribute("red"));
							final int green = Integer.parseInt(colorEntry.getAttribute("green"));
							final int blue = Integer.parseInt(colorEntry.getAttribute("blue"));

							backgroundColor = new Color(red, green, blue);
							break;
						}

						colorEntry = (IIOMetadataNode) colorEntry.getNextSibling();
					}
				}
			}
		}

		BufferedImage master = null;
		boolean hasBackround = false;

		for (int frameIndex = 0;; frameIndex++) {
			BufferedImage image;
			try {
				image = reader.read(frameIndex);
			} catch (final IndexOutOfBoundsException io) {
				break;
			}

			if (width == -1 || height == -1) {
				width = image.getWidth();
				height = image.getHeight();
			}

			final IIOMetadataNode root = (IIOMetadataNode) reader.getImageMetadata(frameIndex)
					.getAsTree("javax_imageio_gif_image_1.0");
			final IIOMetadataNode gce = (IIOMetadataNode) root.getElementsByTagName("GraphicControlExtension").item(0);
			final NodeList children = root.getChildNodes();

			final int delay = Integer.valueOf(gce.getAttribute("delayTime"));

			final String disposal = gce.getAttribute("disposalMethod");

			if (master == null) {
				master = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				master.createGraphics().setColor(backgroundColor);
				master.createGraphics().fillRect(0, 0, master.getWidth(), master.getHeight());

				hasBackround = image.getWidth() == width && image.getHeight() == height;

				master.createGraphics().drawImage(image, 0, 0, null);
			} else {
				int x = 0;
				int y = 0;

				for (int nodeIndex = 0; nodeIndex < children.getLength(); nodeIndex++) {
					final Node nodeItem = children.item(nodeIndex);

					if (nodeItem.getNodeName().equals("ImageDescriptor")) {
						final NamedNodeMap map = nodeItem.getAttributes();

						x = Integer.valueOf(map.getNamedItem("imageLeftPosition").getNodeValue());
						y = Integer.valueOf(map.getNamedItem("imageTopPosition").getNodeValue());
					}
				}

				if (disposal.equals("restoreToPrevious")) {
					BufferedImage from = null;
					for (int i = frameIndex - 1; i >= 0; i--) {
						if (!frames.get(i).disposal.equals("restoreToPrevious") || frameIndex == 0) {
							from = frames.get(i).image;
							break;
						}
					}

					{
						final ColorModel model = from.getColorModel();
						final boolean alpha = from.isAlphaPremultiplied();
						final WritableRaster raster = from.copyData(null);
						master = new BufferedImage(model, raster, alpha, null);
					}
				} else if (disposal.equals("restoreToBackgroundColor") && backgroundColor != null) {
					if (!hasBackround || frameIndex > 1) {
						master.createGraphics().fillRect(lastx, lasty, frames.get(frameIndex - 1).width,
								frames.get(frameIndex - 1).height);
					}
				}
				master.createGraphics().drawImage(image, x, y, null);

				lastx = x;
				lasty = y;
			}

			final ColorModel model = master.getColorModel();
			final boolean alpha = master.isAlphaPremultiplied();
			final WritableRaster raster = master.copyData(null);
			final BufferedImage copy = new BufferedImage(model, raster, alpha, null);

			frames.add(new ImageFrame(copy, delay, disposal, image.getWidth(), image.getHeight()));

			master.flush();
		}
		reader.dispose();

		return frames.toArray(new ImageFrame[frames.size()]);
	}
}
