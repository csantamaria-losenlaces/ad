/* Crea un fichero de texto con algún editor de textos y después realiza un programa Java que
 * visualice su contenido. Cambia el programa Java para que el nombre del fichero se acepte al
 * ejecutar el programa desde la línea de comandos. */

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Ej02_Principal {

	// Declaración de variables globales
	private static FileReader fichero;
	private static String cadena = "";
	private static int codCaracter;
	private static char caracter;
	
	public static void main(String[] args) throws IOException {
		// Comprueba si se han introducido parámetros por consola
		if (args.length >= 1) {
			fichero = new FileReader(args[0]);
			// Se comprueba si el fichero es válido
			if (fichero != null) {
				leerFichero(); // Si es válido, se llama al método leerFichero()
			} else {
				System.out.println("El directorio no existe"); // Si no es válido, se muestra un mensaje de error
			}
		} else {
			fichero = new FileReader("texto.txt"); // Si no se han introducido parámetros por consola, se lee "texto.txt"
			leerFichero();
		}
	}
	
	private static void leerFichero() {
		try {
			codCaracter = fichero.read(); // Se lee el primer caracter del fichero fuera del bucle
			// Bucle que lee los caracteres del fichero hasta el final del mismo
			while (codCaracter != -1) {
				caracter = (char) codCaracter; // "caracter" toma el valor de codCaracter convertido a char
			    cadena += caracter; // Concatena "caracter" al valor que tuviera "cadena"
			    codCaracter = fichero.read(); // Se lee el siguiente caracter
			}
			fichero.close(); // Se cierra el fichero
			System.out.println("El contenido leído del fichero es:\n" + cadena);
		} catch (FileNotFoundException e1) {
			System.out.println("No se ha encontrado el archivo");
		} catch (IOException e2) {
			System.out.println("Ha ocurrido un error");
		}
	}

}