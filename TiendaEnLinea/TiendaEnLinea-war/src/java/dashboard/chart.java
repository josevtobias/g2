package dashboard;

import javax.faces.bean.ManagedBean;
import javax.annotation.PostConstruct;
import java.io.Serializable;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@ManagedBean (name = "barra")
public class chart implements Serializable{
    private BarChartModel barModel;
    BarChartModel model = new BarChartModel();
    ChartSeries producto = new ChartSeries();

    @PostConstruct
    public void init(){
         createBarModels();
    }

    public BarChartModel getBarModel() {
        return barModel;
    }

    private void createBarModels() {
        createBarModel();
    }
     
    private void createBarModel() {
        barModel = initBarModel();
         
        barModel.setTitle("");
        barModel.setLegendPosition("ne");
        barModel.setShadow(true);
        Axis xAxis = barModel.getAxis(AxisType.X);
        xAxis.setLabel("Nombre del Producto");
         
        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("Cantidad");
        yAxis.setMin(0);
        yAxis.setMax(8000);
    } 
    private BarChartModel initBarModel() {
        producto.setLabel("Total");
        model.addSeries(producto);
        cargarDatos();
        return model;
    }
    void cargarDatos(){
        boolean bandera = false;
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            bandera = true;
        }catch(Exception ex){ 
            ex.printStackTrace();
            bandera = false;
        }
        if (bandera) {
            carga();
        } else {
            System.out.println("Sin Conexion");
        }
    }
    void carga(){
        try {
            Connection conn;
            server s = new server();
            String c = s.getServer();
            conn = DriverManager.getConnection(c);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("select producto.nombre, resultados.can\n" +
                    "from (select id_producto, sum(cantidad) as can from detalle_venta group by id_producto order by can desc) as resultados\n" +
                    "inner join producto where resultados.id_producto = producto.id_producto limit 5;");
            while (rs.next()) {
                producto.set(rs.getString("nombre"), rs.getInt("can"));
            }
            conn.close();
        } catch (Exception e) {
        }
    }
}
