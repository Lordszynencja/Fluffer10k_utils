package bot.util.apis;

import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InitUtils {
	private final Map<String, Runnable> inits = new HashMap<>();
	private final Map<String, Boolean> initiated = new HashMap<>();
	private final Map<String, List<String>> initRequirements = new HashMap<>();

	public void addInit(final String id, final Runnable f, final String... requiredInits) {
		inits.put(id, f);
		initiated.put(id, false);
		initRequirements.put(id, asList(requiredInits));
	}

	private boolean requirementsMet(final String id) {
		for (final String requiredId : initRequirements.get(id)) {
			if (!initiated.get(requiredId)) {
				return false;
			}
		}

		return true;
	}

	public void init() {
		Set<String> initsLeft = new HashSet<>(inits.keySet());

		while (!initsLeft.isEmpty()) {
			final Set<String> newInitsLeft = new HashSet<>();
			for (final String init : initsLeft) {
				if (requirementsMet(init)) {
					inits.get(init).run();
					initiated.put(init, true);
				} else {
					newInitsLeft.add(init);
				}
			}

			if (initsLeft.size() == newInitsLeft.size()) {
				throw new RuntimeException("Couldn't solve inits for " + initsLeft);
			}

			initsLeft = newInitsLeft;
		}
	}
}
