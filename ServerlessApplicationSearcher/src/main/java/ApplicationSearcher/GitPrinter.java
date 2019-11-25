package ApplicationSearcher;

import java.util.List;

public class GitPrinter {
	private List<ServerlessApplicationRepository> applications;

	public GitPrinter(List<ServerlessApplicationRepository> applications) {
		this.applications = applications;
	}

	public String createCommandToLoadAll() {

		StringBuilder result = new StringBuilder();
		int counter = 1;
		for (ServerlessApplicationRepository app : applications) {
			String folder = getFormattedFolder(counter);
			result.append("mkdir ").append(folder).append(System.lineSeparator());
			result.append("cd " ).append(folder).append(System.lineSeparator());
			result.append("git clone ").append(app.getHtml_url()).append(System.lineSeparator());
			result.append("cd ..").append(System.lineSeparator());
			
			counter++;
		}
		return result.toString();
		
		

	}

	private String getFormattedFolder(int counter) {
		if (counter < 10) {
			return "00" + counter;
		}
		if (counter < 100) {
			return "0" + counter;
		}
		return String.valueOf(counter);
	}

}
