/* Nombre: Carlos Santamaría Gracia
 * Curso: 2º D.A.M. Vespertino
 * Entrega proyecto 1ª evaluación de Acceso a Datos */

import java.io.File;

public class Ej01_Principal {

	// Declaración de variables globales
	private static File dir;
	
	public static void main(String[] args) {
		// Comprueba si se ha introducido algún argumento por consola
		if (args.length >= 1) {
			dir = new File(args[0]); // Si se ha introducido un argumento por consola, se establece el primero de ellos como directorio actual
			// Se comprueba si el directorio es válido
			if (dir.exists()) {
				listarFicheros(); // Si es válido, se llama al método listarFicheros()
			} else {
				System.out.println("El directorio no existe"); // Si no es válido, se muestra un mensaje de error
			}
		} else {
			dir = new File("."); // Si no se han introducido argumentos por consola se establece como directorio el actual
			listarFicheros(); // Se llama al método listarFicheros()
		}
	}
	
	// Método encagado de listar los ficheros de un directorio
	private static void listarFicheros() {
		System.out.println("Mostrando ficheros del directorio " + dir.getAbsolutePath());
		// Bucle que recorre todos los elementos del directorio
		for (File f : dir.listFiles()) {
			if (f.isFile()) System.out.println(f.getName()); // Se imprimen por consola los nombres de los ficheros (se excluyen directorios)
		}
	}
	
}