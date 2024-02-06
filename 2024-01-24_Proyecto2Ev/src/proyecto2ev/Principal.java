package proyecto2ev;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Principal {

    // Declaración de variables globales
    private static Scanner teclado = new Scanner(System.in);

    private static ArrayList<String> listaPedidos = new ArrayList<>();
    private static ArrayList<String> listaClientes = new ArrayList<>();
    private static ArrayList<String> listaArticulos = new ArrayList<>();

    private static String rutaXML = "";

    private static Statement stmt;

    private static String idCliente, idPedido, fechaPedido, idArticulo;
    private static Integer stockArticulo, cantidadPedida;

	public static void main(String[] args) {
		
	// Declaración de variables del método principal 
	DocumentBuilderFactory dbf;
    DocumentBuilder db;
    Document d;
    NodeList pedidos;
    NodeList articulos;

		try {
			if (cargarConfiguracion()) {

				// Configuración del analizador de documentos XML
				dbf = DocumentBuilderFactory.newInstance();

				// Creación del analizador de documentos XML
				db = dbf.newDocumentBuilder();

				// Parseo del archivo XML y creación del documento XML
				d = db.parse(new File(rutaXML));

				// Normalización de la estructura del documento
				d.getDocumentElement().normalize();

				// Obtención de la lista de nodos "pedido" en el documento XML
				pedidos = d.getElementsByTagName("pedido");

				// Creación la estructura de tablas y campos de la BB.DD., si todavía no existe
				crearTablas();

				// Inserción de datos de muestra para las tablas de clientes y artículos (salta una excepción controlada si ya existen)
				insertarClientesArticulos();

				// Lectura de los pedidos, clientes y artículos existentes en la BB.DD. antes de procesar el XML
				leerBBDD();

				// Se recorre la lista de nodos "pedido"
				for (int p = 0; p < pedidos.getLength(); p++) {

					// Obtención del nodo "pedido" en la posición p
					Node nodoPedido = pedidos.item(p);

					// Verificación de que el nodo sea un elemento
					if (nodoPedido.getNodeType() == Node.ELEMENT_NODE) {

						// Conversión del nodo a un elemento para facilitar el trabajo con él
						Element elementoPedido = (Element) nodoPedido;

						// Obtención de los valores de las tuplas "numero-cliente", "numero-pedido" y "fecha"
						idCliente = obtenerNodo("numero-cliente", elementoPedido);
						idPedido = obtenerNodo("numero-pedido", elementoPedido);
						fechaPedido = obtenerNodo("fecha", elementoPedido);

						// Comprobación de si el cliente existe y, en caso de estar el pedido duplicado, el usuario no ha elegido omitirlo
						if (clienteExiste() && !comprobarPedidoDuplicado().equalsIgnoreCase("O")) {

							// Inserción una nueva tupla en la tabla "pedidos"
							insertarPedido("pedidos");

							// Obtención de la lista de nodos "articulo" específicamente del pedido actual
							articulos = elementoPedido.getElementsByTagName("articulo");

							// Recorre los nodos "articulo" de un pedido
							for (int a = 0; a < articulos.getLength(); a++) {

								// Obtención del nodo "articulo" en la posición a
								Node nodoArticulo = articulos.item(a);

								// Verificación de que el nodo sea un elemento
								if (nodoArticulo.getNodeType() == Node.ELEMENT_NODE) {

									// Conversión del nodo a un elemento para facilitar el trabajo con él
									Element articuloElemento = (Element) nodoArticulo;

									// Obtención de atributos del nodo "articulo"
									idArticulo = obtenerNodo("codigo", articuloElemento).replaceFirst("^0+", "");
									cantidadPedida = Integer.parseInt(obtenerNodo("cantidad", articuloElemento));

									// Comprobación de la existencia del artículo y su stock y posterior inserción del pedido
									if (articuloExiste() && hayStock()) insertarPedido("articulos_pedidos");
								}
							}
						} else {
							System.out.println("Pedido " + idPedido + " omitido");
						}
					}
					// Eliminación del pedido en caso de estar vacío (sin ninguna tupla asociada en la tabla "articulos_pedidos")
					comprobarPedidoVacio();
					System.out.println();
				}
				// Al finalizar el procesamiento del fichero XML se mueve dentro del directorio "procesados"
				moverFicheroXML();
			}
		} catch (FileNotFoundException fileNotFoundException) {
			System.out.println("La ruta especificada no es válida");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Método para cargar archivo de configuración y definir ruta del XML de origen que devuelve "true" si se ejecuta con éxito
	private static boolean cargarConfiguracion() {

		// Declaración del objeto de tipo "Properties"
		Properties appProps = new Properties();
		
		try {
			// Carga en memoria el fichero de configuración
			appProps.load(new FileInputStream(new File("app.properties")));
		} catch (FileNotFoundException e) {
			System.out.println("No se ha encontrado el fichero de configuración");
		} catch (IOException e) {
			System.out.println("Ha ocurrido una excepción al leer el fichero de configuración");
			e.printStackTrace();
		}

		// Obtención del valor de la ruta XML contenida en el fichero de configuración
		String propiedadRuta = appProps.getProperty("rutaXML");
		
		// Comprobación de nulidad o cadena vacía en la propiedad "rutaXML"
		if (propiedadRuta == null || propiedadRuta.equals("")) {
			System.out.println("No se ha encontrado la propiedad con la ruta en el fichero de configuración");
			return false;
		} else {
			rutaXML = propiedadRuta;
			System.out.println("Ruta XML leída: " + rutaXML);
			return true;
		}

	}
    
    // Método para obtener el valor de un nodo dado su nombre de etiqueta y el elemento padre
    private static String obtenerNodo(String etiqueta, Element e) {

        // Obtención de la lista de nodos con el nombre de la etiqueta
        NodeList nodos = e.getElementsByTagName(etiqueta).item(0).getChildNodes();

        // Obtención del primer nodo hijo y devolución de su valor como un String
        Node nodo = nodos.item(0);

        return nodo.getNodeValue();

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

    // Método que crea todas las tablas, si todavía no existen 
    private static void crearTablas() throws SQLException, ClassNotFoundException {

        final String CREA_TABLA_PEDIDOS = "CREATE TABLE IF NOT EXISTS pedidos ("
                + "id_pedido INTEGER, id_cliente INTEGER, fecha_pedido TEXT, "
                + "PRIMARY KEY (id_pedido)"
                + ")";

        final String CREA_TABLA_ARTICULOS_PEDIDOS = "CREATE TABLE IF NOT EXISTS articulos_pedidos ("
                + "id_pedido INTEGER, id_articulo INTEGER, cantidad_pedida REAL, "
                + "PRIMARY KEY (id_pedido, id_articulo)"
                + ")";

        final String CREA_TABLA_CLIENTES = "CREATE TABLE IF NOT EXISTS clientes ("
                + "id_cliente INTEGER, nombre TEXT, apellidos TEXT, direccion TEXT, telefono TEXT, "
                + "PRIMARY KEY (id_cliente)"
                + ")";

        final String CREA_TABLA_ARTICULOS = "CREATE TABLE IF NOT EXISTS articulos ("
                + "id_articulo INTEGER, descripcion TEXT, familia TEXT, fecha_alta TEXT, stock INTEGER, "
                + "PRIMARY KEY (id_articulo)"
                + ")";

        // Conexión a la BB.DD.
        conectarBBDD();

        // Ejecución de las sentencias de creación de las tablas
        stmt.executeUpdate(CREA_TABLA_PEDIDOS);
        stmt.executeUpdate(CREA_TABLA_ARTICULOS_PEDIDOS);
        stmt.executeUpdate(CREA_TABLA_CLIENTES);
        stmt.executeUpdate(CREA_TABLA_ARTICULOS);

        // Cierre del recurso de tipo "Statement"
        stmt.close();

    }

    // Método que inserta un pedido en la tabla "pedidos" o "articulos_pedidos" (definido por parámetro)
    private static void insertarPedido(String nombreTabla) throws ClassNotFoundException, SQLException {

    	// Conexión a la BB.DD.
    	conectarBBDD();

        switch (nombreTabla) {
            case "pedidos":
                // Declaración de la sentencia para insertar un nuevo pedido
            	String sentenciaTablaPedidos = String.format("INSERT INTO pedidos VALUES ('%s', '%s', '%s')",
                        idPedido, idCliente, fechaPedido);
                // Ejecuta la sentencia de inserción
            	stmt.executeUpdate(sentenciaTablaPedidos);
                System.out.println("Insertado pedido en tabla \"pedidos\"");
                break;
            case "articulos_pedidos":
                // Declaración de la sentencia para insertar un artículo de un pedido
            	String sentenciaTablaArticulosPedidos = String.format("INSERT INTO articulos_pedidos VALUES ('%s', '%s', '%d')",
                        idPedido, idArticulo, cantidadPedida);
            	// Ejecuta la sentencia de inserción
            	stmt.executeUpdate(sentenciaTablaArticulosPedidos);
                System.out.println("Insertado pedido en tabla \"articulos_pedidos\"");
                break;
        }
        
        System.out.println("Inserción de datos finalizada");

        // Cierre del recurso de tipo "Statement"
        stmt.close();

    }

    // Método que inserta datos de muestra en la tabla "clientes" y "articulos"
    private static void insertarClientesArticulos() {

        try {
        	// Conexión a la BB.DD.
        	conectarBBDD();

            // Declaración de la sentencia para insertar clientes de muestra
        	String sentenciaTablaClientes = "INSERT INTO clientes VALUES "
                    + "(1234567890, 'Juan', 'Gómez', 'Paseo del Prado, 14, Madrid', '699123456'), "
                    + "(1234567891, 'Maria', 'Rodríguez', 'Calle Mayor, 45, Barcelona', '971234567'), "
                    + "(1234567892, 'Pedro', 'Fernandez', 'Calle del Carmen, 3, Málaga', '677666555'), "
                    + "(1234567893, 'Laura', 'Martínez', 'Calle de Bailén, 2, Madrid', '815551234'), "
                    + "(9876543210, 'Roberto', 'Gutiérrez', 'Gran Vía de les Corts Catalanes, 666, Barcelona', '622111000')";

        	// Declaración de la sentencia para insertar artículos de muestra
            String sentenciaTablaArticulos = "INSERT INTO articulos VALUES "
                    + "(012345, 'Portátil HP Envy', 'Electrónica', '2023-01-15', 45), "
                    + "(123456, 'Silla de Oficina Ergonómica', 'Muebles', '2023-02-22', 12), "
                    + "(123457, 'Cien años de soledad', 'Libros', '2023-03-10', 78), "
                    + "(234567, 'Cámara Digital Canon EOS', 'Electrónica', '2023-04-05', 32), "
                    + "(234568, 'Mesa de Comedor extensible', 'Muebles', '2023-05-20', 89), "
                    + "(345678, 'Camiseta de algodón', 'Ropa', '2023-06-12', 5), "
                    + "(456789, 'Aspiradora robot Samsung', 'Electrodomésticos', '2023-07-25', 67), "
                    + "(567890, 'Set de juguetes educativos para niños', 'Juguetes', '2023-08-30', 21), "
                    + "(678901, 'Raqueta de tenis profesional', 'Deportes', '2023-09-18', 56), "
                    + "(789012, 'Juego de ollas antiadherentes', 'Hogar', '2023-10-04', 90), "
                    + "(890123, 'Collar de plata con diamantes', 'Joyas', '2023-11-15', 3), "
                    + "(901234, 'Smartphone Samsung Galaxy', 'Electrónica', '2023-12-22', 68)";

            // Ejecuta las sentencias de inserción de clientes y artículos
            stmt.executeUpdate(sentenciaTablaClientes);
            stmt.executeUpdate(sentenciaTablaArticulos);

            // Cierre del recurso de tipo "Statement"
            stmt.close();
        } catch (SQLException sql) {
            System.out.println("Error al introducir clientes y artículos de muestra. Es posible que ya existieran");
        }
        
    }

    // Método que comprueba si el cliente leído existe (lo busca en el ArrayList "listaClientes"). Devuelve "true" si es así
    private static boolean clienteExiste() {
    	
		// Comprueba si "idCliente" (cliente actualmente leyéndose durante la ejecución) se encuentra en el ArrayList "listaClientes"
    	if (listaClientes.contains(idCliente)) {
			System.out.println("Cliente " + idCliente + " encontrado");
			return true;
		} else {
			System.out.println("Cliente " + idCliente + " no existe. Omitiendo pedido...");
			return false;
		}
		
	}
    
    private static String comprobarPedidoDuplicado() {

    	String opc = "";
    	
    	if (listaPedidos.contains(idPedido)) {
        	
    		System.out.println("El pedido " + idPedido + " está duplicado. ¿Deseas omitirlo o sustituirlo?\n[O] Omitir\n[S] Sustituir");
    		opc = teclado.nextLine();    		
    		
    		while (!opc.equalsIgnoreCase("O") && !opc.equalsIgnoreCase("S")) {
    			
                System.out.println("La opción introducida no es correcta. Por favor, elige una de las siguientes:\n[O] Omitir\n[S] Sustituir");
                opc = teclado.nextLine();
                
            }
    		
    		if (opc.equalsIgnoreCase("S")) {
    			try {
    				eliminarPedido(idPedido, false);
    				System.out.println("Eliminando datos del anterior pedido...");
				} catch (ClassNotFoundException | SQLException e) {
					System.out.println("No se ha podido eliminar el pedido " + idPedido);
				}
    		}
        } else {
        	System.out.println("El pedido " + idPedido + " no está repetido");
        }
    	
    	return opc;
    	
    }
    
    private static boolean articuloExiste() {
    	
		if (listaArticulos.contains(idArticulo)) {
			System.out.println("Artículo " + idArticulo + " encontrado");
			return true;
		}
		
		System.out.println("Artículo " + idArticulo + " no existe. Omitiendo artículo...");
		return false;
		
	}
    
    private static boolean hayStock() throws NumberFormatException, SQLException, ClassNotFoundException {
    	
		conectarBBDD();

		stockArticulo = 0;

		String sentenciaStockArticulo = String.format("SELECT stock FROM articulos WHERE id_articulo = %s", 
				idArticulo);

		ResultSet rsStockArticulo = stmt.executeQuery(sentenciaStockArticulo);

		if (rsStockArticulo.next()) {
			stockArticulo = Integer.parseInt(rsStockArticulo.getString(1));
			System.out.println("Queda/n " + stockArticulo + " ud/s.");
		}

		stmt.close();

		if ((stockArticulo - cantidadPedida) < 0) {
			System.out.println("No hay suficiente stock del artículo " + idArticulo + " para el pedido " + idPedido + ". Stock: " + stockArticulo + ". Solicitado/s: " + cantidadPedida);
			return false;
		} else {
			System.out.println("Hay stock del artículo " + idArticulo + " para el pedido " + idPedido + ". Stock: " + stockArticulo + ". Solicitado/s: " + cantidadPedida);
			restarStock();
			return true;
		}
	}
    
	private static void restarStock() throws NumberFormatException, SQLException, ClassNotFoundException {
	    	
		conectarBBDD();

		String sentenciaRestarStock = String.format("UPDATE articulos SET stock = %d WHERE id_articulo = %s", 
				(stockArticulo - cantidadPedida), idArticulo);

		stmt.executeUpdate(sentenciaRestarStock);

		stmt.close();

		System.out.println("Se ha restado el stock del artículo " + idArticulo);
		
	}

    private static void leerBBDD() throws ClassNotFoundException, SQLException {

        conectarBBDD();

        ResultSet rsPedidos = stmt.executeQuery("SELECT id_pedido FROM pedidos");
        while (rsPedidos.next()) listaPedidos.add(rsPedidos.getString("id_pedido"));

        ResultSet rsClientes = stmt.executeQuery("SELECT id_cliente FROM clientes");
        while (rsClientes.next()) listaClientes.add(rsClientes.getString("id_cliente"));
        
        ResultSet rsArticulos = stmt.executeQuery("SELECT id_articulo FROM articulos");
        while (rsArticulos.next()) listaArticulos.add(rsArticulos.getString("id_articulo"));

        stmt.close();

    }

    private static void eliminarPedido(String idPedido, Boolean soloTablaPedidos) throws ClassNotFoundException, SQLException {

        conectarBBDD();

        String sentenciaTablaArticulosPedidos = String.format("DELETE FROM articulos_pedidos WHERE id_pedido = %s",
                idPedido);
        String sentenciaTablaPedidos = String.format("DELETE FROM pedidos WHERE id_pedido = %s",
                idPedido);

        stmt.executeUpdate(sentenciaTablaPedidos);
        
        if (!soloTablaPedidos) stmt.executeUpdate(sentenciaTablaArticulosPedidos);

        stmt.close();

    }
    
    private static void comprobarPedidoVacio() throws SQLException, ClassNotFoundException {
		
    	conectarBBDD();
    	
    	Integer numArticulos = 0;
    	
    	String sentenciaArticulosPedidos = String.format("SELECT COUNT(id_articulo) FROM articulos_pedidos WHERE id_pedido = %s",
                idPedido);
    	
    	ResultSet rsArticulosPedidos = stmt.executeQuery(sentenciaArticulosPedidos);
    	
    	if (rsArticulosPedidos.next()) {
    		
    		numArticulos = Integer.parseInt(rsArticulosPedidos.getString(1));
    		System.out.println("Artículos en el pedido: " + numArticulos);
    		
    	}
    	
    	stmt.close();
    	
    	if (numArticulos <= 0) {
    		System.out.println("El pedido " + idPedido + " no contiene artículos o no son válidos. Eliminando...");
    		eliminarPedido(idPedido, true);
    		System.out.println("El pedido " + idPedido + " ha sido eliminado por estar vacío");
    	}
    	
	}
    
    private static void moverFicheroXML() {
    	
    	File f = new File(rutaXML);
    	f.renameTo(new File("procesados/" + f.getName()));
    	System.out.println("El fichero " + f.getName() + " se ha terminado de procesar. El programa ha finalizado");
    	
    }

}