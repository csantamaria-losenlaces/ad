/* Nombre: Carlos Santamaría Gracia
 * Curso: 2º D.A.M. Vespertino
 * Entrega proyecto 1ª evaluación de Acceso a Datos */

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Ej10_Principal {

    public static void main(String[] args) throws IOException {

        // Se comprueba si se proporciona exactamente un argumento por la entrada estándar (línea de comandos)
        if (args.length == 1) {

            // Se declaran constantes para el tamaño de cada registro, el número de registro elegido y la longitud de las cadenas de texto
            final int LONG_REGISTRO = 92;
            final int NUM_REG_ELEGIDO = (Integer.parseInt(args[0])) - 1;
            final int LONG_STRING = 20;

            // Se crea un objeto File que representa el archivo "empleados.dat"
            File f = new File("empleados.dat");

            // Crea un objeto RandomAccessFile para acceder al archivo en modo de solo lectura
            RandomAccessFile ras = new RandomAccessFile(f, "r");

            // Se inicializa la posición inicial de lectura en 0 (principio del archivo) y declara la variable para almacenar el ID del empleado
            long posInicial = 0;
            int idEmpleado;

            // Se declara arrays de caracteres para almacenar el apellido y el departamento, así como variables String para contener sus valores
            char apellidoChar[] = new char[LONG_STRING];
            char departamentoChar[] = new char[LONG_STRING];
            String apellido, departamento;
            char aux;

            // Se declara una variable para almacenar el salario del empleado
            double salario;

            // Se verifica si el número de registro elegido no es el primero y, si es así, calcula la posición inicial de lectura
            if (!args[0].equals("1")) {
                posInicial = NUM_REG_ELEGIDO * LONG_REGISTRO;
            }

            // Se posiciona el cursor en el fichero en función del argumento recibido por consola
            ras.seek(posInicial);

            // Se lee el ID del empleado del archivo y lo almacena en la variable "idEmpleado"
            idEmpleado = ras.readInt();

            // Se leen los caracteres del apellido del archivo y los guarda en el array correspondiente
            for (int i = 0; i < apellidoChar.length; i++) {
                aux = ras.readChar(); // Se lee el char del RandomAccessFile y lo guarda en la variable auxiliar
                apellidoChar[i] = aux; // Se van guardando los caracteres en el array
            }

            // Se convierte el array de caracteres del apellido a un String
            apellido = new String(apellidoChar);

            // Se leen los caracteres del departamento del archivo y los guarda en el array correspondiente
            for (int i = 0; i < departamentoChar.length; i++) {
                aux = ras.readChar(); // Se lee el char del RandomAccessFile y lo guarda en la variable auxiliar
                departamentoChar[i] = aux; // Se va guardando los caracteres en el array
            }

            // Se convierte el array de caracteres del departamento a un String
            departamento = new String(departamentoChar);

            // Se lee el salario del archivo y se almacena en la variable "salario"
            salario = ras.readDouble();

            // Se imprime la información del empleado formateada
            System.out.printf("ID: %s | Apellido: %s | Departamento: %s | Salario: %.2f€%n", idEmpleado, apellido.trim(), departamento.trim(), salario);

            // Se cierra el objeto RandomAccessFile para evitar problemas de rendimiento
            ras.close();
        }
    }
}
