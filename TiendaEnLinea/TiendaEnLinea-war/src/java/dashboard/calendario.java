package dashboard;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.faces.bean.ManagedBean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement; 
 
@ManagedBean (name = "cal")
public class calendario {
    private Date date1;
    private Date date2;
    private String total;
    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
    
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
            String c = "jdbc:mysql://192.168.201.128/CRM?";
            conn = DriverManager.getConnection(c + "user=usuario&password=");
            Statement st = conn.createStatement();
            System.out.println("Dato Encontrado");
            System.out.println(date1.toString());
            System.out.println(date2.toString());
            ResultSet rs = st.executeQuery("select sum(monto) as total from venta where fecha > \'"
                    +s.format(date1)+"\'and fecha < \'"
                    +s.format(date2)+"\';");
            rs.first();
            this.total= "Q."+rs.getString("total");
            System.out.println("Listo");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}