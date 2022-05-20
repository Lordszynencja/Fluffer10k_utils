package bot.util;

import static java.lang.Math.ceil;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {
	public static class RandomValueGenerator {
		private final int min;
		private final int max;

		public RandomValueGenerator(final int min, final int max) {
			this.min = min;
			this.max = max;
		}

		public int getValue() {
			return getRandomInt(min, max);
		}
	}

	public static <T> T getRandom(final T[] array) {
		return array[ThreadLocalRandom.current().nextInt(array.length)];
	}

	public static <T> T getRandom(final List<T> list) {
		return list.get(ThreadLocalRandom.current().nextInt(list.size()));
	}

	public static int getRandomInt(final int max) {
		return ThreadLocalRandom.current().nextInt(max);
	}

	public static int getRandomInt(final int min, final int max) {
		return min + getRandomInt(max - min + 1);
	}

	public static long getRandomLongWithParts(final long bound, final int parts) {
		long total = 0;
		for (int i = 0; i < parts; i++) {
			total += getRandomLong(bound);
		}
		return total / parts;
	}

	public static long getRandomLong(final long bound) {
		return ThreadLocalRandom.current().nextLong(bound);
	}

	public static long getRandomLong(final long min, final long max) {
		return min + getRandomLong(max - min + 1);
	}

	public static BigInteger getRandomBigInteger(final BigInteger min, final BigInteger max) {
		return min.add(new BigDecimal(getRandomDouble()).multiply(new BigDecimal(max.subtract(min).add(BigInteger.ONE)))
				.toBigInteger());
	}

	public static double getRandomDouble() {
		return ThreadLocalRandom.current().nextDouble();
	}

	public static double getRandomDouble(final double max) {
		return getRandomDouble() * max;
	}

	public static boolean getRandomBoolean() {
		return ThreadLocalRandom.current().nextBoolean();
	}

	public static boolean getRandomBoolean(final double chance) {
		return getRandomDouble() < chance;
	}

	private static <T> List<T> shuffleAndCut(List<T> list, final int size) {
		Collections.shuffle(list);
		if (size < list.size()) {
			list = list.subList(0, size);
		}

		return list;
	}

	public static <T> List<T> sample(final List<T> list, final int samples) {
		return shuffleAndCut(new ArrayList<>(list), samples);
	}

	public static <T> List<T> sample(final T[] array, final int samples) {
		final List<T> sampleList = new ArrayList<>();
		for (final T element : array) {
			sampleList.add(element);
		}
		return shuffleAndCut(sampleList, samples);
	}

	public static int getSizeWithChance(int base, final double chanceForNext) {
		if (chanceForNext >= 1 || chanceForNext <= 0) {
			return base;
		}
		while (getRandomBoolean(chanceForNext)) {
			base++;
		}
		return base;
	}

	public static int rollDice(final double power) {
		return (int) getRandomDouble(power);
	}

	public static int partialRoll(final double power, final int q) {
		final int base = (int) (power / q);
		return base + roll(power - base);
	}

	public static int roll(final double power) {
		if (power < 0) {
			return -roll(-power);
		}

		final double rollPower = power / 2 + 1;
		return min((int) ceil(power), max(0, rollDice(rollPower) + rollDice(rollPower)));
	}

	public static double clashChance(final double p1, final double p2, final double offset, final double baseChance) {
		final double q = max(1, max(p1 + offset, p2 + offset));
		final double modifier = (1 - baseChance) * (p1 - p2) / q;
		return baseChance + modifier;
	}

	public static boolean clash(final double p1, final double p2, final double offset, final double baseChance) {
		return getRandomBoolean(clashChance(p1, p2, offset, baseChance));
	}

	public static boolean clash(final double p1, final double p2, final double offset) {
		return clash(p1, p2, offset, 0.5);
	}

	public static boolean clash(final double p1, final double p2) {
		return clash(p1, p2, 0);
	}

	public static void main(final String[] args) {
		final int playerAgility = 1;
		final int enemyAgility = 20;
		final double c0 = clashChance(playerAgility / 1.5, enemyAgility / 1.5, 5, 0.6);
		final double c1 = clashChance(playerAgility / 1.5 - 5, enemyAgility / 1.5, 5, 0.6);
		final double c2 = clashChance(playerAgility / 1.5 - 9, enemyAgility / 1.5, 5, 0.6);
		final double c3 = clashChance(playerAgility / 1.5 - 12, enemyAgility / 1.5, 5, 0.6);
		System.out.println(c0);
		System.out.println(c1);
		System.out.println(c2);
		System.out.println(c3);
		System.out.println(c0 + c1 + c2 + c3);
	}
}
