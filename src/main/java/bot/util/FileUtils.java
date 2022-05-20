package bot.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FileUtils {
	public static String readFile(final String path) throws IOException {
		final FileInputStream in = new FileInputStream(path);
		final byte[] bytes = new byte[in.available()];
		in.read(bytes);
		in.close();

		return new String(bytes);
	}

	public static String[] readFileLines(final String path) throws IOException {
		return readFile(path).split("(\r\n|\r|\n)");
	}

	public static void saveFile(final String path, final byte[] data) throws IOException {
		final FileOutputStream out = new FileOutputStream(path);
		out.write(data);
		out.close();
	}

	public static void saveFile(final String path, final String data) throws IOException {
		saveFile(path, data.getBytes());
	}

	public static void saveFile(final String path, final String[] lines) throws IOException {
		saveFile(path, String.join("\n", lines).getBytes());
	}

	public static void saveFile(final String path, final List<String> lines) throws IOException {
		saveFile(path, String.join("\n", lines).getBytes());
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> readJSON(final String json) throws JsonMappingException, JsonProcessingException {
		return new ObjectMapper().readValue(json, Map.class);
	}

	public static Map<String, Object> readJSONFile(final String path) throws IOException {
		return readJSON(readFile(path));
	}

	public static String writeJSON(final Map<String, Object> data)
			throws JsonMappingException, JsonProcessingException {
		return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(data);
	}

	public static void saveJSONFile(final String path, final Map<String, Object> data)
			throws JsonMappingException, JsonProcessingException, IOException {
		saveFile(path, writeJSON(data));
	}

	public static void saveJSONFileWithBackup(final String path, final Map<String, Object> data)
			throws JsonMappingException, JsonProcessingException, IOException {
		final String json = writeJSON(data);
		saveFile(path, json);

		final String backupPath = path + "-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".txt";
		saveJSONFile(backupPath, data);

		final String oldBackupPath = path + "-"
				+ new SimpleDateFormat("yyyy-MM-dd").format(Date.from(new Date().toInstant().minus(7, ChronoUnit.DAYS)))
				+ ".txt";
		final File oldBackup = new File(oldBackupPath);
		if (oldBackup.exists()) {
			oldBackup.delete();
		}
	}

}
