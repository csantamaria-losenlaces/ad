package proyecto2ev;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Principal {

	// Declaración de variables tratamiento documento XML
	private static DocumentBuilderFactory dbf;
	private static DocumentBuilder db;
	private static Document d;
	private static NodeList pedidos;
	private static NodeList articulos;

	// Declaración de variables manejo BB.DD.
	private static Connection conn;
	private static Statement stmt;

	// Declaración de variables constantes
	private static final String RUTA_XML = "Pedidos_Tiendas.xml";
	private static final String ETIQUETA_PEDIDO = "pedido";
	private static final String DRIVER_BBDD = "org.sqlite.JDBC";
	private static final String RUTA_BBDD = "jdbc:sqlite:bbddpedidos.db";
	
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
			pedidos = d.getElementsByTagName(ETIQUETA_PEDIDO); 
			
			// Crea la estructura de tablas y campos de la BB.DD., si todavía no existe
			crearTablas();
			
			// Bucle para recorrer la lista de nodos "pedido"
			for (int p = 0; p < pedidos.getLength(); p++) {

				Node nodoPedido = pedidos.item(p); // Obtención del nodo "pedido" en la posición p

				// Verificación de que el nodo sea un elemento
				if (nodoPedido.getNodeType() == Node.ELEMENT_NODE) {
					
					Element elementoPedido = (Element) nodoPedido; // Conversión del nodo a un elemento para facilitar el trabajo con él

					String idCliente = getNodo("numero-cliente", elementoPedido);
					String idPedido = getNodo("numero-pedido", elementoPedido);
					String fechaPedido = getNodo("fecha", elementoPedido);

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
							String idArticulo = articuloElemento.getAttribute("codigo");
							String cantidadPedida = articuloElemento.getAttribute("cantidad");
							
							System.out.println("Valor de idArticulo: " + idArticulo);
							System.out.println("Valor de cantidadPedida: " + cantidadPedida);
							
							String sentenciaTablaPedidos = String.format("INSERT INTO pedidos VALUES ('%s', '%s', '%s')",
									idCliente, idPedido, fechaPedido);
							String sentenciaTablaArticulosPedidos = String.format("INSERT INTO articulos_pedidos VALUES ('%s', '%s', '%s')",
									idPedido, idArticulo, cantidadPedida);
							
							insertarPedido(sentenciaTablaPedidos, sentenciaTablaArticulosPedidos);
						}
					}
				}
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
	
	private static void conectarBBDD() throws ClassNotFoundException, SQLException {
		
		Class.forName(DRIVER_BBDD);
		conn = DriverManager.getConnection(RUTA_BBDD);
		
	}

	private static void crearTablas() throws SQLException, ClassNotFoundException {
		
		conectarBBDD();
		
		final String CREA_TABLA_PEDIDOS = "CREATE TABLE IF NOT EXISTS pedidos ("
				+ "id_cliente INTEGER, id_pedido INTEGER, fecha_pedido NUMERIC"
				+ ")";
		
		final String CREA_TABLA_ARTICULOS_PEDIDOS = "CREATE TABLE IF NOT EXISTS articulos_pedidos ("
				+ "id_pedido INTEGER, id_articulo INTEGER, cantidad_pedida REAL"
				+ ")";
		
		stmt = conn.createStatement();

		stmt.executeUpdate(CREA_TABLA_PEDIDOS);
		stmt.executeUpdate(CREA_TABLA_ARTICULOS_PEDIDOS);
		
		stmt.close();
		conn.close();
		
	}
	
	private static void insertarPedido(String sentenciaTablaPedidos, String sentenciaTablaArticulosPedidos) throws ClassNotFoundException, SQLException {
		
		conectarBBDD();
		
		stmt = conn.createStatement();
		
		stmt.executeUpdate(sentenciaTablaPedidos);
		stmt.executeUpdate(sentenciaTablaArticulosPedidos);
		
		stmt.close();
		conn.close();
		
	}

}