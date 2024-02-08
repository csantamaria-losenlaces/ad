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
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		leerBBDD();

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

        ResultSet rsPedidos = stmt.executeQuery("SELECT * FROM pedidos");
        while (rsPedidos.next()) {
        	int idPedido = rsPedidos.getInt("id_pedido");
        	int idCliente = rsPedidos.getInt("id_cliente");
        	String fechaPedido = rsPedidos.getString("fecha_pedido");
        	listaPedidos.add(new Pedido(idPedido, idCliente, fechaPedido));
        }
        
        ResultSet rsArticulosPedidos = stmt.executeQuery("SELECT * FROM articulos_pedidos");
        while (rsArticulosPedidos.next()) {
        	int idPedido = rsArticulosPedidos.getInt("id_pedido");
        	int idArticulo = rsArticulosPedidos.getInt("id_articulo");
        	float cantidadPedida = rsArticulosPedidos.getFloat("cantidad_pedida");
        	listaLineasPedidos.add(new LineaPedido(idPedido, idArticulo, cantidadPedida));
        }
        
        System.out.println("\nPedidos:");
        for (Pedido p : listaPedidos) {
			System.out.println(p.toString());
		}
        
        System.out.println("Líneas de pedido:");
        for (LineaPedido lp : listaLineasPedidos) {
			System.out.println(lp.toString());
		}

        // Cierre del recurso de tipo "Statement"
        stmt.close();

    }

}