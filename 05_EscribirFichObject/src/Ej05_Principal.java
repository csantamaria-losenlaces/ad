import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Ej05_Principal {

	public static void main(String[] args) throws IOException {
		
		// Declaración de variables
		File f = new File("ficheroObjetos.dat"); // File con la ruta y nombre del archivo que contendrá los objetos
		FileOutputStream fos = new FileOutputStream(f, true); // FileOutputStream que gestiona la escritura de File (se indica "true" para que no sobreescribir)
		ObjectOutputStream oos = new ObjectOutputStream(fos); // ObjectOutputStream que gestiona la escritura en binario de objetos en un FileOutputStream
		
		// Se emplea writeObject() para escribir objetos en el archivo y recibe como parámetro un nuevo objeto Persona
		oos.writeObject(new Persona("Carlos", "Santamaría Gracia", "1234567A", "Zaragoza"));
		oos.writeObject(new Persona("Luisa", "Martínez Rodríguez", "9876543B", "Barcelona"));
		oos.writeObject(new Persona("Juan", "Gómez López", "8765432C", "Madrid"));
		oos.writeObject(new Persona("María", "Fernández Pérez", "7654321D", "Valencia"));
		oos.writeObject(new Persona("Pedro", "González Sánchez", "6543210E", "Sevilla"));
		oos.writeObject(new Persona("Laura", "Hernández Martínez", "5432109F", "Bilbao"));
		oos.writeObject(new Persona("Miguel", "Rodríguez González", "4321098G", "Málaga"));
		oos.writeObject(new Persona("Carmen", "Sánchez Rodríguez", "3210987H", "Alicante"));
		oos.writeObject(new Persona("Javier", "Pérez Martín", "2109876I", "Granada"));
		
		oos.close(); // Se cierra el objeto FileOutputStream para evitar consumo de recursos excesivo

	}

}