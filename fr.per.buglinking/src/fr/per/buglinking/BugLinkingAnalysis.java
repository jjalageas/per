package fr.per.buglinking;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.persistence.EntityManager;

import fr.labri.harmony.core.analysis.AbstractAnalysis;
import fr.labri.harmony.core.config.model.AnalysisConfiguration;
import fr.labri.harmony.core.dao.Dao;
import fr.labri.harmony.core.model.Event;
import fr.labri.harmony.core.model.Source;

public class BugLinkingAnalysis extends AbstractAnalysis{


	public static int nbCommit = 0;
	public static int nbLink = 0;
	public static List<IssueEntity> issueList;

	public BugLinkingAnalysis() {
		super();		
	}

	public BugLinkingAnalysis(AnalysisConfiguration config, Dao dao, Properties properties) {
		super(config, dao, properties);
	}

	@SuppressWarnings("restriction")
	@Override
	public void runOn(Source src) throws MalformedURLException {
		//TODO Récupérer les bugs dans la bdd et las stocker dans une HashMap
		//TODO Comparer Les logs et les bug reports
		Map<String, String> bugReport  = new HashMap<String, String>();
		Source bugSource = dao.getSource(2);
		System.out.println(bugSource.getUrl());
		issueList = dao.getData(get, IssueEntity.class, bugSource);
		System.out.println("NOMBRE ID TROUVE " + issueList.size());
		for (IssueEntity ie : issueList) {
			
			bugReport.put(ie.getIssue_key(), ie.getStatus());

		}
		

		ArrayList<String> links = new ArrayList<String>();
		System.out.println("DEBUT DE LANALYSE");
		//		
		//		for (Author auth : src.getAuthors()) {
		//			for (int i=0; i<auth.getEvents().size(); i++){
		//				nbCommit++;
		//				String commitLog = auth.getEvents().get(i).getMetadata().get("commit_message");
		//				//System.out.println(commitLog);
		//				//link search
		//				ArrayList<String> link = compareLogToReport(commitLog, bugReport, projectKey);
		//				if(link.size() > 0){
		//					for(String l: link){
		//						String linkDisplay = "Commit " + auth.getEvents().get(i).getId() + " linked to bug " + l;
		//						links.add(linkDisplay);
		//					}			
		//				}
		//			}
		//		}

		//		for(String s: links)
		//			System.out.println(s);
		//		System.out.println();
		//		
		//		nbLink = links.size();
		//		System.out.println("Nombre de links : " + nbLink);
		//		System.out.println("Nombre de commits : " + nbCommit);
		//		
		Set<Entry<String,String>> set = bugReport.entrySet();
		for(Entry<String,String> ent : set)
			System.out.println("bug recupere : " + ent.getKey());
	}

//
//		public ArrayList<String> compareLogToReport(String commitLog, Map<String, String> bugReportIds, String pk){
//	
//			ArrayList<String> linkReport = new ArrayList<String>();
//			Pattern pattern = Pattern.compile(pk + "\\-" + "(\\d)*");
//			Matcher matcher = pattern.matcher(commitLog);
//	
//			while (matcher.find()) {
//				String foundID = matcher.group();
//				System.out.println("Id bug trouve : " + foundID);
//					if(issueDatabase.isIssue(foundID)) {
//						linkReport.add(foundID);
//					}
//	
//			}
//			return linkReport;
//		}
}
