import java.io.IOException;
import java.io.RandomAccessFile;

public class Ej08_Principal {

	private static RandomAccessFile fichEmpleados;

	public static void main(String[] args) throws IOException {

		String lineaLeida;

		fichEmpleados = new RandomAccessFile("empleados.dat", "r");

		fichEmpleados.seek(0);
		lineaLeida = fichEmpleados.readLine();

		while (lineaLeida != null) {
			System.out.println(lineaLeida);
			lineaLeida = fichEmpleados.readLine();
		}

		fichEmpleados.close();
		
	}

}