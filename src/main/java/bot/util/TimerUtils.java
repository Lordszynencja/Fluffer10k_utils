package bot.util;

import static bot.util.apis.CommandHandlers.addOnExit;

import java.util.HashMap;
import java.util.Map;

public class TimerUtils {
	private static int threadIdSequence = 0;
	private static final Map<Integer, Thread> threads = new HashMap<>();

	private static void sleepUntil(final long nextTime) throws InterruptedException {
		final long currentTime = System.currentTimeMillis();
		if (nextTime <= currentTime) {
			return;
		}
		Thread.sleep(nextTime - currentTime);
	}

	public static void startTimedEvent(final Runnable r, final long time) {
		new Thread(() -> {
			try {
				sleepUntil(time);
				r.run();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}

	public static void startRepeatedTimedEvent(final Runnable r, final int timeout, final int initialDelay,
			final String name) {
		final int newThreadId = threadIdSequence++;
		final long timeoutMs = timeout * 1000;
		final long timeTrimmedToTimeout = System.currentTimeMillis() / timeoutMs * timeoutMs;
		final long startTime = timeTrimmedToTimeout + initialDelay * 1000;

		final Thread thread = new Thread(() -> {
			System.out.println("Starting thread " + name);
			try {
				long t = startTime;

				while (true) {
					sleepUntil(t);
					r.run();
					t += timeoutMs;
				}
			} catch (final InterruptedException e) {
				System.out.println("Stopping thread " + name);
			}
		});

		threads.put(newThreadId, thread);
		thread.start();

		addOnExit(() -> stopRepeatedTimeEvent(newThreadId));
	}

	public static void stopRepeatedTimeEvent(final int id) {
		final Thread t = threads.remove(id);
		if (t != null) {
			t.interrupt();
		}
	}

	public static void onExit() {
		if (threads.size() > 0) {
			System.out.println("Some threads weren't stopped!!!");
			for (final Thread t : threads.values()) {
				t.interrupt();
			}
		}
	}
}
