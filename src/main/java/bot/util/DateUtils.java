package bot.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class DateUtils {
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static Date fromMilis(final long ms) {
		return Date.from(Instant.ofEpochMilli(ms));
	}

	public static String formatDate(final Date d) {
		return dateFormat.format(d);
	}

	public static String formatDateFromMilis(final long ms) {
		return formatDate(fromMilis(ms));
	}
}
