package principal;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static principal.Ej11_CrearElemento.crearElemento; // Importa el método "crearElemento" de la clase "Ej11_CrearElemento"

public class Ej11_Principal {

    public static void main(String[] args) throws IOException {
        
        // Declaración de variables
        final int LONGITUD_STRING = 20, LONGITUD_REGISTRO = 92;
        
        File f = new File("empleados.dat"); // Crea un objeto File para el archivo empleados.dat
        RandomAccessFile ras = new RandomAccessFile(f, "r"); // Crea un objeto RandomAccessFile para acceder al archivo en modo lectura
        
        // Declaración de variables definitivas y auxiliares para almacenar los datos del empleado
        int idEmpleado, pos = 0;
        char apellidoChar[] = new char[LONGITUD_STRING], aux;
        char departamentoChar[] = new char[LONGITUD_STRING];
        double salario;
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); // Crea un objeto DocumentBuilderFactory para construir un DocumentBuilder
        
        // Recuperación de datos del archivo aleatorio y creación del XML
        try {
            DocumentBuilder db = dbf.newDocumentBuilder(); // Crea un DocumentBuilder
            DOMImplementation domi = db.getDOMImplementation(); // Obtiene una implementación de DOM
            Document d = domi.createDocument(null, "empleados", null); // Crea un nuevo archivo XML con "empleados" como elemento raíz
            
            d.setXmlVersion("1.0"); // Establece la versión XML a la 1.0
            
            // Bucle infinito para leer datos del archivo aleatorio y construir el documento XML
            while (true) {
                // Posiciona el puntero en la posición "pos"
                ras.seek(pos);
                
                // Lee el ID
                idEmpleado = ras.readInt();
                
                // Lee y construye el apellido
                for (int i = 0; i < apellidoChar.length; i++) {
                    aux = ras.readChar();
                    apellidoChar[i] = aux;
                }
                
                // Convierte el array de caracteres a String
                String apellido = new String(apellidoChar);
                
                // Lee y construye el departamento
                for (int i = 0; i < departamentoChar.length; i++) {
                    aux = ras.readChar();
                    departamentoChar[i] = aux;
                }
                
                // Convierte el array de caracteres a String
                String departamento = new String(departamentoChar);
                
                // Lee el salario
                salario = ras.readDouble();
                
                // Comprueba que el XML no esté vacío (sin elementos hijos)
                if (idEmpleado > 0) {
                    // Crea un elemento "empleado" en el documento XML
                    Element raiz = d.createElement("empleado");
                    
                    // Agrega el elemento "empleado" al elemento raíz del documento
                    d.getDocumentElement().appendChild(raiz);
                    
                    // Crea elementos XML para "id", "apellido", "departamento" y "salario"
                    crearElemento("id", Integer.toString(idEmpleado), raiz, d);
                    crearElemento("apellido", apellido.trim(), raiz, d);
                    crearElemento("departamento", departamento.trim(), raiz, d);
                    crearElemento("salario", Double.toString(salario), raiz, d);
                }
                
                // Mueve el puntero al siguiente empleado
                pos += LONGITUD_REGISTRO;
                
                // Sale del bucle cuando el puntero llega al final del archivo
                if (ras.getFilePointer() == ras.length()) break;
            }
            
            // Prepara el documento XML para la transformación
            Source s = new DOMSource(d);
            
            // Prepara el resultado de la transformación, escribiendo en el archivo "Empleados.xml"
            Result r = new StreamResult(new File("Empleados.xml"));
            
            // Crea un objeto "Transformer" para realizar la transformación
            Transformer t = TransformerFactory.newInstance().newTransformer();
            
            // Realiza la transformación del documento XML y escribe en el archivo
            t.transform(s, r);
        
        } catch (Exception e) {
            // Manejo de excepciones
            e.printStackTrace();
        } finally {
            // Se cierra el stream de datos RandomAccessFile al final del programa
            ras.close();
        }
        
    }
}
