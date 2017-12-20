/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package g2.web.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author victor
 */
public class ConexionManager {

    private Connection conn;

    public Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                try {
                    Class.forName("com.mysql.jdbc.Driver").newInstance();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    String c = "jdbc:mysql://db-server/CRM?";
                    conn = DriverManager.getConnection(c + "user=crmusr&password=123456");
                } catch (SQLException e) {
                    System.out.println("+++++++++++++Error+++++++++++++");
                    System.out.println("SQLException: " + e.getMessage());
                    System.out.println("SQLState: " + e.getSQLState());
                    System.out.println("VendorError: " + e.getErrorCode());
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConexionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }

    /**
     * Ejecuta un query y retorna un arreglo con las tuplas en una lista con objetos String[]
     */
    public List<String[]> ejecutarQuery(String sql) {
        List<String[]> list = new LinkedList<>();
        getConnection();
        if (conn != null) {
            try {
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                while (rs.next()) {
                    String[] o = new String[rs.getMetaData().getColumnCount()];
                    for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                        o[i] = rs.getString(i + 1);
                    }
                    list.add(o);
                }
            } catch (SQLException ex) {
                Logger.getLogger(ConexionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return list;
    }

    /**
     * Ejecuta una instruccion que no sea un query y retorna el ultimo ID generado...
     */
    public int ejecutarNoQuery(String sql) {
        getConnection();
        if (conn != null) {
            try {
                Statement st = conn.createStatement();
                //VERIFICAR QUE AFECTE LOS DATOS...
                if (st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS) >= 0) {
                    //retornando la ultima llave generada...
                    ResultSet generatedKeys = st.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                } else {
                    return 0;
                }
            } catch (SQLException ex) {
                Logger.getLogger(ConexionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return -1;
    }

}
