/* Nombre: Carlos Santamaría Gracia
 * Curso: 2º D.A.M. Vespertino
 * Entrega proyecto 1ª evaluación de Acceso a Datos */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Ej03_Principal {

	public static void main(String[] args) {
		
		// Declaración de variables
		File ficheroTexto = new File("FicheroTexto.txt"); // Archivo File que representa la ruta del fichero "FicheroTexto.txt"
		BufferedReader br; // Se declara el objeto para realizar operaciones de E/S con un buffer
		String cadena; // Variable auxiliar para leer e imprimir cada una de las líneas del fichero de texto
		
		// Se comprueba si el fichero existe
		if (ficheroTexto.exists()) {
			System.out.println("Leyendo el contenido del fichero \""
			+ ficheroTexto.getName() + "\"...\n"); // Imprimimos un mensaje que confirma que el fichero se va a leer y el nombre del mismo
			
			try {
				br = new BufferedReader(new FileReader(ficheroTexto)); // Se inicializa el objeto para realizar operaciones de E/S con un buffer
				cadena = br.readLine(); // Lee la primera línea del BufferedReader con el fichero que le hemos pasado como argumento
				// Bucle que se repetirá hasta que readLine() devuelva "null", es decir, que se haya llegado al final del fichero
				while (cadena != null) {
					System.out.println(cadena); // Se imprime por consola la línea leída
					cadena = br.readLine(); // Se lee la siguiente línea para que sea imprimida en la siguiente iteración (si no es "null")
				}
				br.close(); // Se cierra el BufferedReader (evita problemas de rendimiento y consumo innecesario de recursos del sistema)
			} catch (IOException e) {
				e.printStackTrace(); // Imprime por la salida de error estándar un mensaje con información del fallo
			}
		}
		// Si no existe, se imprime un mensaje de error por consola
		else {
			System.out.println("ERROR. El fichero no existe.");
		}
		
		System.out.println("\nSe ha terminado de leer el contenido del fichero."); // Se imprime un mensje que confirma que se ha terminado de leer el fichero

	}

}