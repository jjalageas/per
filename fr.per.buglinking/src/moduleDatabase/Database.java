package moduleDatabase;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {

	public static Database me;

	public static Database getDatabase(final Connection con) {
		if (me == null) {
			me = new Database(con);
		}
		return me;
	}

	private Database(final Connection con) {

//		java.sql.PreparedStatement preparedStatement;
//
//		try {
//			preparedStatement = con.prepareStatement("DROP TABLE IF EXISTS issues;");
//
//			preparedStatement.executeUpdate();
//
//			preparedStatement = con.prepareStatement("CREATE TABLE issues (" +
//					"issue_id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY," +
//					"issue_key varchar(128) NOT NULL," +
//					"status varchar(128) NOT NULL);");
//			preparedStatement.executeUpdate();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}
}
