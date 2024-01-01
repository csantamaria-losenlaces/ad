import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;

public class Ej13_Principal {

    public static void main(String[] args) {
        
    	try {
            // Crea un parser SAX
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();

            // Especifica el archivo XML a leer
            File archivo = new File("Empleados.xml");

            // Crea un manejador SAX personalizado
            MyHandler manejador = new MyHandler();

            // Inicia la lectura del XML
            saxParser.parse(archivo, manejador);
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
    }
    
}

// Implementar el manejador SAX
class MyHandler extends DefaultHandler {

    // Declaración de variables
	boolean id = false;
    boolean apellido = false;
    boolean departamento = false;
    boolean salario = false;

    // Maneja el evento de inicio de un elemento
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("id")) {
            id = true;
        } else if (qName.equalsIgnoreCase("apellido")) {
            apellido = true;
        } else if (qName.equalsIgnoreCase("departamento")) {
            departamento = true;
        } else if (qName.equalsIgnoreCase("salario")) {
            salario = true;
        }
    }

    // Maneja el contenido de un elemento
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {        
        if (id) {
            System.out.println("ID: " + new String(ch, start, length));
        } else if (apellido) {
            System.out.println("Apellido: " + new String(ch, start, length));
        } else if (departamento) {
            System.out.println("Departamento: " + new String(ch, start, length));
        } else if (salario) {
            System.out.println("Salario: " + new String(ch, start, length));
        }
    }

    // Maneja el evento de fin de un elemento
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("id")) {
            id = false;
        } else if (qName.equalsIgnoreCase("apellido")) {
            apellido = false;
        } else if (qName.equalsIgnoreCase("departamento")) {
            departamento = false;
        } else if (qName.equalsIgnoreCase("salario")) {
            salario = false;
        }
    }
    
}