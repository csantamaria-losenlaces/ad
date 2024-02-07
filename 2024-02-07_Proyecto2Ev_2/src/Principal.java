import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Principal {

	// Declaración de variables globales
	private static Statement stmt;
	private static ArrayList<Pedido> listaPedidos = new ArrayList<>();
	private static ArrayList<LineaPedido> listaLineasPedidos = new ArrayList<>();
	
	public static void main(String[] args) {
		
		

	}
	
	// Método para realizar la conexión con la BB.DD.
	private static void conectarBBDD() {

		try {
			// Declaración del objeto "Connection" que toma como argumento el URI de la BB.DD.
			Connection conn = DriverManager.getConnection("jdbc:sqlite:bbddpedidos.db");
			// Carga la librería JDBC
			Class.forName("org.sqlite.JDBC");
			// Crea el statement a partir del objeto "Connection"
			stmt = conn.createStatement();
		} catch (ClassNotFoundException cnf) {
			System.out.println("Ha ocurrido un error al cargar la clase");
		} catch (SQLException sql) {
			System.out.println("Ha ocurrido una excepción SQL");
		}

	}
	
	// Método que añade a diferentes ArrayList los pedidos, clientes y artículos existentes en la BB.DD.
    private static void leerBBDD() throws ClassNotFoundException, SQLException {

    	// Conexión a la BB.DD
    	conectarBBDD();

        // Guardado de todos los valores "id_pedido" en la tabla "pedidos"
    	ResultSet rsPedidos = stmt.executeQuery("SELECT id_pedido FROM pedidos");
        //while (rsPedidos.next()) listaPedidos.add(rsPedidos.getString("id_pedido"));

        // Cierre del recurso de tipo "Statement"
        stmt.close();

    }

}