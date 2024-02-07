public class LineaPedido {

	// Declaración de atributos
	private int idPedido;
	private int idArticulo;
	private float cantidadPedida;
	
	// Método constructor
	public LineaPedido(int idPedido, int idArticulo, float cantidadPedida) {
		this.idPedido = idPedido;
		this.idArticulo = idArticulo;
		this.cantidadPedida = cantidadPedida;
	}

	// Getters y setters de los atributos
	public int getIdPedido() {
		return idPedido;
	}

	public void setIdPedido(int idPedido) {
		this.idPedido = idPedido;
	}

	public int getIdArticulo() {
		return idArticulo;
	}

	public void setIdArticulo(int idArticulo) {
		this.idArticulo = idArticulo;
	}

	public float getCantidadPedida() {
		return cantidadPedida;
	}

	public void setCantidadPedida(float cantidadPedida) {
		this.cantidadPedida = cantidadPedida;
	}
	
}