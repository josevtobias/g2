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
    private String tabla="";
    private String visitas="";
    
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

    public String getTabla() {
        return tabla;
    }

    public void setTabla(String tabla) {
        this.tabla = tabla;
    }

    public String getVisitas() {
        return visitas;
    }
    
    
    
    public void envios(){
        try{Class.forName("com.mysql.jdbc.Driver").newInstance();
        }catch(Exception ex){ ex.printStackTrace();}
        
        Connection conn;
        
        try{
            server s = new server();
            String c = s.getServer();
            conn = DriverManager.getConnection(c);
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
            //Lista de Ventas en periodo anterior
            rs = st.executeQuery("select monthname(fecha) as mes, sum(monto) as monto_mensual "
                    + "from venta where fecha > (DATE_SUB(CURDATE(), INTERVAL 3 MONTH)) "
                    + "group by month(fecha) ;");
            tabla ="<table class=\"u-full-width\">\n" +
                            "  <thead>\n" +
                            "    <tr>\n" +
                            "      <th>Mes</th>\n" +
                            "      <th>Venta Mensual</th>\n" +
                            "    </tr>\n" +
                            "  </thead>\n" +
                            "  <tbody>";
            while (rs.next()) {                
                tabla += "<tr> <td>" + rs.getString("mes") + "</td>" ;
                tabla += "<td>" + rs.getString("monto_mensual") + "</td></tr>" ;
            }
            tabla+="</tbody></table>";
            //
            rs = st.executeQuery("select producto.nombre, contador.c \n" +
                    "from producto, (select id_producto p, contador c from contador_click) as contador \n" +
                    "where contador.p = producto.id_producto \n" +
                    "order by contador.c desc;");
            this.visitas ="<table class=\"u-full-width\">\n" +
                            "  <thead>\n" +
                            "    <tr>\n" +
                            "      <th>Producto</th>\n" +
                            "      <th>Conteo de Visitas</th>\n" +
                            "    </tr>\n" +
                            "  </thead>\n" +
                            "  <tbody>";
            while (rs.next()) {                
                this.visitas += "<tr> <td>" + rs.getString("nombre") + "</td>" ;
                this.visitas += "<td>" + rs.getString("c") + "</td></tr>" ;
            }
            this.visitas+="</tbody></table>";
            
            conn.close();
        } catch (SQLException e) {
            System.out.println("+++++++++++++Error+++++++++++++");
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }
    }
    
}
