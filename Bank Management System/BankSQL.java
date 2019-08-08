/* BankSQL class connects to MySQL database 
 * Methods updates and query database for values 
 */

package bank;

import java.sql.*;

public class BankSQL extends Bank {
	// no-arg constructor -- creates a row
	BankSQL()  {
	
	}
	
	/* Method createAccountRow creates a row for Account table */
	public void createAccountRow(int idCount) throws SQLException, ClassNotFoundException {
		/* Load JDBC Driver */
		Class.forName("com.mysql.cj.jdbc.Driver");
	/* Connect to database */
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Bank?serverTimezone=EST", "bank" , "temp");
	/* Create statement object */
		PreparedStatement createAccountRow = conn.prepareStatement("insert into Accounts " + " value " + 
		"(?,?,?,?,?)");
		createAccountRow.setInt(1, idCount);
		createAccountRow.setString(2, "noname");
		createAccountRow.setString(3, "noname");
		createAccountRow.setString(4, null);
		createAccountRow.setString(5, null);
		createAccountRow.executeUpdate();
		conn.close();
	}
	/* Backup table for idCounts just in case lastId text file is deleted or removed */
	public void idCountBackup(int idCount) throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Bank?serverTimezone=EST", "bank", "temp");
		Statement statement1 = conn.createStatement();
		statement1.executeUpdate("insert into idCount (id_count) values ('" + idCount + "')");
		conn.close();
	}
	
	/* Method insertPIN INSERTS pin in database */
	public void insertPIN(int idCount, int pin) throws SQLException, ClassNotFoundException {
	Class.forName("com.mysql.cj.jdbc.Driver");
	/* Connect to database */
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Bank?serverTimezone=EST", "bank" , "temp");
	/* Create statement object */
		PreparedStatement createPINRow = conn.prepareStatement("insert into PIN " + " value " + 
		"(?,?)");
		createPINRow.setInt(1, idCount);
		createPINRow.setInt(2, pin);
		createPINRow.executeUpdate();
		conn.close();
	}
	
	public void getDescription(int id, char type, String description) throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Bank?serverTimezone=EST", "bank" , "temp");
		PreparedStatement createDesc = conn.prepareStatement("insert into Transactions " + " value " + "(?,?,?)");
		String convertType = Character.toString(type);
		createDesc.setInt(1, id);
		createDesc.setString(2, convertType);
		createDesc.setString(3, description);
		createDesc.executeUpdate();
	}
		
	/* Method insertIdCount UPDATES id column in database */
	public void insertIdCount(int idCount) throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Bank?serverTimezone=EST", "bank" , "temp");
		Statement statement1 = conn.createStatement();
		statement1.executeUpdate("update Accounts set id = '" + idCount + "' where lastName = 'noname'");
		conn.close();
	}
	
	/* Method insertChkBalance UPDATES chkBalance column in database */
	public void insertChkBalance(int idCount, double chkBalance) throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Bank?serverTimezone=EST", "bank" , "temp");
		Statement statement1 = conn.createStatement();
		statement1.executeUpdate("update Accounts set chkBalance = '" + chkBalance + "' where id = '" + idCount + "'");
		conn.close();
		
	}
	
	/* Method insertSavBalance UPDATES savBalance column in database*/
	public void insertSavBalance(int idCount, double savBalance) throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Bank?serverTimezone=EST", "bank" , "temp");
		Statement statement1 = conn.createStatement();
		statement1.executeUpdate("update Accounts set savBalance = '" + savBalance + "' where id = '" + idCount + "'");
		conn.close();
	}
	
	/* Method insertFirstName UPDATES firstName column in database */
	public void insertFirstName(String firstName, int idCount) throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Bank?serverTimezone=EST", "bank" , "temp");
		Statement statement1 = conn.createStatement();
		statement1.executeUpdate("update Accounts set firstName='" + firstName + "'where id = '" + 
		idCount + "'");
	}
	
	
	/* Method insertLastName UPDATES lastName column */
	public void insertLasttName(String lastName, int idCount) throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Bank?serverTimezone=EST", "bank" , "temp");
		Statement statement1 = conn.createStatement();
		statement1.executeUpdate("update Accounts set lastName='" + lastName + "'where id = '" + 
		idCount + "'");
		conn.close();
	}
	
	/* Method existingAccounts queries database for existing accounts */
	public int existingAccounts(int tempId) throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Bank?serverTimezone=EST", "bank" , "temp");
		Statement statement1 = conn.createStatement();
		ResultSet rset = statement1.executeQuery("select id from Accounts where id = '" + tempId + "'");
		while(rset.next()) {
			tempId = rset.getInt(1);
		}
		conn.close();
		return tempId;
	}
	
	/* Method existingPin queries existing PIN number in database */
	public int existingPIN(int id) throws SQLException, ClassNotFoundException {
		int pin = 0; 
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Bank?serverTimezone=EST", "bank" , "temp");
		Statement statement1 = conn.createStatement();
		ResultSet rset = statement1.executeQuery("select pin from PIN where id = '" + id + "'");
		while(rset.next()) {
			pin = rset.getInt(1);
		}
		conn.close();
		return pin;
	}
	
	/* Method getChkBalance queries chkBalance from database */
	public double getChkBalance(int id) throws SQLException, ClassNotFoundException {
		double chkBalance = 0;
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Bank?serverTimezone=EST", "bank" , "temp");
		Statement statement1 = conn.createStatement();
		ResultSet rset = statement1.executeQuery("select chkBalance from Accounts where id = '" + id + "'");
		while(rset.next()) {
			chkBalance = rset.getDouble(1);
		}
		conn.close();
		return chkBalance;
	}
	
	/* Method getSavBalance queries savBalance from database */
	public double getSavBalance(int id) throws SQLException, ClassNotFoundException {
		double savBalance = 0;
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Bank?serverTimezone=EST", "bank" , "temp");
		Statement statement1 = conn.createStatement();
		ResultSet rset = statement1.executeQuery("select savBalance from Accounts where id = '" + id + "'");
		while(rset.next()) {
			savBalance = rset.getDouble(1);
		}
		conn.close();
		return savBalance;
	}
	
	/* Method getFirstName queries firstName from database */
	public String getFirstName(int id) throws SQLException, ClassNotFoundException {
		String firstName = null;
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Bank?serverTimezone=EST", "bank" , "temp");
		Statement statement1 = conn.createStatement();
		ResultSet rset = statement1.executeQuery("select firstName from Accounts where id = '" + id + "'");
		while(rset.next()) {
			firstName = rset.getString(1);
		}
		conn.close();
		return firstName;
	}
	
	/* Method getLastName queries lastName from database */
	public String getLastName(int id) throws SQLException, ClassNotFoundException {
		String lastName = null;
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Bank?serverTimezone=EST", "bank" , "temp");
		Statement statement1 = conn.createStatement();
		ResultSet rset = statement1.executeQuery("select lastName from Accounts where id = '" + id + "'");
		while(rset.next()) {
			lastName = rset.getString(1);
		}
		conn.close();
		return lastName;
	}
	
	/* Method getId queries database for existing accounts */
	public int getId(int id) throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Bank?serverTimezone=EST", "bank" , "temp");
		Statement statement1 = conn.createStatement();
		ResultSet rset = statement1.executeQuery("select id from Accounts where id = '" + id + "'");
		while(rset.next()) {
			id = rset.getInt(1);
		}
		conn.close();
		return id;
	}

}
	
