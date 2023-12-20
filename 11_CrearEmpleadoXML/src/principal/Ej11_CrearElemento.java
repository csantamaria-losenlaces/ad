package principal;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class Ej11_CrearElemento {

    // Método para crear un elemento XML con un valor y agregarlo a un elemento raíz
    static void crearElemento(String nombreEtiqueta, String valor, Element raiz, Document d) {
        
        Element e = d.createElement(nombreEtiqueta); // Crea un nuevo elemento
        Text t = d.createTextNode(valor); // Crea un nodo de texto con el valor "valor"

        raiz.appendChild(e); // Agrega el elemento creado al elemento raíz
        e.appendChild(t); // Agrega el nodo de texto al elemento creado
    }
}