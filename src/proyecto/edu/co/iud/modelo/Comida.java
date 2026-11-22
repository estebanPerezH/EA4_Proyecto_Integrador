package proyecto.edu.co.iud.modelo;

public class Comida extends Producto {
    private String tipo;

    public Comida(String nombre, double precio, String tipo) {
        super(nombre, precio);
        this.tipo = tipo;
    }

    public String getTipo() {
        return this.tipo;
    }

    // Sobrescritura (Overriding)
    @Override
    public String mostrarInfo() {
        return nombre + " - " + tipo + " $" + String.format("%.2f", precio);
    }
}
