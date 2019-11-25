package ApplicationSearcher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GitHubRepositoryImplRaw {

	public GitHubRepositoryImplRaw(String token) {
		this.token = token;
	}

	private String token;

	public String getContentOfUrl(String url) {

		try {
			HttpURLConnection connection = null;
			String retVal = "";
			URL endpoint = new URL(url);
			connection = (HttpURLConnection) endpoint.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Authorization", "token " + token);
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder jsonSb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				jsonSb.append(line);
			}
			retVal += jsonSb.toString();

			connection.disconnect();
			reader.close();

			return retVal;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}


}
