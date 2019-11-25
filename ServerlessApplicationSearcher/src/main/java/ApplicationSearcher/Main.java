package ApplicationSearcher;

import java.io.IOException;

import com.google.gson.JsonParser;

public class Main {
	public static GitHubRepositoryImplRaw gitHubRepositoryImplRaw = new GitHubRepositoryImplRaw(
			System.getenv("GITHUB_API_TOKEN"));
	static JsonParser parser = new JsonParser();

	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		String searchKey = "serverless.yml";
		Filesaver filesaver = new Filesaver(gitHubRepositoryImplRaw);
		String filename = "searchResult";

		filesaver.getFilesForFilename(searchKey, filename, 0);

		FileAnalyzer analyzer = new FileAnalyzer();
		analyzer.getInformationOfProjects(filename, 1, 10, "serverless-framework");
		analyzer.serializeApplications("sfAdded");
		analyzer.deserializeApplications("sfAdded");
		analyzer.countNumberOfDiffernetProject();

		analyzer.evaluate();
	}

}
