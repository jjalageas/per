package fr.per.bugextraction;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.swizzle.jira.Issue;
import org.codehaus.swizzle.jira.Jira;

import fr.labri.harmony.core.analysis.AbstractAnalysis;
import fr.labri.harmony.core.model.Source;

public class BugExtractionAnalysis extends AbstractAnalysis{

	@Override
	public void runOn(Source src) throws Exception {
		String projectKey = "AMQ-";
		Jira jira = null;
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
		issueList = new ArrayList<Issue>();
		int issuecount = 0;
		while(issuecount < 10) {
			Issue i = null;
			issuecount ++;
			StringBuffer issueKey = new StringBuffer(projectKey);
			issueKey.append(issuecount);
			try {
				i = jira.getIssue(issueKey.toString());
				if(i != null)
					issueList.add(i);
			}catch(Exception e) {
				e.printStackTrace();
				continue;
			}



			System.out.println(issuecount);
		}
		for(Issue i : issueList)
			dao.saveData(getPersitenceUnitName(), i, src);
		System.out.println("Nombre de bugs trouvés : " + issuecount);
		System.out.println("Extraction Reussie");
	}

}

