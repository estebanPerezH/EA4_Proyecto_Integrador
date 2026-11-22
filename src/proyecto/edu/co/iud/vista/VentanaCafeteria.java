package proyecto.edu.co.iud.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import proyecto.edu.co.iud.modelo.Bebida;
import proyecto.edu.co.iud.modelo.Comida;
import proyecto.edu.co.iud.modelo.Producto;
import proyecto.edu.co.iud.persistencia.ConexionBD;
import proyecto.edu.co.iud.servicio.ReglaDescuento;

public class VentanaCafeteria extends JFrame {
    private ConexionBD conexionBD;
    private ReglaDescuento reglaDescuento;
    private File archivoCsv;

    private JComboBox<Producto> cbProductos;
    private JTextField txtMontoTotal;
    private JButton btnCalcularDescuento;
    private JTextArea txaResultado;
    private String detallePedido = "";

    public VentanaCafeteria() {
        // Expresión lambda: si el monto > $10 aplica 15 %, si no 0 %
        this(
                new ConexionBD(),
                monto -> (monto > 10.0) ? (monto * 0.15) : 0.0,
                new File("evidencias", "pedidos_cafeteria.csv"));
    }

    public VentanaCafeteria(ConexionBD conexionBD, ReglaDescuento reglaDescuento, File archivoCsv) {
        this.conexionBD = conexionBD;
        this.reglaDescuento = reglaDescuento;
        this.archivoCsv = archivoCsv;
        configurarVentana();
        inicializarComponentes();
        probarConexionInicial();
    }

    private void configurarVentana() {
        setTitle("Cafetería IUD - Gestión de Descuentos");
        setSize(680, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelHeader.setBackground(new Color(0, 43, 73));
        JLabel lblTitulo = new JLabel("Cafetería IUD - Descuentos en Pedidos");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panelHeader.add(lblTitulo);
        add(panelHeader, BorderLayout.NORTH);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Pedido"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Menú de productos usando herencia y polimorfismo (Bebida / Comida)
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulario.add(new JLabel("Producto del menú:"), gbc);

        cbProductos = new JComboBox<>(new Producto[] {
                new Bebida("Café Americano", 4.50, "Mediano"),
                new Bebida("Jugo Natural", 5.50, "Grande"),
                new Comida("Sándwich de Pollo", 8.50, "Salado"),
                new Comida("Combo Almuerzo", 12.50, "Plato completo")
        });
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panelFormulario.add(cbProductos, gbc);

        JButton btnAgregar = new JButton("Agregar");
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        panelFormulario.add(btnAgregar, gbc);

        // Campo de monto total
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelFormulario.add(new JLabel("Monto total ($):"), gbc);

        txtMontoTotal = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        panelFormulario.add(txtMontoTotal, gbc);

        // Botón Calcular Descuento
        btnCalcularDescuento = new JButton("Calcular Descuento");
        btnCalcularDescuento.setBackground(new Color(0, 168, 150));
        btnCalcularDescuento.setForeground(Color.WHITE);
        btnCalcularDescuento.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCalcularDescuento.setFocusPainted(false);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        panelFormulario.add(btnCalcularDescuento, gbc);

        // Área de texto para resultados y mensajes de BD
        txaResultado = new JTextArea(10, 45);
        txaResultado.setEditable(false);
        txaResultado.setLineWrap(true);
        txaResultado.setWrapStyleWord(true);
        txaResultado.setFont(new Font("Consolas", Font.PLAIN, 13));

        JScrollPane scrollResultado = new JScrollPane(txaResultado);
        scrollResultado.setBorder(BorderFactory.createTitledBorder("Resultado y Estado de Conexión"));

        JPanel panelCentro = new JPanel(new BorderLayout(10, 10));
        panelCentro.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));
        panelCentro.add(panelFormulario, BorderLayout.NORTH);
        panelCentro.add(scrollResultado, BorderLayout.CENTER);
        add(panelCentro, BorderLayout.CENTER);

        // Eventos con lambdas
        btnAgregar.addActionListener(e -> agregarProducto());
        btnCalcularDescuento.addActionListener(e -> calcularDescuento());
    }

    private void probarConexionInicial() {
        try {
            String estado = conexionBD.conectarYCrearTabla();
            txaResultado.setForeground(new Color(21, 128, 61));
            txaResultado.setText(estado);
        } catch (ClassNotFoundException ex) {
            txaResultado.setForeground(new Color(185, 28, 28));
            txaResultado.setText("Error de driver JDBC (ClassNotFoundException): " + ex.getMessage());
        } catch (SQLException ex) {
            txaResultado.setForeground(new Color(185, 28, 28));
            txaResultado.setText("Error de conexión JDBC (SQLException): " + ex.getMessage());
        }
    }

    private void agregarProducto() {
        Producto seleccionado = (Producto) cbProductos.getSelectedItem();
        if (seleccionado == null) {
            return;
        }
        double actual = 0;
        if (!txtMontoTotal.getText().trim().isEmpty()) {
            try {
                actual = Double.parseDouble(txtMontoTotal.getText().trim().replace(',', '.'));
            } catch (NumberFormatException ignored) {
                actual = 0;
            }
        }
        double nuevoTotal = actual + seleccionado.getPrecio();
        txtMontoTotal.setText(String.format("%.2f", nuevoTotal).replace(',', '.'));
        detallePedido = detallePedido.isEmpty()
                ? seleccionado.mostrarInfo()
                : detallePedido + ", " + seleccionado.mostrarInfo();
        txaResultado.setForeground(new Color(21, 128, 61));
        txaResultado.setText("Producto agregado: " + seleccionado.mostrarInfo()
                + "\nMonto actual: $" + String.format("%.2f", nuevoTotal));
    }

    private void calcularDescuento() {
        try {
            double monto = Double.parseDouble(txtMontoTotal.getText().trim().replace(',', '.'));
            if (monto <= 0) {
                JOptionPane.showMessageDialog(this, "El monto debe ser mayor a 0.", "Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Uso de la expresión lambda mediante ReglaDescuento
            double descuento = reglaDescuento.aplicarDescuento(monto);
            double montoFinal = monto - descuento;
            String descripcion = detallePedido.isEmpty() ? "Pedido en cafetería" : detallePedido;

            String mensajeBd;
            try {
                conexionBD.guardarPedido(descripcion, monto, descuento, montoFinal);
                if (archivoCsv != null) {
                    conexionBD.exportarCsv(archivoCsv);
                }
                mensajeBd = "Guardado en SQLite y CSV correctamente.";
            } catch (ClassNotFoundException | SQLException ex) {
                mensajeBd = "Error al guardar en BD: " + ex.getMessage();
            } catch (Exception ex) {
                mensajeBd = "Error al exportar CSV: " + ex.getMessage();
            }

            txaResultado.setForeground(new Color(21, 128, 61));
            txaResultado.setText(
                    "Detalle: " + descripcion + "\n"
                    + "Monto original: $" + String.format("%.2f", monto) + "\n"
                    + "Descuento aplicado: $" + String.format("%.2f", descuento) + "\n"
                    + "Monto final a pagar: $" + String.format("%.2f", montoFinal) + "\n"
                    + "Base de datos: " + mensajeBd);
            detallePedido = "";
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un número válido para el monto.", "Error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaCafeteria ventana = new VentanaCafeteria();
            ventana.setVisible(true);
        });
    }
}
