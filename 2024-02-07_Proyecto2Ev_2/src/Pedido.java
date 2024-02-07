public class Pedido {
	
	// Declaración de atributos
	private int idPedido;
	private int idCliente;
	private String fechaPedido;
	
	// Método constructor
	public Pedido(int idPedido, int idCliente, String fechaPedido) {
		this.idPedido = idPedido;
		this.idCliente = idCliente;
		this.fechaPedido = fechaPedido;
	}
	
	// Getters y setters de los atributos
	public int getIdPedido() {
		return idPedido;
	}

	public void setIdPedido(int idPedido) {
		this.idPedido = idPedido;
	}

	public int getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(int idCliente) {
		this.idCliente = idCliente;
	}

	public String getFechaPedido() {
		return fechaPedido;
	}

	public void setFechaPedido(String fechaPedido) {
		this.fechaPedido = fechaPedido;
	}
	
}