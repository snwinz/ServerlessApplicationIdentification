package ApplicationSearcher;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class FileAnalyzer {

	JsonParser parser = new JsonParser();

	private LinkedList<ServerlessApplicationRepository> applications = new LinkedList<ServerlessApplicationRepository>();
	HashMap<String, ServerlessApplicationRepository> map = new HashMap<String, ServerlessApplicationRepository>();

	public void deserializeApplications(String source) throws IOException, ClassNotFoundException {

		FileInputStream fis = new FileInputStream(source);
		ObjectInputStream ois = new ObjectInputStream(fis);
		LinkedList<ServerlessApplicationRepository> readObject = (LinkedList<ServerlessApplicationRepository>) ois
				.readObject();
		applications = readObject;

		map = new HashMap<String, ServerlessApplicationRepository>();

		for (ServerlessApplicationRepository app : applications) {
			map.put(app.getHtml_url(), app);
		}
		ois.close();
		fis.close();

	}

	public void serializeApplications(String target) throws IOException {

		FileOutputStream fos = new FileOutputStream(target);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(applications);
		oos.close();
		fos.close();

	}

	public void getInformationOfProjects(String baseOfFilename, int start, int end, String analyzeTyp)
			throws IOException {

		System.out.println(System.lineSeparator());
		for (int listNumber = start; listNumber <= end; listNumber++) {
			System.out.println(listNumber);
			String dataStorageFile = baseOfFilename + listNumber + ".txt";
			String storedData = Files.readString(Path.of(dataStorageFile));
			String resultOfQuery = storedData;

			JsonObject jsonObject = parser.parse(resultOfQuery).getAsJsonObject();

			JsonArray items = jsonObject.getAsJsonArray("items");
			for (JsonElement element : items) {

				String infrastructureFile = element.getAsJsonObject().get("name").toString();
				String filePath = element.getAsJsonObject().get("path").toString();
				JsonObject repositoryObject = element.getAsJsonObject().getAsJsonObject("repository");
				String html_url = repositoryObject.get("html_url").toString();

				String starUrl = repositoryObject.get("url").toString();
				starUrl = starUrl.replaceAll("\"", "");
				String jsonStars = Main.gitHubRepositoryImplRaw.getContentOfUrl(starUrl);
				JsonObject jsonStarObject = parser.parse(jsonStars).getAsJsonObject();

				String stars = jsonStarObject.get("stargazers_count").toString();
				if (map.containsKey(html_url)) {
					ServerlessApplicationRepository app = map.get(html_url);
					app.addFile(infrastructureFile);
					app.addPath(filePath);
					app.addFound(analyzeTyp);
				} else {
					ServerlessApplicationRepository application = new ServerlessApplicationRepository();
					application.addFile(infrastructureFile);
					application.addPath(filePath);
					application.setHtml_url(html_url);
					application.setStars(Integer.valueOf(stars));
					application.addFound(analyzeTyp);
					applications.add(application);
					map.put(html_url, application);
				}
			}

		}
	}

	public void evaluate() throws IOException {

		int popularStarLimit = 4;
		Collections.sort(applications, new RepoComparator());

		System.out.format("Number of applications: %d%n", applications.size());
		System.out.format("More than 0 stars: %d%n", applications.stream().filter(s -> s.getStars() > 0).count());

		List<ServerlessApplicationRepository> popularApplications = applications.stream()
				.filter(app -> app.getStars() >= popularStarLimit).collect(Collectors.toList());

		Collections.sort(popularApplications, new RepoComparator());

		System.out.println("Git commands to load these projects: ");
		GitPrinter gitPrinter = new GitPrinter(popularApplications);
		System.out.println(gitPrinter.createCommandToLoadAll());
		System.out.println("Applications:");
		for (ServerlessApplicationRepository app : popularApplications) {
			System.out.println(app.getHtml_url());
		}

		List<String> applicationAsStringList = popularApplications.stream().map(app -> app.toString())
				.collect(Collectors.toList());

		Files.writeString(Path.of("result.txt"),
				String.join(System.lineSeparator() + System.lineSeparator(), applicationAsStringList),
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

		System.out.format("Number of popular applications: %d%n", popularApplications.size());
		System.out.format("At least %d stars: %d%n", popularStarLimit,
				popularApplications.stream().filter(s -> s.getStars() > 0).count());
		System.out.println("Ende");

	}

	public void countNumberOfDiffernetProject() {
		System.out.format("Number of different projects: %d%n", applications.size());

		int numberOfFilesFound = 0;

		for (ServerlessApplicationRepository repo : applications) {

			numberOfFilesFound += repo.getFiles().size();

		}
		
		System.out.format("Number of files found: %d%n", numberOfFilesFound);

	}

}
