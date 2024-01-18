import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConectarBase {

	public static void main(String[] args) {
		
		try {
			Class.forName("org.sqlite.JDBC");
			Connection connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/Carlos/Downloads/sqlite-tools-win-x64-3450000/test.db");
			Statement statement = connection.createStatement();
			
			statement.executeUpdate("INSERT INTO tabla1 VALUES ('TEXTO9', 'TEXTO10')");
			
			ResultSet resultSet = statement.executeQuery("SELECT * FROM tabla1");
			
			while (resultSet.next()) {
				System.out.println("Campo 1: " + resultSet.getString("campo1"));
				System.out.println("Campo 2: " + resultSet.getString("campo2"));
				System.out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}