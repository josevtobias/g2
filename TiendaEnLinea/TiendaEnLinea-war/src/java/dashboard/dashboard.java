package dashboard;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.faces.bean.ManagedBean;

@ManagedBean(name = "dashboard")

public class dashboard {
    private String enBodega = "None";
    private String enCamino = "None";
    private String entregado = "None"; 
    private String devolucion = "None";
    private String totales = "None";
    
    
    public String getEnBodega() {
        return enBodega;
    }

    public String getEnCamino() {
        return enCamino;
    }

    public String getEntregado() {
        return entregado;
    }

    public String getDevolucion() {
        return devolucion;
    }

    public String getTotales() {
        return totales;
    }
        
    public void envios(){
        try{Class.forName("com.mysql.jdbc.Driver").newInstance();
        }catch(Exception ex){ ex.printStackTrace();}
        
        Connection conn;
        
        try{
            String c = "jdbc:mysql://192.168.201.128/CRM?";
            conn = DriverManager.getConnection(c + "user=usuario&password=");
            System.out.println("-----Conexion Establecida -----");
            Statement st = conn.createStatement();
            //Envios en bodega
            ResultSet rs = st.executeQuery("select count(venta.estado) as e from venta where estado = 0;");
            rs.first();
            this.enBodega = rs.getString("e");
            //Envios en camino
            rs = st.executeQuery("select count(venta.estado) as e from venta where estado = 1;");
            rs.first();
            this.enCamino = rs.getString("e");
            //Envios entregados
            rs = st.executeQuery("select count(venta.estado) as e from venta where estado = 2;");
            rs.first();
            this.entregado = rs.getString("e");
            //Envios entregados
            rs = st.executeQuery("select count(venta.estado) as e from venta where estado = 3;");
            rs.first();
            this.devolucion = rs.getString("e");
            rs = st.executeQuery("select count(id_venta) as e from venta;");
            rs.first();
            this.totales = rs.getString("e");
            System.out.println("Dato Encontrado");
            conn.close();
        } catch (SQLException e) {
            System.out.println("+++++++++++++Error+++++++++++++");
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }
    }
    
}
