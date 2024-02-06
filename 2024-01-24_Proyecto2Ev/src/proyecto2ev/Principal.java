package proyecto2ev;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Principal {

    // Declaración de variables
    private static Scanner teclado = new Scanner(System.in);

    private static ArrayList<String> listaPedidos = new ArrayList<>();
    private static ArrayList<String> listaClientes = new ArrayList<>();
    private static ArrayList<String> listaArticulos = new ArrayList<>();

    // Declaración de variables tratamiento documento XML
    private static DocumentBuilderFactory dbf;
    private static DocumentBuilder db;
    private static Document d;
    private static NodeList pedidos;
    private static NodeList articulos;

    // Declaración de variables manejo BB.DD.
    private static Statement stmt;

    private static String idCliente, idPedido, fechaPedido, idArticulo;
    private static Integer stockArticulo, cantidadPedida;

    // Declaración de variables constantes
    private static final String RUTA_XML = "Pedidos_Tiendas.xml";

    public static void main(String[] args) {

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
            pedidos = d.getElementsByTagName("pedido");

            // Crea la estructura de tablas y campos de la BB.DD., si todavía no existe
            crearTablas();

            // DEBUG, SALTA EXCEPCIÓN (CAPTURADA, POR LO QUE NO SE INTERRUMPE EJECUCIÓN) SI PK YA EXISTE
            insertarClientesArticulos();

            leerBBDD();

            // Bucle para recorrer la lista de nodos "pedido"
            for (int p = 0; p < pedidos.getLength(); p++) {

                Node nodoPedido = pedidos.item(p); // Obtención del nodo "pedido" en la posición p

                // Verificación de que el nodo sea un elemento
                if (nodoPedido.getNodeType() == Node.ELEMENT_NODE) {

                    Element elementoPedido = (Element) nodoPedido; // Conversión del nodo a un elemento para facilitar el trabajo con él

                    idCliente = getNodo("numero-cliente", elementoPedido);
                    idPedido = getNodo("numero-pedido", elementoPedido);
                    fechaPedido = getNodo("fecha", elementoPedido);

                    if (clienteExiste() && !comprobarPedidoDuplicado().equalsIgnoreCase("O")) {
                        	
	                	insertarPedido("pedidos");
	                	
	                	// Obtención de la lista de nodos "articulo" específicamente del pedido actual
	                    articulos = elementoPedido.getElementsByTagName("articulo");
	
	                    // Bucle para recorrer los nodos "articulo" de un pedido
	                    for (int a = 0; a < articulos.getLength(); a++) {
	
	                        // Obtención del nodo "articulo" en la posición a
	                        Node nodoArticulo = articulos.item(a);
	
	                        // Verificación de que el nodo sea un elemento
	                        if (nodoArticulo.getNodeType() == Node.ELEMENT_NODE) {
	
	                            // Conversión del nodo a un elemento para facilitar el trabajo con él
	                            Element articuloElemento = (Element) nodoArticulo;
	
	                            // Obtención de atributos del nodo "articulo"
	                            idArticulo = getNodo("codigo", articuloElemento).replaceFirst("^0+", "");
	                            cantidadPedida = Integer.parseInt(getNodo("cantidad", articuloElemento));
	                            
	                            if (articuloExiste() && hayStock()) insertarPedido("articulos_pedidos");
	
	                        }
	                        
	                    }
	                    
                    } else {
                    	
                    	System.out.println("Pedido " + idPedido + " omitido");
                    	
                    }
                    
                }
                
                comprobarPedidoVacio();
                System.out.println();
                
            }
            
        } catch (Exception e) {
        	
            e.printStackTrace();
            
        }
        
    }

    // Método para obtener el valor de un nodo dado su nombre de etiqueta y el elemento padre
    private static String getNodo(String etiqueta, Element e) {

        // Obtención de la lista de nodos con el nombre de la etiqueta
        NodeList nodos = e.getElementsByTagName(etiqueta).item(0).getChildNodes();

        // Obtención del primer nodo hijo y devolución de su valor como un String
        Node nodo = nodos.item(0);

        return nodo.getNodeValue();

    }

    private static void conectarBBDD() {

        try {

            Connection conn = DriverManager.getConnection("jdbc:sqlite:bbddpedidos.db");
            Class.forName("org.sqlite.JDBC");
            stmt = conn.createStatement();

        } catch (ClassNotFoundException cnf) {

            System.out.println("Ha ocurrido un error al cargar la clase");

        } catch (SQLException sql) {

            System.out.println("Ha ocurrido una excepción SQL");

        }
    }

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

        conectarBBDD();

        stmt.executeUpdate(CREA_TABLA_PEDIDOS);
        stmt.executeUpdate(CREA_TABLA_ARTICULOS_PEDIDOS);
        stmt.executeUpdate(CREA_TABLA_CLIENTES);
        stmt.executeUpdate(CREA_TABLA_ARTICULOS);

        stmt.close();

    }

    private static void insertarPedido(String nombreTabla) throws ClassNotFoundException, SQLException {

        conectarBBDD();

        switch (nombreTabla) {
            case "pedidos":
                String sentenciaTablaPedidos = String.format("INSERT INTO pedidos VALUES ('%s', '%s', '%s')",
                        idPedido, idCliente, fechaPedido);
                stmt.executeUpdate(sentenciaTablaPedidos);
                System.out.println("Insertado pedido en tabla \"pedidos\"");
                break;
            case "articulos_pedidos":
                String sentenciaTablaArticulosPedidos = String.format("INSERT INTO articulos_pedidos VALUES ('%s', '%s', '%d')",
                        idPedido, idArticulo, cantidadPedida);
                stmt.executeUpdate(sentenciaTablaArticulosPedidos);
                System.out.println("Insertado pedido en tabla \"articulos_pedidos\"");
                break;
        }
        
        System.out.println("Inserción de datos finalizada");

        stmt.close();

    }

    private static void insertarClientesArticulos() {

        try {

            conectarBBDD();

            String sentenciaTablaClientes = "INSERT INTO clientes VALUES "
                    + "(1234567890, 'Juan', 'Gómez', 'Paseo del Prado, 14, Madrid', '699123456'), "
                    + "(1234567891, 'Maria', 'Rodríguez', 'Calle Mayor, 45, Barcelona', '971234567'), "
                    + "(1234567892, 'Pedro', 'Fernandez', 'Calle del Carmen, 3, Málaga', '677666555'), "
                    + "(1234567893, 'Laura', 'Martínez', 'Calle de Bailén, 2, Madrid', '815551234'), "
                    + "(9876543210, 'Roberto', 'Gutiérrez', 'Gran Vía de les Corts Catalanes, 666, Barcelona', '622111000')";

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

            stmt.executeUpdate(sentenciaTablaClientes);
            stmt.executeUpdate(sentenciaTablaArticulos);

            stmt.close();

        } catch (SQLException sql) {

            System.out.println("Error al introducir clientes y artículos de muestra. Es posible que ya existieran");

        }
    }

    private static boolean clienteExiste() {
		if (listaClientes.contains(idCliente)) {
			System.out.println("Cliente " + idCliente + " encontrado");
			return true;
		}
		System.out.println("Cliente " + idCliente + " no existe. Omitiendo pedido...");
		return false;
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

}