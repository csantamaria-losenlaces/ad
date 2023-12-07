import java.io.Serializable;

// Clase que implementa la interfaz Serializable que convierte objetos de Java a
// binario y luego los puede recuperar nuevamente como objetos.
public class Persona implements Serializable {

	private static final long serialVersionUID = 284796237982514963L; // Variable autogenerada requerida tras la implementación de Serializable
	
	String nombre;
	String apellidos;
	String dni;
	String provincia;
	
	// Método constructor para el objeto Persona que recibe 4 parámetros: nombre, apellidos, DNI y provincia
	public Persona(String nombre, String apellidos, String dni, String provincia) {
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.dni = dni;
		this.provincia = provincia;
	}

	@Override
	public String toString() {
		return "Persona [nombre=" + nombre + ", apellidos=" + apellidos + ", dni=" + dni + ", provincia=" + provincia
				+ "]";
	}

}