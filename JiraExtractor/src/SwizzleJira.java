import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import moduleBdd.DatabaseApi;
import moduleBdd.IssueDao;

import org.codehaus.swizzle.jira.Jira;


public class SwizzleJira {

	public static void main(String args[])
	{
		String projectKey = "AMQ";
		Jira jira = null;
		DatabaseApi issueDatabase = new IssueDao();
		List<org.codehaus.swizzle.jira.Issue> issueList = null;

		try {
			jira = new Jira("https://issues.apache.org/jira/rpc/xmlrpc");
		} catch (MalformedURLException e1) {
			System.err.println("Mauvaise addresse de repository");
			e1.printStackTrace();
		}
		try {
			jira.login("gmeral", "harmony");
		} catch (Exception e) {
			System.err.println("Mauvais login");			
			e.printStackTrace();
		}

		List<String> projectKeys = new ArrayList<String>();
		projectKeys.add(new String(projectKey));
		try {
			issueList = jira.getIssuesFromTextSearchWithProject(projectKeys, "", 200);
			System.out.println("Nombre de bugs trouvés : " + issueList.size());
		} catch (Exception e) {

			e.printStackTrace();
		}
		for(org.codehaus.swizzle.jira.Issue i : issueList)
			issueDatabase.setNewIssue(i.getKey(), i.getStatus().toString());
		System.out.println("Extraction Reussie");
	}
}
