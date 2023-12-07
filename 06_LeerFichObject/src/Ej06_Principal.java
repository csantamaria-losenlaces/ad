import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Ej06_Principal {

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		
		// Declaración de variables
		File f = new File("ficheroObjetos.dat"); // Fichero donde se encuentran los objetos a leer
		FileInputStream fis = new FileInputStream(f); // Objeto que permitirá leer el archivo File
		ObjectInputStream ois = new ObjectInputStream(fis); // Convierte la información binaria a un objeto Java
		Persona p; // Variable que se irá utilizando para recuperar cada objeto e imprimirlo en cada iteración
		
		// Se captura la excepción "End of File" que lanza ObjectInputStream cuando se ha llegado al final del fichero
		try {
			p = (Persona) ois.readObject(); // Se lee el primer objeto y se guarda en "p"
			System.out.println(p); // Se imprimen los atributos de Persona haciendo uso del método toString() en la clase Persona
			// Bucle que comprueba que el objeto no sea nulo
			while (p != null) {
				p = (Persona) ois.readObject(); // En cada iteración se lee una nueva instancia de cada objeto Persona
				System.out.println(p); // Se imprimen los atributos de Persona haciendo uso del método toString() en la clase Persona
			}
		} catch (EOFException eof) {
			System.out.println("Se ha llegado al fin del fichero"); // Se imprime un mensaje cuando se termina de leer el fichero
		}
		
		ois.close(); // Se cierra ObjectInputStream

	}

}