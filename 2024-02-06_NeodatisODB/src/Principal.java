import java.math.BigInteger;
import java.util.Scanner;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ObjectValues;
import org.neodatis.odb.Objects;
import org.neodatis.odb.Values;
import org.neodatis.odb.core.query.IValuesQuery;
import org.neodatis.odb.core.query.criteria.ICriterion;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.core.query.values.ValuesCriteriaQuery;

public class Principal {

	public static void main(String[] args) {

		Scanner teclado = new Scanner(System.in);
		Integer opc = 999;

		// Añadir objetos a la base de datos
		Sujeto s1 = new Sujeto("Maria", 23, "Madrid");
		Sujeto s2 = new Sujeto("Juan", 26, "Madrid");
		Sujeto s3 = new Sujeto("Lucia", 28, "Zaragoza");
		Sujeto s4 = new Sujeto("Marcos", 20, "Zaragoza");

		ODB odb = ODBFactory.open("tutorial.odb");

		odb.store(s1);
		odb.store(s2);
		odb.store(s3);
		odb.store(s4);

		odb.close();

		while (opc != 0) {
			System.out.println("Elige una opción: \n[1] Listar objetos\n[2] Seleccionar objetos\n[3] Eliminar objetos\n"
					+ "[4] Modificar objetos\n[5] Agregados\n[6]Agregados selección\n[7] Agrupados\n\n[0] SALIR");
			opc = teclado.nextInt();

			switch (opc) {
			case 1:
				listarObjetos();
				break;
			case 2:
				seleccionarObjetos();
				break;
			case 3:
				eliminarObjetos();
				break;
			case 4:
				modificarObjetos();
				break;
			case 5:
				agregados();
				break;
			case 6:
				agregadosSeleccion();
				break;
			case 7:
				agrupados();
				break;
			case 0:
				System.out.println("Has salido del programa");
				break;
			default:
				System.out.println("Error. La opción introducida no es válida");
			}
		}

		teclado.close();

	}

	// Listar objetos de la base de datos
	private static void listarObjetos() {
		ODB odb2 = ODBFactory.open("tutorial.odb");
		Objects<Sujeto> objects = odb2.getObjects(Sujeto.class);
		while (objects.hasNext()) {
			Sujeto sujeto_actual2 = objects.next();
			System.out.println("\t: " + sujeto_actual2.getName() + " " + sujeto_actual2.getEdad() + " "
					+ sujeto_actual2.getCiudad());
		}
		odb2.close();
	}

	// Seleccionar objetos
	private static void seleccionarObjetos() {
		ODB odb3 = ODBFactory.open("tutorial.odb");
		ICriterion criterio = Where.equal("edad", 23);
		CriteriaQuery query = new CriteriaQuery(Sujeto.class, criterio);
		Objects<Sujeto> objects3 = odb3.getObjects(query);
		while (objects3.hasNext()) {
			Sujeto sujeto_actual3 = objects3.next();
			System.out.println("\t: " + sujeto_actual3.getName() + " " + sujeto_actual3.getEdad());
		}
		odb3.close();
	}

	// Eliminar objetos
	private static void eliminarObjetos() {
		ODB odb4 = ODBFactory.open("tutorial.odb");
		ICriterion criterio4 = Where.gt("edad", 1);
		CriteriaQuery query4 = new CriteriaQuery(Sujeto.class, criterio4);
		Objects<Sujeto> objects4 = odb4.getObjects(query4);
		while (objects4.hasNext()) {
			Sujeto sujeto_actual4 = objects4.next();
			System.out.println("\t: " + sujeto_actual4.getName() + " " + sujeto_actual4.getEdad() + " <--Elimiado");
			odb4.delete(sujeto_actual4);
		}
		odb4.close();
	}

	// Modificar objetos
	private static void modificarObjetos() {
		ODB odb5 = ODBFactory.open("tutorial.odb");
		ICriterion criterio5 = Where.gt("edad", 2);
		CriteriaQuery query5 = new CriteriaQuery(Sujeto.class, criterio5);
		Objects<Sujeto> objects5 = odb5.getObjects(query5);
		while (objects5.hasNext()) {
			Sujeto sujeto_actual5 = objects5.next();
			System.out.println(
					"\t: " + sujeto_actual5.getName() + " " + sujeto_actual5.getEdad() + " <--Ciudad Cambiada");
			sujeto_actual5.setCiudad("XXXX");
			odb5.store(sujeto_actual5);
			odb5.commit();
		}
		odb5.close();
	}

	// Agregados
	private static void agregados() {
		ODB odb6 = ODBFactory.open("tutorial.odb");
		IValuesQuery valuesQuery6 = new ValuesCriteriaQuery(Sujeto.class).count("nombre");
		Values values6 = odb6.getValues(valuesQuery6);
		ObjectValues objectValues6 = values6.nextValues();
		BigInteger count6 = (BigInteger) objectValues6.getByAlias("nombre");
		System.out.println("Número de jugadores: " + count6.intValue());
		odb6.close();
	}

	// Agregados con selección
	private static void agregadosSeleccion() {
		ODB odb7 = ODBFactory.open("tutorial.odb");
		IValuesQuery valuesQuery7 = new ValuesCriteriaQuery(Sujeto.class, Where.gt("edad", 21)).count("nombre")
				.field("ciudad").groupBy("ciudad");
		Values values7 = odb7.getValues(valuesQuery7);
		while (values7.hasNext()) {
			ObjectValues objectValues7 = (ObjectValues) values7.next();
			System.out.println(objectValues7.getByAlias("ciudad") + " ---> " + objectValues7.getByAlias("nombre"));
		}
		odb7.close();
	}

	// Agrupados
	private static void agrupados() {
		ODB odb8 = ODBFactory.open("tutorial.odb");
		IValuesQuery valuesQuery8 = new ValuesCriteriaQuery(Sujeto.class, Where.gt("edad", 1)).avg("edad")
				.field("ciudad").groupBy("ciudad");
		Values values8 = odb8.getValues(valuesQuery8);
		while (values8.hasNext()) {
			ObjectValues objectValues8 = (ObjectValues) values8.next();
			System.out.println(objectValues8.getByAlias("ciudad") + " ---> " + objectValues8.getByAlias("edad"));
		}
		odb8.close();
	}

}