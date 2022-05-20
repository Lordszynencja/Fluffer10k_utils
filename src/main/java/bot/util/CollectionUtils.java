package bot.util;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import bot.util.Utils.Pair;

public class CollectionUtils {
	public static class ValueFrom<A, B extends Comparable<B>> implements Comparator<A> {
		private final Function<A, B> valueGetter;

		public ValueFrom(final Function<A, B> valueGetter) {
			this.valueGetter = valueGetter;
		}

		@Override
		public int compare(final A o1, final A o2) {
			return valueGetter.apply(o1).compareTo(valueGetter.apply(o2));
		}

	}

	@SafeVarargs
	public static <A, B> HashMap<A, B> toMap(final Pair<A, B>... pairs) {
		final HashMap<A, B> map = new HashMap<>();
		for (final Pair<A, B> pair : pairs) {
			map.put(pair.a, pair.b);
		}
		return map;
	}

	@SafeVarargs
	public static <A, B> HashMap<A, B> toMap(final Function<B, A> keyMapper, final B... values) {
		final HashMap<A, B> map = new HashMap<>();
		for (final B value : values) {
			map.put(keyMapper.apply(value), value);
		}
		return map;
	}

	public static <A, B> HashMap<A, B> toMap(final Function<B, A> keyMapper, final Collection<B> values) {
		final HashMap<A, B> map = new HashMap<>();
		for (final B value : values) {
			map.put(keyMapper.apply(value), value);
		}
		return map;
	}

	public static <A, B, C> Map<A, C> mapMap(final Map<A, B> map, final Function<B, C> valueMapper) {
		final Map<A, C> newMap = new HashMap<>();
		if (map != null) {
			for (final A key : new HashSet<>(map.keySet())) {
				final B value = map.get(key);
				newMap.put(key, valueMapper.apply(value));
			}
		}
		return newMap;
	}

	public static <A, B, C, D> Map<C, D> mapMap(final Map<A, B> map, final Function<A, C> keyMapper,
			final Function<B, D> valueMapper) {
		final Map<C, D> newMap = new HashMap<>();
		if (map != null) {
			for (final A key : new HashSet<>(map.keySet())) {
				final B value = map.get(key);
				newMap.put(keyMapper.apply(key), valueMapper.apply(value));
			}
		}
		return newMap;
	}

	public static <A, B> Map<String, B> mapMapString(final Map<?, A> map, final Function<A, B> valueMapper) {
		return mapMap(map, o -> o.toString(), valueMapper);
	}

	public static <A> Map<String, A> mapMapString(final Map<?, A> map) {
		return mapMap(map, o -> o.toString(), o -> o);
	}

	public static <A, B> Map<Long, B> mapMapLong(final Map<String, A> map, final Function<A, B> valueMapper) {
		return mapMap(map, o -> Long.valueOf(o), valueMapper);
	}

	public static <A> Map<Long, A> mapMapLong(final Map<String, A> map) {
		return mapMap(map, o -> Long.valueOf(o), o -> o);
	}

	@SuppressWarnings("unchecked")
	public static <A, B> B[] mapArray(final A[] array, final Function<A, B> mapper, final Class<B> c) {
		final B[] newArray = (B[]) Array.newInstance(c, array.length);
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				newArray[i] = mapper.apply(array[i]);
			}
		}
		return newArray;
	}

	public static <K, T> void addToListOnMap(final Map<K, List<T>> map, final K key, final T value) {
		List<T> list = map.get(key);
		if (list == null) {
			list = new ArrayList<>();
			map.put(key, list);
		}
		list.add(value);
	}

	public static <K, T> void addToSetOnMap(final Map<K, Set<T>> map, final K key, final T value) {
		Set<T> set = map.get(key);
		if (set == null) {
			set = new HashSet<>();
			map.put(key, set);
		}
		set.add(value);
	}

	public static <K> void addToLongOnMap(final Map<K, Long> map, final K key, final long value) {
		if (key == null) {
			return;
		}
		map.put(key, map.getOrDefault(key, 0L) + value);
	}

	public static <K> void addToIntOnMap(final Map<K, Integer> map, final K key, final int value) {
		if (key == null) {
			return;
		}
		map.put(key, map.getOrDefault(key, 0) + value);
	}

	public static <A> Set<A> mapToSet(final Collection<A> list) {
		return mapToSet(list, o -> o);
	}

	public static <A, B> Set<B> mapToSet(final Collection<A> list, final Function<A, B> mapper) {
		return list.stream().map(mapper).collect(Collectors.toSet());
	}

	public static <A, B> Set<A> mapToSet(final Map<A, B> map) {
		return mapToSet(map, (a, b) -> a);
	}

	public static <A, B, C> Set<C> mapToSet(final Map<A, B> map, final BiFunction<A, B, C> mapper) {
		return map.entrySet().stream().map(entry -> mapper.apply(entry.getKey(), entry.getValue()))
				.filter(o -> o != null).collect(Collectors.toSet());
	}

	@SuppressWarnings("unchecked")
	public static <A, B> Set<B> mapToSetSafe(final Object collection, final Function<A, B> mapper) {
		try {
			return mapToSet((List<A>) collection, mapper);
		} catch (final Exception e) {
			return mapToSet((Map<A, ?>) collection,
					(a, b) -> (!(b instanceof Boolean) || (boolean) b) ? mapper.apply(a) : null);
		}
	}

	public static Set<String> enumSetToStrings(final Set<? extends Enum<?>> set) {
		return set.stream().map(o -> o.name()).collect(Collectors.toSet());
	}

	public static <A, B> List<B> mapToList(final Collection<A> list, final Function<A, B> mapper) {
		return list.stream().map(mapper).collect(toList());
	}

	public static <A> Set<A> addToSet(final Set<A> set, final A element) {
		set.add(element);
		return set;
	}

	@SafeVarargs
	public static <A> Set<A> toSet(final A... items) {
		return new HashSet<>(asList(items));
	}

	@SuppressWarnings("unchecked")
	public static <A> A[] toArray(final A... items) {
		return items;
	}
}
