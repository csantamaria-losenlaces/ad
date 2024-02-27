package examenprueba;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ObjectValues;
import org.neodatis.odb.Objects;
import org.neodatis.odb.Values;
import org.neodatis.odb.core.query.IValuesQuery;
import org.neodatis.odb.impl.core.query.values.ValuesCriteriaQuery;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Principal {

	// Declaración de variables globales
	private static final String RUTA_XML = "ventas.xml";
	private static final String RUTA_ODB = "ventasODB.odb";
	private static final String RUTA_SQL = "jdbc:sqlite:ventasSQL.db";
	
	// Método principal que se ejecuta al lanzar el programa
	public static void main(String[] args) {
		
		// procesarXML();
		// unidadesVendidas();
		// ventasPorColores();
		// productoMasVendido();
		neodatisASqlite();
		
	}

	// Método que procesa el XML y lo inserta en la ODB
	private static void procesarXML() {
		
		// Declaración de variables 
		ODB odb = ODBFactory.open(RUTA_ODB);
		DocumentBuilderFactory dbf;
	    DocumentBuilder db;
	    Document d;
	    NodeList ventas;
	    ArrayList<Venta> listaVentas = new ArrayList<>();
	    
	    // Configuración del analizador de documentos XML
		dbf = DocumentBuilderFactory.newInstance();

		try {
			// Creación del analizador de documentos XML
			db = dbf.newDocumentBuilder();
			
			// Parseo del archivo XML y creación del documento XML
			d = db.parse(new File(RUTA_XML));
			
			// Normalización de la estructura del documento
			d.getDocumentElement().normalize();

			// Obtención de la lista de nodos "pedido" en el documento XML
			ventas = d.getElementsByTagName("venta");
			
			// Se recorre la lista de nodos "pedido"
			for (int v = 0; v < ventas.getLength(); v++) {

				// Obtención del nodo "venta" en la posición v
				Node nodoVenta = ventas.item(v);

				// Verificación de que el nodo sea un elemento
				if (nodoVenta.getNodeType() == Node.ELEMENT_NODE) {

					// Conversión del nodo a un elemento para facilitar el trabajo con él
					Element elementoVenta = (Element) nodoVenta;

					// Obtención y guardado en una varaible de los valores del las etiquetas del XML
					String fecha = obtenerNodo("fecha", elementoVenta);
					String producto = obtenerNodo("producto", elementoVenta);
					String talla = obtenerNodo("talla", elementoVenta);
					String color = obtenerNodo("color", elementoVenta);
					String precio = obtenerNodo("precio", elementoVenta);
					String cantidad = obtenerNodo("cantidad", elementoVenta);
					
					// Agregación de un nuevo objeto Venta al ArrayList "listaVentas"
					listaVentas.add(new Venta(fecha, producto, talla, color, Double.parseDouble(precio), Integer.parseInt(cantidad)));
					
					// Bucle que recorre el ArrayList "listaVentas" y agrega sus objetos a la ODB
					for (Venta objetoVenta : listaVentas) {
						odb.store(objetoVenta);
					}
					
				}
				
			}
			
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		
		odb.close();
		
	}
	
	// Método para obtener el valor de un nodo dado su nombre de etiqueta y el elemento padre
    private static String obtenerNodo(String etiqueta, Element e) {

        // Obtención de la lista de nodos con el nombre de la etiqueta
        NodeList nodos = e.getElementsByTagName(etiqueta).item(0).getChildNodes();

        // Obtención del primer nodo hijo y devolución de su valor como un String
        Node nodo = nodos.item(0);
        
        return nodo.getNodeValue();

    }
    
    // Método para calcular el total de unidades vendidas (ODB)
    private static void unidadesVendidas() {
    	
    	// Declaración de variables 
    	ODB odb = ODBFactory.open(RUTA_ODB);
    	
    	// Objeto que contiene la consulta ODB
    	IValuesQuery ivqUdsVendidas = new ValuesCriteriaQuery(Venta.class).sum("cantidad");
    	// Objeto que contiene el resultado de la consulta
    	Values vUdsVendidas = odb.getValues(ivqUdsVendidas);
    	
    	if (vUdsVendidas.hasNext()) {
    		// Se obtienen los valores del objeto de respuesta
    		ObjectValues ov = (ObjectValues) vUdsVendidas.next();
    		// Se imprime por consola el resultado
    		System.out.println("Unidades vendidas en total: " + ov.getByIndex(0));
    	}
    	
    	// Cierra la conexión con la base de datos de objetos
    	odb.close();
    	
    }
    
    // Método para calcular el total de unidades vendidas por cada color (ODB)
	private static void ventasPorColores() {
		
		// Declaración de variables 
		ODB odb = ODBFactory.open(RUTA_ODB);
		
		// Objeto que contiene la consulta ODB
		IValuesQuery ivqVentasPorColores = new ValuesCriteriaQuery(Venta.class).field("color").sum("cantidad").groupBy("color");
		
		// Objeto que contiene el resultado de la consulta
		Values vVentasPorColores = odb.getValues(ivqVentasPorColores);
		
		while (vVentasPorColores.hasNext()) {
			
			// Se obtienen los valores del objeto de respuesta
			ObjectValues ov = (ObjectValues) vVentasPorColores.next();
			
			// Se imprime por consola el resultado
			System.out.println("Ventas del color " + ov.getByIndex(0) + ": " + ov.getByIndex(1));
		}
		
		// Cierra la conexión con la base de datos de objetos
		odb.close();
		
	}
	
	// Método para calcular el producto más vendido (ODB)
	private static void productoMasVendido() {
		
		// Declaración de variables 
		ODB odb = ODBFactory.open(RUTA_ODB);
		String nomProdMasVendido = "";
		int udsProdMasVendido = 0;
		
		// Objeto que contiene la consulta ODB
		IValuesQuery ivqProductoMasVendido = new ValuesCriteriaQuery(Venta.class).field("producto").sum("cantidad").groupBy("producto");
		ivqProductoMasVendido.max("cantidad");
		// Objeto que contiene el resultado de la consulta
		Values vProductoMasVendido = odb.getValues(ivqProductoMasVendido);
		
		while (vProductoMasVendido.hasNext()) {
			// Se obtienen los valores del objeto de respuesta
			ObjectValues ov = (ObjectValues) vProductoMasVendido.next();
			
			String nombreArticulo = (String) ov.getByIndex(0);
			BigDecimal sumaUdsVendidas = (BigDecimal) ov.getByIndex(1);
			int sumaUdsVendidasInt = sumaUdsVendidas.intValue();
			
			// Se imprime por consola el resultado
			System.out.println("Producto: " + nombreArticulo + ". Ud/s vendida/s: " + sumaUdsVendidas);
			
			if (sumaUdsVendidasInt > udsProdMasVendido) {
				nomProdMasVendido = nombreArticulo;
				udsProdMasVendido = sumaUdsVendidasInt;
			}
		}
		
		System.out.println("Producto más vendido: " + nomProdMasVendido + ". Ud/s: " + udsProdMasVendido);
		
		// Cierra la conexión con la base de datos de objetos
		odb.close();
		
	}
	
	private static ArrayList<Venta> exportarNeodatis() {

		// Declaración de variables
		ODB odb = ODBFactory.open(RUTA_ODB);
		ArrayList<Venta> listaVentas = new ArrayList<>();

		// Objeto que contiene la consulta ODB
		IValuesQuery ivqVentasPorColores = new ValuesCriteriaQuery(Venta.class).field("fecha").field("producto").field("talla")
				.field("color").field("precio").field("cantidad");

		// Objeto que contiene el resultado de la consulta
		Values vVentasPorColores = odb.getValues(ivqVentasPorColores);

		while (vVentasPorColores.hasNext()) {

			// Se obtienen los valores del objeto de respuesta
			ObjectValues ov = (ObjectValues) vVentasPorColores.next();
			String fecha = (String) ov.getByIndex(0);
			String producto = (String) ov.getByIndex(1);
			String talla = (String) ov.getByIndex(2);
			String color = (String) ov.getByIndex(3);
			Double precio = (Double) ov.getByIndex(4);
			Integer cantidad = (Integer) ov.getByIndex(5);

			listaVentas.add(new Venta(fecha, producto, talla, color, precio, cantidad));	
			
		}
		
		// Cierra la conexión con la base de datos de objetos
		odb.close();
		
		return listaVentas;

	}
	
	private static void neodatisASqlite() {
		
		// Declaración de variables
		ArrayList<Venta> listaObjetosVenta = new ArrayList<>();
		listaObjetosVenta = exportarNeodatis();
		
		crearTablas();
		
		try {
			// Conexión a la B.D.
			Statement stmt = conectarBD();

			for (Venta v : listaObjetosVenta) {
				
				String sentenciaTablaVentas = String.format("INSERT INTO ventas VALUES ('%s', '%s', '%s', '%s', '%s', '%s')",
						v.getFecha(), v.getProducto(), v.getTalla(), v.getColor(), v.getPrecio(), v.getCantidad());
				
				stmt.executeUpdate(sentenciaTablaVentas);
				
			}

			// Cierre del recurso de tipo "Statement"
			stmt.close();
			
		} catch (SQLException sql) {
			
			sql.printStackTrace();
			
		}

	}
	
	private static void crearTablas() {

		// Conexión a la B.D.
		Statement stmt = conectarBD();
		
        // Declaración de variables globales que contienen sentencias SQL
    	final String CREAR_TABLA_VENTAS = "CREATE TABLE IF NOT EXISTS ventas ("
                + "fecha TEXT, producto TEXT, talla TEXT, "
                + "color TEXT, precio REAL, cantidad INTEGER"
                + ")";

        try {
        	// Ejecución de la sentencia de creación de la tablas
        	stmt.executeUpdate(CREAR_TABLA_VENTAS);
	        // Cierre del recurso de tipo "Statement"
	        stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

    }
	
	// Método para realizar la conexión con la B.D.
    private static Statement conectarBD() {

        try {
            // Declaración del objeto "Connection" que toma como argumento el URI de la B.D.
        	Connection conn = DriverManager.getConnection(RUTA_SQL);
            // Carga la librería JDBC
        	Class.forName("org.sqlite.JDBC");
            // Crea el statement a partir del objeto "Connection"
        	return conn.createStatement();
        } catch (ClassNotFoundException cnf) {
            System.out.println("Ha ocurrido un error al cargar la clase");
        } catch (SQLException sql) {
            System.out.println("Ha ocurrido una excepción SQL");
        }
        
        return null;
        
    }

}