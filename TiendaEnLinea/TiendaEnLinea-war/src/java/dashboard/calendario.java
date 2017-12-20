package dashboard;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.faces.bean.ManagedBean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement; 
 
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.primefaces.model.chart.MeterGaugeChartModel;

@ManagedBean (name = "cal")
public class calendario{
    private Date date1;
    private Date date2;
    private String total="Total de ventas Q.0.00";
    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
    private MeterGaugeChartModel meterGaugeModel1;
    
    public Date getDate1() {
        return date1;
    }

    public void setDate1(Date date1) {
        this.date1 = date1;
    }

    public Date getDate2() {
        return date2;
    }

    public void setDate2(Date date2) {
        this.date2 = date2;
    }
    
    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
    
    public void cargar(){
        cargarDatos();
    }
    
    public void cargarDatos(){
        boolean bandera = false;
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            bandera = true;
        }catch(Exception ex){ 
            bandera = false;
        }
        if (bandera) {
            System.out.println("Cargando");
            cargarVentas();
        } else {
            System.out.println("Sin Conexion");
        }
    }
    void cargarVentas(){
            try {
            Connection conn;
            server se = new server();
            String c = se.getServer();
            conn = DriverManager.getConnection(c);
            Statement st = conn.createStatement();
            System.out.println("Dato Encontrado");
            System.out.println(date1.toString());
            System.out.println(date2.toString());
            ResultSet rs = st.executeQuery("select sum(monto) as total from venta where fecha > \'"
                    +s.format(date1)+"\'and fecha < \'"
                    +s.format(date2)+"\';");
            rs.first();
            this.total = "Total de ventas Q." + rs.getString("total");
            System.out.println("Listo");
            if(!rs.getString("total").equals("null")){
            createMeterGaugeModels(rs.getInt("total")/100);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    /*Metodos para el velocimetro*/
    @PostConstruct
    public void init() {
        createMeterGaugeModels(0);
    }
    private void createMeterGaugeModels(int i) {
        List<Number> intervals = new ArrayList<Number>(){{
            add(1000);
            add(2500);
            add(5000);
            add(7500);
            add(10000);
            
        }};
        meterGaugeModel1 = new MeterGaugeChartModel(i, intervals);
        meterGaugeModel1.setTitle("Indicador de Ventas Proyectadas");
        meterGaugeModel1.setGaugeLabel("Q x 100");
        meterGaugeModel1.setSeriesColors("d83737,d85737,d89c37,a7d837,38d86d");
    }
    public MeterGaugeChartModel getMeterGaugeModel1() {
        return meterGaugeModel1;
    }

}