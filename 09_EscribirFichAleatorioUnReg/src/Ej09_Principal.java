import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class Ej09_Principal {

	private static Scanner pedirString = new Scanner(System.in);
	private static Scanner pedirInt = new Scanner(System.in);
	private static Scanner pedirDouble = new Scanner(System.in);
	private static RandomAccessFile fichEmpleados;
	
	public static void main(String[] args) throws IOException {
		
		int idEmpleado;
		String apellido, departamento, seguirIntroduciendo = "S";
		double salario;
		
		fichEmpleados = new RandomAccessFile("empleados.dat", "rw");
		
		while (!seguirIntroduciendo.equals("N") && !seguirIntroduciendo.equals("n")) {
			System.out.println("Introduce un ID de empleado:");
			idEmpleado = pedirInt.nextInt();
			System.out.println("Introduce el apellido:");
			apellido = pedirString.nextLine();
			System.out.println("Introduce el departamento:");
			departamento = pedirString.nextLine();
			System.out.println("Introduce el salario (formato 0,00):");
			salario = pedirDouble.nextDouble();
			
			fichEmpleados.seek(fichEmpleados.length());
			fichEmpleados.writeBytes("ID: " + idEmpleado + " | Apellido: " + apellido + " | Dpto. " + departamento + " | Salario: " + salario);
			fichEmpleados.write('\n');
			
			System.out.println("¿Quieres introducir otro empleado? (S/N)");
			seguirIntroduciendo = pedirString.nextLine();
		}
		
		pedirInt.close();
		pedirString.close();
		pedirDouble.close();
		fichEmpleados.close();
		System.out.println("El programa ha finalizado.");
		
	}
	
}