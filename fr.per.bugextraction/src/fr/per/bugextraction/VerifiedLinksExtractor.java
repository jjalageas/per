package fr.per.bugextraction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;



public class VerifiedLinksExtractor {
	
	private static final int BUG_COLUMN = 0;
	private static final int COMMIT_COLUMN = 1;
	private int minBugId;
	private int maxBugId;
	private String projectKey;
	private Map<String,String> linksMap;
	
	public VerifiedLinksExtractor(String key) {
		projectKey = key;
		maxBugId = -1;
		minBugId = Integer.MAX_VALUE;
		extract();
	}
	
	
	private void extract() {
		Map<String, String> links = new HashMap<>();
		String fileName = projectKey + "Links";
		java.io.File linksFile = new java.io.File("resources/" + fileName);
		try {
			InputStream ips=new FileInputStream(linksFile.getAbsolutePath());
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String ligne;
			while ((ligne=br.readLine())!=null){
				String[] splitLine = ligne.split("\\s");
				System.out.println("link entre le bug : |" + splitLine[BUG_COLUMN] + "| et le commit: |" + splitLine[COMMIT_COLUMN] + "|");
				links.put(splitLine[BUG_COLUMN], splitLine[COMMIT_COLUMN]);
				updateMinMaxBugId(splitLine[BUG_COLUMN]);
			}
			br.close(); 
		}catch(Exception e){
			e.printStackTrace();
		}
		linksMap = links;
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

	public Map<String, String> getLinksMap() {
		return linksMap;
	}
}
