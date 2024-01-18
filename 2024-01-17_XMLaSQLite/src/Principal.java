import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Principal {

	public static void main(String[] args) {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); // Configuración del analizador de documentos XML

		try {
			DocumentBuilder db = dbf.newDocumentBuilder(); // Creación del analizador de documentos XML
			Document d = db.parse(new File("Pedidos.xml")); // Parseo del archivo XML y creación del documento XML
			
			d.getDocumentElement().normalize(); // Normalización de la estructura del documento
			
			NodeList pedidos = d.getElementsByTagName("pedido"); // Obtención de la lista de nodos "pedido" en el documento XML
			
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:C:/Users/Carlos/Documents/Programas/SQLite Tools v3.45.0/pedidos.db");
			Statement stmt = conn.createStatement();
			
			String creaTablaPedidosClientes = "CREATE TABLE IF NOT EXISTS pedidos ("
					+ "nombre_cliente VARCHAR2(255), numero_pedido INT(10), descripcion VARCHAR2(255), cantidad INT(9)"
					+ ")";
			
			stmt.executeUpdate(creaTablaPedidosClientes);

			// Bucle para recorrer la lista de nodos "pedido"
			for (int p = 0; p < pedidos.getLength(); p++) {

				Node pedido = pedidos.item(p); // Obtención del nodo "pedido" en la posición p

				// Verificación de que el nodo sea un elemento
				if (pedido.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) pedido; // Conversión del nodo a un elemento para facilitar el trabajo con él

					String nombreCliente = getNodo("nombre", e);
					String numeroPedido = getNodo("numero_pedido", e);

					NodeList articulosNodos = e.getElementsByTagName("articulo"); // Obtención de la lista de nodos "articulo" específicamente del pedido actual

					// Bucle para recorrer los nodos "articulo" de un pedido
					for (int a = 0; a < articulosNodos.getLength(); a++) {

						Node articulo = articulosNodos.item(a); // Obtención del nodo "articulo" en la posición a

						// Verificación de que el nodo sea un elemento
						if (articulo.getNodeType() == Node.ELEMENT_NODE) {
							Element articuloElemento = (Element) articulo; // Conversión del nodo a un elemento para facilitar el trabajo con él

							// Obtención de atributos del nodo "articulo"
							String descripcion = articuloElemento.getAttribute("descripcion");
							String cantidad = articuloElemento.getAttribute("cantidad");
							
							String insertaTupla = String.format("INSERT INTO pedidos VALUES ('%s', '%s', '%s', '%s')", nombreCliente, numeroPedido, descripcion, cantidad);
							stmt.executeUpdate(insertaTupla);
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
		NodeList nodos = e.getElementsByTagName(etiqueta).item(0).getChildNodes(); // Obtención de la lista de nodos con el nombre de la etiqueta
		Node nodo = nodos.item(0); // Obtención del primer nodo hijo y devolución de su valor como un String
		
		return nodo.getNodeValue();
	}
	
}