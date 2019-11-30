/**
 * SJSU CS 218 Fall 2019 TEAM-5
 */

package edu.sjsu.codepad.dbconfig;
import java.sql.*;

public class CodeDb {

    private static final String DB_SERVICE_ENV_VARNAME = "DB_DOMAIN";
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    static final String USER = "admin";
    static final String PASS = "password";

    private static final String INSERT_SQL = "INSERT INTO Code(codetext) VALUES(?)";
    private static final String SELECT_SQL = "SELECT * FROM Code WHERE id = ?";
    private static final String UPDATE_RESULT_SQL = "UPDATE Code set executionresult=(?) WHERE id = ?";

    private Connection conn;

    public CodeDb() {
        this.conn = null;
    }

    public boolean initialize() {
        try {
            Class.forName(JDBC_DRIVER);
            String dbUrl = System.getenv(DB_SERVICE_ENV_VARNAME);
            if (dbUrl == null || dbUrl.isEmpty()) {
                System.err.println("Variable " + DB_SERVICE_ENV_VARNAME + " not set.");
                return false;
            }
            String dbJdbcUrl = "jdbc:mysql://" + dbUrl + ":3306/codedb";
            conn = DriverManager.getConnection(dbJdbcUrl, USER, PASS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Adds new code to the database.
    // Returns the new code's id in the database.
    // Returns null if unable to add new code due to some error.
    public Integer addCode(String codeText) {
        try {
            PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, codeText);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Search code by id in the database.
    // Returns the code as string.
    // Returns null if id does not exist
    public String searchCode(int id) {
        try {

            PreparedStatement ps = conn.prepareStatement(SELECT_SQL);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("codetext");
            } else {
                return (String) null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return (String) null;
        }
    }

    // Updates code execution result to database
    public void updateResult(int id, String executionResult) {
        try {
            PreparedStatement ps = conn.prepareStatement(UPDATE_RESULT_SQL);
            ps.setInt(2, id);
            ps.setString(1, executionResult);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
