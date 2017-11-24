import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASSWORD = "test";

    public static void main(String[] args) throws SQLException, IOException {

        Scanner sc = new Scanner(System.in);
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

        PreparedStatement stmt =
                        conn.prepareStatement("SELECT * FROM my_company.employees WHERE salary > ?");


                System.out.print("Please enter the salary you want to check: ");
                String salary = sc.nextLine();
                System.out.println();
                stmt.setDouble(1, Double.parseDouble(salary));
                ResultSet rs = stmt.executeQuery();

                while(rs.next()){
                    System.out.println(rs.getString("first_name") + " " + rs.getString("last_name"));
                }
                conn.close();
    }


    public static void createDB(Connection conn) throws IOException, SQLException {
        ScriptRunner sr = new ScriptRunner(conn, false, true);
        Reader reader = new FileReader(new File("src/main/resources/sql-scripts/my_company_db.sql"));
        sr.runScript(reader);
    }
}



