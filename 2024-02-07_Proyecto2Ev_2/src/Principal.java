import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ObjectValues;
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

		// Método para crear la B.D.
		insertarObjetos();

		// Puntos 1 y 2
		contPedidos();

		// Punto 3
		frecArticulos();

		// Punto 4
		pedidosPorCliente();

		// Punto 5
		ventasPorArticulo();

		// Punto 6
		udsPorPedido();

		// Punto 7
		mediaArtPorPedido();

	}
	
	// Método para realizar la conexión con la B.D.
	private static void conectarBD(String tipoBD) {

		// Realiza la conexión con la base de datos SQL u OBD, según el parámetro recibido
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
				// Mensaje informando que no se ha podido cargar la clase
				System.out.println("Ha ocurrido un error al cargar la clase");
			} catch (SQLException sql) {
				// Mensaje indicando que SQL ha lanzado una excepción
				System.out.println("Ha ocurrido una excepción SQL");
			}
			
			break;
			
		case "ODB":
			
			// Abre la conexión con la B.D. orientada a objetos
			odb = ODBFactory.open("pedidos.odb");
			
			break;
			
		}
		
	}
	
	// Método que añade a diferentes ArrayList los pedidos, clientes y artículos existentes en la B.D.
    private static void insertarObjetos() throws SQLException {

    	// Conexión a la BB.DD
    	conectarBD("SQL");
    	
    	// Declaración de dos ArrayList con objetos Pedido y LineaPedido, respectivamente
    	ArrayList<Pedido> listaPedidos = new ArrayList<>();
    	ArrayList<LineaPedido> listaLineasPedidos = new ArrayList<>();

        // ResultSet para guardar los resultados de la consulta especificada 
    	ResultSet rsPedidos = stmt.executeQuery("SELECT * FROM pedidos");
        
        // Recorre cada línea de resultados del ResultSet
    	while (rsPedidos.next()) {
        	// Obtiene el valor de la columna de SQL "id_pedido"
    		int idPedido = rsPedidos.getInt("id_pedido");
    		// Obtiene el valor de la columna de SQL "id_cliente"
    		int idCliente = rsPedidos.getInt("id_cliente");
    		// Obtiene el valor de la columna de SQL "fecha_pedido"
    		String fechaPedido = rsPedidos.getString("fecha_pedido");
        	// Añade al ArrayList un nuevo objeto Pedido con los tres parámetros recuperados
    		listaPedidos.add(new Pedido(idPedido, idCliente, fechaPedido));
        }
        
        // ResultSet para guardar los resultados de la consulta especificada
        ResultSet rsArticulosPedidos = stmt.executeQuery("SELECT * FROM articulos_pedidos");
        
     // Recorre cada línea de resultados del ResultSet
        while (rsArticulosPedidos.next()) {
        	// Obtiene el valor de la columna de SQL "id_pedido"
        	int idPedido = rsArticulosPedidos.getInt("id_pedido");
        	// Obtiene el valor de la columna de SQL "id_articulo"
        	int idArticulo = rsArticulosPedidos.getInt("id_articulo");
        	// Obtiene el valor de la columna de SQL "cantidad_pedida"
        	float cantidadPedida = rsArticulosPedidos.getFloat("cantidad_pedida");
        	// Añade al ArrayList un nuevo objeto LineaPedido con los tres parámetros recuperados
        	listaLineasPedidos.add(new LineaPedido(idPedido, idArticulo, cantidadPedida));
        }
        
        // Cierra los recursos Statement y Connection
        stmt.close();
        conn.close();
        
        // Realiza la conexión con la B.D.O.O.
        conectarBD("ODB");
        
        // Recorre el ArrayList de objetos Pedido
        for (Pedido p : listaPedidos) {
			// Inserta el objeto en la base de datos
        	odb.store(p);
		}
        
        // Recorre el ArrayList de objetos LineaPedido
        for (LineaPedido lp : listaLineasPedidos) {
        	// Inserta el objeto en la base de datos
        	odb.store(lp);
		}
        
		// Cierra la conexión con la base de datos de objetos
        odb.close();
        
        System.out.println("Se han terminado de insertar los objetos en la base de datos");

    }
    
    // Método que devuelve el número de pedidos y líneas de pedidos en la B.D.
    private static void contPedidos() throws SQLException {
    	
    	// Realiza la conexión con la B.D.O.O.
    	conectarBD("ODB");
        
        // Contador de pedidos
        
    	// Objeto que contiene la consulta ODB
    	IValuesQuery ivqContadorPedidos = new ValuesCriteriaQuery(Pedido.class).count("idPedido");
        // Objeto que contiene el resultado de la consulta
    	Values vContadorPedidos = odb.getValues(ivqContadorPedidos);
        
        // Se comprueba si el objeto contiene al menos un resultado
    	if (vContadorPedidos.hasNext()) {
        	// Se obtienen los valores del objeto de respuesta
    		ObjectValues ov = (ObjectValues) vContadorPedidos.next();
        	// Se imprime por consola el resultado
    		System.out.println("Pedidos recibidos y procesados correctamente: " + ov.getByAlias("idPedido"));
        }
    	
    	
    	// Contador de líneas de pedido
    	
    	// Objeto que contiene la consulta ODB
    	IValuesQuery ivqContadorLineasPedido = new ValuesCriteriaQuery(LineaPedido.class).count("idPedido");
    	// Objeto que contiene el resultado de la consulta
    	Values vContadorLineasPedido = odb.getValues(ivqContadorLineasPedido);
        
    	// Se comprueba si el objeto contiene al menos un resultado
    	if (vContadorLineasPedido.hasNext()) {
    		// Se obtienen los valores del objeto de respuesta
    		ObjectValues ov = (ObjectValues) vContadorLineasPedido.next();
    		// Se imprime por consola el resultado
    		System.out.println("Líneas de pedido recibidas y procesadas correctamente: " + ov.getByAlias("idPedido"));
        }
    	
        // Cierra la conexión con la base de datos de objetos
        odb.close();
        
    }
    
    // Método que muestra los artículos diferentes recibidos y en cuántos pedidos se encuentran
    private static void frecArticulos() {
    	
    	// Realiza la conexión con la B.D.O.O.
    	conectarBD("ODB");
    	
    	// Objeto que contiene la consulta ODB
    	IValuesQuery ivqArticulosFrecuencia = new ValuesCriteriaQuery(LineaPedido.class).count("idArticulo").field("idArticulo").groupBy("idArticulo");
    	// Objeto que contiene el resultado de la consulta
    	Values vArticulosFrecuencia = odb.getValues(ivqArticulosFrecuencia);
		
		while (vArticulosFrecuencia.hasNext()) {
			// Se obtienen los valores del objeto de respuesta
			ObjectValues ov = (ObjectValues) vArticulosFrecuencia.next();
			// Se imprime por consola el resultado
			System.out.println("El artículo " + ov.getByIndex(1) + " aparece en " + ov.getByIndex(0) + " pedido/s");
		}
		
		// Cierra la conexión con la base de datos de objetos
		odb.close();
    	
    }
    
    // Método que lista cuántos pedidos ha realizado cada cliente
    private static void pedidosPorCliente() {
    	
    	// Realiza la conexión con la B.D.O.O.
    	conectarBD("ODB");
    	
    	// Objeto que contiene la consulta ODB
    	IValuesQuery ivqPedidosPorCliente = new ValuesCriteriaQuery(Pedido.class).count("idPedido").field("idCliente").groupBy("idCliente");
    	// Objeto que contiene el resultado de la consulta
    	Values vPedidosPorCliente = odb.getValues(ivqPedidosPorCliente);
		
		while (vPedidosPorCliente.hasNext()) {
			// Se obtienen los valores del objeto de respuesta
			ObjectValues ov = (ObjectValues) vPedidosPorCliente.next();
			// Se imprime por consola el resultado
			System.out.println("El cliente " + ov.getByIndex(1) + " ha realizado " + ov.getByIndex(0) + " pedido/s");
		}
		
		// Cierra la conexión con la base de datos de objetos
		odb.close();
    	
    }
    
    // Método que lista los artículos con las cantidades sumadas de todos los pedidos
    private static void ventasPorArticulo() {
    	
    	// Realiza la conexión con la B.D.O.O.
    	conectarBD("ODB");

    	// Objeto que contiene la consulta ODB
    	IValuesQuery ivqVentasPorArticulo = new ValuesCriteriaQuery(LineaPedido.class).sum("cantidadPedida").field("idArticulo").groupBy("idArticulo");
    	// Objeto que contiene el resultado de la consulta
    	Values vVentasPorArticulo = odb.getValues(ivqVentasPorArticulo);

		while (vVentasPorArticulo.hasNext()) {
			// Se obtienen los valores del objeto de respuesta
			ObjectValues ov = (ObjectValues) vVentasPorArticulo.next();
			// Se imprime por consola el resultado
			System.out.println("El artículo " + ov.getByIndex(1) + " ha vendido " + ov.getByIndex(0) + " ud/s");
		}

		// Cierra la conexión con la base de datos de objetos
		odb.close();
    	
    }
    
    // Método que muestra el listado de unidades pedidas por pedido
    private static void udsPorPedido() {
    	
    	// Realiza la conexión con la B.D.O.O.
    	conectarBD("ODB");
    	
    	// Objeto que contiene la consulta ODB
    	IValuesQuery ivqUdsPorPedido = new ValuesCriteriaQuery(LineaPedido.class).field("idPedido").sum("cantidadPedida").groupBy("idPedido");
    	// Objeto que contiene el resultado de la consulta
    	Values vUdsPorPedido = odb.getValues(ivqUdsPorPedido);
    	
    	while (vUdsPorPedido.hasNext()) {
    		// Se obtienen los valores del objeto de respuesta
    		ObjectValues ov = (ObjectValues) vUdsPorPedido.next();
    		// Se imprime por consola el resultado
    		System.out.printf("El pedido %d ha comprado %.0f ud/s%n", ov.getByAlias("idPedido"), ov.getByAlias("cantidadPedida"));
    	}
    	
    	// Cierra la conexión con la base de datos de objetos
    	odb.close();
    	
    }
    
	// Método que muestra la media de artículos por pedido
	private static void mediaArtPorPedido() {

		// Realiza la conexión con la B.D.O.O.
		conectarBD("ODB");

		// Objeto que contiene la consulta ODB
		IValuesQuery ivqNumArticulos = new ValuesCriteriaQuery(LineaPedido.class).count("idArticulo");
		// Objeto que contiene el resultado de la consulta
		Values vNumArticulos = odb.getValues(ivqNumArticulos);
		// Se obtienen los valores del objeto de respuesta
		ObjectValues ovNumArticulos = vNumArticulos.nextValues();
		// Se crea un objeto BigInteger con el valor de ObjectValues en primera posición
		BigInteger biNumArticulos = (BigInteger) ovNumArticulos.getByIndex(0);

		// Objeto que contiene la consulta ODB
		IValuesQuery ivqNumPedidos = new ValuesCriteriaQuery(LineaPedido.class).count("idPedido").groupBy("idPedido");
		// Objeto que contiene el resultado de la consulta
		Values vNumPedidos = odb.getValues(ivqNumPedidos);
		// Se obtienen los valores del objeto de respuesta
		ObjectValues ovNumPedidos = vNumPedidos.nextValues();
		// Se crea un objeto BigInteger con el valor de ObjectValues en primera posición
		BigInteger biNumPedidos = (BigInteger) ovNumPedidos.getByIndex(0);

		// Declaración de variable que calcula la media de artículos por pedido con decimales
		float mediaArtPorPedido = biNumArticulos.floatValue() / biNumPedidos.floatValue();
		// Se imprime por consola el resultado con dos posiciones decimales
		System.out.printf("Hay una media de %.2f artículos/pedido%n", mediaArtPorPedido);

		// Cierra la conexión con la base de datos de objetos
		odb.close();

	}


}