package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 * Database object containing the SQL database connection and information regarding how to interact with it
 * @author Nick Schillaci, Brandon Dixon
 *
 */
public class SQLDatabase {

	private String databaseFileName = "database.db"; //will be serialized and saved when we allow changing the database name
	private Connection c = null;
	private Statement stmt = null;
	
	/**
	 * Create SQLDatabase object and initialize connection to the database file
	 */
	public SQLDatabase() {
	    try {
	    	Class.forName("org.sqlite.JDBC");
	    	c = DriverManager.getConnection("jdbc:sqlite:" + databaseFileName);
	    	c.setAutoCommit(false);
	    	
	    	//TODO initialize the database only if its size is zero (we can revisit this later once we integrate renaming/creating new databases) -Nick
	    	/*boolean isEmpty = true;
	    	stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM TERMS;");
			if (rs.next()) { //loop through entries in the database
				isEmpty = false;
			}
			
			if (isEmpty) {
				initTable();
			}*/
			
			
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	/**
	 * Closes connection to the SQLDatabase
	 */
	public void closeConnection() {
		try {
	    	c.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * Creates table for use. Only for use when initially creating a new database file
	 * @throws SQLException
	 */
	public void initTable() throws SQLException {
		stmt = c.createStatement();
    	String sql = "CREATE TABLE TERMS " +
    				 "(TERM VARCHAR(50), " +
    				 "SCORE INT NOT NULL);";
    	stmt.executeUpdate(sql);
    	stmt.close();
		c.commit();
	}
	
	/**
	 * Performs a SQL INSERT operation on the database
	 * @param String term to insert
	 * @param int score to assign to the term
	 * @throws SQLException
	 */
	public void addTerm(int term, int score) throws SQLException {
    	stmt = c.createStatement();
    	String sql = "INSERT INTO TERMS (TERM,SCORE) " +
    				 "VALUES (\'" + term + "\', " + score + " );";
    	stmt.executeUpdate(sql);
    	stmt.close();
		c.commit();
	}
	
	/**
	 * Performs a SQL DELETE operation on the database
	 * @param String term to remove
	 * @throws SQLException
	 */
	public void removeTerm(int term) throws SQLException {
		stmt = c.createStatement();
		String sql = "DELETE FROM TERMS WHERE TERM=\'" + term + "\';";
		stmt.executeUpdate(sql);
		stmt.close();
		c.commit();
	}
	
	/**
	 * Performs a SQL DELETE operation on the database (removes all terms). Maintains the table and columns
	 * (SQLite doesn't support DELETE * or TRUNCATE)
	 * @throws SQLException
	 */
	public void removeAll() throws SQLException {
		stmt = c.createStatement();
		String sql = "DELETE FROM TERMS;";
		stmt.executeUpdate(sql);
		stmt.close();
		c.commit();
	}
	
	/**
	 * Performs a SQL UPDATE operation on the database to change a term's score
	 * @param String term to change score of
	 * @param int score to change previous score to
	 * @throws SQLException
	 */
	public void changeScore(int term, int score) throws SQLException {
		stmt = c.createStatement();
		String sql = "UPDATE TERMS SET SCORE = " + score + " WHERE TERM='" + term + "';";
		stmt.executeUpdate(sql);
		stmt.close();
		c.commit();
	}
	
	/**
	 * Performs a SQL SELECT operation to query the database for all terms and scores
	 * @return HashMap<%TERM%,%SCORE%> of all terms in the database
	 * @throws SQLException 
	 */
	public HashMap<Integer,Integer> getTerms() throws SQLException {
		HashMap<Integer,Integer> terms = new HashMap<Integer,Integer>();
		stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM TERMS;");
		while(rs.next()) { //loop through entries in the database
			terms.put(rs.getInt("TERM"), rs.getInt("SCORE"));
		}
		rs.close();
		stmt.close();
		c.commit();
		return terms;
	}
	
	/**
	 * @return the file name of the database
	 */
	public String getDatabaseFileName() {
		return databaseFileName;
	}
	
}