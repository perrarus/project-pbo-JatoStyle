package JatoStyle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class Konektor {
    private String driver   = "com.mysql.cj.jdbc.Driver";
    private String db       = "jdbc:mysql://localhost:3306/jatostyle";
    private String user     = "root";      
    private String password = "";      
    private Connection conn = null;
    private Statement state = null;
    private ResultSet rs = null;
    private boolean isConnected = false; 
    
    public Konektor(){
        try {
            Class.forName(driver);
            System.out.println("Driver loaded successfully");
        } catch (Exception e){
            System.out.println("Driver Error: " + e.getMessage());
            return;
        }
        
        try {
            conn = DriverManager.getConnection(db, user, password);
            state = conn.createStatement();
            isConnected = true;
            System.out.println("Database Connected Successfully");
        } catch (Exception e) {
            System.out.println("Connection Error: " + e.getMessage());
            System.out.println("Database URL: " + db);
            System.out.println("Username: " + user);
            isConnected = false;
        }
    }

    // insert update delete
    public void query(String SQLString) {
        if (!isConnected || state == null) {
            System.out.println("Cannot execute query - Database not connected");
            System.out.println("Query: " + SQLString);
            return;
        }
        
        try {
            state.executeUpdate(SQLString);
            System.out.println("Query executed: " + SQLString);
        } catch (Exception e) {
            System.out.println("Query Error: " + e.getMessage());
            System.out.println("Failed Query: " + SQLString);
        }
    }

    // select
    public ResultSet getData(String SQLString) {
        if (!isConnected || state == null) {
            System.out.println("Cannot execute query - Database not connected");
            System.out.println("Query: " + SQLString);
            return null;
        }
        
        try {
            rs = state.executeQuery(SQLString);
            System.out.println("Query executed: " + SQLString);
            return rs;
        } catch (Exception e) {
            System.out.println("GetData Error: " + e.getMessage());
            System.out.println("Failed Query: " + SQLString);
            return null;
        }
    }
    
    // method untuk cek koneksi
    public boolean isConnected() {
        return isConnected;
    }
    
    // method untuk test koneksi
    public void testConnection() {
        if (isConnected && state != null) {
            try {
                ResultSet testRs = state.executeQuery("SELECT 1");
                if (testRs.next()) {
                    System.out.println("Database connection TEST PASSED");
                }
            } catch (Exception e) {
                System.out.println("Database connection TEST FAILED: " + e.getMessage());
            }
        } else {
            System.out.println("Database NOT CONNECTED");
        }
    }
    
    public static Connection getConnection() throws SQLException {
        // Mendefinisikan ulang variabel yang diperlukan secara lokal
        String driver = "com.mysql.cj.jdbc.Driver";
        String db = "jdbc:mysql://localhost:3306/jatostyle";
        String user = "root";
        String password = "";

        // Pemuatan driver harus dilakukan sekali, tapi kita tambahkan try/catch 
        // untuk memastikan ia tersedia saat dipanggil.
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
            throw new SQLException("JDBC Driver not found.", e);
        }

        return DriverManager.getConnection(db, user, password);
    }
}