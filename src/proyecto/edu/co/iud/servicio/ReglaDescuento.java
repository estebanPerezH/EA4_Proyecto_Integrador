package proyecto.edu.co.iud.servicio;

@FunctionalInterface
public interface ReglaDescuento {
    double aplicarDescuento(double montoTotal);
}
