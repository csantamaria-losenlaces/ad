import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Ej08_Principal {

	public static void main(String[] args) throws IOException {

		// Declaración de variables
		File f = new File("empleados.dat"); // Objeto File que representa la ubicación del fichero que leeremos
		RandomAccessFile ras = new RandomAccessFile(f, "r"); // RandomAccessFile para leer los datos del fichero de forma aleatoria 
		
		int id, dpto; // Variable donde guardaremos los identificadores del empleado y del departamento, respectivamente
		double salario; // Variable donde guardaremos el salario del empleado
		char apellido[] = new char[10], aux; // Array de char donde guardaremos el apellido del empleado y otra variable auxiliar donde se irán guardando los caracteres individuales
		int posicion; // Variable que indica en qué posición del archivo se colocará el cursor para leer
		
		posicion = 0; // Inicializaos la variable a 0 para que empiece a leer en el primer caracter
		ras.seek(posicion); // Colocamos el cursor en "posicion"
		
		// Bucle que se repetirá hasta que el puntero tome el valor de la longitud del RandomAccessFile, es decir, el final del fichero
		while (ras.getFilePointer() < ras.length()) {
			id = ras.readInt(); // Lee un dato int y lo guarda en "id"
			
			// Bucle que lee los 10 caracteres fijos de "apellido"
			for (int i = 0; i < apellido.length; i++) {
				aux = ras.readChar(); // Lee el char del RandomAccessFile y lo guarda en la variable auxiliar
				apellido[i] = aux; // Va guardando los caracteres en el array
			}
			
			String apellidos = new String(apellido); // Declara la variable "apellidos" de tipo String
			dpto = ras.readInt(); // Lee un int del RandomAccessFile y lo guarda en la variable "dpto"
			salario = ras.readDouble(); // Lee un double del RandomAccessFile y lo guarda en la variable "salario"
			
			System.out.printf("ID: %s | Apellido: %s | Departamento: %d | Salario: %.2f€%n", id, apellidos.trim(), dpto, salario); // Impresión formateada de cada uno de los empleados
			
			posicion += 36; // Aumenta la variable "posicion" en 36, ya que son el número de bytes que ocupa cada trabajador, de forma que empezareos a leer el siguiente
			ras.seek(posicion); // Coloca el cursor en "posicion"
		}
		
		ras.close(); // Cierra el flujo RandomAccessStream para evitar fuga de recursos del sistema
		
	}

}