import java.net.URL;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.lightningbug.api.BugzillaClient; 
import de.lightningbug.api.domain.Bug;
import de.lightningbug.api.service.BugService;

public class Main {
	

	public static void main(String[] arg) throws MalformedURLException{
		 final BugzillaClient client = new BugzillaClient(new URL("https://issues.apache.org/bugzilla/"), "jjalageas@yahoo.com","pepsi718");
		 client.login();
		 final BugService bugService = new BugService(client);
		 final Map<String, Object[]> searchParams = new HashMap<String, Object[]>();
		 searchParams.put("summary", new Object[]{ "fix"});
		// searchParams.put("priority", new Object[]{ " "});
		 final List<Bug> bugs = bugService.search(searchParams);
		 System.out.println(bugs.size());
		 for(Bug b: bugs){
			 System.out.println(b.getDescription());
			 System.out.println(b.toString());
			// System.out.println(b.getSeverity());
			// System.out.println(b.getId());
			// System.out.println(b.getComponent());
	 
		 }

	}

}
