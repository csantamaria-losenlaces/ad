import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.TreeMap;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.Objects;

public class Principal {

	// Declaración de variables globales
	private static Connection conn;
	private static Statement stmt;
	
	private static ODB odb;
	
	private static ArrayList<Pedido> listaPedidos = new ArrayList<>();
	private static ArrayList<LineaPedido> listaLineasPedidos = new ArrayList<>();
	
	private static TreeMap<Integer, Float> udsArticulosVendidos = new TreeMap<>();
	private static TreeMap<Integer, Integer> frecArticulosVendidos = new TreeMap<>();
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		//leerBBDD();
		
		//insertarObjetos(listaPedidos);
		//insertarObjetos(listaLineasPedidos);
		
		System.out.println("Hay " + contarObjetos("Pedido") + " pedido/s en la B.D.");
		System.out.println("Hay " + contarObjetos("LineaPedido") + " línea/s de pedido/s en la B.D.");
		
		totalArticulosVendidos();

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
    private static void leerBBDD() throws ClassNotFoundException, SQLException {

    	// Conexión a la BB.DD
    	conectarBD("SQL");

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
        
        // Cierre del recurso de tipo "Statement"
        stmt.close();
        conn.close();

    }
    
    // Método que recibe un ArrayList de objetos por parámetro y lo inserta en la B.D.
    private static void insertarObjetos(ArrayList<?> listaObjetos) {
    	
    	conectarBD("ODB");
    	
    	for (Object o : listaObjetos) {
			
    		odb.store(o);
    		
		}
    	
    	odb.close();
    	System.out.println("Se han terminado de insertar los objetos en la base de datos");
    	
    }
    
    // Método que recibe por parámetro un tipo de objeto y devuelve la cantidad encontrada en la B.D.
    private static int contarObjetos(String tipoObjeto) {
    	
        conectarBD("ODB");
        Objects<?> objetos;
        
        switch (tipoObjeto) {
        
        case "Pedido":
        	objetos = odb.getObjects(Pedido.class);
        	odb.close();
        	return objetos.size();
        	
        case "LineaPedido":
        	objetos = odb.getObjects(LineaPedido.class);
        	odb.close();
        	return objetos.size();
        }
        
        return -1;
        
    }
    
    // Método que lista y contabiliza la frecuencia de los artículos
    private static void totalArticulosVendidos() {
    	
    	conectarBD("ODB");
    	Objects<LineaPedido> lineasPedidos = odb.getObjects(LineaPedido.class);
    	
    	while (lineasPedidos.hasNext()) {
    		
    		LineaPedido lineaActual = lineasPedidos.next();
    		int idArticulo = lineaActual.getIdArticulo();
    		float cantPedida = lineaActual.getCantidadPedida();
    		
    		udsArticulosVendidos.compute(idArticulo, (clave, valAnterior) -> valAnterior == null ? cantPedida : valAnterior + cantPedida);
    		
    		frecArticulosVendidos.compute(idArticulo, (clave, valAnterior) -> valAnterior == null ? 1 : valAnterior + 1);
    		
    	}
    	
    	udsArticulosVendidos.forEach((idArticulo, udsVendidas) -> {
    	    System.out.println("Artículo: " + idArticulo + ". Unidades vendidas: " + Math.round(udsVendidas));
    	});
    	
    	frecArticulosVendidos.forEach((idArticulo, frecuenciaArticulo) -> {
    	    System.out.println("Artículo: " + idArticulo + ". Pedidos en los que se ha vendido: " + frecuenciaArticulo);
    	});
    	
    	odb.close();
    	
    }
    
    

}