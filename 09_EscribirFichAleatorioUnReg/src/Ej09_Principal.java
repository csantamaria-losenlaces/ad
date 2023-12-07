/* Nombre: Carlos Santamaría Gracia
 * Curso: 2º D.A.M. Vespertino
 * Entrega proyecto 1ª evaluación de Acceso a Datos */

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class Ej09_Principal {

	public static void main(String[] args) throws IOException {
		
		// Declaración de variables
		Scanner pedirString = new Scanner(System.in); // Se crea un objeto Scanner para leer cadenas de texto
		Scanner pedirInt = new Scanner(System.in); // Se crea un objeto Scanner para leer números enteros
		Scanner pedirDouble = new Scanner(System.in); // Se crea un objeto Scanner para leer números decimales
		
		int idEmpleado; // Se declara una variable entera para almacenar el ID del empleado
		String apellido, departamento, seguirIntroduciendo = "S"; // Se declaran tres variables de tipo String para almacenar el apellido, el departamento y la opción elegida en el bucle
		StringBuffer sb = null;
		double salario; // Se declara una variable de tipo double para almacenar el salario del empleado
		
		RandomAccessFile fichEmpleados = new RandomAccessFile("empleados.dat", "rw"); // Se crea un objeto RandomAccessFile para acceder al archivo "empleados.dat" en modo lectura/escritura
		fichEmpleados.seek(fichEmpleados.length()); // Posiciona el cursor al final del archivo
		
		// Bucle que se ejecutará mientras la respuesta del usuario sea diferente de "N" o "n"
		while (!seguirIntroduciendo.toUpperCase().equals("N")) {
			System.out.println("Introduce un ID de empleado:"); // Se muestra un mensaje para pedir el ID del empleado
			idEmpleado = pedirInt.nextInt(); // Se lee el ID del empleado
			fichEmpleados.writeInt(idEmpleado); // Se escribe el ID de empleado en el archivo (4 bytes)
			
			System.out.println("Introduce el apellido:"); // Se muestra un mensaje para pedir el apellido del empleado
			apellido = pedirString.nextLine(); // Se lee el apellido del empleado
			sb = new StringBuffer(apellido); // Crea un StringBuffer con el apellido como argumento
			sb.setLength(20); // Hace que la longitud del StringBuffer sea de 20 caracteres fijos
			fichEmpleados.writeChars(sb.toString()); // Convierte a String el StringBuffer y lo escribe como "char" en el archivo (40 bytes)
			
			System.out.println("Introduce el departamento:"); // Se muestra un mensaje para pedir el departamento del empleado
			departamento = pedirString.nextLine(); // Se lee el departamento del empleado
			sb = new StringBuffer(departamento); // Crea un StringBuffer con el departamento como argumento
			sb.setLength(20); // Hace que la longitud del StringBuffer sea de 20 caracteres fijos
			fichEmpleados.writeChars(sb.toString()); // Convierte a String el StringBuffer y lo escribe como "char" en el archivo (40 bytes)
			
			System.out.println("Introduce el salario (formato 0,00):"); // Se muestra un mensaje para pedir el salario del empleado
			salario = pedirDouble.nextDouble(); // Se lee el salario del empleado
			fichEmpleados.writeDouble(salario); // Escribe el salario en el fichero (8 bytes)
			
			System.out.println("¿Quieres introducir otro empleado? (S/N)"); // Se muestra un mensaje para preguntar si se desea introducir otro empleado
			seguirIntroduciendo = pedirString.nextLine(); // Se lee la opción elegida por el usuario
		}
		
		// Se cierran los diferentes flujos de E/S de datos
		pedirInt.close();
		pedirString.close();
		pedirDouble.close();
		fichEmpleados.close();
		
		System.out.println("El programa ha finalizado."); // Se muestra un mensaje para indicar que el programa ha finalizado
		
	}
	
}