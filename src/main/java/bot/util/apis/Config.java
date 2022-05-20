package bot.util.apis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import bot.util.FileUtils;

public class Config {
	private final String fileName;

	private final Map<String, String> configValues = new HashMap<>();

	public Config(final String fileName) throws IOException {
		this.fileName = fileName;
		final File f = new File(fileName);
		if (!f.exists()) {
			f.createNewFile();
		}

		for (final String line : FileUtils.readFileLines(fileName)) {
			final int pos = line.indexOf('=');
			if (pos >= 0) {
				configValues.put(line.substring(0, pos), line.substring(pos + 1));
			}
		}

		saveConfig();
	}

	public void saveConfig() throws IOException {
		final List<String> lines = new ArrayList<>(configValues.size());
		for (final Entry<String, String> entry : configValues.entrySet()) {
			lines.add(entry.getKey() + "=" + entry.getValue());
		}
		FileUtils.saveFile(fileName, lines);
	}

	public int getInt(final String code) {
		return Integer.valueOf(configValues.get(code));
	}

	public String getString(final String code) {
		return configValues.get(code);
	}

	public boolean getBoolean(final String code) {
		return Boolean.valueOf(configValues.get(code));
	}
}
