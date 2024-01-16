import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Ej14_Principal {

	public static void main(String[] args) {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); // Configuración del analizador de documentos XML

		try {
			DocumentBuilder db = dbf.newDocumentBuilder(); // Creación del analizador de documentos XML
			Document d = db.parse(new File("Pedidos.xml")); // Parseo del archivo XML y creación del documento XML

			d.getDocumentElement().normalize(); // Normalización de la estructura del documento

			System.out.printf("Elemento raíz: \"%s\"\n", d.getDocumentElement().getNodeName());

			NodeList pedidos = d.getElementsByTagName("pedido"); // Obtención de la lista de nodos "pedido" en el documento XML

			System.out.println("Nodos \"pedido\" a recorrer: " + pedidos.getLength() + "\n");

			// Bucle para recorrer la lista de nodos "pedido"
			for (int p = 0; p < pedidos.getLength(); p++) {

				Node pedido = pedidos.item(p); // Obtención del nodo "pedido" en la posición p

				// Verificación de que el nodo sea un elemento
				if (pedido.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) pedido; // Conversión del nodo a un elemento para facilitar el trabajo con él

					System.out.println("Pedido #" + (p + 1));
					System.out.println("  Nombre: " + getNodo("nombre", e));
					System.out.println("  Nº pedido: " + getNodo("numero_pedido", e));

					NodeList articulosNodos = e.getElementsByTagName("articulo"); // Obtención de la lista de nodos "articulo" específicamente del pedido actual

					// Bucle para recorrer los nodos "articulo" de un pedido
					for (int a = 0; a < articulosNodos.getLength(); a++) {

						Node articulo = articulosNodos.item(a); // Obtención del nodo "articulo" en la posición a

						// Verificación de que el nodo sea un elemento
						if (articulo.getNodeType() == Node.ELEMENT_NODE) {
							Element articuloElemento = (Element) articulo; // Conversión del nodo a un elemento para facilitar el trabajo con él

							System.out.println("    Artículo #" + (a + 1));

							// Obtención de atributos del nodo "articulo"
							String descripcion = articuloElemento.getAttribute("descripcion");
							String cantidad = articuloElemento.getAttribute("cantidad");

							System.out.println("      Descripción: " + descripcion);
							System.out.println("      Cantidad: " + cantidad + "x");
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