import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Ej15_Principal {
	
	public static void main(String[] args) {
		try {
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			File archivo = new File("Pedidos.xml");
			MyHandler manejador = new MyHandler();
			saxParser.parse(archivo, manejador);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

class MyHandler extends DefaultHandler {
	
	boolean nombre = false;
	boolean numero_pedido = false;
	boolean descripcion = false;
	boolean cantidad = false;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("nombre")) {
			nombre = true;
		} else if (qName.equalsIgnoreCase("numero_pedido")) {
			numero_pedido = true;
		} else if (qName.equalsIgnoreCase("articulo")) {
			if (attributes.getValue("descripcion") != null && !attributes.getValue("descripcion").isEmpty()) {
				descripcion = true;
				System.out.println("  - Descripción: " + attributes.getValue("descripcion"));
			}
			if (attributes.getValue("cantidad") != null && !attributes.getValue("cantidad").isEmpty()) {
				cantidad = true;
				System.out.println("  Cantidad: " + attributes.getValue("cantidad"));
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (nombre) {
			System.out.println("Nombre: " + new String(ch, start, length));
		} else if (numero_pedido) {
			System.out.println("Nº Pedido: " + new String(ch, start, length));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("nombre")) {
			nombre = false;
		} else if (qName.equalsIgnoreCase("numero_pedido")) {
			numero_pedido = false;
		}
	}
	
}