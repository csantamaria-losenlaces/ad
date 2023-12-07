import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Ej04_Principal {

	public static void main(String[] args) throws IOException {
		
		// Declaración de variables
		FileWriter fw = new FileWriter("ficheroSalida.txt"); // FileWriter que recibe como parámetro el nombre del archivo en el que se escribirá
		BufferedWriter bw = new BufferedWriter(fw); // Buffer en el que se escribirán cada una de las líneas del FileWriter
		Scanner pedirEntrada = new Scanner(System.in); // Scanner para solicitar entrada de datos por teclado
		boolean salir = false; // Variable para controlar si el usuario quiere introducir más líneas o no
		
		// Bucle que se repetirá hasta "salir = false", es decir, hasta que no se quieran introducir más líneas en el archivo
		do {
			System.out.println("Introduce una línea:"); // Se solicita al ususario que introduzca una línea
			bw.write(pedirEntrada.nextLine()); // Se escribe en el archivo la entrada recibida por teclado
			bw.newLine(); // Se crea un salto de línea
			System.out.println("Línea añadida con éxito. ¿Quieres introducir otra? (S/N)"); // Se pregunta al usuario si quiere introducir otra línea
			// Si se introduce "N" o "n", cambia la variable "salir = true" para salir del bucle
			if (pedirEntrada.nextLine().toUpperCase().equals("N")) {
				salir = true;
			}
		} while (!salir);
		
		System.out.println("Has salido del programa."); // Se informa al usuario de que la ejecución del programa ha terminado
		bw.close(); // Se cierra el BufferedReader
		pedirEntrada.close(); // Se cierra el Scanner
		
	}

}