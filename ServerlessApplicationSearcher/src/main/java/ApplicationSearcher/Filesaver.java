package ApplicationSearcher;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Filesaver {

	private GitHubRepositoryImplRaw gitHubRepositoryImplRaw;

	static JsonParser parser = new JsonParser();

	public Filesaver(GitHubRepositoryImplRaw gitHubRepositoryImplRaw) {
		this.gitHubRepositoryImplRaw = gitHubRepositoryImplRaw;
	}

	private static void saveResult(String resultOfQuery, String fileName) {

		Path path = Path.of(fileName);

		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING)) {
			writer.write(resultOfQuery);
			writer.close();
		} catch (IOException e) {
			System.err.print("Saving was not possible");
		}

	}

	public void getFilesForFilename(String searchFile, String filename, int offsetForFilename)
			throws IOException, InterruptedException {
		for (int pageNumber = 6; pageNumber <= 7; pageNumber++) {
			String url = String.format(
					"https://api.github.com/search/code?page=%d&per_page=100&q=aws+functions+handler+events+filename:%s",
					pageNumber, searchFile);
			String fileName = filename + (pageNumber + offsetForFilename) + ".txt";
			getAndSaveData(url, fileName);
		}

	}

	private void getAndSaveData(String url, String fileName) {
		String githubResult = gitHubRepositoryImplRaw.getContentOfUrl(url);
		githubResult = formatRawString(githubResult);
		saveResult(githubResult, fileName);
	}

	private static String formatRawString(String result) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonElement el = parser.parse(result);
		result = gson.toJson(el);
		return result;
	}

}
