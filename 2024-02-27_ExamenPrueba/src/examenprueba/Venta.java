package examenprueba;

public class Venta {

	// Declaración de atributos
	private String fecha;
	private String producto;
	private String talla;
	private String color;
	private Double precio;
	private Integer cantidad;
	
	// Método constructor
	public Venta(String fecha, String producto, String talla, String color, Double precio, Integer cantidad) {
		this.fecha = fecha;
		this.producto = producto;
		this.talla = talla;
		this.color = color;
		this.precio = precio;
		this.cantidad = cantidad;
	}

	// Métodos Get y Set de los atributos
	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getProducto() {
		return producto;
	}

	public void setProducto(String producto) {
		this.producto = producto;
	}

	public String getTalla() {
		return talla;
	}

	public void setTalla(String talla) {
		this.talla = talla;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	
}