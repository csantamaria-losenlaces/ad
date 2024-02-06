public class Sujeto {

	// Declaración de atributos
	private String nombre;
	private int edad;
	private String ciudad;
	
	// Constructor
	public Sujeto(String nombre, int edad, String ciudad) {
	this.nombre = nombre;
	this.edad = edad;
	this.ciudad = ciudad;
	}
	
	// Getters y setters
	public String getName() {
	return nombre;
	}
	
	public void setName(String nombre) {
	this.nombre = nombre;
	}
	
	public int getEdad() {
	return edad;
	}
	
	public void setEdad(int edad) {
	this.edad = edad;
	}
	
	public String getCiudad() {
	return ciudad;
	}
	
	public void setCiudad(String nombre) {
	this.nombre = ciudad;
	}
	
}