import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.TreeMap;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ObjectValues;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IValuesQuery;
import org.neodatis.odb.impl.core.query.values.ValuesCriteriaQuery;
import org.neodatis.odb.Values;

public class Principal {

	// Declaración de variables globales SQL
	private static Connection conn;
	private static Statement stmt;
	
	// Declaración de variables globales ODB
	private static ODB odb;
	
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		// insertarObjetos();
		
		contarObjetos();
		
		recorrerLineasPedido();
		recorrerPedidos();
		
		udsPorPedido();
		
		mediaArticulos();
		
	}
	
	// Método para realizar la conexión con la B.D.
	private static void conectarBD(String tipoBD) {

		switch (tipoBD) {
		
		case "SQL":
			
			try {
				// Declaración del objeto "Connection" que toma como argumento el URI de la B.D.
				conn = DriverManager.getConnection("jdbc:sqlite:bdpedidos.db");
				// Carga la librería JDBC
				Class.forName("org.sqlite.JDBC");
				// Crea el statement a partir del objeto "Connection"
				stmt = conn.createStatement();
			} catch (ClassNotFoundException cnf) {
				System.out.println("Ha ocurrido un error al cargar la clase");
			} catch (SQLException sql) {
				System.out.println("Ha ocurrido una excepción SQL");
			}
			
			break;
			
		case "ODB":
			
			odb = ODBFactory.open("pedidos.odb");
			
			break;
			
		}
		
	}
	
	// Método que añade a diferentes ArrayList los pedidos, clientes y artículos existentes en la B.D.
    private static void insertarObjetos() throws SQLException {

    	// Conexión a la BB.DD
    	conectarBD("SQL");
    	
    	ArrayList<Pedido> listaPedidos = new ArrayList<>();
    	ArrayList<LineaPedido> listaLineasPedidos = new ArrayList<>();

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
        
        stmt.close();
        conn.close();
        
        conectarBD("ODB");
        
        for (Pedido p : listaPedidos) {
			odb.store(p);
		}
        
        for (LineaPedido lp : listaLineasPedidos) {
			odb.store(lp);
		}
        
		odb.close();
        
        System.out.println("Se han terminado de insertar los objetos en la base de datos");

    }
    
    // Método que recibe por parámetro un tipo de objeto y devuelve la cantidad encontrada en la B.D.
    private static void contarObjetos() throws SQLException {
    	
        conectarBD("ODB");
        
        Objects<Pedido> objetosPedido = odb.getObjects(Pedido.class);
    	System.out.printf("Pedidos: %d%n", objetosPedido.size());
    	
    	Objects<LineaPedido> objetosLineaPedido = odb.getObjects(LineaPedido.class);
    	System.out.printf("Líneas de pedido: %d%n", objetosLineaPedido.size());
    	
    	odb.close();
        
    }
    
    // Método recorre las líneas de pedido y muestra las uds. totales vendidas de cada artículo y el núm. de pedidos en los que aparece
    private static void recorrerLineasPedido() throws SQLException {
    	
    	conectarBD("ODB");
    	
    	TreeMap<Integer, Float> udsArticulosVendidos = new TreeMap<>();
    	TreeMap<Integer, Integer> frecArticulosVendidos = new TreeMap<>();
    	
    	Objects<LineaPedido> lineasPedidos = odb.getObjects(LineaPedido.class);
    	
    	while (lineasPedidos.hasNext()) {
    		
    		LineaPedido lineaActual = lineasPedidos.next();
    		int idArticulo = lineaActual.getIdArticulo();
    		float cantPedida = lineaActual.getCantidadPedida();
    		
    		udsArticulosVendidos.compute(idArticulo, (clave, valAnterior) -> valAnterior == null ? cantPedida : valAnterior + cantPedida);
    		
    		frecArticulosVendidos.compute(idArticulo, (clave, valAnterior) -> valAnterior == null ? 1 : valAnterior + 1);
    		
    	}
    	
    	udsArticulosVendidos.forEach((idArticulo, udsVendidas) -> {
    	    System.out.printf("Uds. vendidas del artículo %d: %.0f%n", idArticulo, udsVendidas);
    	});
    	
    	frecArticulosVendidos.forEach((idArticulo, frecuenciaArticulo) -> {
    	    System.out.printf("Pedidos donde aparece artículo %d: %d%n", idArticulo, frecuenciaArticulo);
    	});
    	
    	odb.close();
    	
    }
    
    // Método que recorre los pedidos
    private static void recorrerPedidos() {
    	
    	conectarBD("ODB");
    	
    	TreeMap<Integer, Integer> ventasPorCliente = new TreeMap<>();
    	
    	Objects<Pedido> pedidos = odb.getObjects(Pedido.class);
    	
    	while (pedidos.hasNext()) {
    		
    		Pedido pedidoActual = pedidos.next();
    		int idCliente = pedidoActual.getIdCliente();
    		
    		ventasPorCliente.compute(idCliente, (clave, valAnterior) -> valAnterior == null ? 1 : valAnterior + 1);
    		
    	}
    	
    	ventasPorCliente.forEach((idCliente, cantidadPedidos) -> {
    	    System.out.printf("Pedidos del cliente %d: %d%n", idCliente, cantidadPedidos);
    	});
    	
    	odb.close();
    	
    }
    
    // Método que muestra el listado de unidades pedidas por pedido
    private static void udsPorPedido() {
    	
    	conectarBD("ODB");
    	
    	IValuesQuery consulta = new ValuesCriteriaQuery(LineaPedido.class).field("idPedido").sum("cantidadPedida").groupBy("idPedido");
    	Values valores = odb.getValues(consulta);
    	
    	while (valores.hasNext()) {
    		
    		ObjectValues objetos = (ObjectValues) valores.next();
    		System.out.printf("Uds. compradas en el pedido %d: %.0f%n", objetos.getByAlias("idPedido"), objetos.getByAlias("cantidadPedida"));
    		
    	}
    	
    	odb.close();
    	
    }
    
    // Método que muestra el la media de artículos por pedido
    private static void mediaArticulos() {
    	
    	conectarBD("ODB");
    	
    	IValuesQuery consultaTotalUdsVendidas = new ValuesCriteriaQuery(LineaPedido.class).sum("cantidadPedida");
    	Values valoresTotalUdsVendidas = odb.getValues(consultaTotalUdsVendidas);
    	ObjectValues objetoTotalUdsVendidas = valoresTotalUdsVendidas.nextValues();
    	BigDecimal valorTotalUdsVendidas = (BigDecimal) objetoTotalUdsVendidas.getByAlias("cantidadPedida");
    	
    	// IValuesQuery consulta
    	
		System.out.printf("Artículos vendidos: %.2f%n", valorTotalUdsVendidas.floatValue());
		
		odb.close();
    	
    }

}