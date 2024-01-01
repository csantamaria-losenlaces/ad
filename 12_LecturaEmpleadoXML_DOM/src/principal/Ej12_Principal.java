package principal;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Ej12_Principal {

    public static void main(String[] args) {
        
        // Declaración de variables
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); // Crea una nueva instancia de DocumentBuilderFactory

        try {
            
            DocumentBuilder db = dbf.newDocumentBuilder(); // Crea un DocumentBuilder a partir de DocumentBuilderFactory
            Document d = db.parse(new File("Empleados.xml")); // Parsea el archivo XML y crea un documento XML

            d.getDocumentElement().normalize(); // Normaliza la estructura del documento para evitar nodos en blanco
            
            System.out.println("Elemento raíz: " + d.getDocumentElement().getNodeName()); // Muestra el nombre del elemento raíz del documento XML

            NodeList empleados = d.getElementsByTagName("empleado"); // Crea una lista con todos los nodos de empleados en el documento XML
            
            System.out.println("Nodos empleado a recorrer: " + empleados.getLength()); // Muestra la cantidad de nodos "empleado" en la lista

            // Bucle para recorrer la lista de nodos "empleado"
            for (int i = 0; i < empleados.getLength(); i++) {
                
            	Node empleado = empleados.item(i); // Obtiene el nodo "empleado" en la posición i
                
                // Verifica que el nodo sea un elemento
                if (empleado.getNodeType() == Node.ELEMENT_NODE) {
                	
                    Element e = (Element) empleado; // Convierte el nodo a un elemento para facilitar el trabajo con él

                    // Muestra información sobre el empleado actual
                    System.out.println("ID: " + getNodo("id", e));
                    System.out.println("Apellido: " + getNodo("apellido", e));
                    System.out.println("Departamento: " + getNodo("departamento", e));
                    System.out.println("Salario: " + getNodo("salario", e));
                    
                }
                
            }
            
        } catch (Exception e) { e.printStackTrace(); }
        
    }

    // Método para obtener el valor de un nodo dado su nombre de etiqueta y el elemento padre
    private static String getNodo(String etiqueta, Element e) {
    	
    	NodeList nodos = e.getElementsByTagName(etiqueta).item(0).getChildNodes(); // Obtiene la lista de nodos con el nombre de la etiqueta
        
        // Obtiene el primer nodo hijo y devuelve su valor como un String
        Node nodo = (Node) nodos.item(0);
        return nodo.getNodeValue();
    }
    
}