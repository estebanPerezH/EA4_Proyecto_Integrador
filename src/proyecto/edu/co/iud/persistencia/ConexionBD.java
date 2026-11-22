package proyecto.edu.co.iud.persistencia;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConexionBD {
    private String url;

    public ConexionBD() {
        this("jdbc:sqlite:cafeteria.db");
    }

    public ConexionBD(String url) {
        this.url = url;
    }

    public String conectarYCrearTabla() throws ClassNotFoundException, SQLException {
        // Carga del driver JDBC para detectar ClassNotFoundException si falta el .jar
        Class.forName("org.sqlite.JDBC");

        String sqlTabla = "CREATE TABLE IF NOT EXISTS pedidos ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "descripcion TEXT, "
                + "monto_total REAL, "
                + "descuento REAL, "
                + "monto_final REAL, "
                + "fecha TEXT)";

        // Cierre automático con try-with-resources
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlTabla);
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM pedidos")) {
                int total = rs.next() ? rs.getInt(1) : 0;
                return "Conexión JDBC exitosa a " + url
                        + "\nTabla 'pedidos' lista (" + total + " registro(s))."
                        + "\nConexión cerrada automáticamente con try-with-resources.";
            }
        }
    }

    public void guardarPedido(String descripcion, double montoTotal, double descuento, double montoFinal)
            throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        String sql = "INSERT INTO pedidos (descripcion, monto_total, descuento, monto_final, fecha) VALUES (?, ?, ?, ?, ?)";
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, descripcion);
            pstmt.setDouble(2, montoTotal);
            pstmt.setDouble(3, descuento);
            pstmt.setDouble(4, montoFinal);
            pstmt.setString(5, fecha);
            pstmt.executeUpdate();
        }
    }

    public void exportarCsv(File archivo) throws ClassNotFoundException, SQLException, IOException {
        Class.forName("org.sqlite.JDBC");
        if (archivo.getParentFile() != null) {
            archivo.getParentFile().mkdirs();
        }

        String sql = "SELECT id, descripcion, monto_total, descuento, monto_final, fecha FROM pedidos ORDER BY id";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql);
             FileOutputStream fos = new FileOutputStream(archivo);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(osw)) {

            // Marca BOM UTF-8 para abrir bien en Excel
            fos.write(0xEF);
            fos.write(0xBB);
            fos.write(0xBF);

            writer.write("ID;Descripcion;MontoTotal;Descuento;MontoFinal;Fecha");
            writer.newLine();

            while (rs.next()) {
                String desc = rs.getString("descripcion");
                String descLimpia = (desc != null) ? desc.replace(";", ",") : "";
                String linea = String.format("%d;%s;%.2f;%.2f;%.2f;%s",
                        rs.getInt("id"),
                        descLimpia,
                        rs.getDouble("monto_total"),
                        rs.getDouble("descuento"),
                        rs.getDouble("monto_final"),
                        rs.getString("fecha"));
                writer.write(linea);
                writer.newLine();
            }
        }
    }
}
