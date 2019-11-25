package ApplicationSearcher;

import java.util.Comparator;

public class RepoComparator implements Comparator<ServerlessApplicationRepository> {
	@Override
	public int compare(ServerlessApplicationRepository a1, ServerlessApplicationRepository a2) {
		return a2.getStars() - a1.getStars();
	}
}
