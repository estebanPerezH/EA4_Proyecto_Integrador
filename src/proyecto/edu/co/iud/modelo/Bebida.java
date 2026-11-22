package proyecto.edu.co.iud.modelo;

public class Bebida extends Producto {
    private String tamano;

    public Bebida(String nombre, double precio, String tamano) {
        super(nombre, precio);
        this.tamano = tamano;
    }

    public String getTamano() {
        return this.tamano;
    }

    // Sobrescritura (Overriding)
    @Override
    public String mostrarInfo() {
        return nombre + " - Bebida (" + tamano + ") $" + String.format("%.2f", precio);
    }
}
