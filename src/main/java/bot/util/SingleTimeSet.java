package bot.util;

public class SingleTimeSet<T> {
	private T v;

	public void set(final T v) {
		if (this.v != null) {
			throw new RuntimeException("Can't set twice!");
		}
		this.v = v;
	}

	public T get() {
		return v;
	}
}
