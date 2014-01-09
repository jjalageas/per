package fr.per.bugextraction;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.codehaus.swizzle.jira.Issue;
import org.codehaus.swizzle.jira.Jira;

import fr.labri.harmony.core.analysis.AbstractAnalysis;
import fr.labri.harmony.core.config.model.AnalysisConfiguration;
import fr.labri.harmony.core.dao.Dao;
import fr.labri.harmony.core.model.Author;
import fr.labri.harmony.core.model.Event;
import fr.labri.harmony.core.model.Source;

public class BugExtractionAnalysis extends AbstractAnalysis{

	public BugExtractionAnalysis() {
		super();		
	}

	public BugExtractionAnalysis(AnalysisConfiguration config, Dao dao, Properties properties) {
		super(config, dao, properties);
	}
	
	@Override
	public void runOn(Source src) throws Exception {
		String projectKey = "AMQ-";
		Jira jira = null;
		List<IssueEntity> issueList = null;

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
		issueList = new ArrayList<IssueEntity>();
		int issuecount = 0;
		while(issuecount < 10) {
			Issue i = null;
			issuecount ++;
			StringBuffer issueKey = new StringBuffer(projectKey);
			issueKey.append(issuecount);
			try {
				i = jira.getIssue(issueKey.toString());
				if(i != null)
				{
					IssueEntity ie = new IssueEntity(i.getKey(), i.getStatus().toString());
					issueList.add(ie);
				}
			}catch(Exception e) {
				e.printStackTrace();
				continue;
			}



			System.out.println(issuecount);
		}
		for(IssueEntity ie : issueList)
			dao.saveData(getPersitenceUnitName(), ie, src);
		System.out.println("Nombre de bugs trouvés : " + issuecount);
		System.out.println("Extraction Reussie");
	}

}

