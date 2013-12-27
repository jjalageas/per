package moduleDatabase;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseApiSql implements DatabaseApi {

	private final static Logger LOGGER = Logger.getLogger(DatabaseApiSql.class.getName()); 

	public static Driver driv;
	public static Connection con = null;

	public DatabaseApiSql() {
		DataBaseConnection dbCon = DataBaseConnection.getBddConnection();
		con = dbCon.getCon();
	}

	public void setNewIssue(String issue_key, String status) {
		java.sql.PreparedStatement preparedStatement;
		try {
			preparedStatement = con.prepareStatement("INSERT INTO issues ("
					+ "issue_key, status) VALUES('"
					+ issue_key + "', '" + status
					+ "');");
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

	}


	public void setIssueKey(int issue_id, String issue_key) {
		java.sql.PreparedStatement preparedStatement;
		try {
			preparedStatement = con.prepareStatement("UPDATE issues SET "
					+ "issue_key = '" + issue_key
					+ "' WHERE issue_id = "
					+ Integer.toString(issue_id) + ";");
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}		
	}

	public void setIssueStatus(int issue_id, String status) {
		java.sql.PreparedStatement preparedStatement;
		try {
			preparedStatement = con.prepareStatement("UPDATE issues SET "
					+ "status = '" + status
					+ "' WHERE issue_id = "
					+ Integer.toString(issue_id) + ";");
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}				
	}

	public String getStringFromBdd(String table, String target,
			String primaryKey, String id) {
		StringBuffer line = new StringBuffer("");
		java.sql.Statement stmt;
		java.sql.ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT " + target + " FROM " + table
					+ " WHERE " + primaryKey + " = '" + id
					+ "';");
			while (rs.next()) {
				line.append(rs.getString(target));
			}
		} catch (SQLException e) {	
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return line.toString();
	}

	public ArrayList<String> getIssue(int issue_id) {
		ArrayList<String> s = new ArrayList<String>();
		s.add(getStringFromBdd("issues", "issue_key", "issue_id", Integer.toString(issue_id)));
		s.add(getStringFromBdd("issues", "status", "issue_id", Integer.toString(issue_id)));
		return s;
	}

	public String getIssuekey(int issue_id) {
		String s = getStringFromBdd("issues", "issue_key", "issue_id", Integer.toString(issue_id));
		return s;
	}

	public String getIssueStatus(int issue_id) {
		String s = getStringFromBdd("issues", "status", "issue_id", Integer.toString(issue_id));
		return s;
	}

	public boolean isIssue(String issue_id) {
		String s = getStringFromBdd("issues", "issue_id", "issue_key", issue_id);
		if (s.compareTo("") != 0)
			return true;
		return false;
	}
}