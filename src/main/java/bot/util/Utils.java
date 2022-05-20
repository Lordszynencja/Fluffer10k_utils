package bot.util;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class Utils {
	public static class Pair<A, B> {
		public static <A, B> Pair<A, B> pair(final A a, final B b) {
			return new Pair<>(a, b);
		}

		public A a;
		public B b;

		public Pair(final A a, final B b) {
			this.a = a;
			this.b = b;
		}

		public Pair(final Map.Entry<A, B> entry) {
			this.a = entry.getKey();
			this.b = entry.getValue();
		}

		@Override
		public String toString() {
			return "(" + a + "," + b + ")";
		}
	}

	public static String repeat(String s, int repeats) {
		final StringBuilder b = new StringBuilder();
		while (repeats > 0) {
			if ((repeats & 1) == 1) {
				b.append(s);
			}
			s += s;
			repeats /= 2;
		}

		return b.toString();
	}

	public static double max(final double... numbers) {
		double max = numbers[0];
		for (final double number : numbers) {
			if (number > max) {
				max = number;
			}
		}
		return max;
	}

	public static String fixString(final String s) {
		return s == null ? null : s.trim().toLowerCase().replaceAll("[^0-9a-zA-Z ]", "_");
	}

	public static Long longFromNumber(final Object o) {
		return o == null ? null : ((Number) o).longValue();
	}

	public static BigInteger bigIntegerFromNumber(final Object o) {
		return o == null ? null : o instanceof BigInteger ? (BigInteger) o : new BigInteger(o + "");
	}

	public static Integer intFromNumber(final Object o) {
		return o == null ? null : ((Number) o).intValue();
	}

	public static Double doubleFromNumber(final Object o) {
		return o == null ? null : ((Number) o).doubleValue();
	}

	public static long toMsFromSystemTime(final long days, final long hours, final long minutes, final long seconds) {
		return toMs(days, hours, minutes, seconds, System.currentTimeMillis());
	}

	public static long toMs(final long days, final long hours, final long minutes, final long seconds) {
		return toMs(days, hours, minutes, seconds, 0);
	}

	public static long toMs(final long days, final long hours, final long minutes, final long seconds, final long ms) {
		return (((days * 24 + hours) * 60 + minutes) * 60 + seconds) * 1000 + ms;
	}

	public static String capitalize(final String s) {
		if (s == null || s.length() == 0) {
			return s;
		}
		if (s.length() == 1) {
			return s.toUpperCase();
		}
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	public static String toMention(final long userId) {
		return "<@" + userId + ">";
	}

	public static String bold(final String s) {
		return "**" + s + "**";
	}

	public static String cursive(final String s) {
		return "*" + s + "*";
	}

	public static String joinNames(final List<String> names) {
		if (names == null || names.isEmpty()) {
			return "";
		}
		if (names.size() == 1) {
			return names.get(0);
		}

		return String.join(", ", names.subList(0, names.size() - 1)) + " and " + names.get(names.size() - 1);
	}

	public static String toTimeString(long t) {
		if (t <= 0) {
			return "0 seconds";
		}

		final long seconds = t % 60;
		t /= 60;
		final long minutes = t % 60;
		t /= 60;
		final long hours = t % 24;
		t /= 24;
		final long days = t;

		String s = "";

		if (days > 0) {
			s += days == 1 ? "1 day " : (days + " days ");
		}
		if (days > 0 || hours > 0) {
			s += hours == 1 ? "1 hour " : (hours + " hours ");
		}
		if (days > 0 || hours > 0 || minutes > 0) {
			s += minutes == 1 ? "1 minute " : (minutes + " minutes ");
		}
		s += seconds == 1 ? "1 second" : (seconds + " seconds");

		return s;
	}
}
