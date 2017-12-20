/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package g2.web.bean;

import com.sun.faces.util.CollectionsUtils;
import g2.web.entities.Producto;
import g2.web.util.ConexionManager;
import g2.web.util.Usuario;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author victor
 */
@Named(value = "beanSessionUsuario")
@SessionScoped
public class BeanSessionUsuario implements Serializable {

    ConexionManager cm;

    private Producto productoSeleccionado;

    private HashMap<Integer, Producto> productosEnCarrito;

    private Usuario usuarioAutenticado;

    private String nit;
    private String pass;

    private String codigoGiftCard = "";

    private int idUltimaVenta = 0;
    
    /**
     * Creates a new instance of BeanSessionUsuario
     */
    public BeanSessionUsuario() {
        cm = new ConexionManager();
        productosEnCarrito = new CollectionsUtils.ConstMap<>();
    }

    public List<Producto> getProductos() {
        List<Producto> list = new LinkedList<>();
        List<String[]> listObj = cm.ejecutarQuery("SELECT p.`id_producto`, p.`nombre`, p.`precio`, p.`descripcion`, c.`nombre` AS categoria\n"
                + "FROM producto p\n"
                + "	INNER JOIN `categoria` c ON c.`id_categoria` = p.`id_categoria`");
        listObj.forEach((row) -> {
            if (!productosEnCarrito.containsKey(Integer.valueOf(row[0]))) {
                list.add(new Producto(Integer.valueOf(row[0]), row[1], row[4], Float.valueOf(row[2]), row[3]));
            }
        });
        return list;
    }

    public List<Producto> getProductosEnCarrito() {
        return new LinkedList<>(this.productosEnCarrito.values());
    }

    public String verProductoDetalle(Producto p) {
        this.productoSeleccionado = p;
        return "producto";
    }

    public String agregarACarrito(Producto p) {
        if (!productosEnCarrito.containsKey(p.getId_producto())) {
            productosEnCarrito.put(p.getId_producto(), p);
        }
        return "carrito_compras";
    }

    public Producto getProductoSeleccionado() {
        if (productoSeleccionado == null) {
            productoSeleccionado = new Producto(0, "---", "---", 0, "---");
        }
        return productoSeleccionado;
    }

    public String comprar() {
        if (!productosEnCarrito.isEmpty()) {
            if (usuarioAutenticado != null) {
                //verificar si ya se uso la giftcard...
                List<String[]> giftCardUsada = cm.ejecutarQuery("SELECT * FROM venta v WHERE v.`id_giftcard` = " + codigoGiftCard);
                if (giftCardUsada.isEmpty()) {
                    List<String[]> giftCard = cm.ejecutarQuery("SELECT * FROM giftcard v WHERE v.`id_giftcard` = " + codigoGiftCard);
                    if (!codigoGiftCard.isEmpty() && giftCard.isEmpty()) {
                        agregarMensaje("La GiftCard que ha ingresado no existe.");
                    } else {
                        //calculando el monto final
                        float monto = 0;
                        for (Producto p : productosEnCarrito.values()) {
                            monto += p.getPrecio() * p.getCantidad();
                        }
                        //verificando si se aplica descuento...
                        if (!giftCard.isEmpty()) {
                            monto = monto / (1 + Float.valueOf(giftCard.get(0)[1]) / 100);
                        }
                        String sqlInsertVenta = "INSERT INTO `venta` (`estado`,`fecha`,`monto`,`nit_cliente`,`id_giftcard`) VALUES "
                                + "(1,NOW()," + monto + ",'" + usuarioAutenticado.getNit() + "'," + ((!giftCard.isEmpty()) ? "'" + giftCard.get(0)[0] + "'" : "null") + ");";
                        int id_venta = cm.ejecutarNoQuery(sqlInsertVenta);
                        if (id_venta > 0) {
                            for (Producto p : productosEnCarrito.values()) {
                                cm.ejecutarNoQuery("INSERT INTO `CRM`.`detalle_venta` ( `id_venta`, `id_producto`, `cantidad`, `precio_unitario` ) VALUES "
                                        + "(" + id_venta + "," + p.getId_producto() + "," + p.getCantidad() + "," + p.getPrecio() + ")");
                            }
                            agregarMensaje("Compra realizada con éxito.");
                            idUltimaVenta = id_venta;
                            productosEnCarrito.clear();
                            codigoGiftCard = "";
                            return "encuesta_compra";
                        }
                    }
                } else {
                    agregarMensaje("La GiftCard que esta intentando utilizar, ya fue utilizada.");
                }
            } else {
                agregarMensaje("Para comprar, primero debe autenticarse.");
            }
        } else {
            agregarMensaje("Para realizar una compra primero debe de seleccionar al menos un producto.");
        }
        return null;
    }

    public String contarVisita() {
        if (productoSeleccionado != null) {
            String sql = "INSERT INTO `CRM`.`contador_click` (`cliente_nit`,`fecha`,`id_producto`) VALUES "
                    + "(" + ((usuarioAutenticado != null) ? "'" + usuarioAutenticado.getNit() + "'" : "null") + ", NOW(), '" + productoSeleccionado.getId_producto() + "')";
            cm.ejecutarNoQuery(sql);
        }
        return "";
    }

    public void agregarMensaje(String mensaje) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Success", mensaje));
    }

    public String renderizarAutenticado() {
        return (usuarioAutenticado != null) ? "" : "hidden";
    }

    public String renderizarNoAutenticado() {
        return (usuarioAutenticado == null) ? "" : "hidden";
    }

    public String logout() {
        productosEnCarrito.clear();
        usuarioAutenticado = null;
        codigoGiftCard = "";
        nit = "";
        pass = "";
        return "index";
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Usuario getUsuarioAutenticado() {
        return usuarioAutenticado;
    }

    public void setUsuarioAutenticado(Usuario usuarioAutenticado) {
        this.usuarioAutenticado = usuarioAutenticado;
    }

    public String getCodigoGiftCard() {
        return codigoGiftCard;
    }

    public void setCodigoGiftCard(String codigoGiftCard) {
        this.codigoGiftCard = codigoGiftCard;
    }

    public int getIdUltimaVenta() {
        return idUltimaVenta;
    }

    public void setIdUltimaVenta(int idUltimaVenta) {
        this.idUltimaVenta = idUltimaVenta;
    }

    public String ingresar() {
        List<String[]> listObj = cm.ejecutarQuery("SELECT nit, nombre, apellido, direccion, telefono, correo, pass\n"
                + "FROM cliente c \n"
                + "WHERE c.nit = '" + this.nit + "'");
        if (listObj.isEmpty()) {
            agregarMensaje("Usuario o contraseña invalido.");
        } else {
            String[] arrayUsuario = listObj.get(0);
            usuarioAutenticado = new Usuario(arrayUsuario[0], arrayUsuario[1], arrayUsuario[2], arrayUsuario[3], arrayUsuario[4], arrayUsuario[5], arrayUsuario[6]);
            return "index";
        }
        return null;
    }

    public List<String[]> getHistorialCompras() {
        List<String[]> list = new LinkedList<>();
        if (usuarioAutenticado != null) {
            list = cm.ejecutarQuery("SELECT *, IF(g.descuento IS NULL, 0, g.descuento) AS descuento\n"
                    + "FROM venta v\n"
                    + "	LEFT JOIN `giftcard` g ON g.`id_giftcard` = v.`id_giftcard`\n"
                    + "WHERE v.`nit_cliente` = " + usuarioAutenticado.getNit() + "\n"
                    + "ORDER BY v.`fecha` DESC");
        }
        return list;
    }

    public List<String[]> getDetalleVenta(String[] venta) {
        List<String[]> list = new LinkedList<>();
        if (usuarioAutenticado != null) {
            list = cm.ejecutarQuery("SELECT v.`id_venta`, v.`estado`, v.`fecha`, v.`monto`, v.`nit_cliente`, p.`id_producto`, p.`nombre` AS nombre_producto, dv.`cantidad`, dv.`precio_unitario`, c.`nombre` AS nombre_categoria\n"
                    + "FROM venta v\n"
                    + "	INNER JOIN `detalle_venta` dv ON dv.`id_venta` = v.`id_venta`\n"
                    + "	INNER JOIN producto p ON p.`id_producto` = dv.`id_producto`\n"
                    + "	INNER JOIN categoria c ON c.`id_categoria` = p.`id_categoria`\n"
                    + "WHERE v.`id_venta` = " + venta[0] + "\n"
                    + "ORDER BY v.`fecha` DESC");
        }
        return list;
    }
    
    public String calcularDetalleVentaCosto(String[] item){
        return String.valueOf(Float.valueOf(item[8]) * Float.valueOf(item[7]));
    }

}
