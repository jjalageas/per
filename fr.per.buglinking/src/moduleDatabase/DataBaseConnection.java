package moduleDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DataBaseConnection {

	private final static Logger LOGGER = Logger.getLogger(DataBaseConnection.class.getName()); 

	private static DataBaseConnection me;

	private static Connection con = null;

	private static String url;
	private static String user = "pchanson";
	private static String password = "Chans-16";
	private static String host = "dbserver";
	private static String port = "3306";
	private static String bdd = "pchanson";

	public static DataBaseConnection getBddConnection() {
		if (me == null) {
			me = new DataBaseConnection();
		}
		return me;
	}

	private DataBaseConnection() {

		url = "jdbc:mysql://" + host + ":" + port + "/" + bdd;

		try {
			Class.forName("com.mysql.jdbc.Driver");

			con = (Connection) DriverManager.getConnection(url, user, password);

			Database.getDatabase(con);

		} catch (ClassNotFoundException e1) {
			LOGGER.log(Level.SEVERE, e1.getMessage(), e1);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public Connection getCon() {
		return (Connection) con;
	}

}
