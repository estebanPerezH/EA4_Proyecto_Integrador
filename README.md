# EA4 - Proyecto Integrador: Descuentos en Cafetería

Aplicación en Java (Swing + Lambda + JDBC) para gestionar los descuentos de los pedidos de la cafetería en la materia **Programación Orientada a Objetos II** (IUDigital de Antioquia).

## Estructura del proyecto

```text
EA4_Proyecto_Integrador/
├── evidencias/
│   ├── 01_inicio_conexion_jdbc.png
│   ├── 02_descuento_aplicado_15.png
│   ├── 03_sin_descuento_monto_menor.png
│   ├── 04_error_jdbc_controlado.png
│   ├── diagrama_uml_ea4.png
│   └── pedidos_cafeteria.csv
├── lib/
│   └── sqlite-jdbc-3.39.4.1.jar
├── src/
│   └── proyecto/edu/co/iud/
│       ├── modelo/
│       │   ├── Bebida.java
│       │   ├── Comida.java
│       │   └── Producto.java
│       ├── persistencia/
│       │   └── ConexionBD.java
│       ├── servicio/
│       │   └── ReglaDescuento.java
│       └── vista/
│           └── VentanaCafeteria.java
└── AA4_Proyecto_integrador_PerezJhoan.pdf
```

## Cómo compilar y ejecutar

```powershell
javac -encoding UTF-8 -d bin (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })
java -cp "bin;lib/*" proyecto.edu.co.iud.vista.VentanaCafeteria
```
