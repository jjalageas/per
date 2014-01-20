package fr.per.bugextraction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class VerifiedLinksExtractor {
	
	private Map<String, Date> datesMap; 
	private static final int BUG_COLUMN = 0;
	private static final int COMMIT_COLUMN = 1;
	private int minBugId;
	private int maxBugId;
	private String projectKey;
	private LinkMap linksMap;
	private int nbLines;
	private int max;

	public VerifiedLinksExtractor(String key) {
		projectKey = key;
		maxBugId = -1;
		minBugId = Integer.MAX_VALUE;
		nbLines = 0;
		extract();
		fillDatesMap();
	}


	
	private void extract() {
		LinkMap links = new LinkMap();
		max = 0;

		String fileName = projectKey + "Links";
		try {
			InputStream ips=new FileInputStream("/home/guiiii/git/per/fr.per.bugextraction/resources/"+fileName);
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String ligne;
			while ((ligne=br.readLine())!=null){
				nbLines++;
				String[] splitLine = ligne.split("\\s");
				String bug = splitLine[BUG_COLUMN];
				String commit = splitLine[COMMIT_COLUMN];
				
				links.put(bug, commit);
				updateMinMaxBugId(splitLine[BUG_COLUMN]);
				
				int tmp = Integer.valueOf((bug.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)"))[1]);
				if(max < tmp)
					max = tmp;

			}
			br.close(); 
		}catch(Exception e){
			e.printStackTrace();
		}
		linksMap = links;
		
	}
	
	@SuppressWarnings("deprecation")
	private void fillDatesMap() {
		datesMap = new HashMap<String, Date>();
		
		datesMap.put("XERCESC-", new Date(2012,04,23,12,45,00));
		datesMap.put("STDCXX-", new Date(2012,01,18,19,06,00));
		datesMap.put("OPENNLP-", new Date(2012,04,20,14,23,00));
		datesMap.put("LUCENE-", new Date(2012,05,07,11,03,00));
		datesMap.put("AMQ-", new Date(2012,05,9,20,06,00));
		datesMap.put("MAHOUT-", new Date(2012,05,10,19,44,00));
		datesMap.put("HADOOP-", new Date(2012,05,12,06,04,00));
		datesMap.put("STR-", new Date(2009,9,18,06,9,00));
		datesMap.put("XALANC-", new Date(2011,11,07,18,35,00));
	}

	public void updateMinMaxBugId(String bugKey) {
		int newId = Integer.parseInt(bugKey.split("-")[1]);
		if (newId > maxBugId)
			maxBugId = newId;
		if (newId < minBugId)
			minBugId = newId;
	}

	public int getMinBugId() {
		return minBugId;
	}

	public int getMaxBugId() {
		return maxBugId;
	}

	public LinkMap getLinksMap() {
		return linksMap;
	}
	
	public int getNbLines(){
		return nbLines;
	}
	
	public int getMax(){
		return max;
	}
	
	public Map<String, Date> getDatesMap() {
		return datesMap;
	}
}
