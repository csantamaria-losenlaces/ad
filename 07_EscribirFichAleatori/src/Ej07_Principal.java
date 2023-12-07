import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Ej07_Principal {

	public static void main(String[] args) throws IOException {
		
		// Declaración de variables
		File rutaFichero = new File("empleados.dat"); // Objeto File que representa ubicación del archivo a crear
		RandomAccessFile ficheroAleatorio = new RandomAccessFile(rutaFichero, "rw"); // Objeto RandomAccessFile para realizar acceso aleatorio al archivo
		
		String[] apellido = {"Pérez", "Martínez", "Gracia", "Álvarez"}; // Array con los apellidos a introducir
		StringBuffer sbApellido = null; // StringBuffer que escribirá los apellidos en el archivo con una longitud fija
		int[] numDepartamento = {2, 4, 5, 1}; // Array con los identificadores de departamento a introducir
		double[] salario = {45800.75, 62430.50, 38920.25, 75600.80}; // Array con los salarios anuales a introducir
		
		// Bucle que se repetirá tantas veces como elementos tenga el array "apellido"
		for (int i = 0; i < apellido.length; i++) {
			ficheroAleatorio.writeInt(i+1); // Escribe el número de iteración + 1 como identificador del empleado
			sbApellido = new StringBuffer(apellido[i]); // Crea un StringBuffer con el apellido del array en la posición "i" como argumento
			sbApellido.setLength(10); // Hace que la longitud del StringBuffer sea de 10 caracteres
			ficheroAleatorio.writeChars(sbApellido.toString()); // Convierte a String el StringBuffer y lo escribe como "char" en el archivo
			ficheroAleatorio.writeInt(numDepartamento[i]); // Escribe el departamento en la posición "i" del array "numDepartamento"
			ficheroAleatorio.writeDouble(salario[i]); // Escribe el salario en la posición "i" del array "salario"
		}
		
		ficheroAleatorio.close(); // Cierra el flujo de datos RandomAccessFile
		
	}

}